
package com.ushahidi.android.app.checkin;

import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.net.ClientHttpRequest;
import com.ushahidi.android.app.net.MainHttpClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/10/11 Time: 4:34 PM To change
 * this template use File | Settings | File Templates.
 */
public class NetworkServices {

    public static String fileName;

    private static MultipartEntity entity = new MultipartEntity();

    public static String postToOnline(String IMEI, String domainName, String checkinDetails,
            String filename, String firstname, String lastname, String email, double latitude,
            double longitude) {

        HashMap<String, String> myParams = new HashMap<String, String>();

        // Build the HTTP response
        StringBuilder urlBuilder = new StringBuilder(domainName);
        urlBuilder.append("/api");
        myParams.put("task", "checkin");
        myParams.put("action", "ci");
        myParams.put("mobileid", IMEI);
        myParams.put("lat", String.valueOf(latitude));
        myParams.put("lon", String.valueOf(longitude));
        myParams.put("message", checkinDetails);
        myParams.put("firstname", firstname);
        myParams.put("lastname", lastname);
        myParams.put("email", email);

        // Specify the file name
        myParams.put("filename", filename);

        try {
            return PostFileUpload(urlBuilder.toString(), myParams);
        } catch (IOException e) {

            return null;
        }
    }

    public static String PostFileUpload(String URL, HashMap<String, String> params)
            throws IOException {
        Log.i("NeworkServices", "Posting Checkins online");

        entity = new MultipartEntity();

        if (params != null) {
            Log.i("NeworkServices", "UploadFile "+params.size());
            entity.addPart("task", new StringBody(params.get("task")));
            entity.addPart("action", new StringBody(params.get("action")));
            entity.addPart("mobileid", new StringBody(params.get("mobileid")));
            entity.addPart("lat", new StringBody(params.get("lat")));
            entity.addPart("lon", new StringBody(params.get("lon")));
            entity.addPart("message", new StringBody(params.get("message")));
            entity.addPart("firstname", new StringBody(params.get("firstname")));
            entity.addPart("lastname", new StringBody(params.get("lastname")));
            entity.addPart("email", new StringBody(params.get("email")));
            
            if (!TextUtils.isEmpty(params.get("filename")) || !(params.get("filename").equals(""))) {
                Log.i("NeworkServices", "Posting file online");
                entity.addPart("photo", new FileBody(new File(params.get("filename"))));
            }
            
            return MainHttpClient.SendMultiPartData(URL, entity);
        }
        return null;
    }

    public static String getCheckins(String URL, String mobileId, String checkinId) {
        StringBuilder fullUrl = new StringBuilder(URL);
        fullUrl.append("/api");

        try {
            URL url = new URL(fullUrl.toString());
            ClientHttpRequest req = new ClientHttpRequest(url);
            req.setParameter("task", "checkin");
            req.setParameter("action", "get_ci");
            req.setParameter("sort", "desc");
            req.setParameter("sqllimit", Preferences.totalReports);
            if (mobileId != null)
                req.setParameter("mobileid", mobileId);
            if (checkinId != null)
                req.setParameter("id", checkinId);

            InputStream inputStream = req.post();

            return GetText(inputStream);
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
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
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
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
