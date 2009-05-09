/* 
 * Copyright (C) 2008 Torgny Bjers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.addhen.ushahidi.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * Handles connection to the Twitter REST API.
 * 
 * @author torgny.bjers
 */
public class Connection 
{

	private static final String BASE_URL = "http://192.168.10.61/ushahidi/api";

	private static final String CATEGORIES_URL = BASE_URL + "?task=categories";
	private static final String USER_AGENT = "Ushahidi/1.0";
	private static final String TAG = "UshahidiConnection";

	private static final Integer DEFAULT_GET_REQUEST_TIMEOUT = 10000;
	private static final Integer DEFAULT_POST_REQUEST_TIMEOUT = 15000;

	private long mlastId;
	
	public Connection() {
		
	}

	/**
	 * Creates a new Connection instance, specifying a last ID.
	 * @param lastId
	 */
	public Connection(long lastId) {
		mlastId = lastId;
	}

	/**
	 * Get ushahidi's categories.
	 * 
	 * 
	 * @return JSONArray
	 * @throws ConnectionException 
	 * @throws ConnectionAuthenticationException 
	 * @throws ConnectionUnavailableException 
	 * @throws SocketTimeoutException 
	 */
	public JSONArray getCategories() throws ConnectionException, ConnectionUnavailableException, SocketTimeoutException {
		String url = CATEGORIES_URL;
		
		JSONArray jArr = null;
		String request = getRequest(url);
		try {
			jArr = new JSONArray(request);
		} catch (JSONException e) {
			try {
				JSONObject jObj = new JSONObject(request);
				String error = jObj.optString("error");
				/*if ("Could not authenticate you.".equals(error)) {
					throw new ConnectionAuthenticationException(error);
				}*/
			} catch (JSONException e1) {
				throw new ConnectionException(e);
			}
		}
		return jArr;
	}

	/**
	 * Update user status by posting to the Twitter REST API.
	 * 
	 * Updates the authenticating user's status. Requires the status parameter
	 * specified. Request must be a POST. A status update with text identical to
	 * the authenticating user's current status will be ignored.
	 * 
	 * @param message
	 * @return JSONObject
	 * @throws UnsupportedEncodingException
	 * @throws ConnectionException 
	 * @throws ConnectionAuthenticationException 
	 * @throws ConnectionUnavailableException 
	 * @throws SocketTimeoutException 
	 *
	public JSONObject updateStatus(String message, long inReplyToId)
			throws UnsupportedEncodingException, ConnectionException, ConnectionAuthenticationException, ConnectionUnavailableException, SocketTimeoutException {
		String url = UPDATE_STATUS_URL;
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair("status", message));
		formParams.add(new BasicNameValuePair("source", SOURCE_PARAMETER));
		if (inReplyToId > 0) {
			formParams.add(new BasicNameValuePair("in_reply_to_status_id", String.valueOf(inReplyToId)));
		}
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(postRequest(url, new UrlEncodedFormEntity(formParams, HTTP.UTF_8)));
			String error = jObj.optString("error");
			if ("Could not authenticate you.".equals(error)) {
				throw new ConnectionAuthenticationException(error);
			}
		} catch (JSONException e) {
			throw new ConnectionException(e);
		}
		return jObj;
	}*/

	/**
	 * Execute a GET request against the Ushahidi REST API.
	 * 
	 * @param url
	 * @return String
	 * @throws ConnectionException 
	 * @throws ConnectionUnavailableException 
	 * @throws ConnectionAuthenticationException 
	 * @throws SocketTimeoutException 
	 */
	protected String getRequest(String url) throws ConnectionException, ConnectionUnavailableException, SocketTimeoutException {
		return getRequest(url, new DefaultHttpClient(new BasicHttpParams()));
	}

	/**
	 * Execute a GET request against the Twitter REST API.
	 * 
	 * @param url
	 * @param client
	 * @return String
	 * @throws ConnectionException
	 * @throws ConnectionUnavailableException 
	 * @throws ConnectionAuthenticationException 
	 * @throws SocketTimeoutException 
	 */
	protected String getRequest(String url, HttpClient client) throws ConnectionException, ConnectionUnavailableException, SocketTimeoutException {
		String result = null;
		int statusCode = 0;
		HttpGet getMethod = new HttpGet(url);
		try {
			getMethod.setHeader("User-Agent", USER_AGENT);
			client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, DEFAULT_GET_REQUEST_TIMEOUT);
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, DEFAULT_GET_REQUEST_TIMEOUT);
			HttpResponse httpResponse = client.execute(getMethod);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = retrieveInputStream(httpResponse.getEntity());
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			throw new ConnectionException(e);
		} finally {
			getMethod.abort();
		}
		parseStatusCode(statusCode, url);
		return result;
	}

	/**
	 * Execute a POST request against the Ushahidi REST API.
	 * 
	 * @param url
	 * @return String
	 * @throws ConnectionException 
	 * @throws ConnectionUnavailableException 
	 * @throws ConnectionAuthenticationException 
	 * @throws SocketTimeoutException 
	 */
	protected String postRequest(String url) throws ConnectionException,ConnectionUnavailableException, SocketTimeoutException {
		return postRequest(url, new DefaultHttpClient(new BasicHttpParams()), null);
	}

	/**
	 * Execute a POST request against the Ushahidi REST API.
	 * 
	 * @param url
	 * @return String
	 * @throws ConnectionException 
	 * @throws ConnectionUnavailableException 
	 * @throws ConnectionAuthenticationException 
	 * @throws SocketTimeoutException 
	 */
	protected String postRequest(String url, UrlEncodedFormEntity formParams) throws ConnectionException,ConnectionUnavailableException, SocketTimeoutException {
		return postRequest(url, new DefaultHttpClient(new BasicHttpParams()), formParams);
	}

	/**
	 * Execute a POST request against the Ushahidi REST API.
	 * 
	 * @param url
	 * @param client
	 * @return String
	 * @throws ConnectionException
	 * @throws ConnectionUnavailableException 
	 * @throws ConnectionAuthenticationException 
	 */
	protected String postRequest(String url, HttpClient client, UrlEncodedFormEntity formParams)
			throws ConnectionException, ConnectionUnavailableException, SocketTimeoutException {
		String result = null;
		int statusCode = 0;
		HttpPost postMethod = new HttpPost(url);
		try {
			postMethod.setHeader("User-Agent", USER_AGENT);
			if (formParams != null) {
				postMethod.setEntity(formParams);
			}
			client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, DEFAULT_POST_REQUEST_TIMEOUT);
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, DEFAULT_POST_REQUEST_TIMEOUT);
			HttpResponse httpResponse = client.execute(postMethod);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			result = retrieveInputStream(httpResponse.getEntity());
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			throw new ConnectionException(e);
		} finally {
			postMethod.abort();
		}
		parseStatusCode(statusCode, url);
		return result;
	}

	/**
	 * Retrieve the input stream from the HTTP connection.
	 * 
	 * @param httpEntity
	 * @return String
	 */
	protected String retrieveInputStream(HttpEntity httpEntity) {
		int length = (int) httpEntity.getContentLength();
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return stringBuffer.toString();
	}

	protected void parseStatusCode(int code, String path) throws ConnectionException, ConnectionUnavailableException {
		switch (code) {
		case 200:
		case 304:
			break;
		case 401:
			break;
		case 400:
		case 403:
		case 404:
			throw new ConnectionException(String.valueOf(code));
		case 500:
		case 502:
		case 503:
			throw new ConnectionUnavailableException(String.valueOf(code));
		}
	}
}
