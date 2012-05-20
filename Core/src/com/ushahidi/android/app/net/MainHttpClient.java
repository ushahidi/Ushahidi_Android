/** 
 ** Copyright (c) 2011 Ushahidi Inc
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;

public class MainHttpClient {

	protected static DefaultHttpClient httpClient;

	private HttpParams httpParameters;

	private static final String CLASS_TAG = MainHttpClient.class
			.getSimpleName();

	private int timeoutConnection = 60000;

	private int timeoutSocket = 60000;

	private static final int IO_BUFFER_SIZE = 512;

	private Context context;

	public MainHttpClient(Context context) {
		this.context = context;
		httpParameters = new BasicHttpParams();
		httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
		httpParameters.setParameter(
				ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
				new ConnPerRouteBean(1));

		httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
				false);
		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParameters, "utf8");

		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);

		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		SchemeRegistry schemeRegistry = new SchemeRegistry();

		// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// https scheme
		try {
			schemeRegistry.register(new Scheme("https",
					new TrustedSocketFactory(Preferences.domain, false), 443));
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				httpParameters, schemeRegistry), httpParameters);

	}

	/*
	 * private static String getCredentials(String userName, String password){
	 * return HttpBase64.encodeBytes((userName + ":" + password).getBytes()); }
	 */

	public HttpResponse GetURL(String URL) throws IOException {
		Preferences.httpRunning = true;

		// only do the connection where there is internet.
		if (isConnected()) {

			try {
				// wrap try around because this constructor can throw Error
				final HttpGet httpget = new HttpGet(URL);
				httpget.addHeader("User-Agent", "Ushahidi-Android/1.0)");

				// Post, check and show the result (not really spectacular, but
				// works):
				HttpResponse response = httpClient.execute(httpget);

				return response;

			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

	public HttpResponse PostURL(String URL, List<NameValuePair> data,
			String Referer) throws IOException {

		if (isConnected()) {
			// Dipo Fix
			try {
				// wrap try around because this constructor can throw Error
				final HttpPost httpost = new HttpPost(URL);
				// org.apache.http.client.methods.
				if (Referer.length() > 0) {
					httpost.addHeader("Referer", Referer);
				}
				if (data != null) {
					try {
						// NEED THIS NOW TO FIX ERROR 417
						httpost.getParams().setBooleanParameter(
								"http.protocol.expect-continue", false);

						httpost.setEntity(new UrlEncodedFormEntity(data,
								HTTP.UTF_8));

					} catch (final UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						return null;
					}
				}

				// Post, check and show the result (not really spectacular, but
				// works):
				try {
					HttpResponse response = httpClient.execute(httpost);
					Preferences.httpRunning = false;
					return response;

				} catch (final Exception e) {

				}
			} catch (final Exception e) {
				e.printStackTrace();
			}

		}
		return null;

	}

	public HttpResponse PostURL(String URL, List<NameValuePair> data)
			throws IOException {
		return PostURL(URL, data, "");
	}

	public static void setHttpHeader(Object header) {

		if (header != null) {

		}
	}

	public String SendMultiPartData(String URL, MultipartEntity postData)
			throws IOException {

		Log.d(CLASS_TAG, "PostFileUpload(): upload file to server.");

		// Dipo Fix
		try {
			// wrap try around because this constructor can throw Error
			final HttpPost httpost = new HttpPost(URL);

			if (postData != null) {
				Log.i(CLASS_TAG, "PostFileUpload(): ");
				// NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);
				httpost.setEntity(postData);
				// Header
				// httpost.addHeader("Authorization","Basic "+
				// getCredentials(userName, userPassword));
				HttpResponse response = httpClient.execute(httpost);
				Preferences.httpRunning = false;

				HttpEntity respEntity = response.getEntity();
				if (respEntity != null) {
					InputStream serverInput = respEntity.getContent();

					return GetText(serverInput);

				}
			}

		} catch (MalformedURLException ex) {
			log("MalformedURLException", ex.toString());
			ex.printStackTrace();
			return "";
			// fall through and return false
		} catch (Exception ex) {
			return "";
		}
		return "";
	}

	public byte[] fetchImage2(String address) throws MalformedURLException,
			IOException {

		HttpResponse response;

		try {
			response = GetURL(address);
			int fileSize = (int) response.getEntity().getContentLength();
			if (fileSize < 0) {
				return null;
			}
			byte[] imageData = new byte[fileSize];

			BufferedInputStream istream = new BufferedInputStream(response
					.getEntity().getContent(), IO_BUFFER_SIZE);
			int bytesRead = 0;
			int offset = 0;
			while (bytesRead != -1 && offset < fileSize) {
				bytesRead = istream.read(imageData, offset, fileSize - offset);
				offset += bytesRead;
			}

			// clean up
			istream.close();
			return imageData;
		} catch (IOException e) {
			// android.util.Log.e("IO", "Could not load buddy icon: " + this,
			// e);

		}
		return null;
	}

	/**
	 * Copy the content of the input stream into the output stream, using a
	 * temporary byte array buffer whose size is defined by
	 * {@link #IO_BUFFER_SIZE}.
	 * 
	 * @param in
	 *            The input stream to copy from.
	 * @param out
	 *            The output stream to copy to.
	 * @throws IOException
	 *             If any error occurs during the copy.
	 */
	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[4 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * Closes the specified stream.
	 * 
	 * @param stream
	 *            The stream to close.
	 */
	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e("IO", "Could not close stream", e);
			}
		}
	}

	public String GetText(HttpResponse response) {
		String text = "";
		try {
			text = GetText(response.getEntity().getContent());
		} catch (final Exception ex) {
		}
		return text;
	}

	public String GetText(InputStream in) {
		String text = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"),
					1024);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			if (reader != null) {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				text = sb.toString();
			}
		} catch (final Exception ex) {
		} finally {
			try {
				in.close();
			} catch (final Exception ex) {
			}
		}
		return text;
	}

	/**
	 * Is there internet connection
	 */
	public boolean isConnected() {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		final NetworkInfo networkInfo;
		networkInfo = connectivity.getActiveNetworkInfo();
		// NetworkInfo info

		if (networkInfo != null && networkInfo.isConnected()
				&& networkInfo.isAvailable()) {
			return true;
		}
		return false;

	}

	protected void log(String message) {
		if (MainApplication.LOGGING_MODE)
			Log.i(getClass().getName(), message);
	}

	protected void log(String format, Object... args) {
		if (MainApplication.LOGGING_MODE)
			Log.i(getClass().getName(), String.format(format, args));
	}

	protected void log(String message, Exception ex) {
		if (MainApplication.LOGGING_MODE)
			Log.e(getClass().getName(), message, ex);
	}

}
