
package com.ushahidi.android.app.checkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.ushahidi.android.app.ImageCapture;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/15/11 Time: 3:05 PM To change
 * this template use File | Settings | File Templates.
 */

public class CheckinActivity extends Activity {
    private ProgressDialog pd = null;

    private Button checkinButton;

    private Button uploadPhotoButton;

    private EditText checkinMessageText;

    private EditText mSelectedPhoto;

    // Photo functionality
    private String mFilename = "";

    private String selectedPhoto = "";

    private String checkinDetails;

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

        checkinButton = (Button)findViewById(R.id.perform_checkin_button);
        uploadPhotoButton = (Button)findViewById(R.id.upload_checkin_photo_button);
        checkinMessageText = (EditText)findViewById(R.id.checkin_message_text);
        mSelectedPhoto = (EditText)findViewById(R.id.checkin_selected_photo_text);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        populateMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        switch (requestCode) {

            case REQUEST_CODE_CAMERA:
                if (resultCode != RESULT_OK) {
                    return;
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // pull
                                                                                      // it
                                                                                      // out
                                                                                      // of
                                                                                      // landscape
                                                                                      // mode
                mBundle = null;
                mExtras = data.getExtras();
                if (mExtras != null)
                    mBundle = mExtras.getBundle("filename");

                if (mBundle != null && !mBundle.isEmpty()) {
                    selectedPhoto = mBundle.getString("name");
                    NetworkServices.fileName = mBundle.getString("name");
                    mSelectedPhoto.setText(NetworkServices.fileName);
                }
                break;

            case REQUEST_CODE_IMAGE:
                if (resultCode != RESULT_OK) {
                    return;
                }

                Uri uri = data.getData();
                Bitmap b = null;

                try {
                    b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (FileNotFoundException e) {
                    break;
                } catch (IOException e) {
                    break;
                }

                ByteArrayOutputStream byteArrayos = new ByteArrayOutputStream();

                try {
                    b.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayos);
                    byteArrayos.flush();
                } catch (OutOfMemoryError e) {
                    break;
                } catch (IOException e) {
                    break;
                }

                mFilename = "android_pic_upload" + randomString() + ".jpg";
                ImageManager.writeImage(byteArrayos.toByteArray(), mFilename);
                UshahidiService.fileName = mFilename;
                selectedPhoto = mFilename;
                mSelectedPhoto.setText(UshahidiService.fileName);
                break;
        }
    }

    private static Random random = new Random();

    protected static String randomString() {
        return Long.toString(random.nextLong(), 10);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CHOOSE_IMAGE_METHOD: {
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

                        Intent launchPreferencesIntent = new Intent().setClass(
                                CheckinActivity.this, ImageCapture.class);

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

    public void dismissCheckinProgressDialog() {
        // Post data online and close the progress dialog

        // Initialize the settings
        UshahidiService.loadSettings(CheckinActivity.this);

        String ushahidiDomain = UshahidiService.domain;
        String firstname = UshahidiService.firstname;
        String lastname = UshahidiService.lastname;
        String email = UshahidiService.email;

        NetworkServices.postToOnline(Util.IMEI(CheckinActivity.this), ushahidiDomain,
                checkinDetails, LocationServices.location, selectedPhoto, firstname, lastname,
                email);

        if (pd != null) {
            pd.dismiss();
        }

        pd = null;

        // Display checkin status and return back to main screen
        com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
                R.string.checkin_success_toast);
        CheckinActivity.this.finish();
    }

    private void performCheckin(String checkinDetails) {
        this.checkinDetails = checkinDetails;

        // Initialize Progress dialog
        pd = ProgressDialog.show(this, getString(R.string.checkin_progress_title),
                getString(R.string.checkin_progress_message));
        LocationServices.getLocation(this);
    }

    private void populateMenu(Menu menu) {
        MenuItem i;
        // Create the menu here
    }
}
