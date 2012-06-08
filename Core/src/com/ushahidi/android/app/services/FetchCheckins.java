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
package com.ushahidi.android.app.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.adapters.ListFetchedCheckinAdapter;
import com.ushahidi.android.app.adapters.ListPendingCheckinAdapter;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.net.CheckinHttpClient;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class FetchCheckins extends SyncServices {

	private static String CLASS_TAG = FetchCheckins.class.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 4;

	private ListFetchedCheckinAdapter fetchedAdapter;

	private ListPendingCheckinAdapter pendingAdapter;

	public FetchCheckins() {
		super(CLASS_TAG);
		statusIntent = new Intent(FETCH_CHECKIN_SERVICES_ACTION);	
	}

	private boolean uploadPendingCheckin() {
		fetchedAdapter = new ListFetchedCheckinAdapter(this);
		pendingAdapter = new ListPendingCheckinAdapter(this);
		
		List<ListCheckinModel> items = pendingAdapter.pendingCheckin();
		StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
		urlBuilder.append("/api");
		if (items != null) {
			for (ListCheckinModel checkin : items) {
				final HashMap<String, String> mParams = new HashMap<String, String>();
				mParams.put("task", "checkin");
				mParams.put("action", "ci");
				mParams.put("mobileid", Util.IMEI(this));
				mParams.put("lat", checkin.getLocationLatitude());
				mParams.put("lon", checkin.getLocationLongitude());
				mParams.put("message", checkin.getMessage());
				mParams.put("firstname", Preferences.firstname);
				mParams.put("lastname", Preferences.lastname);
				mParams.put("email", Preferences.email);
				final String photo = new UploadPhotoAdapter(this)
						.pendingPhotos((int) checkin.getCheckinId());

				// load filenames
				if (!TextUtils.isEmpty(photo)) {
					mParams.put("filename", photo);
				}
				// upload
				try {
					if (new CheckinHttpClient(this).PostFileUpload(
							urlBuilder.toString(), mParams)) {
						deletePendingCheckin((int) checkin.getDbId());
						return true;
					}
					return false;
				} catch (IOException e) {
					return false;
				}
			}
		}
		return false;
	}

	private void deleteFetchedCheckin() {
		fetchedAdapter = new ListFetchedCheckinAdapter(this);
		pendingAdapter = new ListPendingCheckinAdapter(this);
		final List<ListCheckinModel> items = fetchedAdapter.fetchedCheckins();
		for (ListCheckinModel checkin : items) {
			if (new ListCheckinModel().deleteAllFetchedCheckin(checkin
					.getCheckinId())) {
				final List<Photo> photos = new ListPhotoModel()
						.getPhotosByCheckinId(checkin.getCheckinId());

				for (Photo photo : photos) {
					ImageManager.deletePendingPhoto(this,
							"/" + photo.getPhoto());
				}
			}

		}
	}

	private void deletePendingCheckin(int checkinId) {
		
		// make sure it's an existing report
		AddCheckinModel model = new AddCheckinModel();
		UploadPhotoAdapter pendingPhoto = new UploadPhotoAdapter(this);
		if (checkinId > 0) {
			if (model.deleteCheckin(checkinId)) {
				// delete images
				for (int i = 0; i < pendingPhoto.getCount(); i++) {
					ImageManager.deletePendingPhoto(this, "/"
							+ pendingPhoto.getItem(i).getPhoto());
				}
				// return to report listing page.
			}
		}
	}

	@Override
	protected void executeTask(Intent intent) {
		fetchedAdapter = new ListFetchedCheckinAdapter(this);
		pendingAdapter = new ListPendingCheckinAdapter(this);
		Log.i(CLASS_TAG, "executeTask() executing this task");
		// check if there is internet
		if (new ApiUtils(this).isConnected()) {

			// upload pending checkins.
			if (!pendingAdapter.isEmpty()) {
				uploadPendingCheckin();
			}

			// delete everything before updating with a new one
			deleteFetchedCheckin();

			status = new CheckinHttpClient(this).getAllCheckinFromWeb();
		}

		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);

	}
}
