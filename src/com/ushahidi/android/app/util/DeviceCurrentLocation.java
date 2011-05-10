
package com.ushahidi.android.app.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class DeviceCurrentLocation {

    private double latitude;

    private double longitude;

    private Context mContext;
    
    private LocationManager mLocationMgr = null;

    public DeviceCurrentLocation(Context context) {
        mContext = context;
        this.latitude = 0.0d;
        this.longitude = 0.0d;
        
        //get current location
        this.setDeviceLocation();
    }

    // Fetches the current location of the device.
    private void setDeviceLocation() {

        DeviceLocationListener listener = new DeviceLocationListener();
        mLocationMgr = (LocationManager)mContext
                .getSystemService(Context.LOCATION_SERVICE);

        long updateTimeMsec = 1000L;

        // get low accuracy provider
        LocationProvider low = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                Util.createCoarseCriteria(), true));

        // get high accuracy provider
        LocationProvider high = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                Util.createFineCriteria(), true));

        mLocationMgr.requestLocationUpdates(low.getName(), updateTimeMsec, 500.0f, listener);

        mLocationMgr.requestLocationUpdates(high.getName(), updateTimeMsec, 500.0f, listener);

    }
    
    public void stopFetchingLocation()  {
        mLocationMgr.removeUpdates(new DeviceLocationListener());
        mLocationMgr = null;
    }

    // get the current location of the device/user
    private class DeviceLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {

            if (location != null) {
                mLocationMgr.removeUpdates(DeviceLocationListener.this);

                setLocationLatitude(location.getLatitude());
                setLocationLongitude(location.getLongitude());
                
                mLocationMgr = null;
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO: Do something when
        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    public void setLocationLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLocationLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLocationLatitude() {
        return this.latitude;
    }

    public double getLocationLongitude() {
        return this.longitude;
    }

}
