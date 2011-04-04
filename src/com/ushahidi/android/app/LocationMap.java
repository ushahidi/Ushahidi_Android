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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.ushahidi.android.app.data.IncidentsData;
import com.ushahidi.android.app.data.UshahidiDatabase;

public class LocationMap extends MapActivity {
    private MapView mapView = null;

    private MapController mapController;

    private static Geocoder gc;

    private GeoPoint defaultLocation;

    private double latitude;

    private double longitude;

    private List<IncidentsData> mNewIncidents;

    private List<IncidentsData> mOldIncidents;

    private Button btnReset;

    private Button btnSave;

    private Button btnFind;

    private Bundle bundle = new Bundle();

    private List<Address> foundAddresses;

    private String locationName;

    private String title;

    private String date;

    private String description;

    private String location;

    private String categories;

    private String thumbnail;

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.view_map);

        mapView = (MapView)findViewById(R.id.location_map);
        locationName = "";

        foundAddresses = new ArrayList<Address>();
        gc = new Geocoder(this);

        btnSave = (Button)findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                bundle.putString("location", locationName);

                // Pass the data to the calling activity
                Intent intent = new Intent();
                intent.putExtra("locations", bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnFind = (Button)findViewById(R.id.btn_find);
        btnFind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Util.showToast(LocationMap.this, R.string.find_location);
                updateLocation();
            }
        });

        mapController = mapView.getController();

        mOldIncidents = new ArrayList<IncidentsData>();
        mNewIncidents = showIncidents("All");

        if (mNewIncidents.size() > 0) {
            latitude = Double.parseDouble(mNewIncidents.get(0).getIncidentLocLatitude());
            longitude = Double.parseDouble(mNewIncidents.get(0).getIncidentLocLongitude());
        }

        defaultLocation = getPoint(latitude, longitude);
        centerLocation(defaultLocation);

        btnReset = (Button)findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                centerLocation(defaultLocation);
            }
        });
    }

    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    protected void startLongRunningOperation() {

        // Fire off a thread to do some work that we shouldn't do directly in
        // the UI thread
        Thread t = new Thread() {
            @Override
            public void run() {
                updateLocation();
                mHandler.post(mUpdateResults);
            }
        };
        t.start();
    }

    private void updateResultsInUi() {

        Toast.makeText(LocationMap.this, "Found you at " + locationName, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
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
        this.latitude = centerGeoPoint.getLatitudeE6() / 1.0E6;
        this.longitude = centerGeoPoint.getLongitudeE6() / 1.0E6;
        mapView.getOverlays().clear();
        placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());

    }

    /**
     * get the real location name from the latitude and longitude.
     */
    private String getLocationFromLatLon(double lat, double lon) {

        try {
            Address address;
            foundAddresses = gc.getFromLocation(lat, lon, 5);
            if (foundAddresses.size() > 0) {
                address = foundAddresses.get(0);
                return address.getSubAdminArea();

            } else {
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // get incidents from the db
    public List<IncidentsData> showIncidents(String by) {
        Cursor cursor;

        if (by.equals("All"))
            cursor = UshahidiApplication.mDb.fetchAllIncidents();
        else
            cursor = UshahidiApplication.mDb.fetchIncidentsByCategories(by);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_TITLE);
            int dateIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_DATE);
            int verifiedIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_VERIFIED);
            int locationIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_NAME);

            int descIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_DESC);

            int categoryIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_CATEGORIES);

            int mediaIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_MEDIA);

            int latitudeIndex = cursor
                    .getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LATITUDE);

            int longitudeIndex = cursor
                    .getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LONGITUDE);

            do {

                IncidentsData incidentData = new IncidentsData();
                mOldIncidents.add(incidentData);

                int id = Util.toInt(cursor.getString(idIndex));
                incidentData.setIncidentId(id);

                title = Util.capitalizeString(cursor.getString(titleIndex));
                incidentData.setIncidentTitle(title);

                description = cursor.getString(descIndex);
                incidentData.setIncidentDesc(description);

                categories = cursor.getString(categoryIndex);
                incidentData.setIncidentCategories(categories);

                location = cursor.getString(locationIndex);
                incidentData.setIncidentLocLongitude(location);

                date = Util.joinString("Date: ", Util.formatDate("yyyy-MM-dd hh:mm:ss",
                        cursor.getString(dateIndex), "MMMM dd, yyyy 'at' hh:mm:ss aaa"));
                incidentData.setIncidentDate(date);

                thumbnail = cursor.getString(mediaIndex);
                incidentData.setIncidentThumbnail(thumbnail);

                incidentData.setIncidentVerified(Util.toInt(cursor.getString(verifiedIndex)));

                incidentData.setIncidentLocLatitude(cursor.getString(latitudeIndex));
                incidentData.setIncidentLocLongitude(cursor.getString(longitudeIndex));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return mOldIncidents;

    }

    // update the device current location
    private void updateLocation() {
        MyLocationListener listener = new MyLocationListener();
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        long updateTimeMsec = 1000L;

        LocationProvider low = locationManager.getProvider(locationManager.getBestProvider(
                Util.createCoarseCriteria(), true));

        // get high accuracy provider
        LocationProvider high = locationManager.getProvider(locationManager.getBestProvider(
                Util.createFineCriteria(), true));

        // Register for GPS location if enabled or if neither is enabled
        locationManager.requestLocationUpdates(low.getName(), updateTimeMsec, 500.0f, listener);

        locationManager.requestLocationUpdates(high.getName(), updateTimeMsec, 500.0f, listener);

    }

    // get the current location of the user
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            double latitude = 0;
            double longitude = 0;
            String locName = "";
            if (location != null) {
                // Dipo Fix
                // Stop asking for updates when location has been retrieved

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                locName = getLocationFromLatLon(latitude, longitude);
                centerLocation(getPoint(latitude, longitude));
                ((LocationManager)getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
                if (locName == null) {

                    Util.showToast(LocationMap.this, R.string.location_not_found);
                } else {

                    locationName = locName;
                    Toast.makeText(LocationMap.this, "Found you at " + locationName,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void onProviderDisabled(String provider) {
            Util.showToast(LocationMap.this, R.string.location_not_found);
        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    // thread class
    private class GeocodeTask extends AsyncTask<Double, Void, String> {

        protected String localityName;

        protected Context appContext;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }

        @Override
        protected String doInBackground(Double... params) {

            // for some reason, Geocoder couldn't reverse geocode latitude and
            // longitude
            // so had to implement that using google geocde webservice.

            localityName = Util.getFromLocation(params[0], params[1], appContext);
            return localityName;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == "") {
                locationName = "";
                Util.showToast(appContext, R.string.loc_not_found);
            } else {
                locationName = result;
                Toast.makeText(appContext, locationName, Toast.LENGTH_SHORT).show();
            }
            setProgressBarIndeterminateVisibility(false);
        }

    }

    private class MapMarker extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> locations = new ArrayList<OverlayItem>();

        private Drawable marker;

        private OverlayItem myOverlayItem;

        private boolean MoveMap = false;

        private long timer;

        public MapMarker(Drawable defaultMarker, int LatitudeE6, int LongitudeE6) {
            super(defaultMarker);
            this.timer = 0;
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

        /**
         * Fixed by Joey at http://goo.gl/UUiN
         */
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent, MapView mapview) {

            int Action = motionEvent.getAction();
            String foundLoc = "";

            if (Action == MotionEvent.ACTION_UP) {

                if (!MoveMap && (System.currentTimeMillis() - timer <= 1000)) {
                    Projection proj = mapView.getProjection();
                    GeoPoint loc = proj
                            .fromPixels((int)motionEvent.getX(), (int)motionEvent.getY());
                    
                    
                    foundLoc = getLocationFromLatLon(loc.getLatitudeE6() / 1.0E6,
                            loc.getLatitudeE6() / 1.0E6);
                    if (foundLoc == "") {
                        locationName = "";
                        Util.showToast(LocationMap.this, R.string.loc_not_found);
                    } else {
                        locationName = foundLoc;
                        Toast.makeText(LocationMap.this, locationName, Toast.LENGTH_SHORT).show();
                    }
                    // remove the last marker
                    
                    centerLocation(loc);
                }

            } else if (Action == MotionEvent.ACTION_DOWN) {
                timer = System.currentTimeMillis();
                MoveMap = false;
            } else if (Action == MotionEvent.ACTION_MOVE) {
                float difX = 0.0f;
                float difY = 0.0f;

                if (motionEvent.getHistorySize() >= 2) {

                    difX = motionEvent.getHistoricalX(0)
                            - motionEvent.getHistoricalX(motionEvent.getHistorySize() - 1);

                    difY = motionEvent.getHistoricalY(0)
                            - motionEvent.getHistoricalY(motionEvent.getHistorySize() - 1);
                }
                if (difX >= 5 || difX <= -5 || difY >= 5 || difY <= -5) {

                    MoveMap = true;

                }

            }

            return super.onTouchEvent(motionEvent, mapview);
        }
    }

}
