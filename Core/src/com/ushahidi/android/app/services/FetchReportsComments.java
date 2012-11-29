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
import android.os.Bundle;

import com.ushahidi.android.app.net.CommentHttpClient;

/**
 * @author eyedol
 * 
 */
public class FetchReportsComments extends SyncServices {

	private static String CLASS_TAG = FetchReportsComments.class
			.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 100;

	public FetchReportsComments() {
		super(CLASS_TAG);
		statusIntent = new Intent(FETCH_REPORT_COMMENTS_SERVICES_ACTION);
	}

	private void fetchReportComments(Bundle bundle) {
		if (bundle != null) {
			int reportid = bundle.getInt("reportid");
			status = new CommentHttpClient().getReportComments(reportid);
		}
	}

	@Override
	protected void executeTask(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			fetchReportComments(bundle);
		}
		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}
