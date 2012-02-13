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

package com.ushahidi.android.app.data;

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
import com.ushahidi.android.app.checkin.CheckinMedia;
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

    public static final String INCIDENT_ID = "_id";

    public static final String INCIDENT_TITLE = "incident_title";

    public static final String INCIDENT_DESC = "incident_desc";

    public static final String INCIDENT_DATE = "incident_date";

    public static final String INCIDENT_MODE = "incident_mode";

    public static final String INCIDENT_VERIFIED = "incident_verified";

    public static final String INCIDENT_LOC_NAME = "incident_loc_name";

    public static final String INCIDENT_LOC_LATITUDE = "incident_loc_latitude";

    public static final String INCIDENT_LOC_LONGITUDE = "incident_loc_longitude";

    public static final String INCIDENT_CATEGORIES = "incident_categories";

    public static final String INCIDENT_MEDIA = "incident_media";

    public static final String INCIDENT_IMAGE = "incident_image";

    public static final String INCIDENT_IS_UNREAD = "is_unread";

    public static final String CATEGORY_ID = "_id";

    public static final String CATEGORY_TITLE = "category_title";

    public static final String CATEGORY_DESC = "category_desc";

    public static final String CATEGORY_COLOR = "category_color";

    public static final String CATEGORY_IS_UNREAD = "is_unread";

    public static final String CATEGORY_POS = "position";

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

    // Checkin messages
    public static final String CHECKIN_ID = "_id";

    public static final String CHECKIN_USER_ID = "user_id";

    public static final String CHECKIN_MESG = "checkin_mesg";

    public static final String CHECKIN_DATE = "checkin_date";

    public static final String CHECKIN_LOC_NAME = "checki_loc_name";

    public static final String CHECKIN_LOC_LATITUDE = "checkin_loc_latitude";

    public static final String CHECKIN_LOC_LONGITUDE = "checkin_loc_longitude";

    // Checkins users
    public static final String USER_ID = "_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_COLOR = "user_color";

    // Checkins media
    public static final String MEDIA_ID = "_id";

    public static final String MEDIA_CHECKIN_ID = "media_checkin_id";

    public static final String MEDIA_THUMBNAIL_LINK = "media_thumbnail_link";

    public static final String MEDIA_MEDIUM_LINK = "media_medium_link";

    public static final String[] INCIDENTS_COLUMNS = new String[] {
            INCIDENT_ID, INCIDENT_TITLE, INCIDENT_DESC, INCIDENT_DATE, INCIDENT_MODE,
            INCIDENT_VERIFIED, INCIDENT_LOC_NAME, INCIDENT_LOC_LATITUDE, INCIDENT_LOC_LONGITUDE,
            INCIDENT_CATEGORIES, INCIDENT_MEDIA, INCIDENT_IMAGE, INCIDENT_IS_UNREAD
    };

    public static final String[] CATEGORIES_COLUMNS = new String[] {
            CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESC, CATEGORY_COLOR, CATEGORY_IS_UNREAD,
            CATEGORY_POS
    };

    /**
     * Columns of the table that stores off line incidents
     */
    public static final String[] ADD_INCIDENTS_COLUMNS = new String[] {
            ADD_INCIDENT_ID, ADD_INCIDENT_TITLE, ADD_INCIDENT_DESC, ADD_INCIDENT_DATE,
            ADD_INCIDENT_HOUR, ADD_INCIDENT_MINUTE, ADD_INCIDENT_AMPM, ADD_INCIDENT_CATEGORIES,
            INCIDENT_LOC_NAME, INCIDENT_LOC_LATITUDE, INCIDENT_LOC_LONGITUDE, ADD_INCIDENT_PHOTO,
            ADD_INCIDENT_VIDEO, ADD_INCIDENT_NEWS, ADD_PERSON_FIRST, ADD_PERSON_LAST,
            ADD_PERSON_EMAIL
    };

    // Checkins messages
    public static final String[] CHECKINS_COLUMNS = new String[] {
            CHECKIN_ID, CHECKIN_USER_ID, CHECKIN_MESG, CHECKIN_DATE, CHECKIN_LOC_NAME,
            CHECKIN_LOC_LATITUDE, CHECKIN_LOC_LONGITUDE
    };

    // checkins users
    public static final String[] USERS_COLUMNS = new String[] {
            USER_ID, USER_NAME, USER_COLOR
    };

    // Checkin Media
    public static final String[] CHECKIN_MEDIA_COLUMNS = new String[] {
            MEDIA_ID, MEDIA_CHECKIN_ID, MEDIA_THUMBNAIL_LINK, MEDIA_MEDIUM_LINK
    };

    private DatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "ushahidi_db";

    private static final String INCIDENTS_TABLE = "incidents";

    private static final String ADD_INCIDENTS_TABLE = "add_incidents";

    private static final String CATEGORIES_TABLE = "categories";

    private static final String CHECKINS_TABLE = "checkins";

    private static final String USERS_TABLE = "users";

    private static final String CHECKINS_MEDIA_TABLE = "checkin_media";

    private static final int DATABASE_VERSION = 14;

    // NOTE: the incident ID is used as the row ID.
    // Furthermore, if a row already exists, an insert will replace
    // the old row upon conflict.

    private static final String INCIDENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + INCIDENTS_TABLE + " (" + INCIDENT_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + INCIDENT_TITLE + " TEXT NOT NULL, " + INCIDENT_DESC + " TEXT, " + INCIDENT_DATE
            + " DATE NOT NULL, " + INCIDENT_MODE + " INTEGER, " + INCIDENT_VERIFIED + " INTEGER, "
            + INCIDENT_LOC_NAME + " TEXT NOT NULL, " + INCIDENT_LOC_LATITUDE + " TEXT NOT NULL, "
            + INCIDENT_LOC_LONGITUDE + " TEXT NOT NULL, " + INCIDENT_CATEGORIES
            + " TEXT NOT NULL, " + INCIDENT_MEDIA + " TEXT, " + INCIDENT_IMAGE + " TEXT, "
            + INCIDENT_IS_UNREAD + " BOOLEAN NOT NULL " + ")";

    private static final String ADD_INCIDENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ADD_INCIDENTS_TABLE + " (" + ADD_INCIDENT_ID + " INTEGER PRIMARY KEY , "
            + ADD_INCIDENT_TITLE + " TEXT NOT NULL, " + ADD_INCIDENT_DESC + " TEXT, "
            + INCIDENT_DATE + " DATE NOT NULL, " + ADD_INCIDENT_HOUR + " INTEGER, "
            + ADD_INCIDENT_MINUTE + " INTEGER, " + ADD_INCIDENT_AMPM + " TEXT NOT NULL, "
            + ADD_INCIDENT_CATEGORIES + " TEXT NOT NULL, " + ADD_INCIDENT_LOC_NAME
            + " TEXT NOT NULL, " + ADD_INCIDENT_LOC_LATITUDE + " TEXT NOT NULL, "
            + ADD_INCIDENT_LOC_LONGITUDE + " TEXT NOT NULL, " + ADD_INCIDENT_PHOTO + " TEXT, "
            + ADD_INCIDENT_VIDEO + " TEXT, " + ADD_INCIDENT_NEWS + " TEXT, " + ADD_PERSON_FIRST
            + " TEXT, " + ADD_PERSON_LAST + " TEXT, " + ADD_PERSON_EMAIL + " TEXT " + ")";

    private static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + CATEGORIES_TABLE + " (" + CATEGORY_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + CATEGORY_TITLE + " TEXT NOT NULL, " + CATEGORY_DESC + " TEXT, " + CATEGORY_COLOR
            + " TEXT, " + CATEGORY_IS_UNREAD + " BOOLEAN NOT NULL, " + CATEGORY_POS + " INTEGER "
            + ")";

    private static final String CHECKINS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + CHECKINS_TABLE + " (" + CHECKIN_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + CHECKIN_USER_ID + " INTEGER, " + CHECKIN_MESG + " TEXT NOT NULL, " + CHECKIN_DATE
            + " DATE NOT NULL, " + CHECKIN_LOC_NAME + " TEXT NOT NULL, " + CHECKIN_LOC_LATITUDE
            + " TEXT NOT NULL, " + CHECKIN_LOC_LONGITUDE + " TEXT NOT NULL" + ")";

    private static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE
            + " (" + USER_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + USER_NAME
            + " TEXT NOT NULL, " + USER_COLOR + " TEXT" + ")";

    private static final String CHECKINS_MEDIA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + CHECKINS_MEDIA_TABLE + " (" + MEDIA_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + MEDIA_CHECKIN_ID + " INTEGER, " + MEDIA_THUMBNAIL_LINK + " TEXT, "
            + MEDIA_MEDIUM_LINK + " TEXT" + ")";

    private final Context mContext;

    public static MapDb map;
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INCIDENTS_TABLE_CREATE);
            db.execSQL(CATEGORIES_TABLE_CREATE);
            db.execSQL(ADD_INCIDENTS_TABLE_CREATE);
            db.execSQL(CHECKINS_TABLE_CREATE);
            db.execSQL(CHECKINS_MEDIA_TABLE_CREATE);
            db.execSQL(USERS_TABLE_CREATE);
            
            //create map table
            //map.createTable(db);
            db.execSQL(MapDb.DEPLOYMENT_TABLE_CREATE);

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
            db.execSQL(INCIDENTS_TABLE_CREATE);
            incidentsColumns = Database.getColumns(db, INCIDENTS_TABLE);
            db.execSQL("ALTER TABLE " + INCIDENTS_TABLE + " RENAME TO temp_" + INCIDENTS_TABLE);
            db.execSQL(INCIDENTS_TABLE_CREATE);
            incidentsColumns.retainAll(Database.getColumns(db, INCIDENTS_TABLE));
            String cols = Database.join(incidentsColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", INCIDENTS_TABLE,
                    cols, cols, INCIDENTS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + INCIDENTS_TABLE);

            // upgrade category table
            db.execSQL(CATEGORIES_TABLE_CREATE);
            categoriesColumns = Database.getColumns(db, CATEGORIES_TABLE);
            db.execSQL("ALTER TABLE " + CATEGORIES_TABLE + " RENAME TO temp_" + CATEGORIES_TABLE);
            db.execSQL(CATEGORIES_TABLE_CREATE);
            categoriesColumns.retainAll(Database.getColumns(db, CATEGORIES_TABLE));
            String catsCols = Database.join(categoriesColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    CATEGORIES_TABLE, catsCols, catsCols, CATEGORIES_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + CATEGORIES_TABLE);

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
            db.execSQL(CHECKINS_TABLE_CREATE);
            checkinsColums = Database.getColumns(db, CHECKINS_TABLE);
            db.execSQL("ALTER TABLE " + CHECKINS_TABLE + " RENAME TO temp_" + CHECKINS_TABLE);
            db.execSQL(CHECKINS_TABLE_CREATE);
            checkinsColums.retainAll(Database.getColumns(db, CHECKINS_TABLE));
            String checkinsCols = Database.join(checkinsColums, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", CHECKINS_TABLE,
                    checkinsCols, checkinsCols, CHECKINS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + CHECKINS_TABLE);

            // upgrade checkin media table
            db.execSQL(CHECKINS_MEDIA_TABLE_CREATE);
            checkinsMediaColums = Database.getColumns(db, CHECKINS_MEDIA_TABLE);
            db.execSQL("ALTER TABLE " + CHECKINS_MEDIA_TABLE + " RENAME TO temp_"
                    + CHECKINS_MEDIA_TABLE);
            db.execSQL(CHECKINS_MEDIA_TABLE_CREATE);
            checkinsMediaColums.retainAll(Database.getColumns(db, CHECKINS_MEDIA_TABLE));
            String checkinsMediaCols = Database.join(checkinsMediaColums, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s",
                    CHECKINS_MEDIA_TABLE, checkinsMediaCols, checkinsMediaCols,
                    CHECKINS_MEDIA_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + CHECKINS_MEDIA_TABLE);

            // upgrade checkin users table
            db.execSQL(USERS_TABLE_CREATE);
            usersColumns = Database.getColumns(db, USERS_TABLE);
            db.execSQL("ALTER TABLE " + USERS_TABLE + " RENAME TO temp_" + USERS_TABLE);
            db.execSQL(USERS_TABLE_CREATE);
            usersColumns.retainAll(Database.getColumns(db, USERS_TABLE));
            String usersCols = Database.join(usersColumns, ",");
            db.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", USERS_TABLE,
                    usersCols, usersCols, USERS_TABLE));
            db.execSQL("DROP TABLE IF EXISTS temp_" + USERS_TABLE);

            // upgrade deployment table
            //map.upgradeTable(db);
            db.execSQL("DROP TABLE IF EXISTS " + MapDb.DEPLOYMENT_TABLE);
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
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createIncidents(IncidentsData incidents, boolean isUnread) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(INCIDENT_ID, incidents.getIncidentId());
        initialValues.put(INCIDENT_TITLE, incidents.getIncidentTitle());
        initialValues.put(INCIDENT_DESC, incidents.getIncidentDesc());
        initialValues.put(INCIDENT_DATE, incidents.getIncidentDate());
        initialValues.put(INCIDENT_MODE, incidents.getIncidentMode());
        initialValues.put(INCIDENT_VERIFIED, incidents.getIncidentVerified());
        initialValues.put(INCIDENT_LOC_NAME, incidents.getIncidentLocation());
        initialValues.put(INCIDENT_LOC_LATITUDE, incidents.getIncidentLocLatitude());
        initialValues.put(INCIDENT_LOC_LONGITUDE, incidents.getIncidentLocLongitude());
        initialValues.put(INCIDENT_CATEGORIES, incidents.getIncidentCategories());
        initialValues.put(INCIDENT_MEDIA, incidents.getIncidentThumbnail());

        initialValues.put(INCIDENT_IMAGE, incidents.getIncidentImage());
        initialValues.put(INCIDENT_IS_UNREAD, isUnread);

        return mDb.insert(INCIDENTS_TABLE, null, initialValues);
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
        initialValues.put(INCIDENT_LOC_NAME, addIncident.getIncidentLocName());
        initialValues.put(INCIDENT_LOC_LATITUDE, addIncident.getIncidentLocLatitude());
        initialValues.put(INCIDENT_LOC_LONGITUDE, addIncident.getIncidentLocLongitude());
        initialValues.put(ADD_INCIDENT_PHOTO, addIncident.getIncidentPhoto());
        initialValues.put(ADD_INCIDENT_VIDEO, addIncident.getIncidentVideo());
        initialValues.put(ADD_INCIDENT_NEWS, addIncident.getIncidentNews());
        initialValues.put(ADD_PERSON_FIRST, addIncident.getPersonFirst());
        initialValues.put(ADD_PERSON_LAST, addIncident.getPersonLast());
        initialValues.put(ADD_PERSON_EMAIL, addIncident.getPersonEmail());

        return mDb.insert(ADD_INCIDENTS_TABLE, null, initialValues);
    }

    public long createCategories(CategoriesData categories, boolean isUnread) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CATEGORY_ID, categories.getCategoryId());
        initialValues.put(CATEGORY_TITLE, categories.getCategoryTitle());
        initialValues.put(CATEGORY_DESC, categories.getCategoryDescription());
        initialValues.put(CATEGORY_COLOR, categories.getCategoryColor());
        initialValues.put(CATEGORY_POS, categories.getCategoryPosition());
        initialValues.put(CATEGORY_IS_UNREAD, isUnread);
        return mDb.insert(CATEGORIES_TABLE, null, initialValues);
    }

    /**
     * Create table for checkins
     * 
     * @return
     */
    public long createCheckins(Checkin checkins) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHECKIN_ID, checkins.getId());
        initialValues.put(CHECKIN_USER_ID, checkins.getUser());
        initialValues.put(CHECKIN_MESG, checkins.getMsg());
        initialValues.put(CHECKIN_DATE, checkins.getDate());
        initialValues.put(CHECKIN_LOC_NAME, checkins.getLoc());
        initialValues.put(CHECKIN_LOC_LATITUDE, checkins.getLat());
        initialValues.put(CHECKIN_LOC_LONGITUDE, checkins.getLon());
        return mDb.insert(CHECKINS_TABLE, null, initialValues);
    }

    public long createUsers(UsersData users) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(USER_ID, users.getId());
        initialValues.put(USER_NAME, users.getUserName());
        initialValues.put(USER_COLOR, users.getColor());
        return mDb.insert(USERS_TABLE, null, initialValues);
    }

    public long createCheckinMedia(CheckinMedia checkinMedia) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(MEDIA_ID, checkinMedia.getMediaId());
        initialValues.put(MEDIA_CHECKIN_ID, checkinMedia.getCheckinId());
        initialValues.put(MEDIA_THUMBNAIL_LINK, checkinMedia.getThumbnailLink());
        initialValues.put(MEDIA_MEDIUM_LINK, checkinMedia.getMediumLink());
        return mDb.insert(CHECKINS_MEDIA_TABLE, null, initialValues);
    }

   

    public int addNewIncidentsAndCountUnread(ArrayList<IncidentsData> newIncidents) {
        addIncidents(newIncidents, true);
        return fetchUnreadCount();
    }

    public Cursor fetchAllIncidents() {
        return mDb.query(INCIDENTS_TABLE, INCIDENTS_COLUMNS, null, null, null, null, INCIDENT_DATE
                + " DESC");
    }

    public Cursor fetchAllOfflineIncidents() {
        return mDb.query(ADD_INCIDENTS_TABLE, ADD_INCIDENTS_COLUMNS, null, null, null, null,
                ADD_INCIDENT_ID + " DESC");
    }

    public Cursor fetchAllCategories() {
        return mDb.query(CATEGORIES_TABLE, CATEGORIES_COLUMNS, null, null, null, null, CATEGORY_POS
                + " DESC");
    }

    public Cursor fetchIncidentsByCategories(String filter) {

        String likeFilter = '%' + filter + '%';
        String sql = "SELECT * FROM " + INCIDENTS_TABLE + " WHERE " + INCIDENT_CATEGORIES
                + " LIKE ? ORDER BY " + INCIDENT_TITLE + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
            likeFilter
        });
    }

    public Cursor fetchIncidentsById(String id) {
        String sql = "SELECT * FROM " + INCIDENTS_TABLE + " WHERE " + INCIDENT_ID
                + " = ? ORDER BY " + INCIDENT_TITLE + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
            id
        });
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

    public Cursor fetchUsersById(String id) {
        String sql = "SELECT * FROM " + USERS_TABLE + " WHERE " + USER_ID + " = ? ORDER BY "
                + USER_NAME + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
            id
        });

    }

    public Cursor fetchCheckinsMediaByCheckinId(String id) {

        String sql = "SELECT * FROM " + CHECKINS_MEDIA_TABLE + " WHERE " + MEDIA_CHECKIN_ID
                + " = ? ORDER BY " + MEDIA_CHECKIN_ID + " COLLATE NOCASE";
        return mDb.rawQuery(sql, new String[] {
            id
        });

    }

    public boolean clearData() {

        deleteAllIncidents();
        deleteAllCategories();
        deleteUsers();
        deleteAllCheckins();
        deleteCheckinMedia();
        map.deleteAllDeployment();
        // delete all files
        Util.rmDir(Preferences.savePath);
        return true;

    }

    public boolean clearReports() {

        deleteAllIncidents();
        deleteAllCategories();
        deleteUsers();
        deleteAllCheckins();
        deleteCheckinMedia();
        // delete all files
        Util.rmDir(Preferences.savePath);
        return true;

    }

    public boolean deleteAllIncidents() {
        Log.i(TAG, "Deleting all incidents");
        return mDb.delete(INCIDENTS_TABLE, null, null) > 0;
    }

    public boolean deleteAllCategories() {
        Log.i(TAG, "Deleting all categories");
        return mDb.delete(CATEGORIES_TABLE, null, null) > 0;
    }

    public boolean deleteCategory(int id) {
        Log.i(TAG, "Deleteing all Category by id " + id);
        return mDb.delete(CATEGORIES_TABLE, CATEGORY_ID + "=" + id, null) > 0;
    }

    public boolean deleteAllCheckins() {
        Log.i(TAG, "Deleting all Checkins");
        return mDb.delete(CHECKINS_TABLE, null, null) > 0;
    }

    public boolean deleteUsers() {
        Log.i(TAG, "Deleting all Users");
        return mDb.delete(USERS_TABLE, null, null) > 0;
    }

    public boolean deleteCheckinMedia() {
        Log.i(TAG, "Deleting all Media Checkins");
        return mDb.delete(CHECKINS_MEDIA_TABLE, null, null) > 0;
    }

    

    /**
     * Allows for the deletion of individual off line incidents given an id
     * 
     * @param addIncidentId
     * @return
     */
    public boolean deleteAddIncident(int addIncidentId) {
        return mDb.delete(ADD_INCIDENTS_TABLE, CATEGORY_ID + "=" + addIncidentId, null) > 0;
    }

    /**
     * Clear the offline table for adding incidents
     * 
     * @return boolean
     */
    public boolean deleteAddIncidents() {
        return mDb.delete(ADD_INCIDENTS_TABLE, null, null) > 0;
    }

    public void markAllIncidentssRead() {
        ContentValues values = new ContentValues();
        values.put(INCIDENT_IS_UNREAD, 0);
        mDb.update(INCIDENTS_TABLE, values, null, null);
    }

    public void markAllCategoriesRead() {
        ContentValues values = new ContentValues();
        values.put(CATEGORY_IS_UNREAD, 0);
        mDb.update(CATEGORIES_TABLE, values, null, null);
    }

    public int fetchMaxId() {
        Cursor mCursor = mDb.rawQuery("SELECT MAX(" + INCIDENT_ID + ") FROM " + INCIDENTS_TABLE,
                null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    public int fetchUnreadCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + INCIDENT_ID + ") FROM " + INCIDENTS_TABLE
                + " WHERE " + INCIDENT_IS_UNREAD + " = 1", null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    public int addNewCategoryAndCountUnread(List<CategoriesData> categories) {
        addCategories(categories, true);

        return fetchUnreadCategoriesCount();
    }

    public int fetchCategoriesCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + CATEGORY_ID + ") FROM " + CATEGORIES_TABLE,
                null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    private int fetchUnreadCategoriesCount() {
        Cursor mCursor = mDb.rawQuery("SELECT COUNT(" + CATEGORY_ID + ") FROM " + CATEGORIES_TABLE
                + " WHERE " + CATEGORY_IS_UNREAD + " = 1", null);

        int result = 0;

        if (mCursor == null) {
            return result;
        }

        mCursor.moveToFirst();
        result = mCursor.getInt(0);
        mCursor.close();

        return result;
    }

    public void addIncidents(List<IncidentsData> incidents, boolean isUnread) {
        try {
            mDb.beginTransaction();

            for (IncidentsData incident : incidents) {
                createIncidents(incident, isUnread);
            }

            limitRows(INCIDENTS_TABLE, Integer.parseInt(Preferences.totalReports), INCIDENT_ID);
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
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

    public void addCategories(List<CategoriesData> categories, boolean isUnread) {
        try {
            mDb.beginTransaction();

            for (CategoriesData category : categories) {
                createCategories(category, isUnread);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
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

    public void addUsers(List<UsersData> users) {
        try {
            mDb.beginTransaction();

            for (UsersData user : users) {
                createUsers(user);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }

    public void addCheckinMedia(List<CheckinMedia> checkinsMedia) {
        try {
            mDb.beginTransaction();

            for (CheckinMedia checkinMedia : checkinsMedia) {
                createCheckinMedia(checkinMedia);
            }

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
