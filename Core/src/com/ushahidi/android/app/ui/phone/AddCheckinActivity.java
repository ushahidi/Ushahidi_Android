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

package com.ushahidi.android.app.ui.phone;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseEditMapActivity;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.services.UploadCheckins;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.AddCheckinView;

/**
 * @author eyedol
 */
public class AddCheckinActivity extends
		BaseEditMapActivity<AddCheckinView, AddCheckinModel> implements
		OnClickListener, ViewSwitcher.ViewFactory, OnItemClickListener {

	private boolean mError = false;

	private int id = 0;

	private UploadPhotoAdapter pendingPhoto;

	private String mErrorMessage;

	private final String PENDING_FOLDER = "pending/";

	private String photoName;

	private double latitude;

	private double longitude;

	private static final int DIALOG_ERROR_NETWORK = 0;

	private static final int DIALOG_ERROR_SAVING = 1;

	private static final int DIALOG_CHOOSE_IMAGE_METHOD = 2;

	private static final int DIALOG_SHOW_MESSAGE = 3;

	private static final int DIALOG_SHOW_REQUIRED = 4;

	private static final int DIALOG_SHOW_PROMPT = 5;

	private static final int DIALOG_SHOW_DELETE_PROMPT = 6;

	private static final int REQUEST_CODE_CAMERA = 0;

	private static final int REQUEST_CODE_IMAGE = 1;

	private AddCheckinModel model;

	private String locationName;

	private Intent uploadCheckins;

	public AddCheckinActivity() {
		super(AddCheckinView.class, R.layout.add_checkin, R.menu.add_checkin,
				R.id.checkin_location_map);
		model = new AddCheckinModel();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.id = getIntent().getExtras().getInt("id", 0);

		mapController = view.mMapView.getController();
		view.mPickPhoto.setOnClickListener(this);
		pendingPhoto = new UploadPhotoAdapter(this);
		view.gallery.setAdapter(pendingPhoto);
		view.gallery.setOnItemClickListener(this);
		view.mSwitcher.setFactory(this);
		// edit existing report
		if (id > 0) {

			// make the delete button visible because we're editing
			view.mDeleteCheckin.setOnClickListener(this);
			view.mDeleteCheckin.setVisibility(View.VISIBLE);
			setSavedCheckins(id);
		} else {
			// add a new report
			pendingPhoto.refresh();
		}

		registerForContextMenu(view.gallery);

		hidePersonalInfo();

	}

	// Context Menu Stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		new MenuInflater(this).inflate(R.menu.photo_context, menu);

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		boolean result = performAction(item, info.position);

		if (!result) {
			result = super.onContextItemSelected(item);
		}

		return result;

	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(uploadBroadcastReceiver, new IntentFilter(
				SyncServices.UPLOAD_CHECKIN_SERVICES_ACTION));

	}

	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(uploadBroadcastReceiver);
		} catch (IllegalArgumentException e) {
		}
	}

	public boolean performAction(android.view.MenuItem item, int position) {

		if (item.getItemId() == R.id.remove_photo) {

			// adding a new report
			if (id == 0) {

				// Delete by name
				if (ImageManager.deletePendingPhoto(this, "/"
						+ pendingPhoto.getItem(position).getPhoto())) {
					pendingPhoto.refresh();
				}
				return true;
			} else {

				// edit existing report
				if (ImageManager.deletePendingPhoto(this, "/"
						+ pendingPhoto.getItem(position).getPhoto())) {

					pendingPhoto.removeItem(position);
				}
				return true;
			}

		}
		return false;

	}

	private void setSavedCheckins(int id) {
		Checkin checkin = model.fetchPendingCheckinById(id);
		if (checkin != null) {
			this.latitude = Double.valueOf(checkin.getLocationLatitude());
			this.longitude = Double.valueOf(checkin.getLocationLongitude());
			view.mCheckinMessageText.setText(checkin.getMessage());
			view.mCheckinLocation.setText(String.format("%f, %f", latitude,
					longitude));
			// set the photos
			pendingPhoto.refresh(id);
		}
	}

	/**
	 * Validate checkin before sending
	 */
	private void validateCheckins() {
		// Validate so empty text isn't sent over
		mError = false;
		boolean required = false;
		// validate the title field
		if (TextUtils.isEmpty(view.mCheckinMessageText.getText())) {
			mErrorMessage = getString(R.string.checkin_empty_message) + "\n";
			required = true;

		} else if (view.mCheckinMessageText.getText().length() < 3
				|| view.mCheckinMessageText.getText().length() > 200) {
			mErrorMessage = getString(R.string.checkin_empty_message) + "\n";
			mError = true;
		}

		if (required) {
			showDialog(DIALOG_SHOW_REQUIRED);
		} else if (mError) {
			showDialog(DIALOG_SHOW_MESSAGE);
		} else {
			addCheckins();
		}

	}

	/**
	 * Hide contact info if first and last names are set at the settings screen
	 */
	private void hidePersonalInfo() {
		// contact
		if ((!TextUtils.isEmpty(Preferences.firstname))
				&& (!TextUtils.isEmpty(Preferences.lastname))
				&& (!TextUtils.isEmpty(Preferences.email))) {

			view.mContactLabel.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(Preferences.firstname)) {
			view.mFirstNameLabel.setVisibility(View.GONE);
			view.mFirstName.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(Preferences.lastname)) {
			view.mLastNameLabel.setVisibility(View.GONE);
			view.mLastName.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(Preferences.email)) {
			view.mEmaiLabel.setVisibility(View.GONE);
			view.mEmailAddress.setVisibility(View.GONE);
		}

		view.mFirstName.setText(Preferences.firstname);
		view.mLastName.setText(Preferences.lastname);
		view.mEmailAddress.setText(Preferences.email);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			showDialog();
			return true;

		} else if (item.getItemId() == R.id.menu_send_checkin) {

			validateCheckins();
			return true;
		} else if (item.getItemId() == R.id.menu_cancel_checkin) {

			showDialog(DIALOG_SHOW_PROMPT);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CODE_CAMERA) {

				Uri uri = PhotoUtils.getPhotoUri(photoName, this);
				Bitmap bitmap = PhotoUtils.getCameraPhoto(this, uri);
				PhotoUtils.savePhoto(this, bitmap, photoName);
				log(String.format("REQUEST_CODE_CAMERA %dx%d",
						bitmap.getWidth(), bitmap.getHeight()));

			} else if (requestCode == REQUEST_CODE_IMAGE) {

				Bitmap bitmap = PhotoUtils
						.getGalleryPhoto(this, data.getData());
				PhotoUtils.savePhoto(this, bitmap, photoName);
				log(String.format("REQUEST_CODE_IMAGE %dx%d",
						bitmap.getWidth(), bitmap.getHeight()));
			}

			if (id > 0) {
				addPhotoToCheckin();
			} else {
				pendingPhoto.refresh();
			}
		}
	}

	@Override
	public void onClick(View button) {
		if (button.getId() == R.id.checkin_photo_button) {
			// get a file name for the photo to be uploaded
			photoName = Util.getDateTime() + ".jpg";
			showDialog(DIALOG_CHOOSE_IMAGE_METHOD);

		} else if (button.getId() == R.id.delete_checkin) {
			showDialog(DIALOG_SHOW_DELETE_PROMPT);
		}

	}

	/**
	 * Set photo to be attached to a pending checkin
	 */
	private void addPhotoToCheckin() {
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
		if (pendingPhotos != null && pendingPhotos.length > 0) {
			int id = 0;
			for (File file : pendingPhotos) {
				if (file.exists()) {

					id += 1;
					Photo photo = new Photo();
					photo.setDbId(id);
					photo.setPhoto(PENDING_FOLDER + file.getName());
					pendingPhoto.addItem(photo);
				}
			}
		}
	}

	/**
	 * Delete any existing photo in the pending folder
	 */
	private void deleteExistingPhoto() {
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
		if (pendingPhotos != null && pendingPhotos.length > 0) {
			for (File file : pendingPhotos) {
				if (file != null) {
					if (file.exists()) {
						file.delete();
					}
				}
			}
		}
	}

	private void deleteCheckins() {

		// make sure it's an existing report
		if (id > 0) {
			if (model.deleteCheckin(id)) {

				// delete images
				for (int i = 0; i < pendingPhoto.getCount(); i++) {
					ImageManager.deletePendingPhoto(this, "/"
							+ pendingPhoto.getItem(i).getPhoto());
				}

				// return to checkin listing page.
				goToCheckin();
			}
		}
	}

	private void addCheckins() {
		view.dialog.show();
		Checkin checkin = new Checkin();
		checkin.setPending(1);
		checkin.setMessage(view.mCheckinMessageText.getText().toString());
		checkin.setLocationLatitude(String.valueOf(this.latitude));
		checkin.setLocationLongitude(String.valueOf(this.longitude));
		checkin.setDate((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
				.format(new Date()));
		// set location to unknown so to save failed checkin to a database.
		checkin.setLocationName(getString(R.string.unknown));

		Bundle checkins = new Bundle();
		checkins.putString("firstname", view.mFirstName.getText().toString());
		checkins.putString("lastname", view.mLastName.getText().toString());
		checkins.putString("email", view.mEmailAddress.getText().toString());
		checkins.putInt("pending", 1);
		checkins.putString("message", view.mCheckinMessageText.getText()
				.toString());
		checkins.putString("latitude", String.valueOf(this.latitude));
		checkins.putString("longitude", String.valueOf(this.longitude));
		checkins.putString("date",
				(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
						.format(new Date()));
		checkins.putString("location", getString(R.string.unknown));
		checkins.putInt("id", id);

		uploadCheckins = new Intent(this, UploadCheckins.class);
		uploadCheckins.putExtras(checkins);
		startService(uploadCheckins);

	}

	private void goToCheckin() {
		finish();
	}

	/**
	 * Create various dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ERROR_NETWORK: {
			AlertDialog dialog = (new AlertDialog.Builder(this)).create();
			dialog.setTitle(getString(R.string.network_error));
			dialog.setMessage(getString(R.string.network_error_msg));
			dialog.setButton2(getString(R.string.ok),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setCancelable(false);
			return dialog;
		}
		case DIALOG_ERROR_SAVING: {
			AlertDialog dialog = (new AlertDialog.Builder(this)).create();
			dialog.setTitle(getString(R.string.network_error));
			dialog.setMessage(getString(R.string.file_system_error_msg));
			dialog.setButton2(getString(R.string.ok),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setCancelable(false);
			return dialog;
		}

		case DIALOG_CHOOSE_IMAGE_METHOD: {

			AlertDialog dialog = (new AlertDialog.Builder(this)).create();
			dialog.setTitle(getString(R.string.choose_method));
			dialog.setMessage(getString(R.string.how_to_select_pic));
			dialog.setButton(getString(R.string.gallery_option),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteExistingPhoto();
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_PICK);
							intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, REQUEST_CODE_IMAGE);
							dialog.dismiss();
						}
					});
			dialog.setButton2(getString(R.string.cancel),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setButton3(getString(R.string.camera_option),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteExistingPhoto();
							Intent intent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, PhotoUtils
									.getPhotoUri(photoName,
											AddCheckinActivity.this));
							startActivityForResult(intent, REQUEST_CODE_CAMERA);
							dialog.dismiss();
						}
					});

			dialog.setCancelable(false);
			return dialog;
		}

		case DIALOG_SHOW_MESSAGE:
			AlertDialog.Builder messageBuilder = new AlertDialog.Builder(this);
			messageBuilder.setMessage(mErrorMessage).setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog showDialog = messageBuilder.create();
			showDialog.show();
			break;

		case DIALOG_SHOW_REQUIRED:
			AlertDialog.Builder requiredBuilder = new AlertDialog.Builder(this);
			requiredBuilder.setTitle(R.string.required_fields);
			requiredBuilder.setMessage(mErrorMessage).setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog showRequiredDialog = requiredBuilder.create();
			showRequiredDialog.show();
			break;

		// prompt for unsaved changes
		case DIALOG_SHOW_PROMPT: {
			AlertDialog dialog = (new AlertDialog.Builder(this)).create();
			dialog.setTitle(getString(R.string.unsaved_changes));
			dialog.setMessage(getString(R.string.want_to_cancel));
			dialog.setButton(getString(R.string.no),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});
			dialog.setButton2(getString(R.string.yes),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// delete any existing photo in the pending folder
							new DiscardTask(AddCheckinActivity.this)
									.execute((String) null);
							finish();
							dialog.dismiss();
						}
					});

			dialog.setCancelable(false);
			return dialog;
		}

		// prompt for report deletion
		case DIALOG_SHOW_DELETE_PROMPT: {
			AlertDialog dialog = (new AlertDialog.Builder(this)).create();
			dialog.setTitle(getString(R.string.delete_report));
			dialog.setMessage(getString(R.string.want_to_delete));
			dialog.setButton(getString(R.string.no),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setButton2(getString(R.string.yes),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// delete checkin
							deleteCheckins();
							dialog.dismiss();
						}
					});

			dialog.setCancelable(false);
			return dialog;
		}

		}
		return null;
	}

	@Override
	protected boolean onSaveChanges() {
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onLocationChanged(Location location) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected void locationChanged(double latitude, double longitude) {
		updateMarker(latitude, longitude, true);
		this.latitude = latitude;
		this.longitude = longitude;

		if (TextUtils.isEmpty(locationName)) {
			locationName = getString(R.string.unknown);
		}

		view.mCheckinLocation.setText(String.format("%f, %f", latitude,
				longitude));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ViewSwitcher.ViewFactory#makeView()
	 */
	@Override
	public View makeView() {

		ImageView i = new ImageView(this);
		i.setAdjustViewBounds(true);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		return i;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 *      .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.view.mSwitcher.setImageDrawable(ImageManager.getPendingDrawables(
				this, pendingPhoto.getItem(position).getPhoto(),
				Util.getScreenWidth(this)));
	}

	private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int status = intent.getIntExtra("status", 3);
				stopService(uploadCheckins);
				try {
					unregisterReceiver(uploadBroadcastReceiver);
				} catch (IllegalArgumentException e) {
				}
				view.dialog.cancel();
				if (status == 0) {
					toastLong(getString(R.string.uploaded));

				} else if (status == 1 || status == 2) {
					toastLong(R.string.saved);
				}

			} else {
				toastLong(R.string.failed);
			}
			goToCheckin();

		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.activities.BaseEditMapActivity#onDiscardChanges
	 * ()
	 */
	@Override
	protected boolean onDiscardChanges() {
		deleteExistingPhoto();
		return true;
	}
}
