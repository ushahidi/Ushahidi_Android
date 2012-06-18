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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.CommentApiUtils;

/**
 * @author eyedol
 */
public class CommentHttpClient extends MainHttpClient {

	private static MultipartEntity entity;

	/**
	 * @param context
	 */
	private Context context;

	private ApiUtils apiUtils;

	public CommentHttpClient(Context context) {
		super(context);
		this.context = context;
		apiUtils = new ApiUtils(context);
	}

	public int getReportComments(int reportid) {
		HttpResponse response;
		String comments = "";

		// get the right domain to work with
		apiUtils.updateDomain();

		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=comments");
		uriBuilder.append("&by=reportid");
		uriBuilder.append("&id=" + reportid);
		uriBuilder.append("&resp=json");

		try {
			response = GetURL(uriBuilder.toString());

			if (response == null) {
				// Network is down
				return 100;
			}

			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				comments = GetText(response);

				CommentApiUtils commentApiUtils = new CommentApiUtils(comments);
				if (commentApiUtils.saveComments(context)) {
					return 0; // return success
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
	
	public int getCheckinComments(int checkinid) {
		HttpResponse response;
		String comments = "";

		// get the right domain to work with
		apiUtils.updateDomain();

		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=comments");
		uriBuilder.append("&by=checkinid");
		uriBuilder.append("&id=" + checkinid);
		uriBuilder.append("&resp=json");

		try {
			response = GetURL(uriBuilder.toString());

			if (response == null) {
				// Network is down
				return 100;
			}

			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				comments = GetText(response);

				CommentApiUtils commentApiUtils = new CommentApiUtils(comments);
				if (commentApiUtils.saveComments(context)) {
					return 0; // return success
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

				entity.addPart("task", new StringBody("comments"));
				entity.addPart("action", new StringBody("add"));
				entity.addPart("comment_author",
						new StringBody(params.get("comment_author")));
				entity.addPart("comment_description",
						new StringBody(params.get("comment_description"),
								Charset.forName("UTF-8")));
				entity.addPart(
						"comment_email",
						new StringBody(params.get("comment_email"), Charset
								.forName("UTF-8")));
				if (params.get("incident_id") != null)
					entity.addPart("incident_id",
							new StringBody(params.get("incident_id")));

				if (params.get("checkin_id") != null)
					entity.addPart(
							"checkin_id",
							new StringBody(params.get("checkin_id"), Charset
									.forName("UTF-8")));
				// NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);
				httpost.setEntity(entity);

				HttpResponse response = httpClient.execute(httpost);
				Preferences.httpRunning = false;

				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
					InputStream serverInput = respEntity.getContent();
					if (serverInput != null) {
						// TODO:: get the status confirmation code to work
						int status = ApiUtils
								.extractPayloadJSON(GetText(serverInput));

						return true;
					}

					return false;
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
		} catch (IOException e) {
			log("IOException", e);
			// timeout
			return false;
		}
		return false;
	}
}
