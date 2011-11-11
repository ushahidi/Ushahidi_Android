/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ushahidi.android.app.net.MainGeocoder;

/**
 * This is a utility class that has common methods to be used by most clsses.
 * 
 * @author eyedol
 */
public class Util {

    private static JSONObject jsonObject;

    private static NetworkInfo networkInfo;

    private static Random random = new Random();

    private static final String VALID_EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern pattern;

    private static Matcher matcher;

    /**
     * joins two strings together
     * 
     * @param first
     * @param second
     * @return
     */
    public static String joinString(String first, String second) {
        return first.concat(second);
    }

    /**
     * Converts a string integer
     * 
     * @param value
     * @return
     */
    public static int toInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Capitalize any string given to it.
     * 
     * @param text
     * @return capitalized string
     */
    public static String capitalizeString(String text) {
        if (text.length() == 0)
            return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Create csv
     * 
     * @param Vector<String> text
     * @return csv
     */
    public static String implode(Vector<String> text) {
        String implode = "";
        int i = 0;
        for (String value : text) {
            implode += i == text.size() - 1 ? value : value + ",";
            i++;
        }

        return implode;
    }

    /**
     * Is there internet connection
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivity.getActiveNetworkInfo();
        // NetworkInfo info

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            return true;
        }
        return false;

    }

    /**
     * Truncates any given text.
     * 
     * @param String text - the text to be truncated
     * @return String
     */
    public static String truncateText(String text) {
        if (text.length() > 30) {
            return text.substring(0, 25).trim() + "�";
        } else {
            return text;
        }
    }

    /**
     * Limit a string to defined length
     * 
     * @param int limit - the total length
     * @param string limited - the limited string
     */
    public static String limitString(String value, int length) {
        StringBuilder buf = new StringBuilder(value);
        if (buf.length() > length) {
            buf.setLength(length);
            buf.append(" ...");
        }
        return buf.toString();
    }

    /**
     * Format date into more readable format.
     * 
     * @param date - the date to be formatted.
     * @return String
     */
    public static String formatDate(String dateFormat, String date, String toFormat) {

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
     * For debugging purposes. Append content of a string to a file
     * 
     * @param text
     */
    public static void appendLog(String text) {
        File logFile = new File(Environment.getExternalStorageDirectory(), "ush_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Extract Google geocode JSON data
     * 
     * @apram json_data - the json data to be formatted.
     * @return String
     */
    public static String getFromLocation(double latitude, double longitude, Context context) {
        String json_data = "";
        int status = 0;
        JSONArray jsonArray;
        try {
            if (Util.isConnected(context)) {
                json_data = MainGeocoder.reverseGeocode(latitude, longitude);
            } else {
                return "";
            }
            if (json_data != null) {
                jsonObject = new JSONObject(json_data);

                status = jsonObject.getJSONObject("Status").getInt("code");

                if (status == 200) {
                    jsonArray = jsonObject.getJSONArray("Placemark");

                    return jsonArray.getJSONObject(0).getJSONObject("AddressDetails")
                            .getJSONObject("Country").getJSONObject("AdministrativeArea")
                            .getJSONObject("Locality").getString("LocalityName");

                } else {
                    return "";
                }
            }

        } catch (JSONException e) {
            return "";
            // e.printStackTrace();
        } catch (IOException e) {
            return "";
        }
        return "";
    }

    /**
     * Show toast
     * 
     * @param Context - the application's context
     * @param Int - string resource id
     * @return void
     */
    public static void showToast(Context context, int i) {
        int duration = Toast.LENGTH_LONG;
        Toast.makeText(context, i, duration).show();
    }

    /**
     * Validates an email address Credits:
     * http://www.mkyong.com/regular-expressions
     * /how-to-validate-email-address-with-regular-expression/
     * 
     * @param String - email address to be validated
     * @return boolean
     */
    public static boolean validateEmail(String emailAddress) {
        if (!emailAddress.equals("")) {
            pattern = Pattern.compile(VALID_EMAIL_PATTERN);
            matcher = pattern.matcher(emailAddress);
            return matcher.matches();
        }
        return true;
    }

    /**
     * Delete content of a folder recursively.
     * 
     * @param String path - path to the directory.
     * @return void
     */
    public static void rmDir(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {

            String[] children = dir.list();
            Log.d("Directory", "dir.list returned some files" + children.length + "--");
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);

                if (temp.isDirectory()) {

                    rmDir(temp.getName());
                } else {
                    temp.delete();
                }
            }

            dir.delete();
        } else {
            Log.d("Directory", "This is not a directory" + path);
        }
    }

    /**
     * Capitalize each word in a text.
     * 
     * @param String text - The text to be capitalized.
     * @return String
     */
    public static String capitalize(String text) {
        if (text != null) {
            String[] words = text.split("\\s");
            String capWord = "";
            for (String word : words) {

                capWord += capitalizeString(word) + " ";

                return capWord;
            }
        }
        return "";
    }

    /** this criteria will settle for less accuracy, high power, and cost */
    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }

    /** this criteria needs high accuracy, high power, and cost */
    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }

    public static String generateFilename(boolean thumbnail) {
        if (thumbnail) {
            return randomString() + "_t.jpg";
        }

        return randomString() + ".jpg";
    }

    public static String randomString() {
        return Long.toString(Math.abs(random.nextLong()), 10);
    }

    /**
     * Checks that the device supports Camera.
     * 
     * @param Context context - The calling activity's context.
     * @return boolean - True if it supports otherwise false.
     */
    public static boolean deviceHasCamera(Context context) {

        PackageManager pm = context.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks that the device supports Camera supports auto focus.
     * 
     * @param Context context - The calling activity's context.
     * @return boolean - True if it supports otherwise false.
     */
    public static boolean deviceCameraHasAutofocus(Context context) {

        PackageManager pm = context.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            return true;
        } else {
            return false;
        }
    }
}
