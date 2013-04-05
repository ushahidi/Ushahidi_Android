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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.json.UshahidiApiVersion;
import com.ushahidi.java.sdk.UshahidiException;
import com.ushahidi.java.sdk.api.tasks.BaseTask;
import com.ushahidi.java.sdk.net.UshahidiHttpClient;

/**
 * This is a Util class for an Ushahidi deployment API
 * 
 * @author eyedol
 */
public class ApiUtils {

	Context context;

	/**
	 * @param context
	 */
	public ApiUtils(Context context) {
		this.context = context;
		client = new UshahidiHttpClient();
	}

	private static final String CLASS_TAG = Util.class.getSimpleName();

	private static final String VALID_URL_PATTERN = "^(https?|ftp)://[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].+)?$";

	private static Pattern pattern;

	private static Matcher matcher;

	private static JSONObject jsonObject;

	private UshahidiHttpClient client;

	public void clearAllReportData() {

		// clear fields
		Database.mReportCategoryDao.deleteAllReportCategory();

		// clear database
		Database.mReportDao.deleteAllReport();

		// clear data
		Database.mMediaDao.deleteAllMedia();

		// clear up all categories
		Database.mCategoryDao.deleteAllCategories();

	}

	public void clearAllFetchedReportData(int reportId) {

		// clear fields
		Database.mReportCategoryDao.deleteAllReportCategory();

		// clear database
		Database.mReportDao.deleteAllReport();

		// clear data
		Database.mMediaDao.deleteAllMedia();

		// clear up all categories
		Database.mCategoryDao.deleteAllCategories();

	}

	/**
	 * Validate an Ushahidi instance
	 * 
	 * @param String
	 *            - URL to be validated.
	 * @return boolean
	 */
	public static boolean validateUshahidiInstance(String ushahidiUrl) {

		if (!TextUtils.isEmpty(ushahidiUrl)) {
			pattern = Pattern.compile(VALID_URL_PATTERN);
			matcher = pattern.matcher(ushahidiUrl);
			return matcher.matches();
		}

		return false;
	}

	/**
	 * Extract Ushahidi payload JSON data
	 * 
	 * @papram json_data - the json data to be formatted.
	 * @return int 0 - success, 1 - missing parameter, 2 - invalid parameter, 3
	 *         - post failed, 5 - access denied, 6 - access limited, 7 - no
	 *         data, 8 - api disabled, 9 - no task found, 10 - json is wrong
	 */
	public static int extractPayloadJSON(String json_data) {
		try {
			jsonObject = new JSONObject(json_data);
			final String errorCode = jsonObject.getJSONObject("error")
					.getString("code");
			return Integer.parseInt(errorCode);
		} catch (JSONException e) {
			Log.e(CLASS_TAG, e.toString());
			return 10;
		}

	}

	private String trimVersion(String s) {
		return s.replaceAll("[^0-9.]", "");
	}

	/**
	 * Check if an ushahidi deployment has changed it's HTTP protocol to HTTPS
	 * or not. Then update if it has.
	 * 
	 * @param context
	 *            - the calling activity.
	 */
	public void updateDomain() {

		Preferences.loadSettings(context);

		StringBuilder uriBuilder = new StringBuilder(Preferences.domain);
		uriBuilder.append("/api?task=version");
		uriBuilder.append("&resp=json");

		try {
			if (client != null) {
				String jsonString = client
						.sendGetRequest(uriBuilder.toString());
				UshahidiApiVersion ver = BaseTask.fromString(jsonString,
						UshahidiApiVersion.class);

				String domain = ver.getDomain();
				new Util()
						.log(String.format("%s %s ", "Update domain", domain));

				Preferences.domain = domain;

				String ogsVer = client.sendGetRequest(Preferences.domain
						+ "/opengeosms/version");
				Preferences.ogsPluginVersion = ogsVer == null ? ""
						: trimVersion(ogsVer);

				String sms = ver.getSms();
				Preferences.phonenumber = sms != null ? sms : "";
				// save changes
				Preferences.saveSettings(context);
			}

		} catch (UshahidiException e) {
			new Util().log(CLASS_TAG, e);
		}
	}

}
