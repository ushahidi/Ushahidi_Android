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
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.checkin.Checkin;
import com.ushahidi.android.app.data.AddIncidentData;
import com.ushahidi.android.app.data.MapDb;
import com.ushahidi.android.app.util.Util;

public class Database {

    /**
     * Group of constants that specify the different columns in the offline
     * incident table and there corresponding column indexes
     */
    public static final int ADD_INCIDENT_ID_INDEX = 0;

    public static final int ADD_INCIDENT_TITLE_INDEX = 1;

    public static final int ADD_INCIDENT_DESC_INDEX = 2;

    public static final int ADD_INCIDENT_DATE_INDEX = 3;

    public static final int ADD_INCIDENT_HOUR_INDEX = 4;

    public static final int ADD_INCIDENT_MINUTE_INDEX = 5;

    public static final int ADD_INCIDENT_AMPM_INDEX = 6;

    public static final int ADD_INCIDENT_CATEGORIES_INDEX = 7;

    public static final int INCIDENT_LOC_NAME_INDEX = 8;

    public static final int INCIDENT_LOC_LATITUDE_INDEX = 9;

    public static final int INCIDENT_LOC_LONGITUDE_INDEX = 10;

    public static final int ADD_INCIDENT_PHOTO_INDEX = 11;

    public static final int ADD_INCIDENT_VIDEO_INDEX = 12;

    public static final int ADD_INCIDENT_NEWS_INDEX = 13;

    public static final int ADD_PERSON_FIRST_INDEX = 14;

    public static final int ADD_PERSON_LAST_INDEX = 15;

    public static final int ADD_PERSON_EMAIL_INDEX = 16;

    /**
     */

    private static final String TAG = "UshahidiDatabase";

    public static final String ADD_INCIDENT_ID = "_id";

    public static final String ADD_INCIDENT_TITLE = "incident_title";

    public static final String ADD_INCIDENT_DESC = "incident_desc";

    public static final String ADD_INCIDENT_DATE = "incident_date";

    public static final String ADD_INCIDENT_HOUR = "incident_hour";

    public static final String ADD_INCIDENT_MINUTE = "incident_minute";

    public static final String ADD_INCIDENT_AMPM = "incident_ampm";

    public static final String ADD_INCIDENT_CATEGORIES = "incident_categories";

    public static final String ADD_INCIDENT_LOC_NAME = "incident_loc_name";

    public static final String ADD_INCIDENT_LOC_LATITUDE = "incident_loc_latitude";

    public static final String ADD_INCIDENT_LOC_LONGITUDE = "incident_loc_longitude";

    public static final String ADD_INCIDENT_PHOTO = "incident_photo";

    public static final String ADD_INCIDENT_VIDEO = "incident_video";

    public static final String ADD_INCIDENT_NEWS = "incident_news";

    public static final String ADD_PERSON_FIRST = "person_first";

    public static final String ADD_PERSON_LAST = "person_last";

    public static final String ADD_PERSON_EMAIL = "person_email";

    /**
     * Columns of the table that stores off line incidents
     */
    public static final String[] ADD_INCIDENTS_COLUMNS = new String[] {
            ADD_INCIDENT_ID, ADD_INCIDENT_TITLE, ADD_INCIDENT_DESC, ADD_INCIDENT_DATE,
            ADD_INCIDENT_HOUR, ADD_INCIDENT_MINUTE, ADD_INCIDENT_AMPM, ADD_INCIDENT_CATEGORIES,
            IReportSchema.INCIDENT_LOC_NAME, IReportSchema.INCIDENT_LOC_LATITUDE,
            IReportSchema.INCIDENT_LOC_LONGITUDE, ADD_INCIDENT_PHOTO, ADD_INCIDENT_VIDEO,
            ADD_INCIDENT_NEWS, ADD_PERSON_FIRST, ADD_PERSON_LAST, ADD_PERSON_EMAIL
    };

    private DatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "ushahidi_db";

    private static final String ADD_INCIDENTS_TABLE = "add_incidents";

    private static final int DATABASE_VERSION = 15;

    // NOTE: the incident ID is used as the row ID.
    // Furthermore, if a row already exists, an insert will replace
    // the old row upon conflict.

