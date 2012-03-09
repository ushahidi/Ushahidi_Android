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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.ushahidi.android.app.data.MapDb;
import com.ushahidi.android.app.entities.Report;

public class ReportDao extends DbContentProvider implements IReportDao, IReportSchema {

    private Cursor cursor;

    private List<Report> listReport;

    private ContentValues initialValues;

    public static String AUTHORITY = "com.ushahidi.android.app.datatabase.reportdao";

    public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + INCIDENTS_TABLE);;

    // MIME types used for searching words or looking up a single definition
    public static final String DEPLOYMENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ushahidi.searchableincident";

    public static final String DEPLOYMENT_DESC_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ushahidi.searchableincident";

    // UriMatcher stuff
    private static final int SEARCH_DEPLOYMENTS = 0;

    private static final int GET_DEPLOYMENT = 1;

    private static final int SEARCH_SUGGEST = 2;

    private static final int REFRESH_SHORTCUT = 3;

    private final UriMatcher sURIMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        return true;
    }

    public ReportDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Report> fetchAllReports() {

        final String sortOrder = INCIDENT_DATE + " DESC";

        listReport = new ArrayList<Report>();
        cursor = super.query(INCIDENTS_TABLE, null, null, null, sortOrder);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Report report = cursorToEntity(cursor);
                listReport.add(report);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return listReport;
    }

    @Override
    public List<Report> fetchReportByCategory(String category) {
        final String sortOrder = INCIDENT_TITLE + " DESC";
        final String selectionArgs[] = {
            category
        };

        final String selection = INCIDENT_CATEGORIES + " LIKE ?";

        listReport = new ArrayList<Report>();

        cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs, sortOrder);
        if (cursor != null) {

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Report report = cursorToEntity(cursor);
                listReport.add(report);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return listReport;
    }

    @Override
    public List<Report> fetchReportById(long id) {
        final String sortOrder = INCIDENT_TITLE;

        final String selectionArgs[] = {
            String.valueOf(id)
        };

        final String selection = INCIDENT_ID + " = ?";

        listReport = new ArrayList<Report>();

        cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Report report = cursorToEntity(cursor);
                listReport.add(report);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return listReport;
    }

    @Override
    public boolean deleteAllReport() {
        return super.delete(INCIDENTS_TABLE, null, null) > 0;
    }

    @Override
    public boolean deleteReportById(long id) {
        final String selectionArgs[] = {
            String.valueOf(id)
        };
        final String selection = INCIDENT_ID + " = ?";

        return super.delete(INCIDENTS_TABLE, selection, selectionArgs) > 0;
    }

    @Override
    public boolean addReport(Report report) {
        // set values
        setContentValue(report);
        return super.insert(INCIDENTS_TABLE, getContentValue()) > 0;
    }

    @Override
    public boolean addReport(List<Report> reports) {
        try {
            mDb.beginTransaction();

            for (Report report : reports) {

                addReport(report);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Report cursorToEntity(Cursor cursor) {
        Report report = new Report();
        int idIndex;
        int titleIndex;
        int dateIndex;
        int verifiedIndex;
        int locationIndex;
        int descIndex;
        int categoryIndex;
        int mediaIndex;
        int imageIndex;
        int longitudeIndex;
        int latitudeIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(INCIDENT_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(INCIDENT_ID);
                report.setDbId(Long.valueOf(cursor.getString(idIndex)));
            }

            if (cursor.getColumnIndex(INCIDENT_TITLE) != -1) {
                titleIndex = cursor.getColumnIndexOrThrow(INCIDENT_TITLE);
                report.setTitle(cursor.getString(titleIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_DATE) != -1) {
                dateIndex = cursor.getColumnIndexOrThrow(INCIDENT_DATE);
                report.setReportDate(cursor.getString(dateIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_VERIFIED) != -1) {
                verifiedIndex = cursor.getColumnIndexOrThrow(INCIDENT_VERIFIED);
                report.setVerified(cursor.getString(verifiedIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_LOC_NAME) != -1) {
                locationIndex = cursor.getColumnIndexOrThrow(INCIDENT_LOC_NAME);
                report.setLocationName(cursor.getString(locationIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_DESC) != -1) {
                descIndex = cursor.getColumnIndexOrThrow(INCIDENT_DESC);
                report.setDescription(cursor.getString(descIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_CATEGORIES) != -1) {
                categoryIndex = cursor.getColumnIndexOrThrow(INCIDENT_CATEGORIES);
                report.setCategories(cursor.getString(categoryIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_MEDIA) != -1) {
                mediaIndex = cursor.getColumnIndexOrThrow(INCIDENT_MEDIA);
                report.setMedia(cursor.getString(mediaIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_IMAGE) != -1) {
                imageIndex = cursor.getColumnIndexOrThrow(INCIDENT_IMAGE);
                report.setImage(cursor.getString(imageIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_LOC_LATITUDE) != -1) {
                latitudeIndex = cursor.getColumnIndexOrThrow(INCIDENT_LOC_LATITUDE);
                report.setLatitude(cursor.getString(latitudeIndex));
            }

            if (cursor.getColumnIndex(INCIDENT_LOC_LONGITUDE) != -1) {
                longitudeIndex = cursor.getColumnIndexOrThrow(INCIDENT_LOC_LONGITUDE);
                report.setLongitude(cursor.getString(longitudeIndex));
            }
        }

        return report;
    }

    protected void setContentValue(Report report) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(INCIDENT_ID, report.getDbId());
        initialValues.put(INCIDENT_TITLE, report.getTitle());
        initialValues.put(INCIDENT_DESC, report.getDescription());
        initialValues.put(INCIDENT_DATE, report.getReportDate());
        initialValues.put(INCIDENT_MODE, report.getMode());
        initialValues.put(INCIDENT_VERIFIED, report.getVerified());
        initialValues.put(INCIDENT_LOC_NAME, report.getLocationName());
        initialValues.put(INCIDENT_LOC_LATITUDE, report.getLatitude());
        initialValues.put(INCIDENT_LOC_LONGITUDE, report.getLongitude());
        initialValues.put(INCIDENT_CATEGORIES, report.getCategories());
        initialValues.put(INCIDENT_MEDIA, report.getMedia());

        initialValues.put(INCIDENT_IMAGE, report.getImage());
        initialValues.put(INCIDENT_IS_UNREAD, true);
    }

    protected ContentValues getContentValue() {
        return initialValues;
    }

    @Override
    public List<Report> fetchReportByTitle(String title) {

        final String sortOrder = INCIDENT_TITLE + " DESC";
        final String selectionArgs[] = {
            title
        };

        final String selection = INCIDENT_TITLE + " LIKE ?";

        listReport = new ArrayList<Report>();

        cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs, sortOrder);
        if (cursor != null) {

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Report report = cursorToEntity(cursor);
                listReport.add(report);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return listReport;
    }

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */

    private UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get deployments...
        matcher.addURI(AUTHORITY, "incidents", SEARCH_DEPLOYMENTS);
        matcher.addURI(AUTHORITY, "incidents/#", GET_DEPLOYMENT);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /*
         * The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our
         * suggestions table, we could expect to receive refresh queries when a
         * shortcutted suggestion is displayed in Quick Search Box, in which
         * case, the following Uris would be provided and we would return a
         * cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;

    }

    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
                BaseColumns._ID, MapDb.DEPLOYMENT_NAME, MapDb.DEPLOYMENT_DESC,
                MapDb.DEPLOYMENT_URL, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID

        };

        return Database.map.getDeploymentMatches(query, columns);
    }

    private Cursor search(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
                BaseColumns._ID, INCIDENT_TITLE, INCIDENT_DESC, INCIDENT_DATE, INCIDENT_MODE,
                INCIDENT_VERIFIED, INCIDENT_LOC_NAME, INCIDENT_LOC_LATITUDE,
                INCIDENT_LOC_LONGITUDE, INCIDENT_CATEGORIES, INCIDENT_MEDIA, INCIDENT_IMAGE,
                INCIDENT_IS_UNREAD
        };
        Log.d("Search Query", "Query: " + query);
        return Database.map.getDeploymentMatches(query, columns);
    }

    private Cursor getDeployment(Uri uri) {
        String rowId = uri.getLastPathSegment();

        String[] columns = new String[] {
                MapDb.DEPLOYMENT_NAME, MapDb.DEPLOYMENT_DESC, MapDb.DEPLOYMENT_URL
        };

        return Database.map.getDeployment(rowId, columns);
    }

    private Cursor refreshShortcut(Uri uri) {
        /*
         * This won't be called with the current implementation, but if we
         * include {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column
         * in our suggestions table, we could expect to receive refresh queries
         * when a shortcutted suggestion is displayed in Quick Search Box. In
         * which case, this method will query the table for the specific word,
         * using the given item Uri and provide all the columns originally
         * provided with the suggestion query.
         */
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
                MapDb.DEPLOYMENT_NAME, MapDb.DEPLOYMENT_DESC, MapDb.DEPLOYMENT_URL,
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
        };

        return Database.map.getDeployment(rowId, columns);
    }

    /**
     * This method is required in order to query the supported types. It's also
     * useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_DEPLOYMENTS:
                return DEPLOYMENT_MIME_TYPE;
            case GET_DEPLOYMENT:
                return DEPLOYMENT_DESC_MIME_TYPE;
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case REFRESH_SHORTCUT:
                return SearchManager.SHORTCUT_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    // Other required implementations...

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // Use the UriMatcher to see what kind of query we have and format the
        // db query accordingly
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_DEPLOYMENTS:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_DEPLOYMENT:
                return getDeployment(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
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
    private Cursor query2(String selection, String[] selectionArgs, String[] columns) {
        HashMap<String, String> mColumnMap = new HashMap<String, String>();
        mColumnMap.put(INCIDENT_ID, INCIDENT_ID);
        mColumnMap.put(INCIDENT_TITLE, INCIDENT_TITLE);
        mColumnMap.put(INCIDENT_DESC, INCIDENT_DESC);
        mColumnMap.put(INCIDENT_DATE, INCIDENT_DATE);
        mColumnMap.put(INCIDENT_MODE, INCIDENT_MODE);
        mColumnMap.put(INCIDENT_VERIFIED, INCIDENT_VERIFIED);
        mColumnMap.put(INCIDENT_LOC_NAME, INCIDENT_LOC_NAME);
        mColumnMap.put(INCIDENT_LOC_LATITUDE, INCIDENT_LOC_LATITUDE);
        mColumnMap.put(INCIDENT_LOC_LONGITUDE, INCIDENT_LOC_LONGITUDE);
        mColumnMap.put(INCIDENT_CATEGORIES, INCIDENT_CATEGORIES);
        mColumnMap.put(INCIDENT_MEDIA, INCIDENT_MEDIA);
        mColumnMap.put(INCIDENT_IMAGE, INCIDENT_IMAGE);
        mColumnMap.put(INCIDENT_IS_UNREAD, INCIDENT_IS_UNREAD);
        mColumnMap.put(BaseColumns._ID, INCIDENT_ID);
        mColumnMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, INCIDENT_ID + " AS "
                + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        mColumnMap.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, INCIDENT_ID + " AS "
                + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        /*
         * The SQLiteBuilder provides a map for all possible columns requested
         * to actual columns in the database, creating a simple column alias
         * mechanism by which the ContentProvider does not need to know the real
         * column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(INCIDENTS_TABLE);
        builder.setProjectionMap(mColumnMap);
        String orderBy = INCIDENT_DATE + " DESC";
        Cursor cursor = builder.query(mDb, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

}
