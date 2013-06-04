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

package com.ushahidi.android.app.ui.tablet;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.PopupAdapter;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.fragments.BaseMapFragment;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.ui.phone.ViewReportSlideActivity;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.Util;

public class MapFragment extends BaseMapFragment implements
		OnInfoWindowClickListener {

	private ListReportModel mListReportModel;

	private List<ReportEntity> mReportModel;

	private Handler mHandler;

	private int filterCategory = 0;

	private MenuItem refresh;

	private CategorySpinnerAdater spinnerArrayAdapter;

	private boolean refreshState = false;

	private UpdatableMarker mMarker = createUpdatableMarker();

	public MapFragment() {
		super(R.menu.map_report);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListReportModel = new ListReportModel();
		mListReportModel.load();
		mReportModel = mListReportModel.getReports();
		showCategories();
		mHandler = new Handler();

		if (checkForGMap()) {
			map = getMap();

			Preferences.loadSettings(getActivity());

			initMap();
			map.setInfoWindowAdapter(new PopupAdapter(
					getLayoutInflater(savedInstanceState)));
			map.setOnInfoWindowClickListener(this);

		}

	}

	private void initMap() {
		// set up the map tile use
		Util.setMapTile(getActivity(), map);
		if (mReportModel.size() > 0) {
			setupMapCenter();
			mHandler.post(mMarkersOnMap);

		} else {
			toastLong(R.string.no_reports);
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if (mReportModel != null) {

			List<String> markers = mMarker.markersHolder;

			// FIX ME: Using the title to find which latlng have been tapped.
			// This ugly hack has to do with the limitation in Google maps api
			// for android. There is a
			// posibility of having the wront position returned in case there
			// are two or more of the same title.
			// SEE:https://code.google.com/p/gmaps-api-issues/issues/detail?id=4650
			final int position = markers.indexOf(marker.getTitle());
			if (markers != null && markers.size() > 0) {
				
				launchViewReport(position, "");
			}
		}

		if (marker.isInfoWindowShown())
			marker.hideInfoWindow();
	}

	private void launchViewReport(int position, final String filterCategory) {
		Intent i = new Intent(getActivity(), ViewReportSlideActivity.class);
		i.putExtra("id", position);
		if (filterCategory != null
				&& !filterCategory.equalsIgnoreCase(getActivity().getString(
						R.string.all_categories))) {
			i.putExtra("category", filterCategory);
		} else {
			i.putExtra("category", "");
		}
		getActivity().startActivityForResult(i, 0);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);

	}

	protected void setupMapCenter() {
		if (map != null) {
			final View mapView = getView();
			if (mapView != null) {
				if (mapView.getViewTreeObserver().isAlive()) {
					mapView.getViewTreeObserver().addOnGlobalLayoutListener(
							new OnGlobalLayoutListener() {
								@SuppressWarnings("deprecation")
								// We use the new method when supported
								@SuppressLint("NewApi")
								// We check which build version we are using.
								@Override
								public void onGlobalLayout() {

									LatLng latLng = getReportLatLng();

									if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
										mapView.getViewTreeObserver()
												.removeGlobalOnLayoutListener(
														this);
									} else {
										mapView.getViewTreeObserver()
												.removeOnGlobalLayoutListener(
														this);
									}
									if (latLng != null)
										map.moveCamera(CameraUpdateFactory
												.newLatLng(latLng));

								}
							});
				}
			}
		}
	}

	private LatLng getReportLatLng() {
		if (mReportModel != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (ReportEntity reportEntity : mReportModel) {
				double latitude = 0.0;
				double longitude = 0.0;
				try {
					latitude = Double.valueOf(reportEntity.getIncident()
							.getLatitude());
				} catch (NumberFormatException e) {
					latitude = 0.0;
				}

				try {
					longitude = Double.valueOf(reportEntity.getIncident()
							.getLongitude());
				} catch (NumberFormatException e) {
					longitude = 0.0;
				}

				builder.include(new LatLng(latitude, longitude));
			}
			return Util.getCenter(builder.build());
		}
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_refresh) {
			refresh = item;
			new RefreshReports(getActivity()).execute((String) null);
			return true;
		} else if (item.getItemId() == R.id.menu_add) {
			launchAddReport();
			return true;
		} else if (item.getItemId() == R.id.menu_normal) {
			if (Preferences.mapTiles.equals("google")) {
				map.setMapType(GoogleMap.MAP_TYPE_NONE);
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}

			return true;
		} else if (item.getItemId() == R.id.menu_satellite) {
			if (Preferences.mapTiles.equals("google")) {
				map.setMapType(GoogleMap.MAP_TYPE_NONE);
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}

			return true;

		} else if (item.getItemId() == R.id.filter_by) {

			showDropDownNav();

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	protected View headerView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.map_view_header, null, false);
		TextView textView = (TextView) viewGroup.findViewById(R.id.map_header);
		textView.setText(R.string.all_categories);
		return viewGroup;
	}

	// FIXME:: look into how to put this in it own class
	private void showDropDownNav() {
		showCategories();
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.prompt_mesg))
				.setAdapter(spinnerArrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								filterCategory = spinnerArrayAdapter.getTag(
										which).getCategoryId();
								final String all = spinnerArrayAdapter.getTag(
										which).getCategoryTitle();
								if ((all != null)
										&& (!TextUtils.isEmpty(all))
										&& (all != getString(R.string.all_categories))) {

									mHandler.post(fetchReportListByCategory);

								} else {
									mHandler.post(fetchReportList);
								}

								dialog.dismiss();
							}
						}).create().show();
	}

	public void showCategories() {
		spinnerArrayAdapter = new CategorySpinnerAdater(getActivity());
		spinnerArrayAdapter.refresh();
	}

	/**
	 * refresh by category id
	 */
	final Runnable fetchReportListByCategory = new Runnable() {
		public void run() {
			try {
				final boolean loaded = mListReportModel
						.loadReportByCategory(filterCategory);
				if (loaded) {
					mReportModel = mListReportModel.getReports();
					populateMap();
				}
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Refresh the list view with new items
	 */
	final Runnable fetchReportList = new Runnable() {
		public void run() {
			try {
				mListReportModel.load();
				mReportModel = mListReportModel.getReports();
				populateMap();
				showCategories();
			} catch (Exception e) {
				return;
			}
		}
	};

	private void updateRefreshStatus() {
		if (refresh != null) {
			if (refreshState)
				refresh.setActionView(R.layout.indeterminate_progress_action);
			else
				refresh.setActionView(null);
		}

	}

	/**
	 * Restart the receiving, when we are back on line.
	 */

	@Override
	public void onResume() {
		super.onResume();
		initMap();
	}

	public void onDestroy() {
		super.onDestroy();
		if (new RefreshReports(getActivity()).cancel(true)) {
			refreshState = false;
			updateRefreshStatus();
		}
	}

	// put this stuff in a seperate thread
	final Runnable mMarkersOnMap = new Runnable() {
		public void run() {
			populateMap();
		}
	};

	/**
	 * add marker to the map
	 */
	public void populateMap() {

		if (mReportModel != null) {
			mMarker.markersHolder.clear();
			for (ReportEntity reportEntity : mReportModel) {
				double latitude = 0.0;
				double longitude = 0.0;
				try {
					latitude = Double.valueOf(reportEntity.getIncident()
							.getLatitude());
				} catch (NumberFormatException e) {
					latitude = 0.0;
				}

				try {
					longitude = Double.valueOf(reportEntity.getIncident()
							.getLongitude());
				} catch (NumberFormatException e) {
					longitude = 0.0;
				}
				final String description = Util.limitString(reportEntity
						.getIncident().getDescription(), 30);

				mMarker.addMarkerWithIcon(map, latitude, longitude,
						reportEntity.getIncident().getTitle(), description,
						reportEntity.getThumbnail());

			}
		}
	}

	public void launchAddReport() {
		Intent i = new Intent(getActivity(), AddReportActivity.class);
		i.putExtra("id", 0);
		startActivityForResult(i, 2);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	private void deleteFetchedReport() {
		final List<ReportEntity> items = new ListFetchedReportAdapter(
				getActivity()).fetchedReports();
		for (ReportEntity report : items) {
			if (new ListReportModel().deleteAllFetchedReport(report
					.getIncident().getId())) {
				final List<PhotoEntity> photos = new ListPhotoModel()
						.getPhotosByReportId(report.getIncident().getId());

				for (PhotoEntity photo : photos) {
					ImageManager.deletePendingPhoto(getActivity(),
							"/" + photo.getPhoto());
				}
			}

		}

	}

	/**
	 * Refresh for new reports
	 */
	class RefreshReports extends ProgressTask {

		protected Integer status = 4; // there is no internet

		public RefreshReports(Activity activity) {
			super(activity, R.string.loading_);
			// pass custom loading message to super call
			refreshState = true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.cancel();
			refreshState = true;
			updateRefreshStatus();
		}

		@Override
		protected Boolean doInBackground(String... strings) {
			try {
				// check if there is internet
				if (Util.isConnected(getActivity())) {
					// delete everything before updating with a new one
					deleteFetchedReport();

					// fetch categories -- assuming everything will go just
					// right!
					new CategoriesApi().getCategoriesList();

					status = new ReportsApi().saveReports(getActivity()) ? 0
							: 99;
				}

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				log("fetching ");
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_reports);
				} else if (status == 0) {
					log("successfully fetched");
					mReportModel = mListReportModel.getReports();
					populateMap();
					showCategories();

				}
			}
			refreshState = false;
			updateRefreshStatus();
		}
	}

}