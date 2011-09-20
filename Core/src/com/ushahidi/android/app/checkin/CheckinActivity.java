
package com.ushahidi.android.app.checkin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.About;
import com.ushahidi.android.app.CaptureImage;
import com.ushahidi.android.app.DeploymentSearch;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.IncidentTab;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.MapUserLocation;
import com.ushahidi.android.app.Dashboard;
import com.ushahidi.android.app.Preferences;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/15/11 Time: 3:05 PM To change
 * this template use File | Settings | File Templates.
 */

public class CheckinActivity extends MapUserLocation {

    private Button checkinButton;

    private Button uploadPhotoButton;

    private ImageButton mSearchButton;

    private EditText checkinMessageText;

    private EditText firstName;

    private EditText lastName;

    private EditText emailAddress;

    private ImageView mCheckImgPrev;

    private TextView mSelectedPhotoText;

    private TextView mCheckinLocation;

    private TextView lblFirstName;

    private TextView lblLastName;

    private TextView lblEmail;

    private TextView lblContact;

    private TextView activityTitle;

    // Photo functionality
    private String mFilename = "";

    private String selectedPhoto = "";

    private String checkinDetails;

    private static final int HOME = Menu.FIRST + 1;

    private static final int LIST_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_MAP = Menu.FIRST + 3;

    private static final int INCIDENT_REFRESH = Menu.FIRST + 4;

    private static final int SETTINGS = Menu.FIRST + 5;

    private static final int ABOUT = Menu.FIRST + 6;

    private static final int GOTOHOME = 0;

    private static final int MAP_CHECKINS = 1;

    private static final int LIST_CHECKINS = 2;

    private static final int REQUEST_CODE_SETTINGS = 3;

    private static final int REQUEST_CODE_ABOUT = 4;

    // Used for the camera
    private static final int REQUEST_CODE_CAMERA = 5;

    private static int requestedCode = 5;

    // Used to choose the method for picture selection
    private static final int DIALOG_CHOOSE_IMAGE_METHOD = 7;

    private static final int REQUEST_CODE_IMAGE = 8;

    private static final int VIEW_SEARCH = 9;

    private static final int INCIDENTS = 2;

    private Handler mHandler;

    private Bundle checkinsBundle = new Bundle();

    private PostCheckinsJSONServices jsonServices;

    private String postCheckinJsonErrorCode = "";

    private String jsonResponse = "";

    private String errorMessage = "";

    private ProgressDialog progressDialog;

    private boolean mError = false;

    private String mErrorMessage = "";

    private static final String CLASS_TAG = CheckinActivity.class.getCanonicalName();

    private CaptureImage captureImage;

