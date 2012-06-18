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

import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.net.CommentHttpClient;

/**
 * @author eyedol
 * 
 */
public class UploadComments extends SyncServices {

	private static String CLASS_TAG = UploadComments.class.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 113;

	public UploadComments() {
		super(CLASS_TAG);
		statusIntent = new Intent(UPLOAD_COMMENT_SERVICES_ACTION);
	}

	private int addComments(Bundle bundle) {

		final StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
		urlBuilder.append("/api");
		final HashMap<String, String> mParams = new HashMap<String, String>();

		if (bundle != null) {
			if (bundle.getInt("report_id") > 0) {
				
				mParams.put("incident_id",
						String.valueOf(bundle.getInt("report_id")));
			}

			if (bundle.getInt("checkin_id") > 0) {
				mParams.put("checkin_id",
						String.valueOf(bundle.getInt("checkin_id")));
			}
			
			mParams.put("comment_author", bundle.getString("comment_author"));
			mParams.put("comment_description",
					bundle.getString("comment_description"));
			mParams.put("comment_email", bundle.getString("comment_email"));

			try {
				if (new CommentHttpClient(this).PostFileUpload(
						urlBuilder.toString(), mParams)) {

					return 0;
				}
				return 1;
			} catch (IOException e) {
				return 2;
			}
		}
		return 1;
	}

	@Override
	protected void executeTask(Intent intent) {

		Log.i(CLASS_TAG, "executeTask() executing this task");
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			status = addComments(bundle);
			statusIntent.putExtra("status", status);
			sendBroadcast(statusIntent);
		}

	}
}
