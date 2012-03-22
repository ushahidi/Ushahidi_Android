
package com.ushahidi.android.app.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ushahidi.android.app.data.DeploymentsData;
import com.ushahidi.android.app.database.Database;

/**
 * Contains logic to load the details of a deployment and find a list of
 * matching deployments given a query. Everything is held in SQLite database;
 */
public class Deployments {

    private static final String DEPLOYMENT_SEARCH_URL = "http://tracker.ushahidi.com/list/";

    private String mDistance;

    private double lat;

    private double lon;

    private JSONObject jsonObject;

    private boolean processingResult;

    private String deploymentJson;

    private ArrayList<DeploymentsData> deploymentsData;

    public Deployments(Context context) {
        deploymentJson = "";

    }

    /**
     * Fetches deployments from the internet.
     * 
     * @param String distance
     */
    public boolean fetchDeployments(String distance, Location location) {
        this.mDistance = distance;

        // check if current location was retrieved.
        if (location != null) {
            processingResult = true;
            lat = location.getLatitude();
            lon = location.getLongitude();

            deploymentJson = getDeploymentsFromOnline();
            if (deploymentJson != null) {
                try {
                    jsonObject = new JSONObject(deploymentJson);
                    deploymentsData = retrieveDeploymentJson();

                    if (deploymentsData != null) {
                        Database.map.deleteAllAutoDeployment();
                        //Database.map.addMap(deploymentsData);
                        return true;
                    }
                } catch (JSONException e) {
                    processingResult = false;
                }
            }

        }
        return false;
    }

    public String getDeploymentsFromOnline() {
        StringBuilder fullUrl = new StringBuilder(DEPLOYMENT_SEARCH_URL);
        fullUrl.append("?return_vars=name,latitude,longitude,description,url,category_id,discovery_date,id");
        fullUrl.append("&units=km");
        fullUrl.append("&distance=" + mDistance);
        fullUrl.append("&lat=" + String.valueOf(lat));
        fullUrl.append("&lon=" + String.valueOf(lon));
        HttpResponse response;

        //try {
            response = null;
            //response = MainHttpClient.GetURL(fullUrl.toString());
            if (response == null) {
                return null;
            }
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                 return null;
               // return MainHttpClient.GetText(response);
            }
            // UshahidiPref.incidentsResponse = incidents;
        //} catch (MalformedURLException e) {
           // return null;
        //} catch (IOException e) {
           // return null;
       // }
        return null;
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
        JSONArray names = jsonObject.names();
        ArrayList<DeploymentsData> deploymentsList = new ArrayList<DeploymentsData>();
        if (processingResult) {
            for (int i = 0; i < names.length(); i++) {
                DeploymentsData deploymentData = new DeploymentsData();
                try {

                    deploymentData.setId(jsonObject.getJSONObject(names.getString(i)).getString(
                            "id"));
                    deploymentData.setDate(jsonObject.getJSONObject(names.getString(i)).getString(
                            "discovery_date"));
                    deploymentData.setActive("0");
                    deploymentData.setLat(jsonObject.getJSONObject(names.getString(i)).getString(
                            "latitude"));
                    deploymentData.setLon(jsonObject.getJSONObject(names.getString(i)).getString(
                            "longitude"));
                    deploymentData.setName(jsonObject.getJSONObject(names.getString(i)).getString(
                            "name"));
                    deploymentData.setUrl(jsonObject.getJSONObject(names.getString(i)).getString(
                            "url"));

                    // use deployment name if there is no deployment description
                    if (jsonObject.getJSONObject(names.getString(i)).getString("description")
                            .equals("")) {
                        deploymentData.setDesc(jsonObject.getJSONObject(names.getString(i))
                                .getString("name"));
                    } else {
                        deploymentData.setDesc(jsonObject.getJSONObject(names.getString(i))
                                .getString("description"));
                    }
                    deploymentData.setCatId(jsonObject.getJSONObject(names.getString(i)).getString(
                            "category_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
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
