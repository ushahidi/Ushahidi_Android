package com.ushahidi.android.app.checkin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import com.google.android.maps.*;
import com.ushahidi.android.app.R;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 2/17/11
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckinMap extends MapActivity {
    private LinearLayout linearLayout;
    private MapView mapView;

    List<Overlay> mapOverlays;
    Drawable drawable;
    CheckinItemizedOverlay itemizedOverlay;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.checkin_map);

        mapView = (MapView) findViewById(R.id.checkin_mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.marker);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        itemizedOverlay = new CheckinItemizedOverlay(drawable);

        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "", "");

        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);

        mapView.getController().setCenter(point);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
