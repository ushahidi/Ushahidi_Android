package com.ushahidi.android.app;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ushahidi.android.app.util.Util;

public abstract class MapUserLocation extends SherlockFragmentActivity
		implements LocationListener {

	protected static final int ONE_MINUTE = 60 * 1000;

	protected static final int FIVE_MINUTES = 5 * ONE_MINUTE;

	protected static final int ACCURACY_THRESHOLD = 30; // in meters

	protected static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
	
	protected static final int ZOOM = 15;

	protected GoogleMap map;

	protected LocationManager locationManager;

	protected UpdatableMarker updatableMarker;

	protected Location currrentLocation;
	

	/*
	 * Subclasses must implement a method which updates any relevant interface
	 * elements when the location changes. e.g. TextViews displaying the
	 * location.
	 */
	protected abstract void locationChanged(double latitude, double longitude);

	/* Override this to set a custom marker */
	protected UpdatableMarker createUpdatableMarker(LatLng point) {
		return new MapMarker(point);
	}

	protected void setDeviceLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Location lastNetLocation = null;
		Location lastGpsLocation = null;

		boolean netAvailable = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean gpsAvailable = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!netAvailable && !gpsAvailable) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.location_disabled))
					.setMessage(getString(R.string.location_reenable))
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startActivity(new Intent(
											android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).create().show();
		}
		if (netAvailable) {
			lastNetLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (gpsAvailable) {
			lastGpsLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		setBestLocation(lastNetLocation, lastGpsLocation);
		// If chosen location is more than a minute old, start querying
		// network/GPS
		if (currrentLocation == null
				|| (new Date()).getTime() - currrentLocation.getTime() > ONE_MINUTE) {
			if (netAvailable) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}
			if (gpsAvailable) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, this);
			}
		}
	}

	public void stopLocating() {
		if (locationManager != null) {
			try {
				locationManager.removeUpdates(this);
			} catch (Exception ex) {
				Log.e(getClass().getSimpleName(), "stopLocating", ex);
			}
			locationManager = null;
		}
	}

	protected void updateMarker(double latitude, double longitude,
			boolean center) {
		updateMarker(getPoint(latitude, longitude), center);
	}

	protected void updateMarker(LatLng point, boolean center) {
		if (map != null) {
			
			if (updatableMarker == null) {
				/*
				 * LatLngBounds bounds = new LatLngBounds.Builder()
				 * .include(point).build();
				 */
				CameraUpdate p = CameraUpdateFactory.newLatLng(point);
				map.moveCamera(p);

				updatableMarker = createUpdatableMarker(point);

			} else {
				updatableMarker.update(point);
			}
			if (center) {
				CameraUpdate c = CameraUpdateFactory.newLatLng(point);
				map.moveCamera(c);
			}
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(ZOOM);
			map.animateCamera(zoom);
			map.getUiSettings().setZoomControlsEnabled(false);
		}

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
	protected LatLng getPoint(double latitude, double longitude) {
		return (new LatLng(latitude, longitude));
	}

	protected void setBestLocation(Location location1, Location location2) {
		if (location1 != null && location2 != null) {
			boolean location1Newer = location1.getTime() - location2.getTime() > FIVE_MINUTES;
			boolean location2Newer = location2.getTime() - location1.getTime() > FIVE_MINUTES;
			boolean location1MoreAccurate = location1.getAccuracy() < location2
					.getAccuracy();
			boolean location2MoreAccurate = location2.getAccuracy() < location1
					.getAccuracy();
			if (location1Newer || location1MoreAccurate) {
				locationChanged(location1.getLatitude(),
						location1.getLongitude());
			} else if (location2Newer || location2MoreAccurate) {
				locationChanged(location2.getLatitude(),
						location2.getLongitude());
			}
		} else if (location1 != null) {
			locationChanged(location1.getLatitude(), location1.getLongitude());
		} else if (location2 != null) {
			locationChanged(location2.getLatitude(), location2.getLongitude());
		}
	}

	/**
	 * Check if Google Maps exist on the device
	 * 
	 * @return
	 */
	protected boolean checkForGMap() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (status == ConnectionResult.SUCCESS) {
			return (true);
		} else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
			ErrorDialogFragment.newInstance(status).show(
					getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
		} else {
			Util.showToast(this, R.string.no_maps);
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

	private class MapMarker implements UpdatableMarker {

		public MapMarker(LatLng point) {
			update(point);
		}

		public void update(LatLng point) {
			if (point != null)
				map.addMarker(new MarkerOptions().position(point));
		}

		/*
		 * @Override public boolean onTouchEvent(MotionEvent event, MapView
		 * mapView) { final int action = event.getAction(); final int x = (int)
		 * event.getX(); final int y = (int) event.getY(); if (action ==
		 * MotionEvent.ACTION_DOWN) { long thisTime =
		 * System.currentTimeMillis(); if (thisTime - lastTouchTime < 250) {
		 * lastTouchTime = -1; GeoPoint geoPoint =
		 * mapView.getProjection().fromPixels( (int) event.getX(), (int)
		 * event.getY()); double latitude = geoPoint.getLatitudeE6() / 1E6;
		 * double longitude = geoPoint.getLongitudeE6() / 1E6;
		 * Log.i(getClass().getSimpleName(), String.format( "%d, %d >> %f, %f",
		 * x, y, latitude, longitude)); locationChanged(latitude, longitude);
		 * return true; } else { lastTouchTime = thisTime; } } return
		 * super.onTouchEvent(event, mapView); }
		 */
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			locationChanged(location.getLatitude(), location.getLongitude());
			if (location.hasAccuracy()
					&& location.getAccuracy() < ACCURACY_THRESHOLD) {
				// accuracy is within ACCURACY_THRESHOLD, de-activate location
				// detection
				stopLocating();
			}
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		setDeviceLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocating();
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		stopLocating();
	}

	public abstract interface UpdatableMarker {
		public abstract void update(LatLng point);
	}
}