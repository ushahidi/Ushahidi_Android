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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseEditMapActivity;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.AddCheckinView;

/**
 * @author eyedol
 */
public class AddCheckinActivity extends
		BaseEditMapActivity<AddCheckinView, AddCheckinModel> implements
		OnClickListener {

	private boolean mError = false;

	private int id = 0;

	private UploadPhotoAdapter pendingPhoto;

	private String mErrorMessage;

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

	public AddCheckinActivity() {
		super(AddCheckinView.class, R.layout.add_checkin, R.menu.add_checkin,
				R.id.checkin_location_map);
		model = new AddCheckinModel();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pendingPhoto = new UploadPhotoAdapter(this);
		this.id = getIntent().getExtras().getInt("id", 0);
		mapController = view.mMapView.getController();
		view.mPickPhoto.setOnClickListener(this);
		// Validate so empty text isn't sent over
		view.mCheckinMessageText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							if (TextUtils.isEmpty(view.mCheckinMessageText
									.getText())) {
								view.mCheckinMessageText
										.setError(getString(R.string.checkin_empty_message));
							}
						}
					}
				});
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

				// editing existing report
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
			view.mLastName.setVisibility(View.GONE);
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
			// validateReports();
			return true;
		} else if (item.getItemId() == R.id.menu_cancel_checkin) {
			showDialog(DIALOG_SHOW_PROMPT);
			return true;
		}
		return super.onOptionsItemSelected(item);

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

	private void deleteCheckins() {

	}

	private boolean addCheckins() {
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(this);
		Checkin checkin = new Checkin();
		checkin.setPending(1);
		checkin.setMessage(view.mCheckinMessageText.getText().toString());
		checkin.setLocationLatitude(String.valueOf(this.latitude));
		checkin.setLocationLongitude(String.valueOf(this.longitude));
		if( id == 0 ) {
			//add new checkin
			
		} else {
			//edit an existing checkin.
			
		}
		return false;
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
			dialog.setButton2(getString(R.string.btn_ok),
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
			dialog.setButton2(getString(R.string.btn_ok),
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
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_PICK);
							intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent, REQUEST_CODE_IMAGE);
							dialog.dismiss();
						}
					});
			dialog.setButton2(getString(R.string.btn_cancel),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setButton3(getString(R.string.camera_option),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
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
		addCheckins();
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
		view.mCheckinLocation.setText(String.format("%f, %f", latitude,
				longitude));
	}

}
