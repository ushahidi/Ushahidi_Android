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

package com.ushahidi.android.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private EditTextPreference ushahidiInstancePref;

    private EditTextPreference firstNamePref;

    private EditTextPreference lastNamePref;

    private EditTextPreference emailAddressPref;

    private CheckBoxPreference autoFetchCheckBoxPref;

    private CheckBoxPreference vibrateCheckBoxPref;

    private CheckBoxPreference ringtoneCheckBoxPref;

    private CheckBoxPreference flashLedCheckBoxPref;

    private DialogPreference clearCacheCheckBoxPref;

    private ListPreference autoUpdateTimePref;

    private ListPreference saveItemsPref;

    private ListPreference totalReportsPref;

    private SharedPreferences settings;

    private SharedPreferences.Editor editor;

    private boolean validUrl = false;

    private final Handler mHandler = new Handler();

    public static final String AUTO_FETCH_PREFERENCE = "auto_fetch_preference";

    public static final String SMS_PREFERENCE = "sms_preference";

    public static final String VIBRATE_PREFERENCE = "vibrate_preference";

    public static final String RINGTONE_PREFERENCE = "ringtone_preference";

    public static final String FLASH_LED_PREFERENCE = "flash_led_preference";

    public static final String USHAHIDI_DEPLOYMENT_PREFERENCE = "ushahidi_instance_preference";

    public static final String EMAIL_ADDRESS_PREFERENCE = "email_address_preference";

    public static final String CHECKIN_PREFERENCE = "checkin_preference";

    private boolean checkin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        ushahidiInstancePref = new EditTextPreference(this);

        firstNamePref = new EditTextPreference(this);

        lastNamePref = new EditTextPreference(this);

        emailAddressPref = new EditTextPreference(this);

        autoFetchCheckBoxPref = new CheckBoxPreference(this);
        vibrateCheckBoxPref = new CheckBoxPreference(this);
        ringtoneCheckBoxPref = new CheckBoxPreference(this);
        flashLedCheckBoxPref = new CheckBoxPreference(this);

        clearCacheCheckBoxPref = (DialogPreference)getPreferenceScreen().findPreference(
                "clear_cache_preference");
        autoUpdateTimePref = new ListPreference(this);
        saveItemsPref = new ListPreference(this);
        totalReportsPref = new ListPreference(this);
        new ListPreference(this);

        setPreferenceScreen(createPreferenceHierarchy());
        this.saveSettings();
    }

    private PreferenceScreen createPreferenceHierarchy() {
        // ROOT element
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // Basic preferences
        PreferenceCategory basicPrefCat = new PreferenceCategory(this);
        basicPrefCat.setTitle(R.string.basic_settings);
        root.addPreference(basicPrefCat);

        // URL entry field
        ushahidiInstancePref.setDialogTitle(R.string.txt_domain);
        ushahidiInstancePref.setKey("ushahidi_instance_preference");
        ushahidiInstancePref.setTitle(R.string.txt_domain);
        ushahidiInstancePref.setDefaultValue("http://");
        ushahidiInstancePref.setSummary(R.string.hint_domain);
        ushahidiInstancePref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        basicPrefCat.addPreference(ushahidiInstancePref);

        // Total reports to fetch at a time
        // set list values
        CharSequence[] totalReportsEntries = {
                "20 Recent Reports", "40 Recent Reports", "60 Recent Reports", "80 Recent Reports",
                "100 Recent Reports", "250 Recent Reports", "500 Recent Reports",
                "1000 Recent Reports"
        };
        CharSequence[] totalReportsValues = {
                "20", "40", "60", "80", "100", "250", "500", "1000"
        };

        totalReportsPref.setEntries(totalReportsEntries);
        totalReportsPref.setEntryValues(totalReportsValues);
        totalReportsPref.setDefaultValue(totalReportsValues[0]);
        totalReportsPref.setDialogTitle(R.string.total_reports);
        totalReportsPref.setKey("total_reports_preference");
        totalReportsPref.setTitle(R.string.total_reports);
        totalReportsPref.setSummary(R.string.hint_total_reports);
        basicPrefCat.addPreference(totalReportsPref);

        // First name entry field
        firstNamePref.setDialogTitle(R.string.txt_first_name);
        firstNamePref.setKey("first_name_preference");
        firstNamePref.setTitle(R.string.txt_first_name);
        firstNamePref.setSummary(R.string.hint_first_name);
        firstNamePref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        basicPrefCat.addPreference(firstNamePref);

        // Last name entry field
        lastNamePref.setDialogTitle(R.string.txt_last_name);
        lastNamePref.setKey("last_name_preference");
        lastNamePref.setTitle(R.string.txt_last_name);
        lastNamePref.setSummary(R.string.hint_last_name);
        lastNamePref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        basicPrefCat.addPreference(lastNamePref);

        // Email name entry field
        emailAddressPref.setDialogTitle(R.string.txt_email);
        emailAddressPref.setKey("email_address_preference");
        emailAddressPref.setTitle(R.string.txt_email);
        emailAddressPref.setSummary(R.string.hint_email);
        emailAddressPref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        basicPrefCat.addPreference(emailAddressPref);

        // Advanced Preferences
        PreferenceCategory advancedPrefCat = new PreferenceCategory(this);
        advancedPrefCat.setTitle(R.string.advanced_settings);
        root.addPreference(advancedPrefCat);

        PreferenceScreen advancedScreenPref = getPreferenceManager().createPreferenceScreen(this);
        advancedScreenPref.setKey("advanced_screen_preference");
        advancedScreenPref.setTitle(R.string.advanced_settings);
        advancedScreenPref.setSummary(R.string.hint_advanced_settings);
        advancedPrefCat.addPreference(advancedScreenPref);

        // Auto fetch reports
        autoFetchCheckBoxPref.setKey("auto_fetch_preference");
        autoFetchCheckBoxPref.setTitle(R.string.chk_auto_fetch);
        autoFetchCheckBoxPref.setSummary(R.string.hint_auto_fetch);
        advancedScreenPref.addPreference(autoFetchCheckBoxPref);

        // Auto update reports time interval
        // set list values
        CharSequence[] autoUpdateEntries = {
                "5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "60 Minutes"
        };
        CharSequence[] autoUpdateValues = {
                "0", "5", "10", "15", "30", "60"
        };
        autoUpdateTimePref.setEntries(autoUpdateEntries);
        autoUpdateTimePref.setEntryValues(autoUpdateValues);
        autoUpdateTimePref.setDefaultValue(autoUpdateValues[0]);
        autoUpdateTimePref.setDialogTitle(R.string.txt_auto_update_delay);
        autoUpdateTimePref.setKey("auto_update_time_preference");
        autoUpdateTimePref.setTitle(R.string.txt_auto_update_delay);
        autoUpdateTimePref.setSummary(R.string.hint_auto_update_delay);
        advancedScreenPref.addPreference(autoUpdateTimePref);

        // location of storage
        // set list values
        CharSequence[] saveItemsEntries = {
                "On Phone", "On SD Card"
        };
        CharSequence[] saveItemsValues = {
                "phone", "card"
        };

        saveItemsPref.setEntries(saveItemsEntries);
        saveItemsPref.setEntryValues(saveItemsValues);
        saveItemsPref.setDefaultValue(saveItemsValues[0]);
        saveItemsPref.setDialogTitle(R.string.option_location);
        saveItemsPref.setKey("save_items_preference");
        saveItemsPref.setTitle(R.string.option_location);
        saveItemsPref.setSummary(R.string.hint_option_location);
        advancedScreenPref.addPreference(saveItemsPref);

        // clear cache
        advancedScreenPref.addPreference(clearCacheCheckBoxPref);

        // notification Preferences
        PreferenceCategory notificationPrefCat = new PreferenceCategory(this);
        notificationPrefCat.setTitle(R.string.bg_notification);
        advancedScreenPref.addPreference(notificationPrefCat);

        // vibrate
        vibrateCheckBoxPref.setKey("vibrate_preference");
        vibrateCheckBoxPref.setTitle(R.string.vibrate);
        vibrateCheckBoxPref.setSummary(R.string.hint_vibrate);
        notificationPrefCat.addPreference(vibrateCheckBoxPref);

        // ringtone
        ringtoneCheckBoxPref.setKey("ringtone_preference");
        ringtoneCheckBoxPref.setTitle(R.string.ringtone);
        ringtoneCheckBoxPref.setSummary(R.string.hint_ringtone);
        notificationPrefCat.addPreference(ringtoneCheckBoxPref);

        // flash led
        flashLedCheckBoxPref.setKey("flash_led_preference");
        flashLedCheckBoxPref.setTitle(R.string.flash_led);
        flashLedCheckBoxPref.setSummary(R.string.hint_flash_led);
        notificationPrefCat.addPreference(flashLedCheckBoxPref);

        return root;
    }

    protected void saveSettings() {

        settings = getSharedPreferences(UshahidiPref.PREFS_NAME, 0);
        editor = settings.edit();

        String autoUpdate = autoUpdateTimePref.getValue();
        String saveItems = saveItemsPref.getValue();
        String totalReports = totalReportsPref.getValue();
        String newSavePath;
        int autoUdateDelay = 0;

        // "5 Minutes", "10 Minutes", "15 Minutes", "c", "60 Minutes"
        if (autoUpdate.matches("5")) {
            autoUdateDelay = 5;
        } else if (autoUpdate.matches("10")) {
            autoUdateDelay = 10;
        } else if (autoUpdate.matches("15")) {
            autoUdateDelay = 15;
        } else if (autoUpdate.matches("30")) {
            autoUdateDelay = 30;
        } else if (autoUpdate.matches("60")) {
            autoUdateDelay = 60;
        }

        if (saveItems.equalsIgnoreCase("phone")) {
            newSavePath = this.getDir("", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE).toString();

        } else { // means on sd is checked
            newSavePath = Environment.getExternalStorageDirectory().toString() + "ushahidi";
        }

        editor.putString("Domain", ushahidiInstancePref.getText().toString().trim());
        editor.putString("Firstname", firstNamePref.getText());
        editor.putString("Lastname", lastNamePref.getText());
        editor.putString("Email", emailAddressPref.getText());
        editor.putString("savePath", newSavePath);
        editor.putInt("AutoUpdateDelay", autoUdateDelay);
        editor.putBoolean("AutoFetch", autoFetchCheckBoxPref.isChecked());
        editor.putString("TotalReports", totalReports);
        editor.putInt("CheckinEnabled", UshahidiPref.isCheckinEnabled);
        editor.commit();

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
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something when a preference value changes

        if (sharedPreferences.getBoolean(AUTO_FETCH_PREFERENCE, false)) {

            startService(new Intent(Settings.this, UshahidiPref.class));
        } else {
            stopService(new Intent(Settings.this, UshahidiPref.class));
        }

        // Reset vibrate
        if (sharedPreferences.getBoolean(VIBRATE_PREFERENCE, false)) {
            UshahidiPref.vibrate = true;
        } else {
            UshahidiPref.vibrate = false;
        }

        // Reset ringtone
        if (sharedPreferences.getBoolean(RINGTONE_PREFERENCE, false)) {

            UshahidiPref.ringtone = true;
        } else {

            UshahidiPref.ringtone = false;
        }

        // Reset flash led
        if (sharedPreferences.getBoolean(FLASH_LED_PREFERENCE, false)) {

            UshahidiPref.flashLed = true;
        } else {
            UshahidiPref.flashLed = false;
        }

        // cache
        if (key.equals(USHAHIDI_DEPLOYMENT_PREFERENCE)) {
            if (!sharedPreferences.getString(USHAHIDI_DEPLOYMENT_PREFERENCE, "").equals(
                    UshahidiPref.domain)) {
                validateUrl(sharedPreferences.getString(USHAHIDI_DEPLOYMENT_PREFERENCE, ""));

            }
        }

        // validate email address
        if (key.equals(EMAIL_ADDRESS_PREFERENCE)) {
            if (!Util.validateEmail(sharedPreferences.getString(EMAIL_ADDRESS_PREFERENCE, ""))) {
                Util.showToast(this, R.string.invalid_email_address);
            }
        }

        // save changes
        this.saveSettings();

    }

    // thread class
    private class ReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        private ProgressDialog dialog;

        protected Context appContext;

        @Override
        protected void onPreExecute() {

            UshahidiPref.loadSettings(appContext);
            this.dialog = ProgressDialog.show(appContext, getString(R.string.please_wait),
                    getString(R.string.fetching_new_reports), true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            // clear previous data
            UshahidiApplication.mDb.clearData();
            status = Util.processReports(appContext);
            isCheckinsEnabled();
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 4) {
                Util.showToast(appContext, R.string.internet_connection);
            } else if (result == 3) {
                Util.showToast(appContext, R.string.invalid_ushahidi_instance);
            } else if (result == 2) {
                Util.showToast(appContext, R.string.no_report);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.no_categories);
            } else {
                Util.showToast(appContext, R.string.reports_successfully_fetched);
            }
            this.dialog.cancel();
        }

    }

    /**
     * Create runnable for validating callback URL.
     */
    Runnable mValidateUrl = new Runnable() {
        public void run() {

            if (!validUrl) {

                // reset whatever was entered in that field.
                ushahidiInstancePref.setText("http://");
                Util.showToast(Settings.this, R.string.invalid_ushahidi_instance);
            } else {

                ReportsTask reportsTask = new ReportsTask();
                reportsTask.appContext = Settings.this;
                reportsTask.execute();
            }
        }
    };

    /**
     * Create a child thread and validate the callback URL in it when enabling
     * 
     * @param String Url - The Callback Url to be validated.
     * @return void
     */
    public void validateUrl(final String Url) {

        Thread t = new Thread() {
            public void run() {

                validUrl = Util.validateUshahidiInstance(Url);
                mHandler.post(mValidateUrl);
            }
        };
        t.start();
    }

    Runnable mIsCheckinsEnabled = new Runnable() {
        public void run() {

            if (checkin) {
                UshahidiPref.isCheckinEnabled = 1;
            } else {
                UshahidiPref.isCheckinEnabled = 0;
            }

            UshahidiPref.saveSettings(Settings.this);

        }
    };

    /**
     * Checks if checkins is enabled on the configured Ushahidi deployment.
     */
    public void isCheckinsEnabled() {
        if (Util.isCheckinEnabled(Settings.this)) {
            UshahidiPref.isCheckinEnabled = 1;
        } else {
            UshahidiPref.isCheckinEnabled = 0;
        }
        UshahidiPref.saveSettings(Settings.this);
    }

}
