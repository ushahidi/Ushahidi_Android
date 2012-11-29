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
import android.util.Log;

import com.ushahidi.android.app.net.CommentHttpClient;
import com.ushahidi.java.sdk.api.Comment;
import com.ushahidi.java.sdk.api.CommentFields;
import com.ushahidi.java.sdk.api.json.Response;

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

		CommentFields comment = new CommentFields();
		CommentHttpClient commentHttpClient = new CommentHttpClient();
		if (bundle != null) {
			Comment c = new Comment();
			c.setAuthor(bundle.getString("comment_author"));
			c.setDescription(bundle.getString("comment_description"));
			c.setReportId(bundle.getInt("report_id"));
			comment.fill(c);
			comment.setEmail(bundle.getString("comment_email"));
			Response response = commentHttpClient.submitComment(comment);
			if (response.getErrorCode() == 0) {
				commentHttpClient.getReportComments(bundle.getInt("report_id"));
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
