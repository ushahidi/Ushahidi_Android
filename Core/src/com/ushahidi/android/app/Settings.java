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

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;

import com.ushahidi.android.app.ui.SeekBarPreference;
import com.ushahidi.android.app.util.Util;

public class Settings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private EditTextPreference firstNamePref;

	private EditTextPreference lastNamePref;

	private EditTextPreference emailAddressPref;

	private EditTextPreference phoneNumberPref;

	private ListPreference totalReportsPref;

	private ListPreference mapTileProviderPref;

	private SeekBarPreference photoSizePref;

	private SharedPreferences settings;

	private SharedPreferences.Editor editor;

	public static final String EMAIL_ADDRESS_PREFERENCE = "email_address_preference";

	public static final String PHONE_NUMBER_PREFERENCE = "phone_number_preference";

	public static final String CHECKIN_PREFERENCE = "checkin_preference";

	public static final String PHOTO_SIZE_PREFERENCE = "photo_size_preference";

	public static final String MAP_TILE_PROVIDER_PREFERENCE = "map_tile_provider_preference";

	private String recentReports = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		firstNamePref = new EditTextPreference(this);

		lastNamePref = new EditTextPreference(this);

		emailAddressPref = new EditTextPreference(this);

		phoneNumberPref = new EditTextPreference(this);

		photoSizePref = (SeekBarPreference) getPreferenceScreen()
				.findPreference(PHOTO_SIZE_PREFERENCE);
		photoSizePref.setMax(Util.getScreenWidth(this));
		recentReports = getString(R.string.recent_reports);
		totalReportsPref = new ListPreference(this);
		mapTileProviderPref = new ListPreference(this);

		new ListPreference(this);

		setPreferenceScreen(createPreferenceHierarchy());

		this.saveSettings();
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// ROOT element
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
				this);

		// Basic preferences
		PreferenceCategory basicPrefCat = new PreferenceCategory(this);
		basicPrefCat.setTitle(R.string.basic_settings);
		root.addPreference(basicPrefCat);

		// Total reports to fetch at a time
		// set list values
		// TODO:// need to look into how to properly handle this. It looks ugly
		// but it works.
		CharSequence[] totalReportsEntries = { "20 ".concat(recentReports),
				"40 ".concat(recentReports), "60 ".concat(recentReports),
				"80 ".concat(recentReports), "100 ".concat(recentReports),
				"250 ".concat(recentReports), "500 ".concat(recentReports),
				"1000 ".concat(recentReports) };

		CharSequence[] totalReportsValues = { "20", "40", "60", "80", "100",
				"250", "500", "1000" };

		CharSequence[] mapTileEntries = { "Google Tiles", "OSM Tiles ",
				"Mapbox Tiles" };

		CharSequence[] mapTileValues = { "google", "osm", "mapbox" };

		totalReportsPref.setEntries(totalReportsEntries);
		totalReportsPref.setEntryValues(totalReportsValues);
		totalReportsPref.setDefaultValue(totalReportsValues[0]);
		totalReportsPref.setDialogTitle(R.string.total_reports);
		totalReportsPref.setKey("total_reports_preference");
		totalReportsPref.setTitle(R.string.total_reports);
		totalReportsPref.setSummary(R.string.hint_total_reports);
		basicPrefCat.addPreference(totalReportsPref);

		// Map tile preference
		mapTileProviderPref.setEntries(mapTileEntries);
		mapTileProviderPref.setKey(MAP_TILE_PROVIDER_PREFERENCE);
		mapTileProviderPref.setTitle(R.string.map_tiles);
		mapTileProviderPref.setSummary(R.string.map_tiles_summary);
		mapTileProviderPref.setDialogTitle(R.string.map_tiles);
		mapTileProviderPref.setEntryValues(mapTileValues);
		mapTileProviderPref.setDefaultValue(mapTileValues[0]);
		basicPrefCat.addPreference(mapTileProviderPref);

		// First name entry field
		firstNamePref.setDialogTitle(R.string.txt_first_name);
		firstNamePref.setKey("first_name_preference");
		firstNamePref.setTitle(R.string.txt_first_name);
		firstNamePref.setSummary(R.string.hint_first_name);
		firstNamePref.getEditText().setInputType(
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		basicPrefCat.addPreference(firstNamePref);

		// Last name entry field
		lastNamePref.setDialogTitle(R.string.txt_last_name);
		lastNamePref.setKey("last_name_preference");
		lastNamePref.setTitle(R.string.txt_last_name);
		lastNamePref.setSummary(R.string.hint_last_name);
		lastNamePref.getEditText().setInputType(
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		basicPrefCat.addPreference(lastNamePref);

		// Email name entry field
		emailAddressPref.setDialogTitle(R.string.txt_email);
		emailAddressPref.setKey("email_address_preference");
		emailAddressPref.setTitle(R.string.txt_email);
		emailAddressPref.setSummary(R.string.hint_email);
		emailAddressPref.getEditText().setInputType(
				InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		basicPrefCat.addPreference(emailAddressPref);

		// phone number entry field
		phoneNumberPref.setDialogTitle(R.string.txt_phonenumber);
		phoneNumberPref.setKey("phone_number_preference");
		phoneNumberPref.setTitle(R.string.txt_phonenumber);
		phoneNumberPref.setSummary(R.string.hint_phonenumber);
		phoneNumberPref.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

		// GeneratePhotoFilename resize seekbar
		basicPrefCat.addPreference(photoSizePref);

		return root;
	}

	protected void saveSettings() {

		settings = getSharedPreferences(Preferences.PREFS_NAME, 0);
		editor = settings.edit();

		String totalReports = totalReportsPref.getValue();
		String  mapTiles = mapTileProviderPref.getValue();
		editor.putString("Domain", Preferences.domain);
		editor.putString("Firstname", firstNamePref.getText());
		editor.putString("Lastname", lastNamePref.getText());
		editor.putString("Email", emailAddressPref.getText());
		editor.putString("Phonenumber", phoneNumberPref.getText());
		editor.putString("TotalReports", totalReports);
		editor.putString("MapTiles", mapTiles);
		editor.putInt("CheckinEnabled", Preferences.isCheckinEnabled);
		editor.putInt("PhotoWidth", photoSizePref.getProgress());
		editor.commit();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		this.saveSettings();

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		// photo size
		if (key.equals(PHOTO_SIZE_PREFERENCE)) {
			if (sharedPreferences.getInt(PHOTO_SIZE_PREFERENCE, 200) > Preferences.photoWidth) {
				Preferences.photoWidth = sharedPreferences.getInt(
						PHOTO_SIZE_PREFERENCE, 200);

			}
		}

		// validate email address
		if (key.equals(EMAIL_ADDRESS_PREFERENCE)) {
			if (!Util.validateEmail(sharedPreferences.getString(
					EMAIL_ADDRESS_PREFERENCE, ""))) {
				Util.showToast(this, R.string.invalid_email_address);
			}
		}

		// save changes
		this.saveSettings();

	}

}
