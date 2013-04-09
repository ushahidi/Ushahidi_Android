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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseMapActivity;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.fragments.BaseMapFragment;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ViewReportView;

public class MapFragment extends BaseMapFragment {

	private GoogleMap map = null;

	private ListReportModel mListReportModel;

	private List<ReportEntity> mReportModel;

	private Handler mHandler;

	private int filterCategory = 0;

	private MenuItem refresh;

	private CategorySpinnerAdater spinnerArrayAdapter;

	private ViewGroup mRootView;

	private ImageButton addReport = null;

	private ImageButton refreshReport = null;

	private ImageButton filterReport = null;

	private boolean refreshState = false;

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
			/*
			 * SupportMapFragment mapFrag = (SupportMapFragment) getActivity()
			 * .getSupportFragmentManager().findFragmentById(
			 * R.id.map_fragment);
			 */

			map = getMap();

			Preferences.loadSettings(getActivity());
			if (mReportModel.size() > 0) {
				// map.setClickable(true);
				// map.setBuiltInZoomControls(true);
				mHandler.post(mMarkersOnMap);

			} else {
				toastLong(R.string.no_reports);
			}
		}
		// ((ViewGroup) getView()).addView(map.);
		// }
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { if
	 * (item.getItemId() == R.id.menu_refresh) { refresh = item; new
	 * RefreshReports(this).execute((String) null); return true; } else if
	 * (item.getItemId() == R.id.menu_add) { launchAddReport(); return true; }
	 * else if (item.getItemId() == R.id.menu_normal) { //
	 * map.setSatellite(false); // map.setTraffic(false); return true; } else if
	 * (item.getItemId() == R.id.menu_satellite) { // map.setSatellite(true);
	 * return true;
	 * 
	 * } else if (item.getItemId() == R.id.filter_by) {
	 * 
	 * showDropDownNav();
	 * 
	 * return true; } else if (item.getItemId() == android.R.id.home) {
	 * finish(); return true; } return super.onOptionsItemSelected(item); }
	 */

	protected View headerView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.map_view_header, null, false);
		TextView textView = (TextView) viewGroup.findViewById(R.id.map_header);
		textView.setText(R.string.all_categories);
		return viewGroup;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.report_map, null);
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

	/*
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { super.onCreateView(inflater,
	 * container, savedInstanceState); mRootView = (ViewGroup)
	 * inflater.inflate(R.layout.list_report, null); addReport = (ImageButton)
	 * mRootView.findViewById(R.id.add_report_btn); refreshReport =
	 * (ImageButton) mRootView .findViewById(R.id.refresh_report_btn);
	 * filterReport = (ImageButton) mRootView
	 * .findViewById(R.id.filter_by_category);
	 * 
	 * if (addReport != null) { addReport.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { launchAddReport(); }
	 * 
	 * }); }
	 * 
	 * if (refreshReport != null) { refreshReport.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { new
	 * RefreshReports(getActivity()).execute((String) null); }
	 * 
	 * }); }
	 * 
	 * if (filterReport != null) { filterReport.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { showDropDownNav(); } }); }
	 * 
	 * return mRootView; }
	 */

	private void updateRefreshStatus() {
		if (mRootView != null) {
			if (addReport != null) {
				mRootView.findViewById(R.id.refresh_report_btn).setVisibility(
						refreshState ? View.GONE : View.VISIBLE);
				mRootView.findViewById(R.id.title_refresh_progress)
						.setVisibility(refreshState ? View.VISIBLE : View.GONE);
			}
		}

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
	/*
	 * @Override public void onResume() { super.onResume(); if
	 * (mReportModel.size() == 0) { mHandler.post(mMarkersOnMap); } }
	 * 
	 * public void onDestroy() { super.onDestroy(); if (new
	 * RefreshReports(getActivity()).cancel(true)) { refreshState = false;
	 * updateRefreshStatus(); } }
	 */

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
			for (ReportEntity reportEntity : mReportModel) {
				updateMarker(Double.valueOf(reportEntity.getIncident()
						.getLatitude()), Double.valueOf(reportEntity
						.getIncident().getLongitude()), false);

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
					;
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