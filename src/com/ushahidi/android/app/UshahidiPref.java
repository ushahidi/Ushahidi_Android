package com.ushahidi.android.app;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;

public class UshahidiPref {
    public static boolean httpRunning = false;
    public static boolean AutoFetch = false;
    public static boolean smsUpdate = false;
    public static boolean vibrate = false;
    public static boolean ringtone = false;
    public static boolean flashLed = false;
    
    public static int countries = 0;
    public static int AutoUpdateDelay = 0;
    public static final int NOTIFICATION_ID = 1;
    
    public static final String PREFS_NAME = "UshahidiService";
    public static String incidentsResponse = "";
    public static String categoriesResponse = "";
    public static String savePath = "";
    public static String domain = "";
    public static String firstname = "";
    public static String lastname = "";
    public static String email = "";
    public static String totalReports = "";
    public static String fileName = "";
    public static String total_reports = "";
    public static String username = "";
    public static String password = "";
    
    public static void saveSettings(Context context) {
        
        final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        
        editor.putString("Domain", domain.trim());
        editor.putString("Firstname", firstname);
        editor.putString("Lastname", lastname);
        
        if( Util.validateEmail(settings.getString("Email", ""))) {
            editor.putString("Email", email);
        }
        
        editor.putString("savePath", savePath);
        editor.putInt("AutoUpdateDelay", AutoUpdateDelay);
        editor.putBoolean("AutoFetch", AutoFetch);
        editor.putString("TotalReports", totalReports);
        editor.putBoolean("SmsUpdate", smsUpdate);
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.commit();
        
    }
    
    public static void loadSettings(Context context) {
        final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        
        final String path = context.getDir("",context.MODE_PRIVATE).toString();
        
        savePath = settings.getString("savePath",path);
        
        domain = settings.getString("Domain", "");
        firstname = settings.getString("Firstname", "");
        lastname = settings.getString("Lastname", "");
        email = settings.getString("Email", "");
        countries = settings.getInt("Countries", 0);
        AutoUpdateDelay = settings.getInt("AutoUpdateDelay", 5);
        AutoFetch = settings.getBoolean("AutoFetch", false);
        totalReports = settings.getString("TotalReports", "");
        smsUpdate = settings.getBoolean("SmsUpdate",false);
        username = settings.getString("Username", "");
        password = settings.getString("Password","");
        
        // make sure folder exists
        final File dir = new File(UshahidiPref.savePath);
        dir.mkdirs();
 
    }
}
