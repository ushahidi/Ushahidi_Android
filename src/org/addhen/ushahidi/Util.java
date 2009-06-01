package org.addhen.ushahidi;

import java.io.IOException;
import java.util.Vector;

import org.addhen.ushahidi.net.UshahidiHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

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
	public static boolean isConnected()  {
		HttpResponse response = null;
		String incidents = "";
		
		StringBuilder uriBuilder = new StringBuilder( UshahidiService.domain);
		
		try {
			response = UshahidiHttpClient.GetURL( uriBuilder.toString() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		if( response == null ) {
			return false;
		}
		
		final int statusCode = response.getStatusLine().getStatusCode();
		
		if( statusCode == 200 ) {
			incidents = UshahidiHttpClient.GetText(response);
			UshahidiService.incidentsResponse = incidents;
			
			return true;
		} else {
			
			return false;
		}
	}
	
}
