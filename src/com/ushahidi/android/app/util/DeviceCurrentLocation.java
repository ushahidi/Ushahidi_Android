
package com.ushahidi.android.app.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class DeviceCurrentLocation implements LocationListener {

    public static double latitude;

    public static double longitude;

    private Context mContext;

    private LocationManager mLocationMgr = null;

    private static Location loc;

    private static final String CLASS_TAG = DeviceCurrentLocation.class.getCanonicalName();

    public DeviceCurrentLocation(Context context) {
        mContext = context;
        setDeviceLocation();
    }

    // Fetches the current location of the device.
    public void setDeviceLocation() {

        mLocationMgr = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);

        long updateTimeMsec = 30 * 1000;
        try {
            // get low accuracy provider
            LocationProvider low = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                    Util.createCoarseCriteria(), true));

            // get high accuracy provider
            LocationProvider high = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                    Util.createFineCriteria(), true));

            mLocationMgr.requestLocationUpdates(low.getName(), updateTimeMsec, 0, this);

            mLocationMgr.requestLocationUpdates(high.getName(), updateTimeMsec, 0, this);

            try {

                // defaulting to Accra :-)

                Location hardFix = new Location("ACC");
                hardFix.setLatitude(5.555717);
                hardFix.setLongitude(-0.196306);

                try {
                    Location gps = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = mLocationMgr
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (gps != null)
                        onLocationChanged(gps);
                    else if (network != null)
                        onLocationChanged(network);
                    else
                        onLocationChanged(hardFix);
                } catch (Exception ex2) {
                    onLocationChanged(hardFix);
                }

            } catch (Exception ex) {
                Log.d(CLASS_TAG, ex.getMessage());
            }
        } catch (Exception ex1) {
            try {

                if (mLocationMgr != null) {
                    mLocationMgr.removeUpdates(this);
                    mLocationMgr = null;
                }
            } catch (Exception ex2) {
                Log.d(CLASS_TAG, ex2.getMessage());
            }
        }

    }

    public void stopLocating() {

        try {

            try {
                mLocationMgr.removeUpdates(this);
            } catch (Exception ex) {
                Log.d(CLASS_TAG, ex.getMessage());
            }
            mLocationMgr = null;
        } catch (Exception ex) {
            Log.d(CLASS_TAG, ex.getMessage());
        }
    }

    public void onLocationChanged(Location location) {
        if( location !=null ) {
            setLocation(location);
            stopLocating();
        }
    }

    public void onProviderDisabled(String provider) {
        // don't mind me

    }

    public void onProviderEnabled(String provider) {
        // don't mind me

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // don't mind me

    }

    public static void setLocation(Location location) {
        if (location != null) {
            loc = location;
        }
    }

    public static Location getLocation() {
        return loc;
    }
}
