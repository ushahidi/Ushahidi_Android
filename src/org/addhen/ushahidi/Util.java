package org.addhen.ushahidi;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.net.Categories;
import org.addhen.ushahidi.net.Incidents;
import org.addhen.ushahidi.net.UshahidiHttpClient;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;


public class Util{

	private static NetworkInfo networkInfo;
	private static List<IncidentsData> mNewIncidents;
	private static List<CategoriesData> mNewCategories;
	private static JSONObject jsonObject;
	private static Pattern pattern;
	private static Matcher matcher;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@" +
			"[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * joins two strings together
	 * @param first
	 * @param second
	 * @return
	 */
	public static String joinString(String first, String second ) {
		return first.concat(second);
	}
	
	/**
	 * Converts a string integer 
	 * @param value
	 * @return
	 */
	public static int toInt( String value){
		return Integer.parseInt(value);
	}
	
	/**
	 * Capitalize any string given to it.
	 * @param text
	 * @return capitalized string
	 */
	public static String capitalizeString( String text ) {
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
	
	/**
	 * Create csv
	 * @param Vector<String> text
	 * 
	 * @return csv
	 */
	public static String implode( Vector<String> text ) {
		String implode = "";
		int i = 0;
		for( String value : text ) {
			implode += i == text.size() -1 ? value : value+",";
			i++;
		}
		
		return implode;
	}
	
	/**
	 * Is there internet connection
	 */
	public static boolean isConnected(Context context )  {
		  
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		networkInfo = connectivity.getActiveNetworkInfo();
		//NetworkInfo info
		
		if(networkInfo == null || !networkInfo.isConnected()){  
	        return false;  
	    } 
	    return true; 
	     
	}
	
	/**
	 * Limit a string to defined length
	 * 
	 * @param int limit - the total length 
	 * @param string limited - the limited string
	 */
	public static String limitString( String value, int length ) {
		StringBuilder buf = new StringBuilder(value);
		if( buf.length() > length ) {
			buf.setLength(length);
			buf.append(" ...");
		}
		return buf.toString();
	}
	
	/**
	 * Fetch reports details from the internet
	 * 
	 * @param context - the activity calling this method.
	 */
	public static void fetchReports( final Context context ) {
		try {
			  if( Util.isConnected(context)) {

				  if(Categories.getAllCategoriesFromWeb() ) {
					  mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
				  }

				  if(Incidents.getAllIncidentsFromWeb()){
					  mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
				  }
				  
				  UshahidiService.total_reports = mNewCategories.size()+" Categories -- " +  mNewIncidents.size()+ " Reports";
				  
				  UshahidiApplication.mDb.addCategories(mNewCategories, false);
				  UshahidiApplication.mDb.addIncidents(mNewIncidents, false);
				  
			  } else {
				  return;
			  }
		  	} catch (IOException e) {
				//means there was a problem getting it
		  	} 
	}
	
	/**
	 * Format date into more readable format.
	 * 
	 * @param  date - the date to be formatted.
	 * @return String
	 */
	public static String formatDate( String dateFormat, String date, String toFormat ) {
	
		String formatted = "";
		
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		try {
			Date dateStr = formatter.parse(date);
			formatted = formatter.format(dateStr);
			Date formatDate = formatter.parse(formatted);
			formatter = new SimpleDateFormat(toFormat);
			formatted = formatter.format(formatDate);
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return formatted;
	}
	
	
	
	/**
	 * Extract Ushahidi payload JSON data
	 * 
	 * @apram json_data - the json data to be formatted.
	 * @return String 
	 */
	public static boolean extractPayloadJSON( String json_data ) {
	
		try {
			jsonObject = new JSONObject(json_data);
			return jsonObject.getJSONObject("payload").getBoolean("success");
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Extract Google geocode JSON data
	 * 
	 * @apram json_data - the json data to be formatted.
	 * @return String 
	 */
	public static boolean extractGeocodeJSON( String json_data ) {
	
		try {
			jsonObject = new JSONObject(json_data);
			return jsonObject.getJSONObject("payload").getBoolean("success");
		
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * process reports
	 * 0 - successful
	 * 1 - failed fetching categories
	 * 2 - failed fetching reports
	 * 3 - non ushahidi instance
	 * 4 - No internet connection
	 * 
	 * @return int - status
	 */
	public static int processReports( Context context ) {
		
		try {
			if( Util.isConnected(context)) {
				 
				if(Categories.getAllCategoriesFromWeb()) {
						
					mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse); 
				} else {
					return 1;
				}
				
				if( Incidents.getAllIncidentsFromWeb() ){
					mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse );
					
				} else {
					return 1;
				}
				
				if(mNewCategories != null && mNewIncidents != null ) {
					 UshahidiApplication.mDb.addCategories(mNewCategories, false);
					 UshahidiApplication.mDb.addIncidents(mNewIncidents, false);
					 return 0;
				 
				 } else {
					 return 1;
				 }
				  

				} else {
					return 4;
				}
		} catch (IOException e) {
			//means there was a problem getting it
		}
		return 0;
	}
	
	/**
	 * Show toast
	 * 
	 * @param Context - the application's context
	 * @param Int - string resource id
	 * 
	 * @return void
	 */
	public static void showToast(Context context, int i ) {
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, i, duration).show();
	}
	
	/**
	 * Validates an email address
	 * Credits: http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
	 * 
	 * @param String - email address to be validated
	 * 
	 * @return boolean
	 */
	public static boolean validateEmail( String emailAddress) {
		if( !emailAddress.equals("") ) {
			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(emailAddress);
			return matcher.matches();
		}
		return true;
	}
	
	/**
	 * Validate an Ushahidi instance
	 * 
	 * @param String - URL to be validated.
	 * 
	 * @return boolean
	 */
	public static boolean validateUshahidiInstance( String ushahidiUrl ) {
		//make an http get request to a dummy api call
		//TODO improve on how to do this
		boolean status = false;
		if( ushahidiUrl == null ) {
			return false;
		}
		
		HttpResponse response;
		String json_string = "";
		String message = "";
		StringBuilder uriBuilder = new StringBuilder( ushahidiUrl );
		uriBuilder.append("/api");

		try {
			response = UshahidiHttpClient.GetURL( uriBuilder.toString() );
			if( response == null ) {
				status = false;
			}
			
			final int statusCode = response.getStatusLine().getStatusCode();
			
			if( statusCode == 200 ) {
				json_string = UshahidiHttpClient.GetText(response);
				
				//extrac data from json object
				try {
					jsonObject = new JSONObject(json_string);
					
					message = jsonObject.getJSONObject("error").getString("message");
					if( message.equals("Not Found") ) {
						status = true;
					
					}else {
						status = false;
					}
				
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
			} else {
				status = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			status = false;
		}
		
		return status;
	}
	
}
