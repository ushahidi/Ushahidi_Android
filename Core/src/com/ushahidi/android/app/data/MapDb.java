
package com.ushahidi.android.app.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.ushahidi.android.app.models.ListMapModel;

/**
 * This calls handles all database stuff related to Maps aka deployments
 * 
 * @author eyedol
 */
public class MapDb {

    // Deployments
    public static final String DEPLOYMENT_ID = "_id";

    public static final String DEPLOYMENT_NAME = "name";

    public static final String DEPLOYMENT_URL = "url";

    public static final String DEPLOYMENT_DESC = "desc";

    public static final String DEPLOYMENT_CAT_ID = "cat_id";

    public static final String DEPLOYMENT_LATITUDE = "latitude";

    public static final String DEPLOYMENT_LONGITUDE = "longitude";

    public static final String DEPLOYMENT_DATE = "discovery_date";

    public static final String DEPLOYMENT_ACTIVE = "deployment_active"; // 1 4
                                                                        // active,
                                                                        // 0 4
                                                                        // inactive

    // Deployments
    public static final String[] DEPLOYMENT_COLUMNS = new String[] {
            DEPLOYMENT_ID, DEPLOYMENT_NAME, DEPLOYMENT_URL, DEPLOYMENT_DESC, DEPLOYMENT_CAT_ID,
            DEPLOYMENT_ACTIVE, DEPLOYMENT_LATITUDE, DEPLOYMENT_LONGITUDE, DEPLOYMENT_DATE
    };

    public static final String DEPLOYMENT_TABLE = "deployment";

    public static final String DEPLOYMENT_TABLE_CREATE = "CREATE VIRTUAL TABLE "
            + DEPLOYMENT_TABLE + " USING fts3 (" + DEPLOYMENT_ID
            + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + DEPLOYMENT_CAT_ID + " INTEGER, "
            + DEPLOYMENT_ACTIVE + " INTEGER, " + DEPLOYMENT_NAME + " TEXT NOT NULL, "
            + DEPLOYMENT_DATE + " DATE NOT NULL, " + DEPLOYMENT_DESC + " TEXT NOT NULL, "
            + DEPLOYMENT_URL + " TEXT NOT NULL, " + DEPLOYMENT_LATITUDE + " TEXT NOT NULL, "
            + DEPLOYMENT_LONGITUDE + " TEXT NOT NULL" + ")";

    private SQLiteDatabase mDb;

    private String TAG = MapDb.class.getSimpleName();

    public MapDb(SQLiteDatabase mDb) {
        this.mDb = mDb;
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL(DEPLOYMENT_TABLE_CREATE);
    }

    public void upgradeTable(SQLiteDatabase db) {
        // upgrade deployment table
        db.execSQL("DROP TABLE IF EXISTS " + DEPLOYMENT_TABLE);
    }