    private static final String ADD_INCIDENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ADD_INCIDENTS_TABLE + " (" + ADD_INCIDENT_ID + " INTEGER PRIMARY KEY , "
            + ADD_INCIDENT_TITLE + " TEXT NOT NULL, " + ADD_INCIDENT_DESC + " TEXT, "
            + IReportSchema.INCIDENT_DATE + " DATE NOT NULL, " + ADD_INCIDENT_HOUR + " INTEGER, "
            + ADD_INCIDENT_MINUTE + " INTEGER, " + ADD_INCIDENT_AMPM + " TEXT NOT NULL, "
            + ADD_INCIDENT_CATEGORIES + " TEXT NOT NULL, " + ADD_INCIDENT_LOC_NAME
            + " TEXT NOT NULL, " + ADD_INCIDENT_LOC_LATITUDE + " TEXT NOT NULL, "
            + ADD_INCIDENT_LOC_LONGITUDE + " TEXT NOT NULL, " + ADD_INCIDENT_PHOTO + " TEXT, "
            + ADD_INCIDENT_VIDEO + " TEXT, " + ADD_INCIDENT_NEWS + " TEXT, " + ADD_PERSON_FIRST
            + " TEXT, " + ADD_PERSON_LAST + " TEXT, " + ADD_PERSON_EMAIL + " TEXT " + ")";

    private final Context mContext;

    public static MapDb map; // Map aka deployments table

    public static ReportDao mReportDao; // Report table

    public static CategoryDao mCategoryDao; // Category table

    public static MapDao mMapDao; // Map aka deployment table

    public static ReportCategoryDao mReportCategoryDao; // ReportCategory table

    public static MediaDao mMediaDao; // Media table

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
            db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
            // create map aka deployment table
            db.execSQL(IMapSchema.MAP_TABLE_CREATE);
            db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
            db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
            db.execSQL(IUserSchema.USER_TABLE_CREATE);
            db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
            db.execSQL(ADD_INCIDENTS_TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + " which destroys all old data");
            List<String> incidentsColumns;
            List<String> categoriesColumns;
            List<String> addIncidentColumns;
            List<String> checkinsColums;
            List<String> checkinsMediaColums;
            List<String> usersColumns;
            // List<String> deploymentColumns;

            // upgrade incident table
            db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
            incidentsColumns = Database.getColumns(db, IReportSchema.INCIDENTS_TABLE);
            db.execSQL("ALTER TABLE " + IReportSchema.INCIDENTS_TABLE + " RENAME TO temp_"
                    + IReportSchema.INCIDENTS_TABLE);
            db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
            incidentsColumns.retainAll(Database.getColumns(db, IReportSchema.INCIDENTS_TABLE));
            String cols = Database.join(incidentsColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    IReportSchema.INCIDENTS_TABLE, cols, cols, IReportSchema.INCIDENTS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + IReportSchema.INCIDENTS_TABLE);

            // upgrade category table
            db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
            categoriesColumns = Database.getColumns(db, ICategorySchema.CATEGORIES_TABLE);
            db.execSQL("ALTER TABLE " + ICategorySchema.CATEGORIES_TABLE + " RENAME TO temp_"
                    + ICategorySchema.CATEGORIES_TABLE);
            db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
            categoriesColumns.retainAll(Database.getColumns(db, ICategorySchema.CATEGORIES_TABLE));
            String catsCols = Database.join(categoriesColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    ICategorySchema.CATEGORIES_TABLE, catsCols, catsCols,
                    ICategorySchema.CATEGORIES_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + ICategorySchema.CATEGORIES_TABLE);

            // upgrade add incident table
            db.execSQL(ADD_INCIDENTS_TABLE_CREATE);
            addIncidentColumns = Database.getColumns(db, ADD_INCIDENTS_TABLE);
            db.execSQL("ALTER TABLE " + ADD_INCIDENTS_TABLE + " RENAME TO temp_"
                    + ADD_INCIDENTS_TABLE);
            db.execSQL(ADD_INCIDENTS_TABLE_CREATE);
            addIncidentColumns.retainAll(Database.getColumns(db, ADD_INCIDENTS_TABLE));
            String addIncidentCols = Database.join(addIncidentColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    ADD_INCIDENTS_TABLE, addIncidentCols, addIncidentCols, ADD_INCIDENTS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + ADD_INCIDENTS_TABLE);

