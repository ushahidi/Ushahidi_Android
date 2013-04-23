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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.ushahidi.android.app.util.Util;

/**
 * Provide maps common function which should be reusable.
 */
public class GMap extends MapUserLocation {

	private Context mContext;
	/**
	 * Initialize GoogleMap
	 * 
	 * @param m The GoogleMap
	 */
	public GMap(GoogleMap m) {
		if (m != null) {
			map = m;
		}
	}
	
	public GMap(Context context) {
		mContext = context;
	}

	public boolean checkForGMaps() {
		return checkForGMap();
	}

	/**
	 * Convert latitude and longitude to a GeoPoint
	 * 
	 * @param latitude
	 *            Latitude
	 * @param longitude
	 *            Lingitude
	 * @return GeoPoint
	 */
	public LatLng getPoints(double latitude, double longitude) {
		return getPoint(latitude, longitude);

	}

	public void setActionBarTitle(String title, SherlockFragmentActivity fragmentActivity) {
		fragmentActivity.getSupportActionBar().setTitle(title);
	}

	public void placeMarker(double latitude, double longitude) {
		updateMarker(latitude, longitude, false);
	}

	public void centerLocationWithMarker(LatLng centerGeoPoint) {
		updateMarker(centerGeoPoint, true);
	}

	public void centerAtLocation(double latitude, double longitude) {
		updateMarker(latitude, longitude, true);
	}

	public void centerAtLocation(double latitude, double longitude, int zoom) {
		// TODO implement updateMarker with zoom in support
	}
	
	/**
	 * Check if Google Maps exist on the device
	 * 
	 * @return
	 */
	@Override
	public boolean checkForGMap() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

		if (status == ConnectionResult.SUCCESS) {
			return (true);
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(
					getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Util.showToast(mContext, R.string.no_maps);
			finish();
		}

		return false;
	}

	public static class ErrorDialogFragment extends DialogFragment {
		static final String ARG_STATUS = "status";

		static ErrorDialogFragment newInstance(int status) {
			Bundle args = new Bundle();

			args.putInt(ARG_STATUS, status);

			ErrorDialogFragment result = new ErrorDialogFragment();

			result.setArguments(args);

			return (result);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();

			return GooglePlayServicesUtil.getErrorDialog(
					args.getInt(ARG_STATUS), getActivity(), 0);
		}

		@Override
		public void onDismiss(DialogInterface dlg) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		}
	}


	@Override
	protected void locationChanged(double latitude, double longitude) {

	}

}
