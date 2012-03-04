
package com.ushahidi.android.app;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapMarker extends ItemizedOverlay<OverlayItem> {

    private List<OverlayItem> locations = new ArrayList<OverlayItem>();

    private Drawable marker;

    private OverlayItem myOverlayItem;

    public MapMarker(Drawable defaultMarker, int LatitudeE6, int LongitudeE6) {
        super(defaultMarker);
        this.marker = defaultMarker;

        // create locations of interest
        GeoPoint myPlace = new GeoPoint(LatitudeE6, LongitudeE6);

        myOverlayItem = new OverlayItem(myPlace, " ", " ");

        locations.add(myOverlayItem);

        populate();

    }

    @Override
    protected OverlayItem createItem(int i) {
        return locations.get(i);
    }

    @Override
    public int size() {
        return locations.size();
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        boundCenterBottom(marker);
    }

}
