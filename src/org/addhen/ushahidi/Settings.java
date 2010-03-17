package org.addhen.ushahidi;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private EditTextPreference ushahidiInstancePref;
	private EditTextPreference firstNamePref;
	private EditTextPreference lastNamePref;
	private EditTextPreference emailAddressPref;
	private EditTextPreference userNamePref;
	private EditTextPreference passwordPref;
	private CheckBoxPreference autoFetchCheckBoxPref;
	private DialogPreference clearCacheCheckBoxPref;
	private CheckBoxPreference smsCheckBoxPref;
	private ListPreference autoUpdateTimePref;
	private ListPreference saveItemsPref;
	private ListPreference totalReportsPref;
	private AttributeSet attrs;
	private Handler mHandler;
	private static final int DIALOG_CLEAR_CACHE = 1;
	public static final String AUTO_FETCH_PREFERENCE = "auto_fetch_preference";
	public static final String SMS_PREFERENCE = "sms_preference";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler();
		addPreferencesFromResource(R.xml.preferences);
		ushahidiInstancePref = new EditTextPreference(this);
		firstNamePref = new EditTextPreference(this);
		lastNamePref = new EditTextPreference(this);
		userNamePref = new EditTextPreference(this);
		passwordPref = new EditTextPreference(this);
		emailAddressPref = new EditTextPreference(this);
		autoFetchCheckBoxPref = new CheckBoxPreference(this);
		clearCacheCheckBoxPref = (DialogPreference) getPreferenceScreen().findPreference("clear_cache_preference");
		autoUpdateTimePref = new ListPreference(this);
		saveItemsPref = new ListPreference(this);
		totalReportsPref = new ListPreference(this);
		smsCheckBoxPref = new CheckBoxPreference(this);
		
		setPreferenceScreen(createPreferenceHierarchy());
		this.saveSettings();
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		//ROOT element
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		
		//Basic preferences
		PreferenceCategory basicPrefCat = new PreferenceCategory(this);
		basicPrefCat.setTitle(R.string.basic_settings);
		root.addPreference(basicPrefCat);
		
		//URL entry field
		ushahidiInstancePref.setDialogTitle(R.string.txt_domain);
		ushahidiInstancePref.setKey("ushahidi_instance_preference");
		ushahidiInstancePref.setTitle(R.string.txt_domain);
		ushahidiInstancePref.setDefaultValue("http://");
		ushahidiInstancePref.setSummary(R.string.hint_domain);
		basicPrefCat.addPreference(ushahidiInstancePref);
		
		//First name entry field
		firstNamePref.setDialogTitle(R.string.txt_first_name);
		firstNamePref.setKey("first_name_preference");
		firstNamePref.setTitle(R.string.txt_first_name);
		firstNamePref.setSummary(R.string.hint_first_name);
		basicPrefCat.addPreference(firstNamePref);
		
		//Last name entry field
		lastNamePref.setDialogTitle(R.string.txt_last_name);
		lastNamePref.setKey("last_name_preference");
		lastNamePref.setTitle(R.string.txt_last_name);
		lastNamePref.setSummary(R.string.hint_last_name);
		basicPrefCat.addPreference(lastNamePref);
		
		//Email name entry field
		emailAddressPref.setDialogTitle(R.string.txt_email);
		emailAddressPref.setKey("email_address_preference");
		emailAddressPref.setTitle(R.string.txt_email);
		emailAddressPref.setSummary(R.string.hint_email);
		basicPrefCat.addPreference(emailAddressPref);
		
		//Advanced Preferences
		PreferenceCategory advancedPrefCat = new PreferenceCategory(this);
		advancedPrefCat.setTitle(R.string.advanced_settings);
		root.addPreference(advancedPrefCat);
		
		PreferenceScreen advancedScreenPref = getPreferenceManager().createPreferenceScreen(this);
		advancedScreenPref.setKey("advanced_screen_preference");
		advancedScreenPref.setTitle(R.string.advanced_settings);
		advancedScreenPref.setSummary(R.string.hint_advanced_settings);
	    advancedPrefCat.addPreference(advancedScreenPref);
	    
	    //Auto fetch reports
        autoFetchCheckBoxPref.setKey("auto_fetch_preference");
        autoFetchCheckBoxPref.setTitle(R.string.chk_auto_fetch);
        autoFetchCheckBoxPref.setSummary(R.string.hint_auto_fetch);
        advancedScreenPref.addPreference(autoFetchCheckBoxPref);
		
        // Auto update reports time interval
        //set list values
        CharSequence[] autoUpdateEntries = {"5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "60 Minutes"}; 
        CharSequence[] autoUpdateValues = {"0","5","10","15","30","60"};
        autoUpdateTimePref.setEntries(autoUpdateEntries);
        autoUpdateTimePref.setEntryValues(autoUpdateValues);
        autoUpdateTimePref.setDefaultValue(autoUpdateValues[0]);
        autoUpdateTimePref.setDialogTitle(R.string.txt_auto_update_delay);
        autoUpdateTimePref.setKey("auto_update_time_preference");
        autoUpdateTimePref.setTitle(R.string.txt_auto_update_delay);
        autoUpdateTimePref.setSummary(R.string.hint_auto_update_delay);
        advancedScreenPref.addPreference(autoUpdateTimePref);
        
        //location of storage
        //set list values
        CharSequence[] saveItemsEntries = {"On Phone", "On SD Card"}; 
        CharSequence[] saveItemsValues = {"phone","card"};
        
        saveItemsPref.setEntries(saveItemsEntries);
        saveItemsPref.setEntryValues(saveItemsValues);
        saveItemsPref.setDefaultValue(saveItemsValues[0]);
        saveItemsPref.setDialogTitle(R.string.option_location);
        saveItemsPref.setKey("save_items_preference");
        saveItemsPref.setTitle(R.string.option_location);
        saveItemsPref.setSummary(R.string.hint_option_location);
        advancedScreenPref.addPreference(saveItemsPref);
        
        //Total reports to fetch at a time
        //set list values
        CharSequence[] totalReportsEntries = {"20 Recent Reports", "40 Recent Reports", "60 Recent Reports", "80 Recent Reports", "100 Recent Reports"}; 
        CharSequence[] totalReportsValues = {"20","40","60","80","100"};
        
        totalReportsPref.setEntries(totalReportsEntries);
        totalReportsPref.setEntryValues(totalReportsValues);
        totalReportsPref.setDefaultValue(totalReportsValues[0]);
        totalReportsPref.setDialogTitle(R.string.total_reports);
        totalReportsPref.setKey("total_reports_preference");
        totalReportsPref.setTitle(R.string.total_reports);
        totalReportsPref.setSummary(R.string.hint_total_reports);
        advancedScreenPref.addPreference(totalReportsPref);
        
        //clear cache
        //clearCacheCheckBoxPref.setKey("clear_cache_preference");
        
        /*PreferenceScreen clearCache = getPreferenceManager().createPreferenceScreen(this);
        clearCache.setKey("clear_cache_preference");
        clearCache.setTitle(R.string.txt_clear_cache);
        clearCache.setSummary(R.string.hint_clear_cache);*/
        advancedScreenPref.addPreference(clearCacheCheckBoxPref);
        
        //SMS Preferences
      
		PreferenceCategory smsPrefCat = new PreferenceCategory(this);
		smsPrefCat.setTitle(R.string.sms_settings);
		root.addPreference(smsPrefCat);
		
	    //Auto fetch reports
        smsCheckBoxPref.setKey("sms_preference");
        smsCheckBoxPref.setTitle(R.string.chk_sms_send);
        smsCheckBoxPref.setSummary(R.string.hint_sms_send);
        smsPrefCat.addPreference(smsCheckBoxPref);
        
        //First name entry field
		userNamePref.setDialogTitle(R.string.txt_user_name);
		userNamePref.setKey("user_name_preference");
		userNamePref.setTitle(R.string.txt_user_name);
		userNamePref.setSummary(R.string.hint_user_name);
		smsPrefCat.addPreference(userNamePref);
		
		//Last name entry field
		passwordPref.setDialogTitle(R.string.txt_password);
		passwordPref.setKey("password_preference");
		passwordPref.setTitle(R.string.txt_password);
		passwordPref.setSummary(R.string.hint_password);
		smsPrefCat.addPreference(passwordPref);
        
		return root;
	}
	
	protected void saveSettings(){
		UshahidiService.domain = ushahidiInstancePref.getText().toString();
		UshahidiService.firstname = firstNamePref.getText();
		UshahidiService.lastname = lastNamePref.getText();
		UshahidiService.email = emailAddressPref.getText();
		UshahidiService.AutoFetch = autoFetchCheckBoxPref.isChecked();
		String autoUpdate = autoUpdateTimePref.getValue();
		String saveItems = saveItemsPref.getValue();
		String totalReports = totalReportsPref.getValue();
		
		//"5 Minutes", "10 Minutes", "15 Minutes", "c", "60 Minutes" 
		if(autoUpdate.matches("5")){
			UshahidiService.AutoUpdateDelay = 5;
		} else if(autoUpdate.matches("10")){
			UshahidiService.AutoUpdateDelay = 10;
		} else if(autoUpdate.matches("15")){
			UshahidiService.AutoUpdateDelay = 15;
		} else if(autoUpdate.matches("30")){
			UshahidiService.AutoUpdateDelay = 30;
		} else if(autoUpdate.matches("60")){
			UshahidiService.AutoUpdateDelay = 60;
		}
		String newSavePath = "";
		if( saveItems.equalsIgnoreCase("phone")){
			newSavePath = "/data/data/org.addhen.ushahidi/files/";
		
		} else {	//means on sd is checked
			
			newSavePath = "/sdcard" + "ushahidi";
		}
		UshahidiService.savePath = newSavePath;
		
		//Total reports
		UshahidiService.totalReports = totalReports;
		
		//sms
		UshahidiService.smsUpdate = smsCheckBoxPref.isChecked();
		UshahidiService.username = userNamePref.getText();
		UshahidiService.password = passwordPref.getText();
		
		UshahidiService.saveSettings(this);
	}
	
	 @Override
	 protected void onResume() {
		 super.onResume();
		 // Set up a listener whenever a key changes
		 getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		 
	 }

	 @Override
	 protected void onPause() {
		 super.onPause();

	        // Unregister the listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	        
	 }
	 public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        // Let's do something when my counter preference value changes
		 
		 if( sharedPreferences.getBoolean(AUTO_FETCH_PREFERENCE, false) ) {
			 
			 startService(new Intent(Settings.this, UshahidiService.class));
		 } else {
			 stopService(new Intent(Settings.this, UshahidiService.class));
		 }
		 
		 //Reset the 
		 if( sharedPreferences.getBoolean(SMS_PREFERENCE, false)) {
			
			 UshahidiService.smsUpdate = true;
			 
		 } else {
		
			 UshahidiService.smsUpdate = false;	
			 
		 }
		 
		 //cache 
		 if(key.equals("clear_cache_preference")) {
			 Log.i("Tag","clear cache was clicked");
			 showDialog(DIALOG_CLEAR_CACHE);
		 }
		 
	 }
	 
	 @Override
	 protected Dialog onCreateDialog( int id ) {
		 switch (id) {
		 	case DIALOG_CLEAR_CACHE: {
		 		AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Clear cache");
                dialog.setMessage("Are you sure you want to clear the cache?");
                dialog.setButton("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//clear cache
						dialog.dismiss();
					}
                });
                dialog.setButton2("Cancel", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
                });
                
                dialog.setCancelable(false);
                return dialog;
		 	}
		 }
		 return null;
	 }
	
}
