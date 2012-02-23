/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.models;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentActivity;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.data.DeploymentProvider;
import com.ushahidi.android.app.data.MapDb;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListMapModel extends Model {

    public List<ListMapModel> mMaps;

    private Cursor cursor;

    private String id;

    private String name;

    private String desc;

    private String url;

    private String date;

    private String lat;

    private String lon;

    private String catId;

    private String active;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActive() {
        return this.active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setCatId(String catId) {

        this.catId = catId;
    }

    public String getCatId() {
        return this.catId;
    }

    @Override
    public boolean load(Context context) {
        cursor = Database.map.fetchAllDeployments();
        mMaps = new ArrayList<ListMapModel>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int deploymentIdIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_ID);
                int deploymentNameIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_NAME);
                int deploymentDescIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_DESC);
                int deploymentUrlIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_URL);

                do {

                    ListMapModel listMapModel = new ListMapModel();

                    listMapModel.setId(cursor.getString(deploymentIdIndex));
                    listMapModel.setName(cursor.getString(deploymentNameIndex));
                    listMapModel.setDesc(cursor.getString(deploymentDescIndex));
                    listMapModel.setUrl(cursor.getString(deploymentUrlIndex));
                    mMaps.add(listMapModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 
     */
    public boolean filter(FragmentActivity activity, String query) {
        cursor = activity.managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
            query
        }, null);
        mMaps = new ArrayList<ListMapModel>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int deploymentIdIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int deploymentNameIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_NAME);
                int deploymentDescIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_DESC);
                int deploymentUrlIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_URL);

                do {

                    ListMapModel listMapModel = new ListMapModel();

                    listMapModel.setId(cursor.getString(deploymentIdIndex));
                    listMapModel.setName(cursor.getString(deploymentNameIndex));
                    listMapModel.setDesc(cursor.getString(deploymentDescIndex));
                    listMapModel.setUrl(cursor.getString(deploymentUrlIndex));
                    mMaps.add(listMapModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * Fetch deployments
     * 
     * @author eyedol
     * @return 0 -- Successfully fetches details of a deployment
     * @return 1 -- Failed to fetch details of a deployment.
     * @return 2 -- No internet connection
     */
    public void activateDeployment(Context context, String id) {

        final Cursor cursor;
        cursor = Database.map.fetchDeploymentById(id);
        String url = "";
        String latitude;
        String longitude;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int urlIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_URL);
                int latitudeIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_LATITUDE);
                int longitudeIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_LONGITUDE);

                do {
                    url = cursor.getString(urlIndex);
                    latitude = cursor.getString(latitudeIndex);
                    longitude = cursor.getString(longitudeIndex);
                    Preferences.activeDeployment = Util.toInt(id);
                    Preferences.domain = url;
                    Preferences.deploymentLatitude = latitude;
                    Preferences.deploymentLongitude = longitude;
                } while (cursor.moveToNext());

            }
            cursor.close();
            Preferences.saveSettings(context);
            Preferences.loadSettings(context);
        }

    }

    // Save stuff fetch from
    @Override
    public boolean save(Context context) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Delete a map by it's id
     * 
     * @param id - The ID of the map to be deleted
     * @return boolean
     */
    public boolean deleteMapById(int id) {
        return Database.map.deleteDeploymentById(String.valueOf(id));
    }

    public boolean deleteAllMap(Context context) {
        Database.map.deleteAllDeployment();
        MainApplication.mDb.clearData();
        // clear the stuff that has been initialized in the
        // sharedpreferences.
        Preferences.activeDeployment = 0;
        Preferences.domain = "";
        Preferences.deploymentLatitude = "0.0";
        Preferences.deploymentLongitude = "0.0";
        Preferences.saveSettings(context);
        return true;
    }

    /**
     * Update an existing map
     * 
     * @param id The map's ID
     * @param name The map's name
     * @param url The map's URL
     * @param desc The map's description
     */
    public void updateMap(String id, String name, String desc, String url) {
        Database.map.updateMap(id, name, url, desc);
    }

    public void setActivness(String id) {
        Database.map.setActivenessDeployment(id);
    }

    /**
     * Loads an existing map by it's ID
     * 
     * @param id
     */
    public List<ListMapModel> loadMapById(String id) {
        cursor = Database.map.fetchDeploymentById(id);
        mMaps = new ArrayList<ListMapModel>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int deploymentIdIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int deploymentNameIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_NAME);
                int deploymentDescIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_DESC);
                int deploymentUrlIndex = cursor.getColumnIndexOrThrow(MapDb.DEPLOYMENT_URL);

                ListMapModel listMapModel = new ListMapModel();
                listMapModel.setId(cursor.getString(deploymentIdIndex));
                listMapModel.setName(cursor.getString(deploymentNameIndex));
                listMapModel.setDesc(cursor.getString(deploymentDescIndex));
                listMapModel.setUrl(cursor.getString(deploymentUrlIndex));

                mMaps.add(listMapModel);

                return mMaps;
            }
        }

        return null;
    }
}
