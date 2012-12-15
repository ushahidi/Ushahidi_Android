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

import com.ushahidi.android.app.Preferences;
import com.ushahidi.java.sdk.api.tasks.UshahidiApiTaskFactory;
import com.ushahidi.java.sdk.net.PasswordAuthentication;
import com.ushahidi.java.sdk.net.UshahidiHttpClient;

/**
 * The gateway to the Ushahidi API
 * 
 * @author eyedol
 * 
 */
public abstract class Ushahidi {

	protected UshahidiApiTaskFactory factory;

	public int connectionTimeout = 30000;

	public int socketTimeout = 30000;

	public Ushahidi() {
		factory = UshahidiApiTaskFactory.newInstance(Preferences.domain);
		factory.client = new UshahidiHttpClient();
		factory.client.setAuthentication(new PasswordAuthentication("admin",
				"admin"));
		factory.client.setConnectionTimeout(connectionTimeout);
		factory.client.setSocketTimeout(socketTimeout);
	}
}
