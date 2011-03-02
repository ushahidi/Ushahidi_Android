
package com.ushahidi.android.app.checkin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import com.google.android.maps.*;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/17/11 Time: 2:21 PM To change
 * this template use File | Settings | File Templates.
 */
public class CheckinMap extends MapActivity {
    private LinearLayout linearLayout;

    private MapView mapView;

    List<Overlay> mapOverlays;

    Drawable drawable;

    CheckinItemizedOverlay itemizedOverlay;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        UshahidiPref.loadSettings(CheckinMap.this);

        boolean firstPoint = true;
        GeoPoint centerGeoPoint = new GeoPoint(0, 0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin_map);

        mapView = (MapView)findViewById(R.id.checkin_mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.marker);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        itemizedOverlay = new CheckinItemizedOverlay(drawable);

        String strCheckinsJSON = NetworkServices.getCheckins(UshahidiPref.domain, null, null);
        JSONServices checkinsJSON = new JSONServices(strCheckinsJSON);

        ArrayList checkinsList = checkinsJSON.getCheckinsList();

        int numCheckins = checkinsList.size();

        for (int checkinsLoop = 0; checkinsLoop < numCheckins; checkinsLoop++) {
            Checkin currentCheckin = (Checkin)checkinsList.get(checkinsLoop);

            Double latitude = Double.valueOf(currentCheckin.getLat()) * 1E6;
            Double longitude = Double.valueOf(currentCheckin.getLon()) * 1E6;

            GeoPoint point = new GeoPoint(latitude.intValue(), longitude.intValue());
            OverlayItem overlayitem = new OverlayItem(point, currentCheckin.getUser(),
                    currentCheckin.getMsg());

            itemizedOverlay.addOverlay(overlayitem);

            if (firstPoint) {
                firstPoint = false;
                centerGeoPoint = point;
            }
        }

        mapOverlays.add(itemizedOverlay);
        mapView.getController().setCenter(centerGeoPoint);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false; // To change body of implemented methods use File |
                      // Settings | File Templates.
    }
}
