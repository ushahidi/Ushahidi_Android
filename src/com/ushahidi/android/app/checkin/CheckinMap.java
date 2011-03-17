
package com.ushahidi.android.app.checkin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.*;
import com.ushahidi.android.app.IncidentMap;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiItemizedOverlay;
import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.Util;

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

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        UshahidiPref.loadSettings(CheckinMap.this);

        boolean firstPoint = true;
        GeoPoint centerGeoPoint = new GeoPoint(0, 0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin_map);

        mHandler = new Handler();
        mapView = (MapView)findViewById(R.id.checkin_mapview);
        mapView.setBuiltInZoomControls(true);

        String strCheckinsJSON = NetworkServices.getCheckins(UshahidiPref.domain, null, null);
        RetrieveCheckinsJSONServices checkinsRetrieveCheckinsJSON = new RetrieveCheckinsJSONServices(
                strCheckinsJSON);
        checkinsList = checkinsRetrieveCheckinsJSON.getCheckinsList();

        int numCheckins = checkinsList.size();

        if (numCheckins > 0) {
            mapView.getController().setCenter(
                    getPoint(Double.valueOf(checkinsList.get(0).getLat()),
                            Double.valueOf(checkinsList.get(0).getLat())));
            mHandler.post(mMarkersOnMap);
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
}
