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

/**
 * @author eyedol
 * 
 */
public class FetchCheckinsComments extends SyncServices {

	private static String CLASS_TAG = FetchCheckinsComments.class
			.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 100;

	public FetchCheckinsComments() {
		super(CLASS_TAG);
		statusIntent = new Intent(FETCH_CHECKIN_COMMENTS_SERVICES_ACTION);
	}


	@Override
	protected void executeTask(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			//fetchCheckinComments(bundle);
		}
		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}
