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

package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.Map;

public class MapDao extends DbContentProvider implements IMapDao, IMapSchema {

	private ContentValues initialValues;

	private Cursor cursor;

	private List<Map> listMap;

	public MapDao(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public List<Map> fetchMapById(int id) {

		// For some reason, selectionArgs doesn't map the values to ID
		// during query execution
		String selection = "rowid =" + id;

		String[] columns = new String[] { "rowid", MAP_ID, NAME, DESC,
				LATITUDE, LONGITUDE, URL };

		final String sortOrder = DATE + " DESC";

		listMap = new ArrayList<Map>();

		cursor = super.query(TABLE, columns, selection, null, sortOrder);

		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Map map = cursorToEntity(cursor);
				listMap.add(map);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listMap;
	}

	@Override
	public void setActiveDeployment(int id) {

		String sql = "UPDATE " + TABLE + " SET " + ACTIVE + "= ? WHERE "
				+ "rowid= ?";

		super.rawQuery(sql, new String[] { "1", String.valueOf(id) });
	}

	@Override
	public boolean deleteMapById(int id) {

		final String selection = " rowid =" + id;

		return super.delete(TABLE, selection, null) > 0;

	}

	@Override
	public boolean deleteAllMap() {
		return super.delete(TABLE, null, null) > 0;
	}

	/**
	 * Delete all deployments that were fetched from the internet
	 */
	@Override
	public boolean deleteAllAutoMap() {
		String whereClause = ID + " <> 0";

		return super.delete(TABLE, whereClause, null) > 0;
	}

	@Override
	public boolean updateMap(Map map) {
		initialValues = new ContentValues();
		initialValues.put(DESC, map.getDesc());
		initialValues.put(NAME, map.getName());
		initialValues.put(URL, map.getUrl());
		String whereClause = "rowid = " + map.getDbId();

		return super.update(TABLE, initialValues, whereClause, null) > 0;
	}

	@Override
	public boolean addMap(Map map) {
		setContentValue(map);
		return super.insert(TABLE, getContentValue()) > 0;
	}

	@Override
	public boolean addMaps(List<Map> maps) {
		try {
			mDb.beginTransaction();
			for (Map map : maps) {
				addMap(map);
			}
			mDb.setTransactionSuccessful();
			return true;
		} finally {
			mDb.endTransaction();
		}

	}

	@Override
	public List<Map> fetchAllMaps() {
		final String sortOrder = DATE + " DESC";
		String[] columns = new String[] { "rowid", MAP_ID, NAME, URL, DESC,
				CAT_ID, ACTIVE, LATITUDE, LONGITUDE, DATE };
		listMap = new ArrayList<Map>();
		cursor = super.query(TABLE, columns, null, null, sortOrder);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Map map = cursorToEntity(cursor);
				listMap.add(map);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listMap;
	}

	@Override
	public List<Map> fetchMapByIdAndUrl(int id, int mapId) {

		String selection = " rowid ="
				+ DatabaseUtils.sqlEscapeString(String.valueOf(id)) + " AND "
				+ MAP_ID + "=" + mapId;

		String[] columns = new String[] { "rowid", NAME, DESC, LATITUDE,
				LONGITUDE, URL };

		final String sortOrder = DATE + " DESC";

		listMap = new ArrayList<Map>();
		cursor = super.query(TABLE, columns, selection, null, sortOrder);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Map map = cursorToEntity(cursor);
				listMap.add(map);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listMap;
	}

	@Override
	public List<Map> fetchMap(Cursor cursor) {

		listMap = new ArrayList<Map>();
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Map map = cursorToEntity(cursor);
				listMap.add(map);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listMap;
	}

	private void setContentValue(Map map) {
		initialValues = new ContentValues();
		initialValues.put(MAP_ID, map.getMapId());
		initialValues.put(CAT_ID, map.getCatId());
		initialValues.put(DESC, map.getDesc());
		initialValues.put(DATE, map.getDate());
		initialValues.put(NAME, map.getName());
		initialValues.put(ACTIVE, map.getActive());
		initialValues.put(URL, map.getUrl());
		initialValues.put(LATITUDE, map.getLat());
		initialValues.put(LONGITUDE, map.getLon());
	}

	private ContentValues getContentValue() {
		return initialValues;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map cursorToEntity(Cursor cursor) {
		Map map = new Map();
		int idIndex;
		int mapIdIndex;
		int nameIndex;
		int urlIndex;
		int descIndex;
		int catIdIndex;
		int latitudeIndex;
		int longitudeIndex;
		int dateIndex;
		int activeIndex;

		if (cursor != null) {

			if (cursor.getColumnIndex("rowid") != -1) {
				idIndex = cursor.getColumnIndexOrThrow("rowid");

				map.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(MAP_ID) != -1) {
				mapIdIndex = cursor.getColumnIndexOrThrow(MAP_ID);

				map.setMapId(cursor.getInt(mapIdIndex));
			}

			if (cursor.getColumnIndex(NAME) != -1) {
				nameIndex = cursor.getColumnIndexOrThrow(NAME);
				map.setName(cursor.getString(nameIndex));
			}

			if (cursor.getColumnIndex(URL) != -1) {
				urlIndex = cursor.getColumnIndexOrThrow(URL);
				map.setUrl(cursor.getString(urlIndex));
			}

			if (cursor.getColumnIndex(DESC) != -1) {
				descIndex = cursor.getColumnIndexOrThrow(DESC);
				map.setDesc(cursor.getString(descIndex));
			}

			if (cursor.getColumnIndex(CAT_ID) != -1) {
				catIdIndex = cursor.getColumnIndexOrThrow(CAT_ID);
				map.setCatId(cursor.getInt(catIdIndex));
			}

			if (cursor.getColumnIndex(LATITUDE) != -1) {
				latitudeIndex = cursor.getColumnIndexOrThrow(LATITUDE);
				map.setLat(cursor.getString(latitudeIndex));
			}

			if (cursor.getColumnIndex(LONGITUDE) != -1) {
				longitudeIndex = cursor.getColumnIndexOrThrow(LONGITUDE);
				map.setLon(cursor.getString(longitudeIndex));
			}

			if (cursor.getColumnIndex(DATE) != -1) {
				dateIndex = cursor.getColumnIndexOrThrow(DATE);
				map.setDate(cursor.getString(dateIndex));
			}

			if (cursor.getColumnIndex(ACTIVE) != -1) {
				activeIndex = cursor.getColumnIndexOrThrow(ACTIVE);
				map.setActive(cursor.getString(activeIndex));
			}
		}

		return map;
	}
}
