package com.ushahidi.android.app.checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ushahidi.android.app.ImageCapture;
import com.ushahidi.android.app.R;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 2/15/11
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */

public class CheckinActivity extends Activity {
    private ProgressDialog pd = null;
    private Button checkinButton;
    private Button uploadPhotoButton;
    private EditText checkinMessageText;
    private EditText mSelectedPhoto;

    // Used for the camera
    private static final int REQUEST_CODE_CAMERA = 5;

    // Used to choose the method for picture selection
    private static final int DIALOG_CHOOSE_IMAGE_METHOD = 4;
    private static final int REQUEST_CODE_IMAGE = 4;

    // To interchange information
    private Bundle mBundle;
	private Bundle mExtras;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);

        checkinButton = (Button) findViewById(R.id.perform_checkin_button);
        uploadPhotoButton = (Button) findViewById(R.id.upload_checkin_photo_button);
        checkinMessageText = (EditText) findViewById(R.id.checkin_message_text);
        mSelectedPhoto = (EditText) findViewById(R.id.checkin_selected_photo_text);

        // Perform the checkin
        checkinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                performCheckin(checkinMessageText.getText().toString());
            }
        });

        // Uploading a photo for the checkin
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    showDialog(DIALOG_CHOOSE_IMAGE_METHOD);
                }
            });
    }

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		populateMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);

		return(super.onCreateOptionsMenu(menu));
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		// The preferences returned if the request code is what we had given
		// earlier in startSubActivity
		switch(requestCode) {

			case REQUEST_CODE_CAMERA:
				if(resultCode != RESULT_OK){
					return;
				}

				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);	//pull it out of landscape mode
				mBundle = null;
				mExtras = data.getExtras();
				if ( mExtras != null ) mBundle = mExtras.getBundle("filename");

				if ( mBundle != null && !mBundle.isEmpty() ) {
					NetworkServices.fileName = mBundle.getString("name");
					mSelectedPhoto.setText(NetworkServices.fileName);
				}
				break;
		}
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CHOOSE_IMAGE_METHOD:{
            	AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(getString(R.string.choose_method));
                dialog.setMessage(getString(R.string.how_to_select_pic));
                dialog.setButton(getString(R.string.gallery_option), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_PICK);
						intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, REQUEST_CODE_IMAGE);
						dialog.dismiss();
					}
                });
                dialog.setButton2(getString(R.string.btn_cancel), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
                });

                dialog.setButton3(getString(R.string.camera_option), new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent launchPreferencesIntent = new Intent().setClass(CheckinActivity.this,
								ImageCapture.class);

						// Make it a subactivity so we know when it returns
						startActivityForResult(launchPreferencesIntent, REQUEST_CODE_CAMERA);
						dialog.dismiss();
					}
        		});

                dialog.setCancelable(false);
                return dialog;

            }
        }
        return null;
    }

    // Progress dialog functionality

    private void dismissCheckinProgressDialog() {
		if(pd != null) {
			pd.dismiss();
		}

		pd = null;
	}

    private void performCheckin(String checkinDetails) {
        // Initialize Progress dialog
		pd = ProgressDialog.show(this, "Working...", "Getting location data...");
        LocationServices.getLocation(this);

        while(!LocationServices.locationSet) {
            // Do nothing for the meantime
        }

        // Post data online and close the progress dialog
        NetworkServices.postToOnline(checkinDetails, LocationServices.location);
        dismissCheckinProgressDialog();
	}

    private void populateMenu(Menu menu) {
		MenuItem i;
        // Create the menu here
	}
}