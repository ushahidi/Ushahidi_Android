package com.ushahidi.android.app.ui.tablet;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.ReportMapItemizedOverlay;
import com.ushahidi.android.app.ReportMapOverlayItem;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.fragments.BaseFragment;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.net.CategoriesHttpClient;
import com.ushahidi.android.app.net.ReportsHttpClient;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;

public class MapFragment<ReportMapItemOverlay> extends BaseFragment {

	private MapView map = null;

	private ListReportModel mListReportModel;

	List<ListReportModel> mReportModel;

	private ReportMapItemizedOverlay<ReportMapOverlayItem> itemOverlay;

	private Handler mHandler;

	private static double latitude;

	private static double longitude;

	private int id = 1;

	private int filterCategory = 0;

	private MenuItem refresh;

	private CategorySpinnerAdater spinnerArrayAdapter;

	private CharSequence filterTitle = null;

	private ViewGroup mRootView;

	private ImageButton addReport = null;

	private ImageButton refreshReport = null;

	private ImageButton filterReport = null;

	private boolean refreshState = false;

	private ApiUtils apiUtils;

	public MapFragment() {
		super(R.menu.map_report);
	}

	/*
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) {
	 * 
	 * return (new FrameLayout(getActivity()));
	 * 
	 * }
	 */

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListReportModel = new ListReportModel();
		mListReportModel.load();
		mReportModel = mListReportModel.getReports(getActivity());
		showCategories();
		mHandler = new Handler();
		apiUtils = new ApiUtils(getActivity());
		map = new MapView(getActivity(), getActivity().getString(
				R.string.google_map_api_key));
		Preferences.loadSettings(getActivity());
		if (mReportModel.size() > 0) {
			if (id > 0) {
				if (!Preferences.deploymentLatitude.equals("0.0")
						&& !Preferences.deploymentLatitude.equals("0.0")) {
					MapFragment.latitude = Double
							.parseDouble(Preferences.deploymentLatitude);
					MapFragment.longitude = Double
							.parseDouble(Preferences.deploymentLongitude);

				} else {
					MapFragment.latitude = Double.parseDouble(mReportModel.get(
							0).getLatitude());
					MapFragment.longitude = Double.parseDouble(mReportModel
							.get(0).getLongitude());

				}
			} else {
				if (!Preferences.deploymentLatitude.equals("0.0")
						&& !Preferences.deploymentLatitude.equals("0.0")) {
					MapFragment.latitude = Double
							.parseDouble(Preferences.deploymentLatitude);
					MapFragment.longitude = Double
							.parseDouble(Preferences.deploymentLongitude);

				} else {
					MapFragment.latitude = Double.parseDouble(mReportModel.get(
							0).getLatitude());
					MapFragment.longitude = Double.parseDouble(mReportModel
							.get(0).getLongitude());
				}

			}

			map.setClickable(true);
			map.getController().setCenter(
					getPoint(MapFragment.latitude, MapFragment.longitude));
			map.setBuiltInZoomControls(true);
			mHandler.post(mMarkersOnMap);

		} else {
			toastLong(R.string.no_reports);
		}
		((ViewGroup) getView()).addView(map);
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
			map.setSatellite(false);
			map.setTraffic(false);
			return true;
		} else if (item.getItemId() == R.id.menu_satellite) {
			map.setSatellite(true);
			return true;

		} else if (item.getItemId() == R.id.menu_traffic) {
			map.setTraffic(true);
			return true;

		} else if (item.getItemId() == R.id.filter_by) {

			showDropDownNav();

			return true;
		} else if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// FIXME:: look into how to put this in it own class
	private void showDropDownNav() {
		showCategories();
		new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.prompt_mesg))
				.setAdapter(spinnerArrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								filterCategory = spinnerArrayAdapter.getTag(
										which).getDbId();
								final String all = spinnerArrayAdapter.getTag(
										which).getCategoryTitle();
								if ((all != null)
										&& (!TextUtils.isEmpty(all))
										&& (all != getActivity().getString(
												R.string.all_categories))) {

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
					mReportModel = mListReportModel.getReports(getActivity());
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
				mReportModel = mListReportModel.getReports(getActivity());
				populateMap();
				showCategories();
			} catch (Exception e) {
				return;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.list_report, null);
		addReport = (ImageButton) mRootView.findViewById(R.id.add_report_btn);
		refreshReport = (ImageButton) mRootView
				.findViewById(R.id.refresh_report_btn);
		filterReport = (ImageButton) mRootView
				.findViewById(R.id.filter_by_category);

		if (addReport != null) {
			addReport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					launchAddReport();
				}

			});
		}

		if (refreshReport != null) {
			refreshReport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new RefreshReports(getActivity()).execute((String) null);
				}

			});
		}

		if (filterReport != null) {
			filterReport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDropDownNav();
				}
			});
		}

		return mRootView;
	}

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
	@Override
	public void onResume() {
		super.onResume();
		if (mReportModel.size() == 0) {
			mHandler.post(mMarkersOnMap);
		}
	}

	// put this stuff in a seperate thread
	final Runnable mMarkersOnMap = new Runnable() {
		public void run() {
			populateMap();
		}
	};

	public GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	/**
	 * add marker to the map
	 */
	public void populateMap() {
		Drawable marker = getResources().getDrawable(R.drawable.map_marker_red);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		itemOverlay = new ReportMapItemizedOverlay<ReportMapOverlayItem>(
				marker, map, getActivity());
		if (mReportModel != null) {
			
			for (ListReportModel reportModel : mReportModel) {
				itemOverlay.addOverlay(new ReportMapOverlayItem(getPoint(
						Double.valueOf(reportModel.getLatitude()),
						Double.valueOf(reportModel.getLongitude())),
						reportModel.getTitle(), Util.limitString(
								reportModel.getDesc(), 30), reportModel
								.getThumbnail(), reportModel.getId(), ""));
			}
		}
		map.getOverlays().clear();
		map.getOverlays().add(itemOverlay);
	}

	public void launchAddReport() {
		Intent i = new Intent(getActivity(), AddReportActivity.class);
		startActivityForResult(i, 1);
	}

	/**
	 * Refresh for new reports
	 */
	class RefreshReports extends ProgressTask {

		protected Integer status;

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

				apiUtils.clearAllReportData();
				// fetch categories
				new CategoriesHttpClient(getActivity()).getCategoriesFromWeb();

				status = new ReportsHttpClient(getActivity())
						.getAllReportFromWeb();

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
				} else if (status == 3) {
					toastLong(R.string.invalid_ushahidi_instance);
				} else if (status == 2) {
					toastLong(R.string.could_not_fetch_reports);
				} else if (status == 1) {
					toastLong(R.string.could_not_fetch_reports);
				} else if (status == 0) {
					log("successfully fetched");
					mReportModel = mListReportModel.getReports(getActivity());
					populateMap();
					showCategories();
					refreshState = false;
					updateRefreshStatus();
				}
			}
		}
	}

}
