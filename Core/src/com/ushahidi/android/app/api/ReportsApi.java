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
package com.ushahidi.android.app.api;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.ReportCategory;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.UshahidiException;
import com.ushahidi.java.sdk.api.Category;
import com.ushahidi.java.sdk.api.Incidents;
import com.ushahidi.java.sdk.api.ReportFields;
import com.ushahidi.java.sdk.api.json.Response;
import com.ushahidi.java.sdk.api.tasks.IncidentsTask;
import com.ushahidi.java.sdk.api.tasks.ReportTask;
import com.ushahidi.java.sdk.net.content.Body;

/**
 * Handles Reports API task
 * 
 */
public class ReportsApi extends UshahidiApi {

	private IncidentsTask task;
	private ReportTask reportTask;
	private List<ReportEntity> reports;
	private boolean processingResult;

	public ReportsApi() {
		task = factory.createIncidentsTask();
		task.limit = Integer.valueOf(Preferences.totalReports);
		processingResult = true;
		reports = new ArrayList<ReportEntity>();
	}

	/**
	 * Fetch reports via the Ushahidi API
	 * 
	 * @param context
	 *            The calling activity
	 * 
	 * @return The list of reports
	 */
	private List<ReportEntity> getReportList(Context context) {
		log("Save report");
		if (processingResult) {
			try {
				List<Incidents> incidents = task.all();
				if (incidents != null && incidents.size() > 0) {
					for (Incidents i : incidents) {
						ReportEntity report = new ReportEntity();
						report.setIncident(i.incident);
						reports.add(report);
						// save categories
						if ((i.getCategories() != null)
								&& (i.getCategories().size() > 0)) {
							for (Category c : i.getCategories()) {
								saveCategories(c.getId(), i.incident.getId());
							}
						}

						// save media
						if ((i.getMedia() != null) && (!i.getMedia().isEmpty())) {
							for (com.ushahidi.java.sdk.api.Media m : i
									.getMedia()) {

								// find photos, it's type is 1
								if (m != null) {
									if (m.getType() == 1) {
										final String fileName = Util
												.getDateTime() + ".jpg";

										// save details of photo to database
										saveMedia(m.getId(),
												i.incident.getId(),
												m.getType(), fileName);

										// save photo to a file
										if (m.getLink().startsWith("http")) {
											saveImages(m.getLink(), fileName,
													context);
										} else {
											final String link = Preferences.domain
													+ "/media/uploads/"
													+ m.getLink();

											saveImages(link, fileName, context);
										}

									} else {
										// other media type to database
										saveMedia(m.getId(),
												(int) i.incident.getId(),
												m.getType(), m.getLink());
									}
								}
							}
						}
					}
				}
			} catch (UshahidiException e) {
				log("UshahidiException", e);
				processingResult = false;
			}

		}
		return reports;
	}

	/**
	 * Save fetched reports to the database
	 * 
	 * @param context
	 *            The calling activity
	 * 
	 * @return boolean
	 */
	public boolean saveReports(Context context) {
		List<ReportEntity> reports = getReportList(context);

		if (reports != null)
			return Database.mReportDao.addReport(reports);
		return false;
	}

	public Response submitReport(ReportFields report) {
		reportTask = factory.createReportTask();

		try {
			return reportTask.submit(report);
		} catch (UshahidiException e) {
			e.getMessage();
		}

		return null;
	}

	public boolean upload(String url, Body body) {
		final String response = task.getClient().sendMultipartPostRequest(url,
				body);
		return response != null ? true : false;
	}

	/**
	 * Save details of categories to the database
	 * 
	 * @param categoryId
	 *            The ID of the category
	 * @param reportId
	 *            The ID of the report
	 * 
	 * @return void
	 */
	private void saveCategories(int categoryId, int reportId) {

		ReportCategory reportCategory = new ReportCategory();
		reportCategory.setCategoryId(categoryId);
		reportCategory.setReportId(reportId);
		List<ReportCategory> reportCategories = new ArrayList<ReportCategory>();
		reportCategories.add(reportCategory);

		// save new data
		Database.mReportCategoryDao.addReportCategories(reportCategories);
	}

	/**
	 * Save fetched media to the database
	 * 
	 * @param mediaId
	 *            The ID of the media
	 * @param reportId
	 *            The report ID associated with the media
	 * @param type
	 *            The media type. 1 for image, 2 for news link, 3 for video link
	 * @param link
	 *            The URL of the media
	 * 
	 * @return void
	 */
	private void saveMedia(int mediaId, int reportId, int type, String link) {
		log("downloading... " + link + " ReportId: " + reportId);
		MediaEntity media = new MediaEntity();
		media.setMediaId(mediaId);
		media.setReportId(reportId);
		media.setType(type);
		media.setLink(link);
		List<MediaEntity> sMedia = new ArrayList<MediaEntity>();
		sMedia.add(media);

		// save new data
		Database.mMediaDao.addMedia(sMedia);
	}

	/**
	 * Download image from the web
	 * 
	 * @param linkUrl
	 *            The URL of the image to be downloaded
	 * @param fileName
	 *            The file name to give to the donwloaded image
	 * @param context
	 *            The calling activity
	 * 
	 * @return void
	 */
	private void saveImages(String linkUrl, String fileName, Context context) {

		if (!TextUtils.isEmpty(linkUrl)) {
			ImageManager.downloadImage(linkUrl, fileName, context);
		}
	}

}
