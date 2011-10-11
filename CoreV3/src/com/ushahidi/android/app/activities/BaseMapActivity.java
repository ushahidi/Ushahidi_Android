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

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.ushahidi.android.app.views.View;

/**
 * BaseMapActivity
 *
 * Add shared functionality that exists between all Map Activities
 */
public abstract class BaseMapActivity<V extends View> extends MapActivity implements LocationListener {

    /**
     * Layout resource id
     */
    protected final int layout;

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     *  MapView resource id
     */
    protected final int mapViewId;

    /**
     * View class
     */
    protected final Class<V> viewClass;

    /**
     * View
     */
    protected V view;

    /**
     * MapView
     */
    protected MapView mapView;

    /**
     *
     */
    protected LocationManager locationManager;

    /**
     *  BaseMapActivity
     * @param view View class type
     * @param layout layout resource id
     * @param menu menu resource id
     * @param mapView Map view resource id
     */
    protected BaseMapActivity(Class<V> view, int layout, int menu, int mapView) {
        this.viewClass = view;
        this.layout = layout;
        this.menu = menu;
        this.mapViewId = mapView;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layout != 0) {
            setContentView(layout);
        }
        if (mapViewId != 0) {
            mapView = (MapView)findViewById(mapViewId);
        }
        if (locationManager == null) {
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
	protected void onResume() {
		super.onResume();
    }

	@Override
	protected void onPause() {
		super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if (this.menu != 0) {
			getMenuInflater().inflate(this.menu, menu);
			return true;
		}
		return false;
    }

    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}

    public void centerAtLocation(double latitude, double longitude) {
		mapView.getController().setCenter(getPoint(latitude, longitude));
	}

	public void centerAtLocation(double latitude, double longitude, int zoom) {
		mapView.getController().setCenter(getPoint(latitude, longitude));
		mapView.getController().setZoom(zoom);
	}

    protected static GeoPoint getPoint(double latitude, double longitude) {
		return new GeoPoint((int)(latitude*1000000.0), (int)(longitude*1000000.0));
	}

    protected void log(String message) {
        Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object...args) {
        Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        Log.e(getClass().getName(), message, ex);
    }

    protected void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
    }

    protected void toastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }
}
