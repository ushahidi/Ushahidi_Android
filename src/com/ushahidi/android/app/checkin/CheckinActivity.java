package com.ushahidi.android.app.checkin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.ImageCapture;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.Util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/15/11 Time: 3:05 PM To change
 * this template use File | Settings | File Templates.
 */

public class CheckinActivity extends MapActivity {

    private Button checkinButton;

    private Button uploadPhotoButton;

    private Button mCancelButton;

    private MapView mapView = null;

    private MapController mapController;

    private static double latitude;

    private static double longitude;

    private EditText checkinMessageText;

    private EditText firstName;

    private EditText lastName;

    private EditText emailAddress;

    private ImageView mCheckImgPrev;

    private TextView mSelectedPhotoText;

    private TextView mCheckinLocation;

    // Photo functionality
    private String mFilename = "";

    private String selectedPhoto = "";

    private String checkinDetails;

    //private String locationName = "";

    // Used for the camera
    private static final int REQUEST_CODE_CAMERA = 5;

    // Used to choose the method for picture selection
    private static final int DIALOG_CHOOSE_IMAGE_METHOD = 4;

    private static final int REQUEST_CODE_IMAGE = 3;

    // To interchange information
    private Bundle mBundle;

    private Bundle mExtras;

    private Handler mHandler;

    private PostCheckinsJSONServices jsonServices;

    private boolean postCheckinJsonSuccess = false;

    private String postCheckinJsonErrorCode = "";

    private String postCheckinJsonErrorMessage = "";

