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
package com.ushahidi.android.app.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public abstract class BaseMapFragment extends SupportMapFragment {

	protected GoogleMap map;
	protected UpdatableMarker updatableMarker;
	protected static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		map = getMap();
	}

	/**
	 * Check if Google Maps exist on the device
	 * 
	 * @return
	 */
	protected boolean checkForGMap() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());

		if (status == ConnectionResult.SUCCESS) {
			return (true);
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(
					getActivity().getSupportFragmentManager(),
					TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Util.showToast(getActivity(), R.string.no_maps);
			getActivity().finish();
		}

		return false;
	}

	private class MapMarker implements UpdatableMarker {

		public MapMarker(LatLng point) {
			update(point);
		}

		public void update(LatLng point) {
			if (point != null)
				map.addMarker(new MarkerOptions().position(point));
		}
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

	public abstract interface UpdatableMarker {
		public abstract void update(LatLng point);
	}
}
