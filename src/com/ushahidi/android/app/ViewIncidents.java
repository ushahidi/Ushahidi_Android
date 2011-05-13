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

package com.ushahidi.android.app;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import com.ushahidi.android.app.util.Util;

public class ViewIncidents extends MapActivity implements AdapterView.OnItemSelectedListener,
        ViewSwitcher.ViewFactory {

    private MapView mapView;

    private MapController mapController;

    private GeoPoint defaultLocation;

    private TextView title;

    private TextView body;

    private TextView date;

    private TextView location;

    private TextView category;

    private TextView status;

    private TextView photos;

    private Bundle extras = new Bundle();

    private String media;

    private String image;

    private String thumbnails[];

    private String images[];

    private String reportLatitude;

    private String reportLongitude;

    private int id;

    private ImageSwitcher mSwitcher;

    private ImageAdapter imageAdapter;

    private ImageAdapter thumbnailAdapter;
    
    private TextView activityTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_incidents);
        mapView = (MapView)findViewById(R.id.loc_map);
        Bundle incidents = getIntent().getExtras();
        
        extras = incidents.getBundle("incidents");
        reportLatitude = extras.getString("latitude");
        reportLongitude = extras.getString("longitude");
        id = extras.getInt("id");
        
        String iStatus = Util.toInt(extras.getString("status")) == 0 ? getString(R.string.status_no)
                : getString(R.string.status_yes);
        title = (TextView)findViewById(R.id.title);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setText(extras.getString("title"));
        
        activityTitle = (TextView)findViewById(R.id.title_text);
        if (activityTitle != null)
            activityTitle.setText(getTitle());
        
        category = (TextView)findViewById(R.id.category);
        category.setTextColor(Color.BLACK);
        category.setText(extras.getString("category"));

        date = (TextView)findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText(extras.getString("date"));

        location = (TextView)findViewById(R.id.location);
        location.setTextColor(Color.BLACK);
        location.setText(extras.getString("location"));

        body = (TextView)findViewById(R.id.webview);
        body.setTextColor(Color.BLACK);
        body.setText(extras.getString("desc"));

        status = (TextView)findViewById(R.id.status);
        status.setText(iStatus);

        media = extras.getString("media");

        image = extras.getString("image");

        imageAdapter = new ImageAdapter(this);

        thumbnailAdapter = new ImageAdapter(this);

        mSwitcher = (ImageSwitcher)findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        if (!media.equals("")) {

            thumbnails = media.split(",");
            for (int i = 0; i < thumbnails.length; i++) {
                thumbnailAdapter.mImageIds.add(ImageManager.getImages(thumbnails[i]));
            }

            images = image.split(",");

            for (int i = 0; i < images.length; i++) {

                imageAdapter.mImageIds.add(ImageManager.getImages(images[i]));
            }

        } else {
            photos = (TextView)findViewById(R.id.report_photo);
            photos.setText("");
        }

        Gallery g = (Gallery)findViewById(R.id.gallery);

        g.setAdapter(thumbnailAdapter);
        g.setOnItemSelectedListener(this);

        mapController = mapView.getController();
        defaultLocation = getPoint(Double.parseDouble(reportLatitude),
                Double.parseDouble(reportLongitude));
        centerLocation(defaultLocation);

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

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT));
        return i;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mSwitcher.setImageDrawable(imageAdapter.mImageIds.get(position));

    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onShareClick(View v) {
        // TODO: consider bringing in shortlink to session
        UshahidiPref.loadSettings(ViewIncidents.this);
        final String reportUrl = UshahidiPref.domain + "/reports/view/" + id;
        final String shareString = getString(R.string.share_template, title.getText().toString(),
                reportUrl);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString);

        startActivity(Intent.createChooser(intent, getText(R.string.title_share)));
    }

    public int imageBackgroundColor() {
        TypedArray a = obtainStyledAttributes(R.styleable.PhotoGallery);
        int mGalleryItemBackground = a.getResourceId(
                R.styleable.PhotoGallery_android_galleryItemBackground, 0);
        a.recycle();

        return mGalleryItemBackground;
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

    public class ImageAdapter extends BaseAdapter {

        public Vector<Drawable> mImageIds;

        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
            mImageIds = new Vector<Drawable>();

        }

        public int getCount() {
            return mImageIds.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            i.setImageDrawable(mImageIds.get(position));

            i.setScaleType(ImageView.ScaleType.FIT_XY);

            i.setLayoutParams(new Gallery.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // The preferred Gallery item background
            i.setBackgroundResource(imageBackgroundColor());

            return i;
        }

    }

}
