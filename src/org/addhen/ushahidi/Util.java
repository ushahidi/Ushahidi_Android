package org.addhen.ushahidi;

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
	
	public static int toInt( String value){
		return Integer.parseInt(value);
	}
	
	public static String capitalizeString( String text ) {
		return text.substring(0,1).toUpperCase() + text.substring(1);
	}
}
