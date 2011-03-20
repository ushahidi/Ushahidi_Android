
package com.ushahidi.android.app.checkin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.maps.*;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/17/11 Time: 2:21 PM To change
 * this template use File | Settings | File Templates.
 */
public class CheckinMap extends MapActivity {

    private MapView mapView;

    private Handler mHandler;

    List<Overlay> mapOverlays;

    Drawable drawable;

    ArrayList<Checkin> checkinsList = null;

    private List<Address> foundAddresses;

    private static Geocoder gc;

    protected double latitude = 0;

    protected double longitude = 0;

    protected String locationName = "";

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        UshahidiPref.loadSettings(CheckinMap.this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin_map);

        mHandler = new Handler();
        mapView = (MapView)findViewById(R.id.checkin_mapview);
        mapView.setBuiltInZoomControls(true);
        foundAddresses = new ArrayList<Address>();
        gc = new Geocoder(this);

        setDeviceLocation();
        String strCheckinsJSON = NetworkServices.getCheckins(UshahidiPref.domain, null, null);
        RetrieveCheckinsJSONServices checkinsRetrieveCheckinsJSON = new RetrieveCheckinsJSONServices(
                strCheckinsJSON);
        checkinsList = checkinsRetrieveCheckinsJSON.getCheckinsList();

        int numCheckins = checkinsList.size();

        if (numCheckins > 0) {
            mapView.getController().setCenter(
                    getPoint(Double.valueOf(checkinsList.get(0).getLat()),
                            Double.valueOf(checkinsList.get(0).getLat())));
            populateMap();
        } else {
            Toast.makeText(CheckinMap.this, "There are no reports to be shown", Toast.LENGTH_LONG)
                    .show();
        }

    }

    

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /**
     * add marker to the map
     */
    private void populateMap() {
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mapView.getOverlays().add(new CheckinsOverlay(marker, mapView));
    }

    // put this stuff in a seperate thread
    final Runnable mMarkersOnMap = new Runnable() {
        public void run() {
            populateMap();
        }
    };

    /**
     * Add marker for current location of the device
     */
    private void addMarker() {
        Drawable marker = getResources().getDrawable(R.drawable.green_dot);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mapView.getOverlays().add(new DeviceLocationOverlay(marker, mapView));
    }

    final Runnable mDeviceLocationMarkerOnMap = new Runnable() {
        public void run() {
            addMarker();
        }
    };

    private void centerLocation(GeoPoint centerGeoPoint) {

        mapView.getController().setCenter(centerGeoPoint);
        addMarker();

    }

    /**
     * get the real location name from the latitude and longitude.
     */
    private String getLocationFromLatLon(double lat, double lon) {

        try {

            foundAddresses = gc.getFromLocation(lat, lon, 5);

            Address address = foundAddresses.get(0);

            return address.getSubAdminArea();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Fetches the current location of the device.
    private void setDeviceLocation() {

        DeviceLocationListener listener = new DeviceLocationListener();
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        long updateTimeMsec = 1000L;
        
        LocationProvider low = manager.getProvider(manager.getBestProvider(Util.createCoarseCriteria(),
                false));

        // get high accuracy provider
        LocationProvider high = manager.getProvider(manager.getBestProvider(Util.createFineCriteria(),
                false));

        manager.requestLocationUpdates(low.getName(), updateTimeMsec, 500.0f, listener);

        manager.requestLocationUpdates(high.getName(), updateTimeMsec, 500.0f, listener);

    }

    // get the current location of the device/user
    public class DeviceLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            // double latitude = 0;
            // double longitude = 0;
            // String locName = "";
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                locationName = getLocationFromLatLon(latitude, longitude);
                centerLocation(getPoint(latitude, longitude));

                ((LocationManager)getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);

            }
        }

        public void onProviderDisabled(String provider) {
            Util.showToast(CheckinMap.this, R.string.location_not_found);
        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    private class CheckinsOverlay extends CheckinItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        public CheckinsOverlay(Drawable marker, MapView mapView) {
            super(boundCenterBottom(marker), mapView, CheckinMap.this);
            mapView.getContext();

            for (Checkin checkin : checkinsList) {

                items.add(new OverlayItem(getPoint(Double.valueOf(checkin.getLat()),
                        Double.valueOf(checkin.getLon())), checkin.getUser(), Util.limitString(
                        checkin.getMsg(), 30)));

            }

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return items.get(i);
        }

        @Override
        protected boolean onBalloonTap(int i) {
            return true;
        }

        @Override
        public int size() {
            return (items.size());
        }
    }

    private class DeviceLocationOverlay extends CheckinItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        public DeviceLocationOverlay(Drawable marker, MapView mapView) {
            super(boundCenterBottom(marker), mapView, CheckinMap.this);
            mapView.getContext();

            items.add(new OverlayItem(getPoint(latitude, longitude), "username", locationName));

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return items.get(i);
        }

        @Override
        protected boolean onBalloonTap(int i) {
            return true;
        }

        @Override
        public int size() {
            return (items.size());
        }
    }
}
