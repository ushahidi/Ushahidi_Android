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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.Dashboard;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.ui.ImagePreviewer;

public class CheckinView extends MapActivity {

    private MapView mapView;

    private MapController mapController;

    private GeoPoint defaultLocation;

    private TextView name;

    private TextView message;

    private TextView date;

    private TextView photo;
    
    private TextView activityTitle;

    private Bundle extras = new Bundle();

    private ImageView image;
    
    private LinearLayout photoLayout;

    private String fileName;

    private String checkinLatitude;

    private String checkinLongitude;
    
    private Bundle photosBundle = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkin_view);

        mapView = (MapView)findViewById(R.id.loc_map);
        image = (ImageView)findViewById(R.id.checkin_img);
        photo = (TextView)findViewById(R.id.checkin_photo);
        photoLayout = (LinearLayout)findViewById(R.id.img_layout);
        photoLayout.setVisibility(View.GONE);
        Bundle incidents = getIntent().getExtras();
        photo.setVisibility(View.GONE);
        extras = incidents.getBundle("checkins");
        photosBundle = new Bundle();
        checkinLatitude = extras.getString("latitude");
        checkinLongitude = extras.getString("longitude");
        activityTitle = (TextView)findViewById(R.id.title_text);
        if (activityTitle != null)
            activityTitle.setText(getTitle());
        name = (TextView)findViewById(R.id.checkin_title);
        name.setTextColor(Color.BLACK);
        name.setText(extras.getString("name"));

        date = (TextView)findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText(extras.getString("date"));

        message = (TextView)findViewById(R.id.checkin_description);
        message.setTextColor(Color.BLACK);
        message.setText(extras.getString("message"));

        fileName = extras.getString("photo");
        if (!TextUtils.isEmpty(fileName)) {
            photoLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);
            //image.setImageDrawable(ImageManager.getImages(Preferences.savePath,fileName));
        }

        mapController = mapView.getController();
        defaultLocation = getPoint(Double.parseDouble(checkinLatitude),
                Double.parseDouble(checkinLongitude));
        centerLocation(defaultLocation);

    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onShareClick(View v) {
        // TODO: Implement URL shortening...
        Preferences.loadSettings(CheckinView.this);
        final String reportUrl = Preferences.domain;
        final String shareString = getString(R.string.share_template, name.getText().toString(),
                reportUrl);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString);

        startActivity(Intent.createChooser(intent, getText(R.string.title_share)));
    }

    public void onImageClick(View v) {
        if (!TextUtils.isEmpty(fileName)) {
            previewImage(fileName);
        }
    }
    
    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    public void goHome(Context context) {
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void previewImage(String filename) {
        String images [] = {filename};
        photosBundle.putStringArray("images", images);
        Intent intent = new Intent(this, ImagePreviewer.class);
        intent.putExtra("photos", photosBundle);
        startActivityForResult(intent, 0);
        setResult(RESULT_OK, intent);
    }

    private void placeMarker(int markerLatitude, int markerLongitude) {

        Drawable marker = getResources().getDrawable(R.drawable.map_marker_red);

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

}
