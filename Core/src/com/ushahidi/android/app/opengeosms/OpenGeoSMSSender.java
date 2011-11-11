package com.ushahidi.android.app.opengeosms;

import java.io.IOException;
import java.util.HashMap;

import com.ushahidi.android.app.net.MainHttpClient;

import android.util.Log;
import android.content.Context;
import android.telephony.SmsManager;

import opengeosms.OpenGeoSMS;
import opengeosms.OpenGeoSMSFactory;
import opengeosms.format.OpenGeoSMSFormat;
import opengeosms.writer.IWriter;
import opengeosms.writer.OnWriteCode;
import opengeosms.writer.IWriter.OnWriteListener;
import opengeosms.writer.OpenGeoSMSWriter;

public class OpenGeoSMSSender {
	
	
	//宣告Open GeoSMSWriter，主要作用為發送Open GeoSMS
	private static OpenGeoSMSWriter smsWriter;
	private static double lat = 0.0;
	private static double lng = 0.0;
    private static String payload = "";
    private static int status = 0;
    private static final String CLASS_TAG = OpenGeoSMSSender.class.getSimpleName();
    
    /**
     * Upload files to server 
     * 0 - success, 1 - missing parameter, 2 - invalid parameter, 3 - send sms failed, 
     * 5 - access denied, 6 - access limited, 7 - no data, 
     * 8 - api disabled, 9 - no task found, 10 - json is wrong
     */
	public static int SendOpenGeoSMS(String url, HashMap<String, String> params, Context appContext)throws IOException {
		
		status = 0;
		Log.d(CLASS_TAG, "sendOpenGeoSMS(): sending report to online by Open GeoSMS");

		String phonenum = params.get("phoneNumber");
		
		String strLat = params.get("latitude");
		String strLng = params.get("longitude");
		
		//取到小數後六位即可
		lat = Double.parseDouble(strLat.substring(0, strLat.indexOf(".") + 6));
		lng = Double.parseDouble(strLng.substring(0, strLat.indexOf(".") + 6));
		
		try{
			smsWriter = new OpenGeoSMSWriter(appContext);
			
			//開啟smsWriter，此一動作主要目的是監視系統傳送SMS 之狀況
			smsWriter.open();
			
			OpenGeoSMS opengeosms = OpenGeoSMSFactory.createBasicPack(lat, lng);
			
			opengeosms.setDomainNamePath(url + "/");		
			opengeosms.setOpenGeoSMSFormat(OpenGeoSMSFormat.EXTENDED);
			opengeosms.setText(SetPayload(params));
			
			//SetPayLoad success
			if(status == 0)
			{
				smsWriter.setOnWriteListener(new EvtSMSWriterOnWrittenListener());
				smsWriter.write(opengeosms, phonenum);
			}
			
		} catch (Exception ex) {
			Log.e(CLASS_TAG, ex.toString());
		    return 3;

		}
		
		return status;
	}
	
	
    /**
     * Upload files to server 
     * 0 - success, 1 - missing parameter, 2 - invalid parameter, 3 - send sms failed, 
     * 5 - access denied, 6 - access limited, 7 - no data, 
     * 8 - api disabled, 9 - no task found, 10 - json is wrong
     */
	private static class EvtSMSWriterOnWrittenListener implements OnWriteListener {
		
		public void onSent(IWriter writer, int code, Object target) {
			switch (code) {
				case OnWriteCode.SUCCESS:
					status = 0;
					break;
				case OnWriteCode.ERROR: //others not described error
					status = 3;
					break;
				case OnWriteCode.UNAVAILABLE_ADDRESS: //phoneNumber 09xx xxx xxx int
					status = 2;
					break;
				case OnWriteCode.UNAVAILABLE_MESSAGE: //should not be happen, happen when message is empty
				case OnWriteCode.UNAVAILABLE_WRITER: //sdk already do sync, so not happen
				case OnWriteCode.UNOPENED_WRITER: //no open first					
				default:
					status = 3;
			}
			
		}
	}
	
    /**
     *  set Payload
     */
    private static String SetPayload(HashMap<String, String> params){
    	
    	String locationName = "";
    	String title = "";
    	String description = "";
    	
    	status = 0;
    	
    	try{
	    	if (params != null) {
	    		
	    		if(params.get("location_name") == null)
	    			locationName = "";
	    		else{
	    			locationName = params.get("location_name");
	    			locationName = locationName.replace("\n", " ");
	    		}
    		
	    		title = params.get("incident_title");
	    		title = title.replace("\n", " ");
	    		
	    		description = params.get("incident_description");
	    		description = description.replace("\n", "&br");
	    		
			  	payload = title+ "\n" +
			       		  description+ "\n" +
			       		  params.get("incident_datetime")+ "\n" +
			       		  params.get("incident_category")+ "\n" +		        
			       		  locationName+ "\n" +
			       		  params.get("person_first")+ "\n" +
			       		  params.get("person_last")+ "\n" +
			       		  params.get("person_email")+ "\n";	     
			 }
	    	
    	}catch (Exception ex) {
			Log.e(CLASS_TAG, ex.toString());
		    status = 2;
		}
		
    	return payload;
    }
    
}
