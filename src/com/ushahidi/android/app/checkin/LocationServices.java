
package com.ushahidi.android.app.checkin;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.ushahidi.android.app.Util;

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
        final LocationManager locationManager = (LocationManager)activity
                .getSystemService(Context.LOCATION_SERVICE);

        LocationProvider low = locationManager.getProvider(locationManager.getBestProvider(
                Util.createCoarseCriteria(), true));

        // get high accuracy provider
        LocationProvider high = locationManager.getProvider(locationManager.getBestProvider(
                Util.createFineCriteria(), true));

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub

                LocationServices.location = location;

                if (LocationServices.location != null) {
                    LocationServices.locationSet = true;
                }
                ((LocationManager)checkin_activity.getSystemService(Context.LOCATION_SERVICE))
                        .removeUpdates(this);
                dismissActionDialog();

            }

            public void onProviderDisabled(String provider) {
                LocationServices.location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (LocationServices.location != null) {
                    LocationServices.locationSet = true;
                }

                ((LocationManager)checkin_activity.getSystemService(Context.LOCATION_SERVICE))
                        .removeUpdates(this);
                dismissActionDialog();

            }

            public void onProviderEnabled(String provider) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        };

        locationManager.requestLocationUpdates(low.getName(), 1000L, 500.0f, locationListener);
        locationManager.requestLocationUpdates(high.getName(), 1000L, 500.0f, locationListener);

    }

}
