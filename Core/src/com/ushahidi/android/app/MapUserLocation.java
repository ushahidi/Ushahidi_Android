
package com.ushahidi.android.app;

import java.util.Date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.util.Util;

public abstract class MapUserLocation extends MapActivity implements LocationListener {

    protected static final int ONE_MINUTE = 60 * 1000;

    protected static final int FIVE_MINUTES = 5 * ONE_MINUTE;

    protected double sLongitude = 0.0;

    protected double sLatitude = 0.0;

    protected MapView mapView = null;

    protected MapController mapController;

    protected LocationManager mLocationMgr;

    protected UpdatableMarker mMapMark;

    protected Location curLocation;

    /*
     * Subclasses must implement a method which updates any relevant interface
     * elements when the location changes. e.g. TextViews displaying the
     * location.
     */
    protected abstract void updateInterface();

    /* Override this to set a custom marker */
    protected UpdatableMarker createUpdatableMarker(Drawable marker, GeoPoint point) {
        return new MapMarker(marker, point);
    }

    protected void setDeviceLocation() {
        mLocationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location nloc = null;
        Location gloc = null;

        boolean netAvail = mLocationMgr.getProvider(LocationManager.NETWORK_PROVIDER) != null;
        boolean gpsAvail = mLocationMgr.getProvider(LocationManager.GPS_PROVIDER) != null;
        boolean anyEnabled = (mLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || mLocationMgr
                .isProviderEnabled(LocationManager.GPS_PROVIDER));

        if ((!netAvail && !gpsAvail) || !anyEnabled)
            Util.showToast(MapUserLocation.this, R.string.location_not_found);

        if (netAvail)
            nloc = mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gpsAvail)
            gloc = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setBestLocation(nloc, gloc);

        // If chosen location is more than a minute old, start querying
        // network/GPS
        if (curLocation == null || (new Date()).getTime() - curLocation.getTime() > ONE_MINUTE) {
            if (netAvail)
                mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            if (gpsAvail)
                mLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public void stopLocating() {
        if (mLocationMgr != null) {
            try {
                mLocationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mLocationMgr = null;
        }
    }

    protected void placeMarker(GeoPoint point) {
        if (mMapMark == null) {
            Drawable marker = getResources().getDrawable(R.drawable.map_marker_green);

            marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
            mapController.setZoom(14);

            mapView.setBuiltInZoomControls(true);
            mMapMark = createUpdatableMarker(marker, point);
            mapView.getOverlays().add((Overlay)mMapMark);
        } else {
            mMapMark.updateLocation(point);
        }
    }

    // Location stuff
    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)));
    }

    protected void setLocation(Location loc) {
        if (loc != null) {
            curLocation = loc;
            sLatitude = loc.getLatitude();
            sLongitude = loc.getLongitude();

            GeoPoint gp = getPoint(sLatitude, sLongitude);
            mapController.animateTo(gp);
            placeMarker(gp);
            updateInterface();
        }
    }

    protected void setBestLocation(Location f1, Location f2) {
        if (f1 == null && f2 == null)
            return;
        if (f1 == null) {
            setLocation(f2);
            return;
        }
        if (f2 == null) {
            setLocation(f1);
            return;
        }
        boolean f1SigNewer = f1.getTime() - f2.getTime() > FIVE_MINUTES;
        boolean f2SigNewer = f2.getTime() - f1.getTime() > FIVE_MINUTES;
        if (f1SigNewer)
            setLocation(f1);
        if (f2SigNewer)
            setLocation(f1);
        boolean f1MoreAccurate = f1.getAccuracy() < f2.getAccuracy();
        if (f1.hasAccuracy() && f2.hasAccuracy() && f1MoreAccurate) {
            setLocation(f1);
        } else {
            setLocation(f2);
        }
    }

    private class MapMarker extends ItemizedOverlay<OverlayItem> implements UpdatableMarker {
        private OverlayItem myOverlayItem;

        public MapMarker(Drawable defaultMarker, GeoPoint point) {
            super(boundCenterBottom(defaultMarker));
            updateLocation(point);
        }

        public void updateLocation(GeoPoint point) {
            myOverlayItem = new OverlayItem(point, " ", " ");
            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return myOverlayItem;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    public void onLocationChanged(Location loc) {
        if (loc != null) {
            setLocation(loc);
            // TODO: Don't settle for first location, condition
            // stopLocating on quality of fix + probability that we'll
            // get a better one.
            stopLocating();
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        setDeviceLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocating();
    }

    @Override
    protected void onDestroy() {
        super.onPause();
        stopLocating();
    }

    public abstract interface UpdatableMarker {
        public abstract void updateLocation(GeoPoint point);
    }
}
