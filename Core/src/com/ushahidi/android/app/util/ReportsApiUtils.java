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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.entities.Report;

/**
 * Handle processing of the JSON string as returned from the HTTP request. Main
 * deals with reports related HTTP request.
 * 
 * @author eyedol
 */
public class ReportsApiUtils {

    private JSONObject jsonObject;

    private boolean processingResult;

    public ReportsApiUtils(String jsonString) {
        processingResult = true;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            log("JSONException", e);
            processingResult = false;
        }
    }

    private JSONObject getReportPayloadObj() {
        try {
            return jsonObject.getJSONObject("payload");
        } catch (JSONException e) {
            log("JSONException", e);
            return new JSONObject();
        }
    }

    private JSONArray getReportsArr() {
        try {
            return getReportPayloadObj().getJSONArray("incident");
        } catch (JSONException e) {
            log("JSONException", e);
            return new JSONArray();
        }
    }

    private JSONArray getCategoriesArr() {
        try {
            return getReportPayloadObj().getJSONArray("categories");
        } catch (JSONException e) {
            log("JSONException", e);
            return new JSONArray();
        }
    }

    private JSONArray getMediaArr() {
        try {
            return getReportPayloadObj().getJSONArray("media");
        } catch (JSONException e) {
            log("JSONException", e);
            return new JSONArray();
        }
    }

    public List<Report> getReportList() {
        if (processingResult) {
            List<Report> listReport = new ArrayList<Report>();
            JSONArray reportsArr = getReportsArr();
            long id = 0;
            if (reportsArr != null) {
                for (int i = 0; i < reportsArr.length(); i++) {
                    Report report = new Report();
                    try {
                        id = Long.valueOf(reportsArr.getJSONObject(i).getString("incidentid"));
                        report.setDbId(id);
                        report.setTitle(reportsArr.getJSONObject(i).getString("incidenttitle"));
                        report.setDescription(reportsArr.getJSONObject(i).getString(
                                "incidentdescription"));
                        report.setReportDate(reportsArr.getJSONObject(i).getString("incidentdate"));
                        report.setMode(reportsArr.getJSONObject(i).getString("incidentmode"));
                        report.setVerified(reportsArr.getJSONObject(i)
                                .getString("incidentverified"));
                        report.setLocationName(reportsArr.getJSONObject(i)
                                .getString("locationname"));
                        report.setLatitude(reportsArr.getJSONObject(i)
                                .getString("locationlatitude"));
                        report.setLongitude(reportsArr.getJSONObject(i).getString(
                                "locationlongitude"));
                        // retrieve categories
                        JSONArray catsArr = getCategoriesArr();
                        for (int j = 0; j < catsArr.length(); j++) {
                            try {
                                saveCategories(catsArr.getJSONObject(i).getJSONObject("category")
                                        .getInt("id"), (int)id);
                            } catch (JSONException ex) {
                                log("JSONException", ex);
                            }
                        }
                        // retrieve media.
                        JSONArray mediaArr = getMediaArr();
                        for (int w = 0; w < mediaArr.length(); w++) {
                            try {
                                saveMedia(mediaArr.getJSONObject(w).getInt("id"), (int)id, mediaArr
                                        .getJSONObject(w).getInt("type"), mediaArr.getJSONObject(w)
                                        .getString("link"));
                                
                                if (mediaArr.getJSONObject(w).getInt("type") == 1) {
                                    saveImages(mediaArr.getJSONObject(w).getString("link_url"));
                                }
                            } catch (JSONException exc) {
                                log("JSONException", exc);
                            }
                        }
                    } catch (JSONException e) {
                        log("JSONException", e);
                        processingResult = false;
                        return null;
                    }
                    listReport.add(report);
                }
                return listReport;
            }

        }
        return null;
    }

    private void saveCategories(int catId, int reportId) {
        // TODO save this into a database
    }

    private void saveMedia(int mediaId, int reportId, int type, String link) {
        // TODO save this into a database
    }

    private void saveImages(String linkUrl) {
        // TODO save images to SD card
    }

    private void log(String message) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), message);
    }

    private void log(String format, Object... args) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), String.format(format, args));
    }

    private void log(String message, Exception ex) {
        if (MainApplication.LOGGING_MODE)
            Log.e(getClass().getName(), message, ex);
    }
}
