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
package com.ushahidi.android.app.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Map;
import com.ushahidi.java.sdk.net.UshahidiHttpClient;

/**
 * Handles the map aka deployments search API
 */
public class MapSearchApi {

	private static final String MAP_SEARCH_URL = "http://tracker.ushahidi.com/list/";

	private UshahidiHttpClient client;

	private boolean processingResult;

	private List<Map> mMap;

	public MapSearchApi(String distance, Location location) {

		client = new UshahidiHttpClient();
		client.setRequestParameters("return_vars",
				"name,latitude,longitude,description,url,category_id,discovery_date,id");
		client.setRequestParameters("units", "km");
		fetchMaps(distance, location);
	}

	/**
	 * Fetch for maps based on location and proximity - distance
	 * 
	 * @param distance
	 *            The distance to use to search for the maps
	 * 
	 * @param location
	 *            The current location of the user.
	 * 
	 * @return boolean
	 */
	public boolean fetchMaps(String distance, Location location) {

		// current location
		if (location != null) {
			processingResult = true;
			client.setRequestParameters("distance", distance);
			client.setRequestParameters("lat",
					String.valueOf(location.getLatitude()));
			client.setRequestParameters("lon",
					String.valueOf(location.getLongitude()));
			final String mapsJson = client.sendGetRequest(MAP_SEARCH_URL);

			mMap = retrieveMapJson(mapsJson);

			if (mMap != null) {
				Database.mMapDao.deleteAllAutoMap();
				Database.mMapDao.addMaps(mMap);
				return true;
			}
			
		}

		return false;
	}

	/**
	 * Deserialize the JSON string returned from a successful search of a map.
	 * 
	 * @param jsonString
	 * @return
	 */
	private List<Map> retrieveMapJson(String jsonString) {
		// TODO: figure out how to use GSON instead.
		if (jsonString != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONArray names = jsonObject.names();
				List<Map> mapsList = new ArrayList<Map>();
				if (processingResult) {
					for (int i = 0; i < names.length(); i++) {
						Map mapModel = new Map();

						mapModel.setMapId(jsonObject.getJSONObject(
								names.getString(i)).getInt("id"));
						mapModel.setDate(jsonObject.getJSONObject(
								names.getString(i)).getString("discovery_date"));
						mapModel.setActive("0");
						mapModel.setLat(jsonObject.getJSONObject(
								names.getString(i)).getString("latitude"));
						mapModel.setLon(jsonObject.getJSONObject(
								names.getString(i)).getString("longitude"));
						mapModel.setName(jsonObject.getJSONObject(
								names.getString(i)).getString("name"));
						mapModel.setUrl(jsonObject.getJSONObject(
								names.getString(i)).getString("url"));

						// use deployment name if there is no deployment
						// description returned from the search
						if (jsonObject.getJSONObject(names.getString(i))
								.getString("description").equals("")) {
							mapModel.setDesc(jsonObject.getJSONObject(
									names.getString(i)).getString("name"));
						} else {
							mapModel.setDesc(jsonObject.getJSONObject(
									names.getString(i))
									.getString("description"));
						}
						mapModel.setCatId(jsonObject.getJSONObject(
								names.getString(i)).getInt("category_id"));

						mapsList.add(mapModel);
					}
					return mapsList;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				processingResult = false;
				return null;
			}
		}
		return null;
	}
}
