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

package com.ushahidi.android.app.util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.checkin.Checkin;
import com.ushahidi.android.app.checkin.NetworkServices;
import com.ushahidi.android.app.checkin.RetrieveCheckinsJSONServices;
import com.ushahidi.android.app.data.CategoriesData;
import com.ushahidi.android.app.data.HandleXml;
import com.ushahidi.android.app.data.IncidentsData;
import com.ushahidi.android.app.data.UsersData;
import com.ushahidi.android.app.net.Categories;
import com.ushahidi.android.app.net.Incidents;
import com.ushahidi.android.app.net.MainHttpClient;

/**
 * This is a Util class for an Ushahidi deployment API
 * 
 * @author eyedol
 */
public class ApiUtils {

    private static final String CLASS_TAG = Util.class.getSimpleName();

    private static final String VALID_URL_PATTERN = "^(https?|ftp)://[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].+)?$";

    private static Pattern pattern;

    private static Matcher matcher;

    private static List<IncidentsData> mNewIncidents;

    private static List<CategoriesData> mNewCategories;

    private static List<Checkin> mCheckins;

    private static List<UsersData> mUsers;

    private static JSONObject jsonObject;

    private static HttpResponse response;

    private static String jsonString;

    public static boolean isCheckinEnabled(Context context) {

        Preferences.loadSettings(context);

        StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
        uriBuilder.append("/api?task=version");
        uriBuilder.append("&resp=json");

        try {
            response = MainHttpClient.GetURL(uriBuilder.toString());
            if (response == null) {
                return false;
            }

            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {

                jsonString = MainHttpClient.GetText(response);
                JSONObject jsonObject = new JSONObject(jsonString);
                int checkinStatus = jsonObject.getJSONObject("payload").getInt("checkins");

                if (checkinStatus == 1) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } catch (IOException e) {

            return false;
        } catch (JSONException e) {

            return false;
        }
    }

    public static void checkForCheckin(Context context) {
        if (isCheckinEnabled(context)) {
            Preferences.isCheckinEnabled = 1;
        } else {
            Preferences.isCheckinEnabled = 0;
        }
        Preferences.saveSettings(context);
    }

    /**
     * Validate an Ushahidi instance
     * 
     * @param String - URL to be validated.
     * @return boolean
     */
    public static boolean validateUshahidiInstance(String ushahidiUrl) {

        if (!TextUtils.isEmpty(ushahidiUrl)) {
            pattern = Pattern.compile(VALID_URL_PATTERN);
            matcher = pattern.matcher(ushahidiUrl);
            return matcher.matches();
        }

        return false;
    }

    /**
     * process checkins 0 - successful 1 - failed fetching categories 2 - failed
     * fetching checkins 3 - non ushahidi instance 4 - No internet connection
     * 
     * @return int - status
     */
    public static int processCheckins(Context context) {
        String strCheckinsJSON = "";

        if (Util.isConnected(context)) {

            // check if the ushahidi deployment domain has been updated or
            // not
            ApiUtils.updateDomain(context);
            strCheckinsJSON = NetworkServices.getCheckins(Preferences.domain, null, null);

            if (!TextUtils.isEmpty(strCheckinsJSON) && strCheckinsJSON != null) {
                RetrieveCheckinsJSONServices checkinsRetrieveCheckinsJSON = new RetrieveCheckinsJSONServices(
                        strCheckinsJSON);
                mUsers = checkinsRetrieveCheckinsJSON.getCheckinsUsersList();
                mCheckins = checkinsRetrieveCheckinsJSON.getCheckinsList();
            }

            if (mCheckins != null && mUsers != null) {
                // clear existin data
                MainApplication.mDb.deleteAllCheckins();
                MainApplication.mDb.deleteUsers();
                MainApplication.mDb.addUsers(mUsers);
                MainApplication.mDb.addCheckins(mCheckins);
                return 0;

            } else {
                return 1;
            }

        } else {
            return 2;
        }
    }

    /**
     * process reports 0 - successful 1 - failed fetching categories 2 - failed
     * fetching reports 3 - non ushahidi instance 4 - No internet connection
     * 
     * @return int - status
     */
    public static int processReports(Context context) {
        try {
            if (Util.isConnected(context)) {
                // check if the ushahidi deployment domain has been updated or
                // not
                ApiUtils.updateDomain(context);
                if (Categories.getAllCategoriesFromWeb()) {

                    mNewCategories = HandleXml.processCategoriesXml(Preferences.categoriesResponse);
                } else {

                    return 1;
                }

                if (Incidents.getAllIncidentsFromWeb()) {
                    mNewIncidents = HandleXml.processIncidentsXml(Preferences.incidentsResponse);

                } else {

                    return 2;
                }

                if (mNewCategories != null && mNewIncidents != null) {
                    Log.d(CLASS_TAG, "processReport(): categories total: " + mNewCategories.size()
                            + " incidents total:" + mNewIncidents.size());
                    // delete all categories
                    MainApplication.mDb.deleteAllCategories();
                    MainApplication.mDb.deleteAllIncidents();
                    MainApplication.mDb.addCategories(mNewCategories, false);
                    MainApplication.mDb.addIncidents(mNewIncidents, false);
                    return 0;

                } else {
                    return 3;
                }

            } else {
                return 4;
            }
        } catch (IOException e) {
            // means there was a problem getting it
        }
        return 0;
    }

    /**
     * Fetch reports details from the internet
     * 
     * @param context - the activity calling this method.
     */
    public static void fetchReports(final Context context) {
        try {
            if (Util.isConnected(context)) {

                if (Categories.getAllCategoriesFromWeb()) {
                    mNewCategories = HandleXml.processCategoriesXml(Preferences.categoriesResponse);
                }

                if (Incidents.getAllIncidentsFromWeb()) {
                    mNewIncidents = HandleXml.processIncidentsXml(Preferences.incidentsResponse);
                }

                Preferences.totalReportsFetched = mNewCategories.size() + " Categories \n"
                        + mNewIncidents.size() + " Reports";

                MainApplication.mDb.addCategories(mNewCategories, false);
                MainApplication.mDb.addIncidents(mNewIncidents, false);

            } else {
                return;
            }
        } catch (IOException e) {
            // means there was a problem getting it
        }
    }

    /**
     * Extract Ushahidi payload JSON data
     * 
     * @papram json_data - the json data to be formatted.
     * @return int 0 - success, 1 - missing parameter, 2 - invalid parameter, 3
     *         - post failed, 5 - access denied, 6 - access limited, 7 - no
     *         data, 8 - api disabled, 9 - no task found, 10 - json is wrong
     */
    public static int extractPayloadJSON(String json_data) {
        Log.d(CLASS_TAG, "extractPayloadJSON(): " + json_data);
        try {
            jsonObject = new JSONObject(json_data);
            final String errorCode = jsonObject.getJSONObject("error").getString("code");
            return Integer.parseInt(errorCode);
        } catch (JSONException e) {
            Log.e(CLASS_TAG, e.toString());
            return 10;
        }

    }

    /**
     * Check if an ushahidi deployment has changed it's HTTP protocol to HTTPS
     * or not. Then update if it has.
     * 
     * @param context - the calling activity.
     */
    public static void updateDomain(Context context) {

        Preferences.loadSettings(context);

        StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
        uriBuilder.append("/api?task=version");
        uriBuilder.append("&resp=json");

        try {
            response = MainHttpClient.GetURL(uriBuilder.toString());
            if (response != null) {

                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {

                    jsonString = MainHttpClient.GetText(response);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String changedDomain = jsonObject.getJSONObject("payload").getString("domain");

                    if (ApiUtils.validateUshahidiInstance(changedDomain)) {
                        // changed
                        if (!changedDomain.equals(Preferences.domain)) {
                            Preferences.domain = changedDomain;
                            // save changes
                            Preferences.saveSettings(context);
                        }
                    }
                }
            }

        } catch (IOException e) {
            Log.e(CLASS_TAG, e.toString());
        } catch (JSONException e) {
            Log.e(CLASS_TAG, e.toString());
        }
    }
}