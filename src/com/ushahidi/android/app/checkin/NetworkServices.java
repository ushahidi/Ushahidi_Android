package com.ushahidi.android.app.checkin;

import android.location.Location;
import android.util.Log;
import com.ushahidi.android.app.UshahidiService;
import com.ushahidi.android.app.net.UshahidiHttpClient;

import java.io.IOException;
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
    public static boolean postToOnline(String checkinDetails, Location location) {
		// Time formating
		String dateFormat = "MM/dd/yyyy";
		String timeFormat = "HH:mm:ss a";

		SimpleDateFormat dateSdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat timeSdf = new SimpleDateFormat(timeFormat);

		// Variables
		Date currentDate = new Date();
		HashMap<String,String> myParams = new HashMap<String, String>();
		StringBuilder dateToSubmit =  new StringBuilder()
	        .append(dateSdf.format(currentDate)).append(" ")
	        .append(timeSdf.format(currentDate));

    	String dates[] = dateToSubmit.toString().split(" ");
    	String time[] = dates[1].split(":");
    	String categories = "N/A"; // TODO establish a means to define a category

    	Log.i("Domain name ", "Domain : " + UshahidiService.domain);
    	Log.i("Dates 0: ", dates[0]);
    	Log.i("Dates 2: ", dates[2]);

    	// Build the HTTP response
    	StringBuilder urlBuilder = new StringBuilder(UshahidiService.domain);
    	urlBuilder.append("/api");
    	myParams.put("task","report");
		myParams.put("incident_title", "Check In");
		myParams.put("incident_description", checkinDetails);
		myParams.put("incident_date", dates[0]);
		myParams.put("incident_hour", time[0]);
		myParams.put("incident_minute", time[1]);
		myParams.put("incident_ampm", dates[2].toLowerCase());
		myParams.put("incident_category", categories);
		myParams.put("latitude", String.valueOf(location.getLatitude()));
		myParams.put("longitude", String.valueOf(location.getLongitude()));
		myParams.put("location_name", "N/A");
		myParams.put("person_first", UshahidiService.firstname);
		myParams.put("person_last", UshahidiService.lastname);
		myParams.put("person_email", UshahidiService.email);
		myParams.put("filename", UshahidiService.fileName);

		Log.i("Ushahidi URL: ",urlBuilder.toString());

		try {
			UshahidiHttpClient.PostFileUpload(urlBuilder.toString(), myParams);

			return true;
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
	}
}
