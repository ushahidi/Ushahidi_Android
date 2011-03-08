package com.ushahidi.android.app.checkin;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/10/11 Time: 4:17 PM To change
 * this template use File | Settings | File Templates.
 */
public class LocationServices {
    public static boolean locationSet = false;

    public static Location location;

    public static CheckinActivity checkin_activity;
    
    private static boolean gps_provider = false;
    private static boolean network_provider = false;
    
    public static void dismissActionDialog() {
        checkin_activity.dismissCheckinProgressDialog();
    }

    public static void getLocation(CheckinActivity activity) {
        checkin_activity = activity;
        LocationServices.locationSet = false;
        final LocationManager locationManager = (LocationManager)activity
                .getSystemService(Context.LOCATION_SERVICE);
        
        List<String> providers = locationManager.getProviders(true);
        
     
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
                boolean isGpsFix = (SystemClock.elapsedRealtime() - 
                        location.getTime()) < 5000;
                //check if a fix was made
                if (isGpsFix) {
                    // make sure we are not sending a null object 
                    if (LocationServices.location != null) {
                        LocationServices.locationSet = true;
                    } else {
                        LocationServices.locationSet = false;
                    }
                
                    dismissActionDialog();
                } else {
                    LocationServices.locationSet = false;
                    dismissActionDialog();
                }
            }

            public void onProviderDisabled(String provider) {
                LocationServices.location = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
                if (LocationServices.location != null) {
                    LocationServices.locationSet = true;
                } else {
                    LocationServices.locationSet = false;
                }
                dismissActionDialog();
                
            }

            public void onProviderEnabled(String provider) {
              
                
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
