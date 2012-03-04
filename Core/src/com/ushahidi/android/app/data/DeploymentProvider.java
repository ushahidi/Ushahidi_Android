/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ushahidi.android.app.data;

import com.ushahidi.android.app.database.Database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Provides search suggestions for a list of deployments and their details.
 */
public class DeploymentProvider extends ContentProvider {

    public  static String AUTHORITY =  "com.ushahidi.android.app.data.deploymentprovider";

    public static Uri CONTENT_URI =  Uri.parse("content://" + AUTHORITY+ "/deployment");;

    // MIME types used for searching words or looking up a single definition
    public static final String DEPLOYMENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ushahidi.searchabledeployment";

    public static final String DEPLOYMENT_DESC_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ushahidi.searchabledeployment";

    // UriMatcher stuff
    private static final int SEARCH_DEPLOYMENTS = 0;

    private static final int GET_DEPLOYMENT = 1;

    private static final int SEARCH_SUGGEST = 2;

    private static final int REFRESH_SHORTCUT = 3;

    private  final UriMatcher sURIMatcher = buildUriMatcher();
    @Override
    public boolean onCreate() {
        return true;
    }
    
    
    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */

    private  UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get deployments...
        matcher.addURI(AUTHORITY, "deployment", SEARCH_DEPLOYMENTS);
        matcher.addURI(AUTHORITY, "deployment/#", GET_DEPLOYMENT);
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

    /**
     * Handles all the dictionary searches and suggestion queries from the
     * Search Manager. When requesting a specific word, the uri alone is
     * required. When searching all of the dictionary for matches, the
     * selectionArgs argument must carry the search query as the first element.
     * All other arguments are ignored.
     */
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
                BaseColumns._ID, MapDb.DEPLOYMENT_NAME, MapDb.DEPLOYMENT_DESC,
                MapDb.DEPLOYMENT_URL
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

}
