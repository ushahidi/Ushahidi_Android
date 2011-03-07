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
    
    private static long lastKnownLocationTimeMillis = 0;
    private static boolean isGpsFix = false;

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
                lastKnownLocationTimeMillis = SystemClock.elapsedRealtime();
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
        
        /** 
         * This Class reports on the GPS engine status. It assumes after 3seconds of no reporting of 
         * a fix. It stops getting the fix
         */ 
        GpsStatus.Listener gpsListener = new GpsStatus.Listener() {

            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (LocationServices.location != null)
                            isGpsFix = (SystemClock.elapsedRealtime() - lastKnownLocationTimeMillis) < 3000;

                        if (isGpsFix) { // A fix has been acquired.
          
                            LocationServices.locationSet = true;
                            dismissActionDialog();
                        } else { // The fix has been lost.
                            LocationServices.locationSet = false;
                            dismissActionDialog();
                            
                        }
                        break;
                    
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        
                        isGpsFix = true;    
                        // A fix has been acquired.
                        LocationServices.locationSet = true;
                        dismissActionDialog();
                        
                        break;
                    default:
                            
                        LocationServices.locationSet = false;
                        dismissActionDialog();
                        
                }
                
            }
                   
        };
        
        locationManager.addGpsStatusListener(gpsListener);
        
    }
    
    
}