    private String jsonResponse = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin);
        UshahidiPref.loadSettings(CheckinActivity.this);
        checkinButton = (Button)findViewById(R.id.perform_checkin_button);
        uploadPhotoButton = (Button)findViewById(R.id.upload_checkin_photo_button);
        mCancelButton = (Button)findViewById(R.id.checkin_cancel);
        checkinMessageText = (EditText)findViewById(R.id.checkin_message_text);
        firstName = (EditText)findViewById(R.id.checkin_firstname);
        lastName = (EditText)findViewById(R.id.checkin_lastname);
        emailAddress = (EditText)findViewById(R.id.checkin_email);
        mCheckImgPrev = (ImageView)findViewById(R.id.checkin_img_prev);
        mSelectedPhotoText = (TextView)findViewById(R.id.checkin_selected_photo_label);
        mCheckinLocation = (TextView)findViewById(R.id.latlon);
        mSelectedPhotoText.setVisibility(View.GONE);

        mHandler = new Handler();

        // map stuff
        mapView = (MapView)findViewById(R.id.checkin_location_map);

        mapController = mapView.getController();
        // location stuff
        mCheckinLocation.setText(getString(R.string.checkin_progress_message));
        setDeviceLocation();
        // contact
        firstName.setText(UshahidiPref.firstname);
        lastName.setText(UshahidiPref.lastname);
        emailAddress.setText(UshahidiPref.email);

        // Perform the checkin
        checkinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                performCheckin();
            }
        });

        // cancel checking
        // Perform the checkin
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CheckinActivity.this.finish();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        // house keeping
        ImageManager.deleteImage(selectedPhoto);
        ((LocationManager)getSystemService(Context.LOCATION_SERVICE))
                .removeUpdates(new DeviceLocationListener());
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // house keeping
        ImageManager.deleteImage(selectedPhoto);
        ((LocationManager)getSystemService(Context.LOCATION_SERVICE))
        .removeUpdates(new DeviceLocationListener());
        super.onPause();
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
                    mSelectedPhotoText.setVisibility(View.VISIBLE);
                    mCheckImgPrev.refreshDrawableState();
                    mCheckImgPrev
                            .setImageDrawable(ImageManager.getImages(NetworkServices.fileName));
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
                UshahidiPref.fileName = mFilename;
                selectedPhoto = mFilename;

                if (!TextUtils.isEmpty(selectedPhoto)) {
                    mSelectedPhotoText.setVisibility(View.VISIBLE);
                    mCheckImgPrev.refreshDrawableState();
                    mCheckImgPrev.setImageBitmap(ImageManager.getBitmap(UshahidiPref.fileName));
                }

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
        UshahidiPref.loadSettings(CheckinActivity.this);

        if (!LocationServices.locationSet) {
            // if (pd != null) {
            // pd.dismiss();
            com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
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
        if (com.ushahidi.android.app.Util.isConnected(CheckinActivity.this)) {
            String domain = UshahidiPref.domain;
            String firstname = firstName.getText().toString();
            String lastname = lastName.getText().toString();
            String email = emailAddress.getText().toString();
            String imei = com.ushahidi.android.app.checkin.Util.IMEI(CheckinActivity.this);
            this.checkinDetails = checkinMessageText.getText().toString();
            postCheckin(imei, domain, firstname, lastname, email);
        } else {
            com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
                    R.string.network_error_msg);
        }
    }

    final Runnable mPostCheckin = new Runnable() {
        public void run() {

            if (jsonResponse != null) {
                jsonServices = new PostCheckinsJSONServices(jsonResponse);

                // JSON Post

                if (jsonServices.isProcessingResult()) {
                    postCheckinJsonSuccess = true;

                    postCheckinJsonErrorCode = jsonServices.getErrorCode();
                    postCheckinJsonErrorMessage = jsonServices.getErrorMessage();
                }

                // Display checkin status and return back to main screen
                if (postCheckinJsonErrorCode != "0") {

                    // delete uploaded image after successful checkin
                    com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
                            R.string.checkin_success_toast);

                } else {
                    com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
                            R.string.checkin_error_toast);
                }
                CheckinActivity.this.finish();
            } else {
                com.ushahidi.android.app.Util.showToast(CheckinActivity.this,
                        R.string.checkin_error_toast);
            }
            setProgressBarIndeterminateVisibility(false);
        }
    };

    public void postCheckin(final String imei, final String domain, final String firstname,
            final String lastname, final String email) {
        setProgressBarIndeterminateVisibility(true);
        Thread t = new Thread() {
            public void run() {

                jsonResponse = NetworkServices.postToOnline(imei, domain, checkinDetails,
                        selectedPhoto, firstname, lastname, email, CheckinActivity.latitude,
                        CheckinActivity.longitude);

                mHandler.post(mPostCheckin);
            }
        };
        t.start();
    }

    private void placeMarker(int markerLatitude, int markerLongitude) {

        Drawable marker = getResources().getDrawable(R.drawable.marker);

        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mapView.getController().setZoom(14);

        mapView.setBuiltInZoomControls(true);
        mapView.getOverlays().add(new MapMarker(marker, markerLatitude, markerLongitude));
    }

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    private void centerLocation(GeoPoint centerGeoPoint) {

        mapController.animateTo(centerGeoPoint);

        // initilaize latitude and longitude for them to be passed to the
        // AddIncident Activity.
        CheckinActivity.latitude = centerGeoPoint.getLatitudeE6() / 1.0E6;
        CheckinActivity.longitude = centerGeoPoint.getLongitudeE6() / 1.0E6;

        placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());

    }

    private class MapMarker extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> locations = new ArrayList<OverlayItem>();

        private Drawable marker;

        private OverlayItem myOverlayItem;

        public MapMarker(Drawable defaultMarker, int LatitudeE6, int LongitudeE6) {
            super(defaultMarker);
            this.marker = defaultMarker;

            // create locations of interest
            GeoPoint myPlace = new GeoPoint(LatitudeE6, LongitudeE6);

            myOverlayItem = new OverlayItem(myPlace, " ", " ");

            locations.add(myOverlayItem);

            populate();

        }

        @Override
        protected OverlayItem createItem(int i) {
            return locations.get(i);
        }

        @Override
        public int size() {
            return locations.size();
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            super.draw(canvas, mapView, shadow);
            boundCenterBottom(marker);
        }

    }

    // Fetches the current location of the device.
    private void setDeviceLocation() {

        DeviceLocationListener listener = new DeviceLocationListener();
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        long updateTimeMsec = 1000L;

        // get low accuracy provider
        LocationProvider low = manager.getProvider(manager.getBestProvider(
                Util.createCoarseCriteria(), true));

        // get high accuracy provider
        LocationProvider high = manager.getProvider(manager.getBestProvider(
                Util.createFineCriteria(), true));

        manager.requestLocationUpdates(low.getName(), updateTimeMsec, 500.0f, listener);

        manager.requestLocationUpdates(high.getName(), updateTimeMsec, 500.0f, listener);

    }

    // get the current location of the device/user
    public class DeviceLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {

            if (location != null) {
                ((LocationManager)getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                centerLocation(getPoint(latitude, longitude));
                mCheckinLocation.setText(String.valueOf(latitude) + ", "
                        + String.valueOf(longitude));
            }
        }

        public void onProviderDisabled(String provider) {
            Util.showToast(CheckinActivity.this, R.string.location_not_found);
        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

}
