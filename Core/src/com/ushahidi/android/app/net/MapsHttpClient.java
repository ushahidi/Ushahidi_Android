
package com.ushahidi.android.app.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Map;

/**
 * Contains logic to load the details of a deployment and find a list of
 * matching deployments given a query. Everything is held in SQLite database;
 */
public class MapsHttpClient extends MainHttpClient {

    private static final String MAP_SEARCH_URL = "http://tracker.ushahidi.com/list/";

    private String mDistance;

    private double lat;

    private double lon;

    private JSONObject jsonObject;

    private boolean processingResult;

    private String mapJson;

    private List<Map> mMap;

    public MapsHttpClient(Context context) {
        super(context);
        mapJson = "";
    }

    /**
     * Fetches maps from the internet.
     * 
     * @param String distance
     */
    public boolean fetchMaps(String distance, Location location) {
        this.mDistance = distance;

        // check if current location was retrieved.
        if (location != null) {
            processingResult = true;
            lat = location.getLatitude();
            lon = location.getLongitude();

            mapJson = getMapsFromOnline();
            if (mapJson != null) {
                try {
                    jsonObject = new JSONObject(mapJson);
                    mMap = retrieveMapJson();

                    if (mMap != null) {
                        Database.mMapDao.deleteAllAutoMap();
                        Database.mMapDao.addMaps(mMap);
                        return true;
                    }
                } catch (JSONException e) {
                    processingResult = false;
                }
            }

        }
        return false;
    }

    public String getMapsFromOnline() {
        StringBuilder fullUrl = new StringBuilder(MAP_SEARCH_URL);
        fullUrl.append("?return_vars=name,latitude,longitude,description,url,category_id,discovery_date,id");
        fullUrl.append("&units=km");
        fullUrl.append("&distance=" + mDistance);
        fullUrl.append("&lat=" + String.valueOf(lat));
        fullUrl.append("&lon=" + String.valueOf(lon));
        HttpResponse response;

        try {

            response = GetURL(fullUrl.toString());
            if (response == null) {
                return null;
            }
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {

                return GetText(response);
            }
            // UshahidiPref.incidentsResponse = incidents;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public List<Map> retrieveMapJson() {
        JSONArray names = jsonObject.names();
        List<Map> mapsList = new ArrayList<Map>();
        if (processingResult) {
            for (int i = 0; i < names.length(); i++) {
                Map mapModel = new Map();
                try {

                    mapModel.setDbId(Long.valueOf(jsonObject.getJSONObject(names.getString(i))
                            .getString("id")));
                    mapModel.setDate(jsonObject.getJSONObject(names.getString(i)).getString(
                            "discovery_date"));
                    mapModel.setActive("0");
                    mapModel.setLat(jsonObject.getJSONObject(names.getString(i)).getString(
                            "latitude"));
                    mapModel.setLon(jsonObject.getJSONObject(names.getString(i)).getString(
                            "longitude"));
                    mapModel.setName(jsonObject.getJSONObject(names.getString(i)).getString("name"));
                    mapModel.setUrl(jsonObject.getJSONObject(names.getString(i)).getString("url"));

                    // use deployment name if there is no deployment description
                    if (jsonObject.getJSONObject(names.getString(i)).getString("description")
                            .equals("")) {
                        mapModel.setDesc(jsonObject.getJSONObject(names.getString(i)).getString(
                                "name"));
                    } else {
                        mapModel.setDesc(jsonObject.getJSONObject(names.getString(i)).getString(
                                "description"));
                    }
                    mapModel.setCatId(jsonObject.getJSONObject(names.getString(i)).getString(
                            "category_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    processingResult = false;
                    return null;
                }
                mapsList.add(mapModel);
            }
            return mapsList;
        }
        return null;
    }

}
