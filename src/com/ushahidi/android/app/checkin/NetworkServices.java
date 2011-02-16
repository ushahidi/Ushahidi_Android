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

    public static boolean postToOnline(String checkinDetails, Location location) {
		HashMap<String,String> myParams = new HashMap<String, String>();

    	// Build the HTTP response
    	StringBuilder urlBuilder = new StringBuilder(UshahidiService.domain);
    	urlBuilder.append("/api");
    	myParams.put("task","checkin");
		myParams.put("action", "ci");
		myParams.put("mobileid", "testingmobileid439234");
		myParams.put("lat", String.valueOf(location.getLatitude()));
		myParams.put("lon", String.valueOf(location.getLongitude()));
        myParams.put("message", checkinDetails);

        // Specify the file name
        myParams.put("filename", "");

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
             req.setParameter("incident_title", params.get("incident_title"));
             req.setParameter("incident_description", params.get("incident_description"));
             req.setParameter("incident_date",params.get("incident_date"));
             req.setParameter("incident_hour", params.get("incident_hour"));
             req.setParameter("incident_minute", params.get("incident_minute"));
             req.setParameter("incident_ampm", params.get("incident_ampm"));
             req.setParameter("incident_category", params.get("incident_category"));
             req.setParameter("latitude", params.get("latitude"));
             req.setParameter("longitude", params.get("longitude"));
             req.setParameter("location_name", params.get("location_name"));
             req.setParameter("person_first", params.get("person_first"));
             req.setParameter("person_last", params.get("person_last"));
             req.setParameter("person_email", params.get("person_email"));
             Log.i("HTTP Client:", "filename:"+UshahidiService.savePath + params.get("filename"));
             if( !TextUtils.isEmpty(params.get("filename")))
             req.setParameter("incident_photo[]", new File(UshahidiService.savePath + params.get("filename")));

             InputStream serverInput = req.post();

             if( Util.extractPayloadJSON(GetText(serverInput)) ){
            	 return true;
             }

        } catch (MalformedURLException ex) {
        	//fall through and return false
        }
        return false;
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
