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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.CategoriesApiUtils;

/**
 * @author eyedol
 */
public class CategoriesHttpClient extends MainHttpClient {

	private ApiUtils apiUtils;

	/**
	 * @param context
	 */
	public CategoriesHttpClient(Context context) {
		super(context);
		apiUtils = new ApiUtils(context);
	}

	public int getCategoriesFromWeb() {
		HttpResponse response;
		String categoriesResponse = "";

		// get the right domain to work with
		apiUtils.updateDomain();
		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=categories");
		uriBuilder.append("&resp=json");
		try {
			response = GetURL(uriBuilder.toString());

			if (response == null) {
				// Network is down
				return 100;
			}

			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				categoriesResponse = GetText(response);
				log("categories : "+categoriesResponse);
				CategoriesApiUtils categoriesApiUtils = new CategoriesApiUtils(
						categoriesResponse);
				if (categoriesApiUtils.getCategoriesList()) {
					Preferences.categoriesResponse = categoriesResponse;
					return 0;
				}

				// bad json string
				return 99;
			} else {
				// network down?
				return 100;
			}
		} catch (SocketTimeoutException e) {
			log("SocketTimeoutException e", e);
			return 110;
		} catch (ConnectTimeoutException e) {
			log("ConnectTimeoutException", e);
			return 110;
		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);
			// connection refused
			return 111;
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return 120;
		} catch (IOException e) {
			log("IOException", e);
			// There is no default deployment
			return 112;
		}

	}
}
