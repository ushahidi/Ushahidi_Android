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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.text.TextUtils;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.json.GsonHelper;
import com.ushahidi.android.app.json.UshahidiApiResponse;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.ReportsApiUtils;

/**
 * @author eyedol
 */
public class ReportsHttpClient extends MainHttpClient {

	private static MultipartEntity entity;

	/**
	 * @param context
	 */
	private Context context;

	private ApiUtils apiUtils;

	public ReportsHttpClient(Context context) {
		super(context);
		this.context = context;
		apiUtils = new ApiUtils(context);
	}

	public int getAllReportFromWeb() {
		HttpResponse response;
		String incidents = "";

		// get the right domain to work with
		apiUtils.updateDomain();

		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=incidents");
		uriBuilder.append("&by=all");
		uriBuilder.append("&limit=" + Preferences.totalReports);
		uriBuilder.append("&resp=json");

		try {
			response = GetURL(uriBuilder.toString());

			if (response == null) {
				// Network is down
				return 100;
			}

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {

				incidents = GetText(response);
				ReportsApiUtils reportsApiUtils = new ReportsApiUtils(incidents);
				if (reportsApiUtils.saveReports(context)) {
					return 0; // return success even if geographic fails
				}

				// bad json string
				return 99;
			}
			return 100; // network down?
		} catch (SocketTimeoutException e) {
			log("SocketTimeoutException e", e);
			return 110;
		} catch (ConnectTimeoutException e) {
			log("ConnectTimeoutException", e);
			return 110;
		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);
			// invalid URL
			return 111;
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return 120;
		} catch (IOException e) {
			log("IOException", e);
			// connection refused
			return 112;
		}

	}

	/**
	 * Upload files to server 0 - success, 1 - missing parameter, 2 - invalid
	 * parameter, 3 - post failed, 5 - access denied, 6 - access limited, 7 - no
	 * data, 8 - api disabled, 9 - no task found, 10 - json is wrong
	 */
	public boolean PostFileUpload(String URL, HashMap<String, String> params)
			throws IOException {
		log("PostFileUpload(): upload file to server.");

		apiUtils.updateDomain();
		entity = new MultipartEntity();
		// Dipo Fix
		try {
			// wrap try around because this constructor can throw Error
			final HttpPost httpost = new HttpPost(URL);

			if (params != null) {

				for(Entry<String, String> en: params.entrySet()){
					String key = en.getKey();
					if ( key == null || "".equals(key)){
						continue;
					}
					String val = en.getValue();
					if( !"filename".equals(key)){
						entity.addPart(
								key,
								new StringBody(val, Charset.forName("UTF-8"))
						);
						continue;
					}

					if (!TextUtils.isEmpty(val)) {
						String filenames[] = val.split(",");
						log("filenames "
								+ ImageManager.getPhotoPath(context,
										filenames[0]));
						for (int i = 0; i < filenames.length; i++) {
							if (ImageManager
									.getPhotoPath(context, filenames[i]) != null) {
								File file = new File(ImageManager.getPhotoPath(
										context, filenames[i]));
								if (file.exists()) {
									entity.addPart("incident_photo[]",
											new FileBody(file));
								}
							}
						}
					}
				}

				// NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);
				httpost.setEntity(entity);

				HttpResponse response = httpClient.execute(httpost);
				Preferences.httpRunning = false;

				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
					UshahidiApiResponse resp = GsonHelper.fromStream(respEntity.getContent(), UshahidiApiResponse.class);				
					return resp.getErrorCode() == 0;
				}
			}

		} catch (MalformedURLException ex) {
			log("PostFileUpload(): MalformedURLException", ex);

			return false;
			// fall through and return false
		} catch (IllegalArgumentException ex) {
			log("IllegalArgumentException", ex);
			// invalid URI
			return false;
		} catch (ConnectTimeoutException ex) {
			//connection timeout
			log("ConnectionTimeoutException");
			return false;
		}catch(SocketTimeoutException ex) {
			log("SocketTimeoutException");
		} catch (IOException e) {
			log("IOException", e);
			// timeout
			return false;
		}
		return false;
	}
}
