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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ushahidi.android.app.MapUserLocation;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.Objects;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.View;

/**
 * BaseMapActivity Add shared functionality that exists between all Map
 * Activities
 */
public abstract class BaseMapActivity<V extends View> extends MapUserLocation
		implements LocationListener {

	/**
	 * Layout resource id
	 */
	protected final int layout;

	/**
	 * Menu resource id
	 */
	protected final int menu;

	/**
	 * MapView resource id
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
	protected GoogleMap mapView;

	/**
     *
     */
	protected LocationManager locationManager;

	/**
	 * BaseMapActivity
	 * 
	 * @param view
	 *            View class type
	 * @param layout
	 *            layout resource id
	 * @param menu
	 *            menu resource id
	 * @param mapView
	 *            Map view resource id
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
			if (checkForGMap()) {
				SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(mapViewId);

				mapView = mapFrag.getMap();
				super.map = mapView;
			}
		}
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		view = Objects.createInstance(viewClass, Activity.class, this);
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
			getSupportMenuInflater().inflate(this.menu, menu);
			return true;
		}
		return false;
	}

	protected void shareText(String shareItem) {

		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, shareItem);

		startActivity(Intent.createChooser(intent,
				getText(R.string.title_share)));
	}

	protected void sharePhoto(String path) {

		// TODO: consider bringing in shortlink to session
		Preferences.loadSettings(this);
		final String reportUrl = Preferences.domain;
		final String shareString = getString(R.string.share_template, "",
				reportUrl);
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/jpg");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		intent.putExtra(Intent.EXTRA_TEXT, shareString);
		startActivityForResult(
				Intent.createChooser(intent, getText(R.string.title_share)), 0);
		setResult(RESULT_OK);

	}

	protected void setActionBarTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

	protected void placeMarker(double latitude, double longitude) {
		updateMarker(latitude, longitude,false);
	}

	protected void centerLocationWithMarker(LatLng centerGeoPoint) {
		updateMarker(centerGeoPoint,true);
	}

	protected void centerAtLocation(double latitude, double longitude) {
		updateMarker(latitude, longitude,true);
	}

	protected void centerAtLocation(double latitude, double longitude, int zoom) {
		//TODO implement updateMarker with zoom in support
	}

	protected LatLng getPoint(double latitude, double longitude) {
		return getPoint(latitude, longitude);
	}

	protected void log(String message) {
		new Util().log(message);
	}

	protected void log(String format, Object... args) {
		new Util().log(String.format(format, args));
	}

	protected void log(String message, Exception ex) {
		new Util().log(message, ex);
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