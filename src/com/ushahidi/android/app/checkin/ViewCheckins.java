/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app.checkin;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;

public class ViewCheckins extends MapActivity {

    private MapView mapView;

    private MapController mapController;

    private GeoPoint defaultLocation;

    private TextView name;

    private TextView message;

    private TextView date;

    private TextView photos;

    private Bundle extras = new Bundle();

    private ImageView image;
    private String fileName;
    private String checkinLatitude;

    private String checkinLongitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_checkins);

        mapView = (MapView)findViewById(R.id.loc_map);
        image = (ImageView)findViewById(R.id.checkin_img);
        Bundle incidents = getIntent().getExtras();

        extras = incidents.getBundle("checkins");
        
        // id = extras.getInt("id");
        checkinLatitude = extras.getString("latitude");
        checkinLongitude = extras.getString("longitude");

        name = (TextView)findViewById(R.id.title);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setText(com.ushahidi.android.app.checkin.CheckinUtil.getCheckinUser(extras
                .getString("name")));

        date = (TextView)findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText(extras.getString("date"));

        message = (TextView)findViewById(R.id.checkin_desc);
        message.setTextColor(Color.BLACK);
        message.setText(extras.getString("message"));

        
        fileName = extras.getString("photo");
        
        image.setImageDrawable(ImageManager
                .getImages(fileName));

        mapController = mapView.getController();
        defaultLocation = getPoint(Double.parseDouble(checkinLatitude),
                Double.parseDouble(checkinLongitude));
        centerLocation(defaultLocation);

    }
    
    public void onDestroy() {
        ViewCheckins.this.finish();
        super.onDestroy();
    }
    
    public void onPause() {
        ViewCheckins.this.finish();
        super.onPause();
    }

    private void placeMarker(int markerLatitude, int markerLongitude) {

        Drawable marker = getResources().getDrawable(R.drawable.marker);

        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mapView.getController().setZoom(14);

        mapView.setBuiltInZoomControls(true);
        mapView.getOverlays().add(new MapMarker(marker, markerLatitude, markerLongitude));
    }

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    private void centerLocation(GeoPoint centerGeoPoint) {

        mapController.animateTo(centerGeoPoint);
        placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());

    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    private class MapMarker extends ItemizedOverlay<OverlayItem> {

        private List<OverlayItem> locations = new ArrayList<OverlayItem>();

        private Drawable marker;

        private OverlayItem myOverlayItem;

        private boolean MoveMap = false;

        private long timer;

        public MapMarker(Drawable defaultMarker, int LatitudeE6, int LongitudeE6) {
            super(defaultMarker);
            this.timer = 0;
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

}