    public long createMap(ListMapModel map) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(DEPLOYMENT_ID, map.getId());
        initialValues.put(DEPLOYMENT_CAT_ID, map.getCatId());
        initialValues.put(DEPLOYMENT_DESC, map.getDesc());
        initialValues.put(DEPLOYMENT_DATE, map.getDate());
        initialValues.put(DEPLOYMENT_NAME, map.getName());
        initialValues.put(DEPLOYMENT_ACTIVE, map.getActive());
        initialValues.put(DEPLOYMENT_URL, map.getUrl());
        initialValues.put(DEPLOYMENT_LATITUDE, map.getLat());
        initialValues.put(DEPLOYMENT_LONGITUDE, map.getLon());
        return mDb.insert(DEPLOYMENT_TABLE, null, initialValues);
    }

    public long createAddMap(String name, String description, String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DEPLOYMENT_ID, "0");
        initialValues.put(DEPLOYMENT_CAT_ID, 0);
        initialValues.put(DEPLOYMENT_NAME, name);
        initialValues.put(DEPLOYMENT_DESC, description);
        initialValues.put(DEPLOYMENT_URL, url);
        initialValues.put(DEPLOYMENT_DATE,
                (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        initialValues.put(DEPLOYMENT_LATITUDE, "0.0");
        initialValues.put(DEPLOYMENT_LONGITUDE, "0.0");
        return mDb.insert(DEPLOYMENT_TABLE, null, initialValues);

    }

    public Cursor getDeploymentMatches(String query, String[] columns) {
        String selection = DEPLOYMENT_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {
            query + "*"
        };

        return query(selection, selectionArgs, columns);

        /*
         * This builds a query that looks like: SELECT <columns> FROM <table>
         * WHERE deployment_name = <deployment_name>
         */
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
        mColumnMap.put(DEPLOYMENT_ID, DEPLOYMENT_ID);
        mColumnMap.put(DEPLOYMENT_CAT_ID, DEPLOYMENT_CAT_ID);
        mColumnMap.put(DEPLOYMENT_DESC, DEPLOYMENT_DESC);
        mColumnMap.put(DEPLOYMENT_DATE, DEPLOYMENT_DATE);
        mColumnMap.put(DEPLOYMENT_NAME, DEPLOYMENT_NAME);
        mColumnMap.put(DEPLOYMENT_URL, DEPLOYMENT_URL);
        mColumnMap.put(DEPLOYMENT_LATITUDE, DEPLOYMENT_LATITUDE);
        mColumnMap.put(DEPLOYMENT_LONGITUDE, DEPLOYMENT_LONGITUDE);
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
        builder.setTables(DEPLOYMENT_TABLE);
        builder.setProjectionMap(mColumnMap);
        String orderBy = DEPLOYMENT_DATE + " DESC";
        Cursor cursor = builder.query(mDb, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor fetchDeploymentById(String id) {

        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {
            id
        };
        String[] columns = new String[] {
                BaseColumns._ID, DEPLOYMENT_NAME, DEPLOYMENT_DESC, DEPLOYMENT_LATITUDE,
                DEPLOYMENT_LONGITUDE, DEPLOYMENT_URL
        };

        return query(selection, selectionArgs, columns);
    }

    public Cursor fetchDeploymentByIdAndUrl(String id, String url) {
        String sql = "SELECT * FROM " + DEPLOYMENT_TABLE + " WHERE " + DEPLOYMENT_ID + " = ? AND "
                + DEPLOYMENT_URL + " =? ORDER BY " + DEPLOYMENT_NAME + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
                id, url
        });
    }

    public Cursor fetchDeploymentUrlById(String id) {
        String sql = "SELECT " + DEPLOYMENT_URL + " FROM " + DEPLOYMENT_TABLE + " WHERE "
                + DEPLOYMENT_ID + " = ? ";
        return mDb.rawQuery(sql, new String[] {
            id
        });
    }

    public boolean deleteAllDeployment() {
        Log.i(TAG, "Deleting all Deployment");
        return mDb.delete(DEPLOYMENT_TABLE, null, null) > 0;
    }

    /**
     * Delete all deployments that were fetched from the internet
     */
    public boolean deleteAllAutoDeployment() {
        String whereClause = DEPLOYMENT_ID + " <> ?";
        String whereArgs[] = {
            "0"
        };
        return mDb.delete(DEPLOYMENT_TABLE, whereClause, whereArgs) > 0;
    }

    public boolean deleteDeploymentByIdAndUrl(String id, String url) {
        String whereClause = "WHERE " + DEPLOYMENT_ID + " =? AND " + DEPLOYMENT_URL + " =? ";
        String whereArgs[] = {
                id, url
        };
        return mDb.delete(DEPLOYMENT_TABLE, whereClause, whereArgs) > 0;
    }

    public boolean deleteDeploymentById(String id) {
        String whereClause = "rowid = ? ";
        String whereArgs[] = {
            id
        };
        return mDb.delete(DEPLOYMENT_TABLE, whereClause, whereArgs) > 0;
    }

    /**
     * Add new deployments to table
     * 
     * @param deployments
     */
    public void addMap(List<ListMapModel> maps) {
        try {
            mDb.beginTransaction();

            for (ListMapModel map : maps) {
                createMap(map);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * Add new deployments to table
     */
    public void addMap(String name, String description, String url) {
        try {
            mDb.beginTransaction();
            createAddMap(name, description, url);

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    public void setActivenessDeployment(String id) {
        String sql = "UPDATE " + DEPLOYMENT_TABLE + " SET " + DEPLOYMENT_ACTIVE + "= ? WHERE "
                + DEPLOYMENT_ID + "= ?";

        mDb.rawQuery(sql, new String[] {
                "1", id
        });
    }

    public boolean updateMap(String id, String name, String desc, String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DEPLOYMENT_DESC, desc);
        initialValues.put(DEPLOYMENT_NAME, name);
        initialValues.put(DEPLOYMENT_URL, url);

        String whereClause = "rowid= ?";
        String whereArgs[] = {
            id
        };
        
        return mDb.update(DEPLOYMENT_TABLE, initialValues, whereClause, whereArgs) > 0;

    }

    public Cursor fetchAllDeployments() {

        return query(null, null, null);
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     * 
     * @param rowId id of deployment to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching deployment, or null if not found.
     */
    public Cursor getDeployment(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {
            rowId
        };
        
        /*
         * This builds a query that looks like: SELECT <columns> FROM <table>
         * WHERE id = <rowId>
         */
        return query(selection, selectionArgs, columns);
    }

}
