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

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Map;

/**
 * @author eyedol
 */
public class ListMapModel extends Model {

	public List<Map> mMaps;

	public List<ListMapModel> mMapModel;

	private int id;

	private String name;

	private String desc;

	private String url;

	private String date;

	private String lat;

	private String lon;

	private int catId;

	private String active;

	private int mapId;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
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

	public void setCatId(int catId) {

		this.catId = catId;
	}

	public int getCatId() {
		return this.catId;
	}

	public void setMapId(int mapId) {

		this.mapId = mapId;
	}

	public int getMapId() {
		return this.mapId;
	}

	public ListMapModel() {
		mMaps = new ArrayList<Map>();
	}

	@Override
	public boolean load() {
		mMaps = Database.mMapDao.fetchAllMaps();
		if (mMaps != null) {
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
	public void activateDeployment(Context context, int id) {

		List<Map> listMap = Database.mMapDao.fetchMapById(id);

		if (listMap != null & listMap.size() > 0) {
			Preferences.loadSettings(context);
			Preferences.activeDeployment = id;
			Preferences.activeMapName = listMap.get(0).getName();
			Preferences.domain = listMap.get(0).getUrl();
			Preferences.deploymentLatitude = listMap.get(0).getLat();
			Preferences.deploymentLongitude = listMap.get(0).getLon();
			Preferences.saveSettings(context);
		}

	}

	// Save stuff fetch from
	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addMap(List<Map> maps) {
		return Database.mMapDao.addMaps(maps);
	}

	/**
	 * Delete a map by it's id
	 * 
	 * @param id
	 *            - The ID of the map to be deleted
	 * @return boolean
	 */
	public boolean deleteMapById(int id) {
		return Database.mMapDao.deleteMapById(id);
	}

	public boolean deleteAllMap(Context context) {
		Database.mMapDao.deleteAllMap();
		MainApplication.mDb.clearData();
		// clear the stuff that has been initialized in the
		// sharedpreferences.
		Preferences.loadSettings(context);
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
	 * @param id
	 *            The map's ID
	 * @param name
	 *            The map's name
	 * @param url
	 *            The map's URL
	 * @param desc
	 *            The map's description
	 */
	public boolean updateMap(int id, String name, String desc, String url) {
		Map map = new Map();
		map.setDbId(id);
		map.setName(name);
		map.setDesc(desc);
		map.setUrl(url);
		return Database.mMapDao.updateMap(map);
	}

	public void setActivness(int id) {
		Database.mMapDao.setActiveDeployment(id);
	}

	/**
	 * Loads an existing map by it's ID
	 * 
	 * @param id
	 */
	public List<ListMapModel> loadMapById(int id, int mapId) {
		mMapModel = new ArrayList<ListMapModel>();
		mMaps = Database.mMapDao.fetchMapByIdAndUrl(id, mapId);

		if (mMaps != null && mMaps.size() > 0) {
			for (Map map : mMaps) {
				ListMapModel mapModel = new ListMapModel();
				mapModel.setId(map.getDbId());
				mapModel.setMapId(map.getMapId());
				mapModel.setActive(map.getActive());
				mapModel.setCatId(map.getCatId());
				mapModel.setDate(map.getDate());
				mapModel.setDesc(map.getDesc());
				mapModel.setLat(map.getLat());
				mapModel.setLon(map.getLon());
				mapModel.setName(map.getName());
				mapModel.setUrl(map.getUrl());
				mMapModel.add(mapModel);
			}
		}
		return mMapModel;
	}

	public List<ListMapModel> getMaps() {

		mMapModel = new ArrayList<ListMapModel>();

		if (mMaps != null && mMaps.size() > 0) {
			for (Map map : mMaps) {
				ListMapModel mapModel = new ListMapModel();

				mapModel.setId(map.getDbId());
				mapModel.setMapId(map.getMapId());
				mapModel.setActive(map.getActive());
				mapModel.setCatId(map.getCatId());
				mapModel.setDate(map.getDate());
				mapModel.setDesc(map.getDesc());
				mapModel.setLat(map.getLat());
				mapModel.setLon(map.getLon());
				mapModel.setName(map.getName());
				mapModel.setUrl(map.getUrl());
				mMapModel.add(mapModel);
			}
		}
		return mMapModel;
	}

}
