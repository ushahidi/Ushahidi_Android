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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.ApiUtils;

public class MainHttpClient {

    public static final String TASK = "task";

    public static final String INCIDENT_TITLE = "incident_title";

    public static final String INCIDENT_DESCRIPTION = "incident_description";

    public static final String INCIDENT_DATE = "incident_date";

    public static final String INCIDENT_HOUR = "incident_hour";

    public static final String INCIDENT_MINUTE = "incident_minute";

    public static final String INCIDENT_AMPM = "incident_ampm";

    public static final String INCIDENT_CATEGORY = "incident_category";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String LOCATION_NAME = "location_name";

    public static final String PERSON_FIRST = "person_first";

    public static final String PERSON_LAST = "person_last";

    public static final String PERSON_EMAIL = "person_email";

    public static final String PHOTO = "filename";

    private static DefaultHttpClient httpClient;

    private HttpParams httpParameters;

    private static MultipartEntity entity;

    private static final String CLASS_TAG = MainHttpClient.class.getSimpleName();

    private int timeoutConnection = 60000;

    private int timeoutSocket = 60000;

    private static final int IO_BUFFER_SIZE = 512;

    public MainHttpClient(Context context) {

        httpParameters = new BasicHttpParams();
        httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        httpParameters.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
                new ConnPerRouteBean(1));

        httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, "utf8");

        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        try {
            schemeRegistry.register(new Scheme("https", new TrustedSocketFactory(
                    Preferences.domain, false), 443));
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters,
                schemeRegistry), httpParameters);

    }

    /*
     * private static String getCredentials(String userName, String password){
     * return HttpBase64.encodeBytes((userName + ":" + password).getBytes()); }
     */

    public static HttpResponse GetURL(String URL) throws IOException {
        Preferences.httpRunning = true;

        try {
            // wrap try around because this constructor can throw Error
            final HttpGet httpget = new HttpGet(URL);
            httpget.addHeader("User-Agent", "Ushahidi-Android/1.0)");

            // Post, check and show the result (not really spectacular, but
            // works):
            HttpResponse response = httpClient.execute(httpget);
            Preferences.httpRunning = false;

            return response;

        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Preferences.httpRunning = false;
        return null;

    }

    public static HttpResponse PostURL(String URL, List<NameValuePair> data, String Referer)
            throws IOException {
        Preferences.httpRunning = true;
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
                    httpost.getParams().setBooleanParameter("http.protocol.expect-continue", false);

                    httpost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));

                } catch (final UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Preferences.httpRunning = false;
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

        Preferences.httpRunning = false;
        return null;

    }

    public static HttpResponse PostURL(String URL, List<NameValuePair> data) throws IOException {
        return PostURL(URL, data, "");
    }

    public static void setHttpHeader(Object header) {

        if (header != null) {

        }
    }

    public static String SendMultiPartData(String URL, MultipartEntity postData) throws IOException {

        Log.d(CLASS_TAG, "PostFileUpload(): upload file to server.");

        // Dipo Fix
        try {
            // wrap try around because this constructor can throw Error
            final HttpPost httpost = new HttpPost(URL);

            if (postData != null) {
                Log.i(CLASS_TAG, "PostFileUpload(): ");
                // NEED THIS NOW TO FIX ERROR 417
                httpost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
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
            Log.d(CLASS_TAG, "PostFileUpload(): MalformedURLException");
            ex.printStackTrace();
            return "";
            // fall through and return false
        } catch (Exception ex) {
            return "";
        }
        return "";
    }

    /**
     * Upload files to server 0 - success, 1 - missing parameter, 2 - invalid
     * parameter, 3 - post failed, 5 - access denied, 6 - access limited, 7 - no
     * data, 8 - api disabled, 9 - no task found, 10 - json is wrong
     */
    public static int PostFileUpload(String URL, HashMap<String, String> params) throws IOException {
        Log.d(CLASS_TAG, "PostFileUpload(): upload file to server.");

        entity = new MultipartEntity();
        // Dipo Fix
        try {
            // wrap try around because this constructor can throw Error
            final HttpPost httpost = new HttpPost(URL);

            if (params != null) {

                entity.addPart("task", new StringBody(params.get("task")));
                entity.addPart("incident_title", new StringBody(params.get("incident_title"),
                        Charset.forName("UTF-8")));
                entity.addPart(
                        "incident_description",
                        new StringBody(params.get("incident_description"), Charset.forName("UTF-8")));
                entity.addPart("incident_date", new StringBody(params.get("incident_date")));
                entity.addPart("incident_hour", new StringBody(params.get("incident_hour")));
                entity.addPart("incident_minute", new StringBody(params.get("incident_minute")));
                entity.addPart("incident_ampm", new StringBody(params.get("incident_ampm")));
                entity.addPart("incident_category", new StringBody(params.get("incident_category")));
                entity.addPart("latitude", new StringBody(params.get("latitude")));
                entity.addPart("longitude", new StringBody(params.get("longitude")));
                entity.addPart("location_name",
                        new StringBody(params.get("location_name"), Charset.forName("UTF-8")));
                entity.addPart("person_first",
                        new StringBody(params.get("person_first"), Charset.forName("UTF-8")));
                entity.addPart("person_last",
                        new StringBody(params.get("person_last"), Charset.forName("UTF-8")));
                entity.addPart("person_email",
                        new StringBody(params.get("person_email"), Charset.forName("UTF-8")));
                if (params.get("filename") != null) {
                    if (!TextUtils.isEmpty(params.get("filename"))) {
                        File file = new File(params.get("filename"));
                        if (file.exists()) {
                            entity.addPart("incident_photo[]",
                                    new FileBody(new File(params.get("filename"))));
                        }
                    }
                }

                // NEED THIS NOW TO FIX ERROR 417
                httpost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
                httpost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpost);
                Preferences.httpRunning = false;

                HttpEntity respEntity = response.getEntity();
                if (respEntity != null) {
                    InputStream serverInput = respEntity.getContent();
                    return ApiUtils.extractPayloadJSON(GetText(serverInput));

                }
            }

        } catch (MalformedURLException ex) {
            Log.d(CLASS_TAG, "PostFileUpload(): MalformedURLException");
            ex.printStackTrace();
            return 11;
            // fall through and return false
        } catch (IllegalArgumentException ex) {
            Log.e(CLASS_TAG, ex.toString());
            // invalid URI
            return 12;
        } catch (IOException e) {
            Log.e(CLASS_TAG, e.toString());
            // timeout
            return 13;
        }
        return 10;
    }

    public static byte[] fetchImage(String address) throws MalformedURLException, IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(address).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 4 * 1024);
            copy(in, out);
            out.flush();

            // need to close stream before return statement
            closeStream(in);
            closeStream(out);

            return dataStream.toByteArray();
        } catch (IOException e) {
            // android.util.Log.e("IO", "Could not load buddy icon: " + this,
            // e);

        } finally {
            closeStream(in);
            closeStream(out);

        }
        return null;

    }

    public static byte[] fetchImage2(String address) throws MalformedURLException, IOException {
        InputStream in = null;
        OutputStream out = null;
        HttpResponse response;

        try {
            response = GetURL(address);
            in = new BufferedInputStream(response.getEntity().getContent(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 4 * 1024);
            copy(in, out);
            out.flush();

            // need to close stream before return statement
            closeStream(in);
            closeStream(out);

            return dataStream.toByteArray();
        } catch (IOException e) {
            // android.util.Log.e("IO", "Could not load buddy icon: " + this,
            // e);

        } finally {
            closeStream(in);
            closeStream(out);

        }
        return null;

    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     * 
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[4 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * Closes the specified stream.
     * 
     * @param stream The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e("IO", "Could not close stream", e);
            }
        }
    }

    public static String GetText(HttpResponse response) {
        String text = "";
        try {
            text = GetText(response.getEntity().getContent());
        } catch (final Exception ex) {
        }
        return text;
    }

    public static String GetText(InputStream in) {
        String text = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 1024);
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
}
