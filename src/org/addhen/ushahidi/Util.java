package org.addhen.ushahidi;

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
	 * @return
	 */
	public static String capitalizeString( String text ) {
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
	
	/**
	 * Checks if there is internet connection with the device.
	 */
	public static boolean isInternetConnection( Context context) {
		ConnectivityManager conManager = ( ConnectivityManager) 
			context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo= conManager.getActiveNetworkInfo();
		
		return networkInfo.isConnected();
	}
}
