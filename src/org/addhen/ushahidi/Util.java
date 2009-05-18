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
}
