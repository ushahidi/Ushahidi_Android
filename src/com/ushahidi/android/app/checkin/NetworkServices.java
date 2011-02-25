package com.ushahidi.android.app.checkin;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import com.ushahidi.android.app.UshahidiService;
import com.ushahidi.android.app.Util;
import com.ushahidi.android.app.net.ClientHttpRequest;
import com.ushahidi.android.app.net.UshahidiHttpClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 2/10/11
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkServices {
    public static String fileName;

    public static boolean postToOnline(String IMEI, String checkinDetails, Location location, String filename) {

		HashMap<String,String> myParams = new HashMap<String, String>();

    	// Build the HTTP response
    	StringBuilder urlBuilder = new StringBuilder(UshahidiService.domain);
    	urlBuilder.append("/api");
    	myParams.put("task","checkin");
		myParams.put("action", "ci");
		myParams.put("mobileid", IMEI);
		myParams.put("lat", String.valueOf(location.getLatitude()));
		myParams.put("lon", String.valueOf(location.getLongitude()));
        myParams.put("message", checkinDetails);

        // Specify the file name
        myParams.put("filename", filename);

		Log.i("Ushahidi URL: ",urlBuilder.toString());

		try {
			PostFileUpload(urlBuilder.toString(), myParams);

			return true;
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
	}

    public static boolean PostFileUpload(String URL, HashMap<String, String> params) throws IOException{
        ClientHttpRequest req = null;

        try {
             URL url = new URL(URL);
             req = new ClientHttpRequest(url);

             req.setParameter("task", params.get("task"));
             req.setParameter("action", params.get("action"));
             req.setParameter("mobileid", params.get("mobileid"));
             req.setParameter("lat",params.get("lat"));
             req.setParameter("lon", params.get("lon"));
             req.setParameter("message", params.get("message"));

             Log.i("HTTP Client:", "filename:" + UshahidiService.savePath + params.get("filename"));

             if( !TextUtils.isEmpty(params.get("filename")) || !(params.get("filename").equals("")))
                req.setParameter("message[]", new File(UshahidiService.savePath + params.get("filename")));

             InputStream serverInput = req.post();

             if( Util.extractPayloadJSON(GetText(serverInput)) ){
            	 return true;
             }

        } catch (MalformedURLException ex) {
        	//fall through and return false
        }
        return false;
    }

    public static String getCheckins(String URL, String mobileId, String checkinId)
    {
        StringBuilder fullUrl = new StringBuilder(URL);
    	fullUrl.append("/api");

        try {
            URL url = new URL(fullUrl.toString());
            ClientHttpRequest req = new ClientHttpRequest(url);
            req.setParameter("task", "checkin");
            req.setParameter("action", "get_ci");

            if(mobileId != null)
                req.setParameter("mobileid", mobileId);
            if(checkinId != null)
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
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in), 1024);
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
