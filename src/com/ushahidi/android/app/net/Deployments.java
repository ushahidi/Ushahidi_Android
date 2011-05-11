
package com.ushahidi.android.app.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ushahidi.android.app.UshahidiApplication;
import com.ushahidi.android.app.data.DeploymentsData;
import com.ushahidi.android.app.util.DeviceCurrentLocation;

/**
 * Contains logic to load the details of a deployment and find a list of
 * matching deployments given a query. Everything is held in SQLite database;
 */
public class Deployments {

    private static final String DEPLOYMENT_SEARCH_URL = "http://tracker.ushahidi.com/list/";

    private Context mContext;

    private String mDistance;

    private double lat;

    private double lon;

    private DeviceCurrentLocation deviceLocation;

    private JSONObject jsonObject;

    private boolean processingResult;

    private String deploymentJson;
    
    private ArrayList<DeploymentsData> deploymentsData;

    public Deployments(Context context) {
        mContext = context;
        deploymentJson = "";
        
    }

    /**
     * Fetches deployments from the internet.
     * 
     * @param String distance
     */
    public boolean fetchDeployments(String distance, Location location) {
        this.mDistance = distance;
       
        Log.d("deploymentSearch","Lat: "+lat+"-- Lon"+lon);
        // check if current location was retrieved.
        if (location !=null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            deploymentJson = getDeploymentsFromOnline();
            processingResult = true;
            
            try {
                jsonObject = new JSONObject(deploymentJson);
                deploymentsData = retrieveDeploymentJson();
                
                if( deploymentsData != null ) {
                    UshahidiApplication.mDb.addDeployment(deploymentsData);
                    return true;
                }
            } catch (JSONException e) {
                processingResult = false;
            }
            
        }
        return false;
    }

    public String getDeploymentsFromOnline() {
        StringBuilder fullUrl = new StringBuilder(DEPLOYMENT_SEARCH_URL);

        try {
            URL url = new URL(fullUrl.toString());
            ClientHttpRequest req = new ClientHttpRequest(url);
            req.setParameter("units", "km");
            req.setParameter("distance", mDistance);
            req.setParameter("lat", String.valueOf(lat));
            req.setParameter("lon", String.valueOf(lon));
            req.setParameter("limit", 10);

            InputStream inputStream = req.post();

            return GetText(inputStream);
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String GetText(InputStream in) {
        String text = "";
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1024);
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (final Exception ex) {
        } finally {
            try {
                in.close();
            } catch (final Exception ex) {
            }
        }
        return text;
    }

    public ArrayList<DeploymentsData> retrieveDeploymentJson() {
        int len = jsonObject.length();
        ArrayList<DeploymentsData> deploymentsList = new ArrayList<DeploymentsData>();
        if (processingResult) {
            for (int i = 0; i > len; i++) {
                DeploymentsData deploymentData = new DeploymentsData();
                try {
                    deploymentData.setId(jsonObject.getString("id"));
                    deploymentData.setDate(jsonObject.getString("discovery_date"));
                    deploymentData.setActive("0");
                    deploymentData.setLat(jsonObject.getString("latitude"));
                    deploymentData.setLon(jsonObject.getString("longitude"));
                    deploymentData.setName(jsonObject.getString("name"));
                    deploymentData.setUrl(jsonObject.getString("url"));
                    deploymentData.setDesc(jsonObject.getString("description"));
                    deploymentData.setCatId(jsonObject.getString("category_id"));
                } catch (JSONException e) {
                    processingResult = false;
                    return null;
                }
                deploymentsList.add(deploymentData);
            }
            return deploymentsList;
        }
        return null;
    }

}
