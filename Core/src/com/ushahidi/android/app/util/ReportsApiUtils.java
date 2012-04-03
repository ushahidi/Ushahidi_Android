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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Report;
import com.ushahidi.android.app.entities.ReportCategory;

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
			return getReportPayloadObj().getJSONArray("incidents");
		} catch (JSONException e) {
			log("JSONException", e);
			return new JSONArray();
		}
	}

	public List<Report> getReportList(Context context) {
		log("Save report");
		if (processingResult) {
			List<Report> listReport = new ArrayList<Report>();
			JSONArray reportsArr = getReportsArr();
			int id = 0;
			if (reportsArr != null) {
				for (int i = 0; i < reportsArr.length(); i++) {
					Report report = new Report();
					try {
						id = reportsArr.getJSONObject(i)
								.getJSONObject("incident").getInt("incidentid");
						report.setDbId(id);
						report.setTitle(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("incidenttitle"));
						report.setDescription(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("incidentdescription"));
						report.setReportDate(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("incidentdate"));
						report.setMode(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("incidentmode"));
						report.setVerified(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("incidentverified"));
						report.setLocationName(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("locationname"));
						report.setLatitude(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("locationlatitude"));
						report.setLongitude(reportsArr.getJSONObject(i)
								.getJSONObject("incident")
								.getString("locationlongitude"));

						// retrieve categories
						JSONArray catsArr = reportsArr.getJSONObject(i)
								.getJSONArray("categories");
						for (int j = 0; j < catsArr.length(); j++) {
							try {

								saveCategories(
										catsArr.getJSONObject(j)
												.getJSONObject("category")
												.getInt("id"), (int) id);
							} catch (JSONException ex) {
								log("JSONException", ex);
							}
						}

						// retrieve media.
						if (!reportsArr.getJSONObject(i).isNull("media")) {
							JSONArray mediaArr = reportsArr.getJSONObject(i)
									.getJSONArray("media");
							for (int w = 0; w < mediaArr.length(); w++) {
								try {
									if (!mediaArr.getJSONObject(w).isNull("id")) {

										//save media
										if (mediaArr.getJSONObject(w).getInt(
												"type") == 1
												&& (!mediaArr.getJSONObject(w)
														.isNull("link"))) {

											final String fileName = getDateTime()
													+ "jpg";
											// save media
											saveMedia(mediaArr.getJSONObject(w)
													.getInt("id"), (int) id,
													mediaArr.getJSONObject(w)
															.getInt("type"),
													fileName);
											if (mediaArr.getJSONObject(w)
													.getString("link")
													.startsWith("http")) {
												saveImages(mediaArr
														.getJSONObject(w)
														.getString("link"),
														fileName, context);
											} else {
												final String link = Preferences.domain
														+ "/media/uploads/"
														+ mediaArr
																.getJSONObject(
																		w)
																.getString(
																		"link");

												saveImages(link, fileName,context);
											}

										} else {
											// save media
											saveMedia(mediaArr.getJSONObject(w)
													.getInt("id"), (int) id,
													mediaArr.getJSONObject(w)
															.getInt("type"),
													mediaArr.getJSONObject(w)
															.getString("link"));
										}
									}
								} catch (JSONException exc) {
									log("JSONException", exc);
								}
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

	// Save report into databaset
	public boolean saveReports(Context context) {
		List<Report> reports = getReportList(context);

		if (reports != null) {

			return Database.mReportDao.addReport(reports);
		}

		return false;
	}

	private void saveCategories(int categoryId, int reportId) {

		ReportCategory reportCategory = new ReportCategory();
		reportCategory.setCategoryId(categoryId);
		reportCategory.setReportId(reportId);
		List<ReportCategory> reportCategories = new ArrayList<ReportCategory>();
		reportCategories.add(reportCategory);

		// save new data
		Database.mReportCategoryDao.addReportCategories(reportCategories);
	}

	private void saveMedia(int mediaId, int reportId, int type, String link) {
		log("Save Images: " + getDateTime());
		Media media = new Media();
		media.setDbId(mediaId);
		media.setReportId(reportId);
		media.setType(type);
		media.setLink(link);
		List<Media> sMedia = new ArrayList<Media>();
		sMedia.add(media);

		// save new data
		Database.mMediaDao.addMedia(sMedia);
	}

	private void saveImages(String linkUrl, String fileName, Context context) {

		if (!TextUtils.isEmpty(linkUrl)) {
			ImageManager.downloadImage(linkUrl, fileName, context);
		}
	}

	private String getDateTime() {
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(new Date());
	}

	private void log(String message) {
		if (MainApplication.LOGGING_MODE)
			Log.i(getClass().getName(), message);
	}

	private void log(String message, Exception ex) {
		if (MainApplication.LOGGING_MODE)
			Log.e(getClass().getName(), message, ex);
	}
}
