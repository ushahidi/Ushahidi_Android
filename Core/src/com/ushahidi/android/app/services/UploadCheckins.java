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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.adapters.ListFetchedCheckinAdapter;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.net.CheckinHttpClient;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class UploadCheckins extends SyncServices {

	private static String CLASS_TAG = UploadCheckins.class.getSimpleName();

	private Intent statusIntent; // holds the status of the sync and sends it to

	private int status = 113;

	private UploadPhotoAdapter pendingPhoto;
	private AddCheckinModel model;

	public UploadCheckins() {
		super(CLASS_TAG);
		statusIntent = new Intent(UPLOAD_CHECKIN_SERVICES_ACTION);
		model = new AddCheckinModel();
	}

	private int addCheckins(Bundle bundle) {

		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
		if (bundle != null) {
			int id = bundle.getInt("id");
			Checkin checkin = new Checkin();
			checkin.setPending(1);
			checkin.setMessage(bundle.getString("message"));
			checkin.setLocationLatitude(bundle.getString("latitude"));
			checkin.setLocationLongitude(bundle.getString("longitude"));
			checkin.setDate(bundle.getString("date"));

			// set location to unknown so to save failed checkin to a database.
			checkin.setLocationName(bundle.getString("location"));
			// upload to the web
			if (!uploadPendingCheckin(bundle)) {
				if (id == 0) {
					// add new checkin
					if (model.addPendingCheckin(checkin, pendingPhotos)) {
						// move saved photos
						ImageManager.movePendingPhotos(this);
						return 1; // successfully added to database.
					}
				} else {

					// update an existing checkin.
					List<PhotoEntity> photos = new ArrayList<PhotoEntity>();
					for (int i = 0; i < pendingPhoto.getCount(); i++) {
						photos.add(pendingPhoto.getItem(i));
					}
					if (model.updatePendingCheckin(id, checkin, photos)) {
						// move saved photos
						ImageManager.movePendingPhotos(this);
						return 2; // update succeeded
					}
				}
				return 3;// upload failed
			}
		}
		return 0; // upload succeeded
	}

	private boolean uploadPendingCheckin(Bundle bundle) {

		final StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
		urlBuilder.append("/api");
		final HashMap<String, String> mParams = new HashMap<String, String>();
		pendingPhoto = new UploadPhotoAdapter(this);
		if (bundle != null) {

		/*	final String photo = new UploadPhotoAdapter(this)
					.pendingCheckinPhotos();*/

			mParams.put("task", "checkin");
			mParams.put("action", "ci");
			mParams.put("mobileid", Util.IMEI(this));
			mParams.put("lat", bundle.getString("latitude"));
			mParams.put("lon", bundle.getString("longitude"));
			mParams.put("message", bundle.getString("message"));
			mParams.put("firstname", bundle.getString("firstname"));
			mParams.put("lastname", bundle.getString("lastname"));
			mParams.put("email", bundle.getString("email"));

			// load filenames
			/*if (!TextUtils.isEmpty(photo)) {
				mParams.put("filename", photo);
			}*/
			// upload
			try {
				if (new CheckinHttpClient(this).PostFileUpload(
						urlBuilder.toString(), mParams)) {
					model.deleteCheckin(0);
					// delete pending photo.
					for (int i = 0; i < pendingPhoto.getCount(); i++) {
						ImageManager.deletePendingPhoto(this, "/"
								+ pendingPhoto.getItem(i).getPhoto());
					}
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;

	}

	private void deleteFetchedCheckin() {
		final List<ListCheckinModel> items = new ListFetchedCheckinAdapter(this)
				.fetchedCheckins();
		for (ListCheckinModel checkin : items) {
			new ListCheckinModel().deleteAllFetchedCheckin(checkin
					.getCheckinId());
		}
		// delete fetched photos
		ImageManager.deleteImages(this);
	}

	@Override
	protected void executeTask(Intent intent) {

		new Util().log("executeTask() executing this task");
		if (intent != null) {
			Bundle bundle = intent.getExtras();

			status = addCheckins(bundle);
			if (status < 3) {
				// get uploaded checkin
				// delete everything before updating with a new one
				deleteFetchedCheckin();

				new CheckinHttpClient(this).getAllCheckinFromWeb();

			}
		}
		statusIntent.putExtra("status", status);
		sendBroadcast(statusIntent);
	}
}