            // upgrade checkin table
            db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
            checkinsColums = Database.getColumns(db, ICheckinSchema.CHECKINS_TABLE);
            db.execSQL("ALTER TABLE " + ICheckinSchema.CHECKINS_TABLE + " RENAME TO temp_"
                    + ICheckinSchema.CHECKINS_TABLE);
            db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
            checkinsColums.retainAll(Database.getColumns(db, ICheckinSchema.CHECKINS_TABLE));
            String checkinsCols = Database.join(checkinsColums, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    ICheckinSchema.CHECKINS_TABLE, checkinsCols, checkinsCols,
                    ICheckinSchema.CHECKINS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + ICheckinSchema.CHECKINS_TABLE);

            // upgrade checkin media table
            db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
            checkinsMediaColums = Database.getColumns(db, IMediaSchema.MEDIA_TABLE);
            db.execSQL("ALTER TABLE " + IMediaSchema.MEDIA_TABLE + " RENAME TO temp_"
                    + IMediaSchema.MEDIA_TABLE);
            db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
            checkinsMediaColums.retainAll(Database.getColumns(db, IMediaSchema.MEDIA_TABLE));
            String checkinsMediaCols = Database.join(checkinsMediaColums, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    IMediaSchema.MEDIA_TABLE, checkinsMediaCols, checkinsMediaCols,
                    IMediaSchema.MEDIA_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + IMediaSchema.MEDIA_TABLE);

            // upgrade checkin users table
            db.execSQL(IUserSchema.USER_TABLE_CREATE);
            usersColumns = Database.getColumns(db, IUserSchema.USER_TABLE);
            db.execSQL("ALTER TABLE " + IUserSchema.USER_TABLE + " RENAME TO temp_"
                    + IUserSchema.USER_TABLE);
            db.execSQL(IUserSchema.USER_TABLE_CREATE);
            usersColumns.retainAll(Database.getColumns(db, IUserSchema.USER_TABLE));
            String usersCols = Database.join(usersColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    IUserSchema.USER_TABLE, usersCols, usersCols, IUserSchema.USER_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + IUserSchema.USER_TABLE);

