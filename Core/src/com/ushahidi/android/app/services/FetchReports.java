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
package com.ushahidi.android.app.services;

import android.content.Intent;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ListCommentModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.net.CheckinHttpClient;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class FetchReports extends SyncServices {

	private static String CLASS_TAG = FetchReports.class.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 113;

	public FetchReports() {
		super(CLASS_TAG);
		statusIntent = new Intent(SYNC_SERVICES_ACTION);
	}

	/**
	 * Clear saved reports
	 */
	public void clearCachedData() {
		// delete reports
		new ListReportModel().deleteReport();

		// delete checkins data
		new ListCheckinModel().deleteCheckin();

		// delete comment data
		new ListCommentModel().deleteComments();

		// delete fetched photos
		ImageManager.deleteImages(this);

		// delete pending photos
		ImageManager.deletePendingImages(this);

		// delete Open GeoSMS reports
		Database.mOpenGeoSmsDao.deleteReports();
	}

	@Override
	protected void executeTask(Intent intent) {

		new Util().log("executeTask() executing this task");
		clearCachedData();
		if (!new ApiUtils(this).isCheckinEnabled()) {

			// fetch categories
			new CategoriesApi().getCategoriesList();
			// fetch reportsx
			status = new ReportsApi().saveReports(this) ? 0 : 99; 

		} else {
			status = new CheckinHttpClient(this).getAllCheckinFromWeb();
		}

		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}
