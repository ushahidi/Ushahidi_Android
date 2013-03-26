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

import android.util.Log;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.java.sdk.api.tasks.UshahidiApiTaskFactory;
import com.ushahidi.java.sdk.net.UshahidiHttpClient;

/**
 * Initializes the Ushahidi API
 * 
 * @author eyedol
 * 
 */
public abstract class UshahidiApi {

	protected UshahidiApiTaskFactory factory;

	public int connectionTimeout = 30000;

	public int socketTimeout = 30000;

	public UshahidiApi() {
		factory = UshahidiApiTaskFactory.newInstance(Preferences.domain);
		factory.client = new UshahidiHttpClient();
		factory.client.setConnectionTimeout(connectionTimeout);
		factory.client.setSocketTimeout(socketTimeout);
	}

	protected void log(String message) {
		if (MainApplication.LOGGING_MODE)
			Log.i(getClass().getName(), message);
	}

	protected void log(String message, Exception ex) {
		if (MainApplication.LOGGING_MODE)
			Log.e(getClass().getName(), message, ex);
	}
}
