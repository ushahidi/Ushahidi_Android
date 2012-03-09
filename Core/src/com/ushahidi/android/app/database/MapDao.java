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
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ushahidi.android.app.entities.Map;

public class MapDao extends DbContentProvider implements IMapDao, IMapSchema {

    private ContentValues initialValues;

    private Cursor cursor;

    private List<Map> listMap;

    public MapDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Map> fetchMapById(long id) {
        
        String selection = MAP_ID+" = ?";
        String[] selectionArgs = new String[] {
            String.valueOf(id)
        };

        String[] columns = new String[] {
                MAP_ID, MAP_NAME, MAP_DESC, MAP_LATITUDE, MAP_LONGITUDE, MAP_URL
        };
        
        final String sortOrder = MAP_DATE + " DESC";

        listMap = new ArrayList<Map>();
        cursor = super.query(MAP_TABLE, columns, selection, selectionArgs, sortOrder);
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
    public void setActiveDeployment(long id) {

        String sql = "UPDATE " + MAP_TABLE + " SET " + MAP_ACTIVE + "= ? WHERE " + MAP_ID + "= ?";

        mDb.rawQuery(sql, new String[] {
                "1", String.valueOf(id)
        });
    }

    @Override
    public boolean deleteMapById(long id) {
        final String selectionArgs[] = {
            String.valueOf(id)
        };

        final String selection = MAP_ID+" = ? ";

        return super.delete(MAP_TABLE, selection, selectionArgs) > 0 ;
        
    }

    @Override
    public boolean deleteAllMap() {
        return super.delete(MAP_TABLE, null, null) > 0;
    }

    @Override
    public boolean updateMap(Map map) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MAP_DESC, map.getDesc());
        initialValues.put(MAP_NAME, map.getName());
        initialValues.put(MAP_URL, map.getUrl());
        String whereClause = MAP_ID+" = ?";
        String whereArgs[] = {
            String.valueOf(map.getDbId())
        };

        return super.update(MAP_TABLE, initialValues, whereClause, whereArgs) > 0;
    }

    @Override
    public boolean addMap(Map map) {
        setContentValue(map);
        return super.insert(MAP_TABLE, getContentValue()) > 0;
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
        final String sortOrder = MAP_DATE + " DESC";

        listMap = new ArrayList<Map>();
        cursor = super.query(MAP_TABLE, null, null, null, sortOrder);
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
    public List<Map> fetchMapByIdAndUrl(long id, String url) {
        String selection = MAP_ID+" = ? AND "+MAP_URL +"= ?";
        String[] selectionArgs = new String[] {
            String.valueOf(id), url
        };

        String[] columns = new String[] {
                MAP_ID, MAP_NAME, MAP_DESC, MAP_LATITUDE, MAP_LONGITUDE, MAP_URL
        };
        
        final String sortOrder = MAP_DATE + " DESC";

        listMap = new ArrayList<Map>();
        cursor = super.query(MAP_TABLE, columns, selection, selectionArgs, sortOrder);
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
        ContentValues initialValues = new ContentValues();

        initialValues.put(MAP_ID, map.getDbId());
        initialValues.put(MAP_CAT_ID, map.getCatId());
        initialValues.put(MAP_DESC, map.getDesc());
        initialValues.put(MAP_DATE, map.getDate());
        initialValues.put(MAP_NAME, map.getName());
        initialValues.put(MAP_ACTIVE, map.getActive());
        initialValues.put(MAP_URL, map.getUrl());
        initialValues.put(MAP_LATITUDE, map.getLat());
        initialValues.put(MAP_LONGITUDE, map.getLon());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map cursorToEntity(Cursor cursor) {
        Map map = new Map();
        int idIndex;
        int nameIndex;
        int urlIndex;
        int descIndex;
        int catIdIndex;
        int latitudeIndex;
        int longitudeIndex;
        int dateIndex;
        int activeIndex;

        if (cursor != null) {

            if (cursor.getColumnIndex(MAP_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(MAP_ID);
                
                map.setDbId(Long.valueOf(cursor.getString(idIndex)));
            }

            if (cursor.getColumnIndex(MAP_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(MAP_NAME);
                map.setName(cursor.getString(nameIndex));
            }

            if (cursor.getColumnIndex(MAP_URL) != -1) {
                urlIndex = cursor.getColumnIndexOrThrow(MAP_URL);
                map.setUrl(cursor.getString(urlIndex));
            }

            if (cursor.getColumnIndex(MAP_DESC) != -1) {
                descIndex = cursor.getColumnIndexOrThrow(MAP_DESC);
                map.setDesc(cursor.getString(descIndex));
            }

            if (cursor.getColumnIndex(MAP_CAT_ID) != -1) {
                catIdIndex = cursor.getColumnIndexOrThrow(MAP_CAT_ID);
                map.setCatId(cursor.getString(catIdIndex));
            }

            if (cursor.getColumnIndex(MAP_LATITUDE) != -1) {
                latitudeIndex = cursor.getColumnIndexOrThrow(MAP_LATITUDE);
                map.setLat(cursor.getString(latitudeIndex));
            }

            if (cursor.getColumnIndex(MAP_LONGITUDE) != -1) {
                longitudeIndex = cursor.getColumnIndexOrThrow(MAP_LONGITUDE);
                map.setLon(cursor.getString(longitudeIndex));
            }

            if (cursor.getColumnIndex(MAP_DATE) != -1) {
                dateIndex = cursor.getColumnIndexOrThrow(MAP_DATE);
                map.setDate(cursor.getString(dateIndex));
            }

            if (cursor.getColumnIndex(MAP_ACTIVE) != -1) {
                activeIndex = cursor.getColumnIndexOrThrow(MAP_ACTIVE);
                map.setActive(cursor.getString(activeIndex));
            }
        }

        return map;
    }

    /**
     * Performs a database query.
     * 
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the
     *            selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        HashMap<String, String> mColumnMap = new HashMap<String, String>();
        mColumnMap.put(MAP_ID, MAP_ID);
        mColumnMap.put(MAP_CAT_ID, MAP_CAT_ID);
        mColumnMap.put(MAP_DESC, MAP_DESC);
        mColumnMap.put(MAP_DATE, MAP_DATE);
        mColumnMap.put(MAP_NAME, MAP_NAME);
        mColumnMap.put(MAP_URL, MAP_URL);
        mColumnMap.put(MAP_LATITUDE, MAP_LATITUDE);
        mColumnMap.put(MAP_LONGITUDE, MAP_LONGITUDE);
        mColumnMap.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        mColumnMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS "
                + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        mColumnMap.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS "
                + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        /*
         * The SQLiteBuilder provides a map for all possible columns requested
         * to actual columns in the database, creating a simple column alias
         * mechanism by which the ContentProvider does not need to know the real
         * column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MAP_TABLE);
        builder.setProjectionMap(mColumnMap);
        String orderBy = MAP_DATE + " DESC";
        Cursor cursor = builder.query(mDb, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
