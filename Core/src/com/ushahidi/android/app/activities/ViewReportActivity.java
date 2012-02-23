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

package com.ushahidi.android.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ListReportListFragment;
import com.ushahidi.android.app.fragments.ViewReportFragment;
import com.ushahidi.android.app.models.ViewReportModel;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ViewReportView;

/**
 * @author eyedol
 */
public class ViewReportActivity extends BaseMapViewActivity<ViewReportView, ViewReportModel>
        implements AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    private long id;

    private MapController mapController;

    private GeoPoint defaultLocation;

    private Bundle extras;
    
    private ViewReportView mViewReportView;
    
    private Bundle photosBundle = new Bundle();

    public ViewReportActivity() {
        super(ViewReportView.class, R.layout.view_report, R.menu.view_report, R.id.loc_map);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.isTablet(this) || Util.isHoneycombTablet(this)) {
            finish();
            return;
        }
        mViewReportView = new ViewReportView(this);
        extras = new Bundle();
        id = extras.getLong("id");
        
        mViewReportView.getGallery().setOnItemSelectedListener(this);
        mViewReportView.getGallery().setOnItemClickListener(new OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                previewImage(position);
            }
            
        });
        
        if (savedInstanceState == null) {
            ViewReportFragment details = new ViewReportFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details)
                    .commit();
        }
    }
    
    private void previewImage(int position) {
        //FIXME redo this
       /* photosBundle.putInt("position", position);
        
        photosBundle.putStringArray("images", thumbnails);
        Intent intent = new Intent(this, ImagePreviewer.class);
        intent.putExtra("photos", photosBundle);
        startActivityForResult(intent, 0);
        setResult(RESULT_OK, intent);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ListReportListFragment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Get rid of the slide-in animation, if possible
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    OverridePendingTransition.invoke(this);
                }
        }

        return super.onOptionsItemSelected(item);
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

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

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
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    private static final class OverridePendingTransition {
        static void invoke(Activity activity) {
            activity.overridePendingTransition(0, 0);
        }
    }

    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
