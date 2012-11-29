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
package com.ushahidi.android.app.net;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.java.sdk.UshahidiApi;
import com.ushahidi.java.sdk.net.PasswordAuthentication;
import com.ushahidi.java.sdk.net.UshahidiHttpClient;

/**
 * @author eyedol
 * 
 */
public class UshahidiClient {

	public static UshahidiApi ushahidiApi;

	public static int connectionTimeout;

	public static int socketTimeout;

	static {
		ushahidiApi = new UshahidiApi(Preferences.domain);
		ushahidiApi.factory.client = new UshahidiHttpClient();
		ushahidiApi.factory.client
				.setAuthentication(new PasswordAuthentication("admin", "admin"));
		ushahidiApi.factory.client.setConnectionTimeout(connectionTimeout);
		ushahidiApi.factory.client.setSocketTimeout(socketTimeout);
	}
}
