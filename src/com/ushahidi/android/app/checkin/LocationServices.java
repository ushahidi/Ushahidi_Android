
package com.ushahidi.android.app.checkin;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/10/11 Time: 4:17 PM To change
 * this template use File | Settings | File Templates.
 */
public class LocationServices {
    public static boolean locationSet = false;

    public static Location location;

    public static CheckinActivity checkin_activity;

    public static void dismissActionDialog() {
        checkin_activity.dismissCheckinProgressDialog();
    }

    public static void getLocation(CheckinActivity activity) {
        checkin_activity = activity;
        LocationServices.locationSet = false;
        LocationManager locationManager = (LocationManager)activity
                .getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        boolean gps_provider = false, network_provider = false;

        for (String name : providers) {
            if (name.equals(LocationManager.GPS_PROVIDER))
                gps_provider = true;
            if (name.equals(LocationManager.NETWORK_PROVIDER))
                network_provider = true;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                LocationServices.location = location;
                LocationServices.locationSet = true;
                dismissActionDialog();
            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        };

        if (gps_provider || (!gps_provider && !network_provider)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f,
                    locationListener);
        } else if (network_provider) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f,
                    locationListener);
        }
    }
}
