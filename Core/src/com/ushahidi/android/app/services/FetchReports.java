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

import java.util.List;

import android.content.Intent;

import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.CustomFormApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.CustomFormMetaEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListCommentModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.ImageManager;
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

		// delete comment data
		new ListCommentModel().deleteComments();

		// delete fetched photos
		ImageManager.deleteImages(this);

		// delete pending photos
		ImageManager.deletePendingImages(this);

		// delete Open GeoSMS reports
		Database.mOpenGeoSmsDao.deleteReports();
		
		// delete custom form definitions 
		Database.mCustomFormDao.deleteAllCustomForms();
		Database.mCustomFormMetaDao.deleteAllCustomFormMetas();
	}

	@Override
	protected void executeTask(Intent intent) {

		new Util().log("executeTask() executing this task");
		clearCachedData();
		ApiUtils.updateDomain(this);
		// fetch categories
		new CategoriesApi().getCategoriesList();
		
		// fetch custom form definitions 
		new CustomFormApi().fetchCustomFormList();
		
		List<CustomFormMetaEntity> cfm = Database.mCustomFormMetaDao.fetchAllCustomFormMetas();
		for(CustomFormMetaEntity cf : cfm){
			System.out.println(cf);
		}
		// fetch reports
		boolean reportFetched = new ReportsApi().saveReports(this);
		
		if(reportFetched){//fetch also customforms values
			List<ReportEntity> reports = Database.mReportDao.fetchAllReports();
			new CustomFormApi().fetchReportCustomFormList(reports);
		}
			
		//TODO adding CONSTANT status values	
		status = reportFetched ? 0 : 99;
		
		
		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}