            // upgrade deployment table
            db.execSQL("DROP TABLE IF EXISTS " + IMapSchema.MAP_TABLE);
            onCreate(db);
        }

    }

    /**
     * Credits http://goo.gl/7kOpU
     * 
     * @param db
     * @param tableName
     * @return
     */
    public static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }

        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    public static String join(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String)list.get(i));
        }
        return buf.toString();
    }

    public Database(Context context) {
        this.mContext = context;
    }

    public Database open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        map = new MapDb(mDb);
        mReportDao = new ReportDao(mDb);
        mCategoryDao = new CategoryDao(mDb);
        mMapDao = new MapDao(mDb);
        mMediaDao = new MediaDao(mDb);
        mReportCategoryDao = new ReportCategoryDao(mDb);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createAddIncident(AddIncidentData addIncident) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(ADD_INCIDENT_TITLE, addIncident.getIncidentTitle());
        initialValues.put(ADD_INCIDENT_DESC, addIncident.getIncidentDesc());
        initialValues.put(ADD_INCIDENT_DATE, addIncident.getIncidentDate());
        initialValues.put(ADD_INCIDENT_HOUR, addIncident.getIncidentHour());
        initialValues.put(ADD_INCIDENT_MINUTE, addIncident.getIncidentMinute());
        initialValues.put(ADD_INCIDENT_AMPM, addIncident.getIncidentAmPm());
        initialValues.put(ADD_INCIDENT_CATEGORIES, addIncident.getIncidentCategories());
        initialValues.put(IReportSchema.INCIDENT_LOC_NAME, addIncident.getIncidentLocName());
        initialValues
                .put(IReportSchema.INCIDENT_LOC_LATITUDE, addIncident.getIncidentLocLatitude());
        initialValues.put(IReportSchema.INCIDENT_LOC_LONGITUDE,
                addIncident.getIncidentLocLongitude());
        initialValues.put(ADD_INCIDENT_PHOTO, addIncident.getIncidentPhoto());
        initialValues.put(ADD_INCIDENT_VIDEO, addIncident.getIncidentVideo());
        initialValues.put(ADD_INCIDENT_NEWS, addIncident.getIncidentNews());
        initialValues.put(ADD_PERSON_FIRST, addIncident.getPersonFirst());
        initialValues.put(ADD_PERSON_LAST, addIncident.getPersonLast());
        initialValues.put(ADD_PERSON_EMAIL, addIncident.getPersonEmail());

        return mDb.insert(ADD_INCIDENTS_TABLE, null, initialValues);
    }

    

    public Cursor fetchAllOfflineIncidents() {
        return mDb.query(ADD_INCIDENTS_TABLE, ADD_INCIDENTS_COLUMNS, null, null, null, null,
                ADD_INCIDENT_ID + " DESC");
    }

    public Cursor fetchAllCategories() {
        /*
         * return mDb.query(CATEGORIES_TABLE, CATEGORIES_COLUMNS, null, null,
         * null, null, CATEGORY_POS + " DESC");
         */
        return null;
    }

    public Cursor fetchAllCheckins() {
        return mDb.query(CHECKINS_TABLE, CHECKINS_COLUMNS, null, null, null, null, CHECKIN_DATE
                + " DESC");
    }

    public Cursor fetchCheckinsByUserdId(String id) {
        String sql = "SELECT * FROM " + CHECKINS_TABLE + " WHERE " + CHECKIN_USER_ID
                + " = ? ORDER BY " + CHECKIN_ID + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
            id
        });

    }

    public boolean clearData() {

        // deleteAllIncidents();
        deleteAllCategories();
        // deleteUsers();
        deleteAllCheckins();
        deleteCheckinMedia();
        map.deleteAllDeployment();
        // delete all files
        Util.rmDir(Preferences.savePath);
        return true;

    }

    public boolean clearReports() {

        // deleteAllIncidents();
        deleteAllCategories();
        // deleteUsers();
        deleteAllCheckins();
        deleteCheckinMedia();
        // delete all files
        Util.rmDir(Preferences.savePath);
        return true;

    }

    public boolean deleteAllCategories() {
        Log.i(TAG, "Deleting all categories");
        // return mDb.delete(CATEGORIES_TABLE, null, null) > 0;
        return true;
    }

    public boolean deleteCategory(int id) {
        Log.i(TAG, "Deleteing all Category by id " + id);
        // return mDb.delete(CATEGORIES_TABLE, CATEGORY_ID + "=" + id, null) >
        // 0;
        return true;
    }

    public boolean deleteAllCheckins() {
        Log.i(TAG, "Deleting all Checkins");
        return mDb.delete(CHECKINS_TABLE, null, null) > 0;
    }

    public boolean deleteCheckinMedia() {
        Log.i(TAG, "Deleting all Media Checkins");
        return mDb.delete(IMediaSchema.MEDIA_TABLE, null, null) > 0;
    }

    /**
     * Allows for the deletion of individual off line incidents given an id
     * 
     * @param addIncidentId
     * @return
     */
    public boolean deleteAddIncident(int addIncidentId) {
        return true;
        // return mDb.delete(ADD_INCIDENTS_TABLE, CATEGORY_ID + "=" +
        // addIncidentId, null) > 0;
    }

    /**
     * Clear the offline table for adding incidents
     * 
     * @return boolean
     */
    public boolean deleteAddIncidents() {
        return mDb.delete(ADD_INCIDENTS_TABLE, null, null) > 0;
    }

    public void markAllCategoriesRead() {
        /*
         * ContentValues values = new ContentValues();
         * values.put(CATEGORY_IS_UNREAD, 0); mDb.update(CATEGORIES_TABLE,
         * values, null, null);
         */
    }

    /**
     * Adds new incidents to be posted online to the db.
     */
    public long addIncidents(List<AddIncidentData> addIncidents) {
        long rowId = 0;
        try {
            mDb.beginTransaction();
            for (AddIncidentData addIncident : addIncidents) {
                rowId = createAddIncident(addIncident);
            }
            mDb.setTransactionSuccessful();

        } finally {
            mDb.endTransaction();
        }

        return rowId;
    }

    public void addCheckins(List<Checkin> checkins) {
        try {
            mDb.beginTransaction();

            for (Checkin checkin : checkins) {
                createCheckins(checkin);
            }

            // limitRows(CHECKINS_TABLE,
            // Integer.parseInt(UshahidiPref.totalReports), CHECKIN_ID);
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    /**
     * Limit number of records to retrieve.
     * 
     * @param tablename
     * @param limit
     * @param KEY_ID
     * @return
     */

    public int limitRows(String tablename, int limit, String KEY_ID) {
        Cursor cursor = mDb.rawQuery("SELECT " + KEY_ID + " FROM " + tablename + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1 OFFSET ?", new String[] {
            limit - 1 + ""
        });

        int deleted = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int limitId = cursor.getInt(0);
                deleted = mDb.delete(tablename, KEY_ID + "<" + limitId, null);
            }
            cursor.close();
        }

        return deleted;
    }

}
