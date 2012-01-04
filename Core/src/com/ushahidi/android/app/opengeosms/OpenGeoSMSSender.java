package com.ushahidi.android.app.opengeosms;

import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * @inoran
 * Open GeoSMS
 */
public class OpenGeoSMSSender {
	
	private static String smsMsg = "";
	private static String payload = "";
	private static String phonenum = "";
	private static String latitude = "";
	private static String longitude = "";
	private static ArrayList<String> smsMsgList;
	private static final String CLASS_TAG = OpenGeoSMSSender.class.getSimpleName();
	private final String piFilterSent = "OPENGEOSMS_SENT";
	private Context mContext;
	private ArrayList<PendingIntent> smsSentPIList;
    
    
    
    /**
     * Send Open GeoSMS to server
     * 
     * @param receiver: who are expected to receive the Open GeoSMS
     * @param url: for example, http://ushahidi.tw/
     * @param lat: latitude
     * @param lng: longitude
     * @param payloadParams: includes title, description, dateTime, category, locationName, person_first, person_last, person_email
     * 
     * @return number of SMS segments
     * 
     * Open GeoSMS template
     * Schema://domain/?FirstParameter&RestParameters&GeoSMS=OP
     * Payload
     * 
     * example:
     * http://ushahidi.tw/?q=lat,lng&GeoSMS
     * IncidentReport
     * something happen
     * 01/03/2010 12:00 AM
     * 2
     * Taiwan
     * cai faing
     * ye
     * caifangye@itri.org.tw
     *  
     */
	public int SendOpenGeoSMS(String reciver, String url, String lat, String lng, HashMap<String, String> payloadParams, Context appContext)throws IOException {
		
		Log.d(CLASS_TAG, "sendOpenGeoSMS(): Using Open GeoSMS to send report to server");

		mContext = appContext;
		phonenum = reciver;
		
		latitude = SetDecimalPalce(lat, 6);
		longitude = SetDecimalPalce(lng, 6);
		
		SetPayload(payloadParams);
		
		//SetPayLoad success
		smsMsg = ComposedOpenGeoSMS(url, latitude , longitude, payload);
   		SmsManager smsManager = SmsManager.getDefault();
   	 	smsMsgList = smsManager.divideMessage(smsMsg);
   	 	smsSentPIList = getSMSSentPendingIntents(smsMsgList) ;
   	 	
   		try{
    		smsManager.sendMultipartTextMessage(phonenum, null, smsMsgList, smsSentPIList, null);
    		return smsMsgList.size();
   		} catch (IllegalArgumentException ex) {
   			Log.e(CLASS_TAG, ex.toString());
   			return 0;
   		}
		
	}
	

	private String SetDecimalPalce(String locationStr, int length){
		
		int decimalPlace = locationStr.indexOf("."); 
		if(decimalPlace != -1){
			int decimalPlaceNum = locationStr.substring(1 + decimalPlace).length();	
			if(decimalPlaceNum > length)
				return locationStr.substring(0, decimalPlace + length);	
			else
				return locationStr;
						
		}else
			return locationStr;
		
	}
	
	
    /**
     * Set Open GeoSMS payload
     * title,
	 * description,
	 * dateTime,
	 * category,		        
	 * locationName,
	 * person_first,
	 * person_last,
	 * person_email	
	 * 
     */
    private static String SetPayload(HashMap<String, String> payloadParams){
    	
    	String locationName = "";
    	String title = "";
    	String description = "";
    	
    	try{
	    	if (payloadParams != null) {
	    		
	    		if(payloadParams.get("location_name") == null)
	    			locationName = "";
	    		else{
	    			locationName = payloadParams.get("location_name");
	    			locationName = locationName.replace("\n", " ");
	    		}
    		
	    		title = payloadParams.get("incident_title");
	    		
	    		title = title.replace("\n", " ");
	    		
	    		description = payloadParams.get("incident_description");
	    		description = description.replace("\n", "&br");
	    		
			  	payload = title+ "\n" +
			       		  description+ "\n" +
			       		  payloadParams.get("incident_datetime")+ "\n" +
			       		  payloadParams.get("incident_category")+ "\n" +		        
			       		  locationName+ "\n" +
			       		  payloadParams.get("person_first")+ "\n" +
			       		  payloadParams.get("person_last")+ "\n" +
			       		  payloadParams.get("person_email")+ "\n";	     
			 }
	    	
    	}catch (Exception ex) {
			Log.e(CLASS_TAG, ex.toString());
		}
		
    	return payload;
    }
    
    /* 
    * Open GeoSMS template
    * Schema://domain/?FirstParameter&RestParameters&GeoSMS=OP
    * Payload
    */
    private static String ComposedOpenGeoSMS(String url, String lat, String lng, String payload){
        String firstParam = "q=";
        final String postfix = "GeoSMS";
        
		String openGeoSMSMessage = url + "/?" + firstParam + lat + "," + lng + "&" + postfix + "\n" + payload;
		
		return openGeoSMSMessage;
    }
    
    
	private ArrayList<PendingIntent> getSMSSentPendingIntents(ArrayList<String> msgs) {
		ArrayList<PendingIntent> piList = null;
		
		if(msgs != null) {
			piList = new ArrayList<PendingIntent>();
			Intent intent;
			PendingIntent piSendSMS;
			
			for(int i=0; i<msgs.size(); i++) {
				intent = new Intent(piFilterSent);
				intent.putExtra("", i+1);
				intent.putExtra("", msgs.get(i));
				piSendSMS = PendingIntent.getBroadcast(mContext, 0, intent, 0);
				piList.add(piSendSMS);
			}
		}
		return piList;
	}
    
	
}