    private static final int CHECKIN_PREF = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin);
        captureImage = new CaptureImage();

        Preferences.loadSettings(CheckinActivity.this);

        // manager =
        // (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        checkinButton = (Button)findViewById(R.id.perform_checkin_button);
        uploadPhotoButton = (Button)findViewById(R.id.upload_checkin_photo_button);
        checkinMessageText = (EditText)findViewById(R.id.checkin_message_text);
        firstName = (EditText)findViewById(R.id.checkin_firstname);
        lastName = (EditText)findViewById(R.id.checkin_lastname);
        emailAddress = (EditText)findViewById(R.id.checkin_email);
        lblFirstName = (TextView)findViewById(R.id.checkin_lbl_firstname);
        lblLastName = (TextView)findViewById(R.id.checkin_lbl_lastname);
        lblEmail = (TextView)findViewById(R.id.txt_lbl_email);
        lblContact = (TextView)findViewById(R.id.checkin_contact);
        mCheckImgPrev = (ImageView)findViewById(R.id.checkin_img_prev);
        mSelectedPhotoText = (TextView)findViewById(R.id.checkin_selected_photo_label);
        mCheckinLocation = (TextView)findViewById(R.id.latlon);
        mSelectedPhotoText.setVisibility(View.GONE);
        activityTitle = (TextView)findViewById(R.id.title_text);
        if (activityTitle != null)
            activityTitle.setText(getTitle());
        mSearchButton = (ImageButton)findViewById(R.id.search_report_btn);

        mHandler = new Handler();

        // map stuff
        mapView = (MapView)findViewById(R.id.checkin_location_map);

        mapController = mapView.getController();
        // location stuff
        mCheckinLocation.setText(getString(R.string.checkin_progress_message));

        // Validate so empty text isn't sent over
        checkinMessageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(checkinMessageText.getText())) {
                        checkinMessageText.setError(getString(R.string.checkin_empty_message));
                    }
                }
            }

        });

        // contact
        if ((!TextUtils.isEmpty(Preferences.firstname))
                && (!TextUtils.isEmpty(Preferences.lastname))
                && (!TextUtils.isEmpty(Preferences.email))) {
            lblContact.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(Preferences.firstname)) {
            lblFirstName.setVisibility(View.GONE);
            firstName.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(Preferences.lastname)) {
            lblLastName.setVisibility(View.GONE);
            lastName.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(Preferences.email)) {
            lblEmail.setVisibility(View.GONE);
            emailAddress.setVisibility(View.GONE);
        }

        firstName.setText(Preferences.firstname);
        lastName.setText(Preferences.lastname);
        emailAddress.setText(Preferences.email);

        // Perform the checkin
        checkinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (TextUtils.isEmpty(checkinMessageText.getText())) {
                    mErrorMessage = getString(R.string.checkin_empty_message);
                    mError = true;
                }

                if (!mError) {
                    performCheckin();
                } else {
                    final Toast t = Toast.makeText(CheckinActivity.this, "Error!\n\n"
                            + mErrorMessage, Toast.LENGTH_LONG);
                    t.show();
                    mErrorMessage = "";
                }
            }
        });

        // Uploading a photo for the checkin
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(selectedPhoto)) {
                    ImageManager.deleteImage(selectedPhoto, "");
                }
                showDialog(DIALOG_CHOOSE_IMAGE_METHOD);
            }
        });

        // search for deployments
        mSearchButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                onSearchDeployments();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Preferences.loadSettings(CheckinActivity.this);
    }

    /**
     * Any time we are paused we need to save away the current state, so it will
     * be restored correctly when we are resumed.
     */
    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = getPreferences(CHECKIN_PREF).edit();
        editor.putString("message", checkinMessageText.getText().toString());
        editor.putString("firstname", firstName.getText().toString());
        editor.putString("lastname", lastName.getText().toString());
        editor.putString("email", emailAddress.getText().toString());
        editor.putString("selectedphoto", Preferences.fileName);
        editor.putInt("requestedcode", requestedCode);
        editor.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = getPreferences(CHECKIN_PREF);
        String message = prefs.getString("message", null);
        String firstname = prefs.getString("firstname", null);
        String lastname = prefs.getString("lastname", null);
        String email = prefs.getString("email", null);
        String filename = prefs.getString("selectedphoto", null);
        int requestcode = prefs.getInt("requestedcode", REQUEST_CODE_IMAGE);

        Intent data = null;

        if (message != null)
            checkinMessageText.setText(message, TextView.BufferType.EDITABLE);
        if (firstname != null)
            firstName.setText(firstname, TextView.BufferType.EDITABLE);

        if (lastname != null)
            lastName.setText(lastname, TextView.BufferType.EDITABLE);

        if (email != null)
            emailAddress.setText(email, TextView.BufferType.EDITABLE);

        if (filename != null) {
            Preferences.fileName = filename;
            if (captureImage.imageExist(filename, this))
                uploadPhotoButton.setText(getString(R.string.change_photo));
            setSelectedImage(requestcode, data);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private void clearFields() {
        Log.d(CLASS_TAG, "clearFields(): clearing fields");
        // delete unset photo
        File f = new File(Preferences.fileName);
        if (f.exists()) {
            f.delete();
        }

        Preferences.fileName = "";
        if (!captureImage.imageExist(Preferences.fileName, this))
            uploadPhotoButton.setText(getString(R.string.btn_add_photo));

        checkinMessageText.setText("");

        firstName.setText("");

        lastName.setText("");

        emailAddress.setText("");

        mCheckImgPrev.setImageDrawable(null);
        mCheckImgPrev.setImageBitmap(null);

        SharedPreferences.Editor editor = getPreferences(CHECKIN_PREF).edit();
        editor.putString("message", "");
        editor.putString("firstname", "");
        editor.putString("lastname", "");
        editor.putString("email", "");
        editor.putString("selectedphoto", "");
        editor.putInt("requestedcode", 0);
        editor.commit();

    }

    protected void onSearchDeployments() {
        Intent intent = new Intent(CheckinActivity.this, DeploymentSearch.class);
        startActivityForResult(intent, VIEW_SEARCH);
        setResult(RESULT_OK);
    }

    // Implementation of UserLocationMap abstract methods
    protected void updateInterface() {
        mCheckinLocation.setText(String.valueOf(sLatitude) + ", " + String.valueOf(sLongitude));
    }

    // menu stuff
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
    public boolean onOptionsItemSelected(MenuItem item) {

        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }

    private void populateMenu(Menu menu) {
        MenuItem i;

        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.menu_home);

        i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.checkin_list);
        i.setIcon(R.drawable.menu_list);

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.menu_map);

        i = menu.add(Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.incident_menu_refresh);
        i.setIcon(R.drawable.menu_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.menu_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.menu_about);

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent launchPreferencesIntent;
        switch (item.getItemId()) {

            case LIST_INCIDENT:
                launchPreferencesIntent = new Intent(CheckinActivity.this, IncidentTab.class);
                startActivityForResult(launchPreferencesIntent, LIST_CHECKINS);
                setResult(RESULT_OK);
                return true;

            case INCIDENT_MAP:
                checkinsBundle.putInt("tab_index", 1);
                launchPreferencesIntent = new Intent(CheckinActivity.this, IncidentTab.class);
                launchPreferencesIntent.putExtra("tab", checkinsBundle);
                startActivityForResult(launchPreferencesIntent, MAP_CHECKINS);
                return true;

            case HOME:
                launchPreferencesIntent = new Intent(CheckinActivity.this, Dashboard.class);
                startActivityForResult(launchPreferencesIntent, GOTOHOME);
                setResult(RESULT_OK);
                return true;

            case ABOUT:
                launchPreferencesIntent = new Intent(CheckinActivity.this, About.class);
                startActivityForResult(launchPreferencesIntent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                launchPreferencesIntent = new Intent().setClass(CheckinActivity.this,
                        Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
                return true;

        }
        return false;
    }

    /**
     * Set selected / captured image
     *
     */
    public void setSelectedImage(int requestCode, Intent data) {

        Log.i(CLASS_TAG, "setSelectedImage(): requestCode: " + requestCode);

        switch (requestCode) {

            case REQUEST_CODE_CAMERA:
                Log.i(CLASS_TAG, "ActivityResult has returned");
                // Do something with image taken with camera
                Bitmap original = new CaptureImage().getBitmap(
                        new CaptureImage().getPhotoUri("photo.jpg", CheckinActivity.this),
                        CheckinActivity.this);
                Log.d(CLASS_TAG, "image path" + Preferences.fileName);
                if (original != null) {
                    // get image URL
                    Uri u = new CaptureImage().getPhotoUri("photo.jpg", CheckinActivity.this);

                    Log.i(CLASS_TAG, "Image File Path" + u.getPath());
                    selectedPhoto = u.getPath();
                    NetworkServices.fileName = u.getPath();
                    mSelectedPhotoText.setVisibility(View.VISIBLE);
                    Preferences.fileName = u.getPath();

                    // use resized images

                    if (captureImage.imageExist(Preferences.fileName, this))
                        uploadPhotoButton.setText(getString(R.string.change_photo));
                    mCheckImgPrev.setImageBitmap(original);

                }
                break;
            case REQUEST_CODE_IMAGE:
                // do something with image taken from image gallery
                mFilename = "photo.jpg";
                final String filepath = new CaptureImage().getPhotoPath(CheckinActivity.this);
                Log.i(CLASS_TAG, "Image File Path" + filepath);
                if (data != null) {

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
                        b.compress(CompressFormat.JPEG, 75, byteArrayos);
                        b.recycle();
                        byteArrayos.flush();

                    } catch (OutOfMemoryError e) {
                        break;
                    } catch (IOException e) {
                        break;
                    }

                    if (!TextUtils.isEmpty(filepath)) {
                        
                        ImageManager.writeImage(byteArrayos.toByteArray(), mFilename, filepath);
                        // Do something with image taken with camera
                        Bitmap selectedImage = new CaptureImage().getBitmap(
                                new CaptureImage().getPhotoUri("photo.jpg", CheckinActivity.this),
                                CheckinActivity.this);
                        Log.i(CLASS_TAG, "Image File Path" + new CaptureImage().getPhotoUri("photo.jpg",
                                CheckinActivity.this));
                        if (selectedImage != null) {

                            // get image URL
                            Uri u = new CaptureImage().getPhotoUri("photo.jpg",
                                    CheckinActivity.this);

                            Log.i(CLASS_TAG, "Image File Path" + u.getPath());
                            Preferences.fileName = u.getPath();
                            selectedPhoto = u.getPath();
                            mSelectedPhotoText.setVisibility(View.VISIBLE);

                            // use resized images

                            if (captureImage.imageExist(Preferences.fileName, this))
                                uploadPhotoButton.setText(getString(R.string.change_photo));
                            mCheckImgPrev.setImageBitmap(selectedImage);

                        }

                    }

                } else {

                    if (!TextUtils.isEmpty(filepath)) {
                        Bitmap selectedImage = new CaptureImage().getBitmap(
                                new CaptureImage().getPhotoUri("photo.jpg", CheckinActivity.this),
                                CheckinActivity.this);

                        if (selectedImage != null) {

                            // get image URL
                            Uri u = new CaptureImage().getPhotoUri("photo.jpg",
                                    CheckinActivity.this);

                            Log.i(CLASS_TAG, "Image File Path " + u.getPath());
                            Preferences.fileName = u.getPath();
                            selectedPhoto = u.getPath();
                            mSelectedPhotoText.setVisibility(View.VISIBLE);

                            // use resized images

                            if (captureImage.imageExist(Preferences.fileName, this))
                                uploadPhotoButton.setText(getString(R.string.change_photo));
                            mCheckImgPrev.setImageBitmap(selectedImage);

                        }

                    }
                }
                break;
        }
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
                requestedCode = REQUEST_CODE_CAMERA;
                setSelectedImage(requestCode, data);
                break;
            case REQUEST_CODE_IMAGE:
                requestedCode = REQUEST_CODE_IMAGE;
                setSelectedImage(requestCode, data);
                break;
        }
    }

    /**
     * Go to checkins screen
     */
    public void goToCheckins() {
        Intent intent = new Intent(CheckinActivity.this, IncidentTab.class);
        startActivityForResult(intent, INCIDENTS);
        setResult(RESULT_OK);
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

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                new CaptureImage().getPhotoUri("photo.jpg", CheckinActivity.this));
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);

                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(false);
                return dialog;

            }
        }
        return null;
    }

    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    public void goHome(Context context) {
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    // Progress dialog functionality

    public void dismissCheckinProgressDialog() {
        // Post data online and close the progress dialog

        // Initialize the settings
        Preferences.loadSettings(CheckinActivity.this);

        if (!LocationServices.locationSet) {
            // if (pd != null) {
            // pd.dismiss();
            com.ushahidi.android.app.util.Util.showToast(CheckinActivity.this,
                    R.string.checkin_no_location);
            // }

            // pd = null;
        } else {
            // if (pd != null) {
            // pd.dismiss();
            // }

        }
    }

    private void performCheckin() {

        /**
         * Check if there is internet connection on the device.
         */
        if (com.ushahidi.android.app.util.Util.isConnected(CheckinActivity.this)) {
            String domain = Preferences.domain;
            String firstname = firstName.getText().toString();
            String lastname = lastName.getText().toString();
            String email = emailAddress.getText().toString();
            String imei = com.ushahidi.android.app.checkin.CheckinUtil.IMEI(CheckinActivity.this);
            this.checkinDetails = checkinMessageText.getText().toString();
            postCheckin(imei, domain, firstname, lastname, email);
        } else {
            com.ushahidi.android.app.util.Util.showToast(CheckinActivity.this,
                    R.string.network_error_msg);
        }
    }

    final Runnable mPostCheckin = new Runnable() {
        public void run() {
            Log.i(CLASS_TAG, "mPostCheckin: posting checking");
            if (jsonResponse != null) {
                Log.i(CLASS_TAG, "mPostCheckin: jsonResponse is not null");
                jsonServices = new PostCheckinsJSONServices(jsonResponse);

                // JSON Post

                if (jsonServices.isProcessingResult()) {

                    postCheckinJsonErrorCode = jsonServices.getErrorCode();
                    errorMessage = jsonServices.getErrorMessage();
                    Log.i(CLASS_TAG, "error code " + postCheckinJsonErrorCode);
                }

                // Display checkin status and return back to main screen
                if (postCheckinJsonErrorCode.equals("0")) {
                    Log.i(CLASS_TAG, "mPostCheckin: posting went successful. error code "
                            + postCheckinJsonErrorCode);

                    clearFields();

                    // after a successful upload, delete the file
                    File f = new File(Preferences.fileName);
                    if (f.exists()) {
                        f.delete();
                    }
                    com.ushahidi.android.app.util.Util.showToast(CheckinActivity.this,
                            R.string.checkin_success_toast);

                    goToCheckins();

                } else {
                    com.ushahidi.android.app.util.Util.showToast(CheckinActivity.this,
                            R.string.checkin_error_toast);
                }

            } else {
                com.ushahidi.android.app.util.Util.showToast(CheckinActivity.this,
                        R.string.checkin_error_toast);
            }
            progressDialog.dismiss();
            setProgressBarIndeterminateVisibility(false);
        }
    };

    public void postCheckin(final String imei, final String domain, final String firstname,
            final String lastname, final String email) {
        setProgressBarIndeterminateVisibility(true);
        this.progressDialog = ProgressDialog.show(CheckinActivity.this,
                getString(R.string.checkin_progress_title),
                getString(R.string.checkin_in_progress), true);
        Thread t = new Thread() {
            public void run() {

                jsonResponse = NetworkServices.postToOnline(imei, domain, checkinDetails,
                        selectedPhoto, firstname, lastname, email, sLatitude, sLongitude);

                mHandler.post(mPostCheckin);
            }
        };
        t.start();
    }
}
