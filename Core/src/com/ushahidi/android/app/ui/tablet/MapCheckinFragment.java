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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.ushahidi.android.app.CheckinMapItemizedOverlay;
import com.ushahidi.android.app.CheckinMapOverlayItem;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.UserSpinnerAdater;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.fragments.BaseFragment;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.models.ListReportPhotoModel;
import com.ushahidi.android.app.net.CategoriesHttpClient;
import com.ushahidi.android.app.net.ReportsHttpClient;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddCheckinActivity;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;

public class MapCheckinFragment<CheckinMapItemOverlay> extends BaseFragment {

	private MapView map = null;

	private ListCheckinModel mListCheckinModel;

	List<ListCheckinModel> mCheckinModel;

	private CheckinMapItemizedOverlay<CheckinMapOverlayItem> itemOverlay;

	private Handler mHandler;

	private int filterCategory = 0;

	private MenuItem refresh;

	private UserSpinnerAdater spinnerArrayAdapter;

	private ViewGroup mRootView;

	private ImageButton addReport = null;

	private ImageButton refreshReport = null;

	private ImageButton filterReport = null;

	private boolean refreshState = false;

	private ApiUtils apiUtils;

	public MapCheckinFragment() {
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
		mListCheckinModel = new ListCheckinModel();
		mListCheckinModel.load();
		mCheckinModel = mListCheckinModel.getCheckins(getActivity());
		showCategories();
		mHandler = new Handler();
		apiUtils = new ApiUtils(getActivity());
		map = new MapView(getActivity(), getActivity().getString(
				R.string.google_map_api_key));
		Preferences.loadSettings(getActivity());
		if (mCheckinModel.size() > 0) {
			map.setClickable(true);
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
				.setTitle(getActivity().getString(R.string.prompt_mesg))
				.setAdapter(spinnerArrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								filterCategory = spinnerArrayAdapter.getTag(
										which).getUserId();
								final String all = spinnerArrayAdapter.getTag(
										which).getUsername();
								if ((all != null)
										&& (!TextUtils.isEmpty(all))
										&& (all != getActivity().getString(
												R.string.all_users))) {

									mHandler.post(fetchReportListByCategory);

								} else {
									mHandler.post(fetchReportList);
								}

								dialog.dismiss();
							}
						}).create().show();
	}

	public void showCategories() {
		spinnerArrayAdapter = new UserSpinnerAdater(getActivity());
		spinnerArrayAdapter.refresh();
	}

	/**
	 * refresh by category id
	 */
	final Runnable fetchReportListByCategory = new Runnable() {
		public void run() {
			try {
				final boolean loaded = mListCheckinModel
						.loadCheckinByUser(filterCategory);
				if (loaded) {
					mCheckinModel = mListCheckinModel
							.getCheckins(getActivity());
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
				mListCheckinModel.load();
				mCheckinModel = mListCheckinModel.getCheckins(getActivity());
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
		if (mCheckinModel.size() == 0) {
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
		itemOverlay = new CheckinMapItemizedOverlay<CheckinMapOverlayItem>(
				marker, map, getActivity());
		if (mCheckinModel != null) {
			for (ListCheckinModel checkinModel : mCheckinModel) {
				itemOverlay.addOverlay(new CheckinMapOverlayItem(getPoint(
						Double.valueOf(checkinModel.getLocationLatitude()),
						Double.valueOf(checkinModel.getLocationLongitude())),
						checkinModel.getUsername(), Util.limitString(
								checkinModel.getMessage(), 30), checkinModel
								.getThumbnail(), checkinModel.getDbId(), ""));
			}
		}
		map.getOverlays().clear();
		if (itemOverlay.size() > 0) {
			map.getController().animateTo(itemOverlay.getCenter());
			map.getController().zoomToSpan(itemOverlay.getLatSpanE6(),
					itemOverlay.getLonSpanE6());
			map.getOverlays().add(itemOverlay);
		}

	}

	public void launchAddReport() {
		Intent i = new Intent(getActivity(), AddCheckinActivity.class);
		i.putExtra("id", 0);
		startActivityForResult(i, 2);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	private void deleteFetchedReport() {
		final List<ListReportModel> items = new ListFetchedReportAdapter(
				getActivity()).fetchedReports();
		for (ListReportModel report : items) {
			if (new ListReportModel().deleteAllFetchedReport(report
					.getReportId())) {
				final List<Photo> photos = new ListReportPhotoModel()
						.getPhotosByReportId(report.getReportId());

				for (Photo photo : photos) {
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
				if (apiUtils.isConnected()) {
					// delete everything before updating with a new one
					deleteFetchedReport();

					// fetch categories -- assuming everything will go just
					// right!
					new CategoriesHttpClient(getActivity())
							.getCategoriesFromWeb();

					status = new ReportsHttpClient(getActivity())
							.getAllReportFromWeb();
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
					toastLong(R.string.could_not_fetch_checkin);
				} else if (status == 0) {
					log("successfully fetched");
					mCheckinModel = mListCheckinModel
							.getCheckins(getActivity());
					populateMap();
					showCategories();

				}
			}
			refreshState = false;
			updateRefreshStatus();
		}
	}

}
