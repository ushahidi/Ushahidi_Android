
package com.ushahidi.android.app.checkin;

import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.net.ClientHttpRequest;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/10/11 Time: 4:34 PM To change
 * this template use File | Settings | File Templates.
 */
public class NetworkServices {
    public static String fileName;

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
        ClientHttpRequest req = null;
        Log.i("NeworkServices", "Posting Checkins online");
        try {
            URL url = new URL(URL);
            req = new ClientHttpRequest(url);
            req.setParameter("task", params.get("task"));
            req.setParameter("action", params.get("action"));
            req.setParameter("mobileid", params.get("mobileid"));
            req.setParameter("lat", params.get("lat"));
            req.setParameter("lon", params.get("lon"));
            req.setParameter("message", params.get("message"));
            req.setParameter("firstname", params.get("firstname"));
            req.setParameter("lastname", params.get("lastname"));
            req.setParameter("email", params.get("email"));

            if (!TextUtils.isEmpty(params.get("filename")) || !(params.get("filename").equals(""))) {
                Log.i("NeworkServices", "Posting file online");
                req.setParameter("photo", new File(params.get("filename")));
            }

            InputStream serverInput = req.post();

            return GetText(serverInput);

        } catch (MalformedURLException ex) {
            // fall through and return false
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
            req.setParameter("sqllimit", UshahidiPref.totalReports);
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
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1024);
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
