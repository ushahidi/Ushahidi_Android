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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.view.MenuItem;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.ListPendingReportAdapter;
import com.ushahidi.android.app.adapters.ListReportAdapter;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IOpenGeoSmsSchema;
import com.ushahidi.android.app.database.OpenGeoSmsDao;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.fragments.BaseSectionListFragment;
import com.ushahidi.android.app.models.AddReportModel;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.net.CategoriesHttpClient;
import com.ushahidi.android.app.net.ReportsHttpClient;
import com.ushahidi.android.app.opengeosms.OpenGeoSMSSender;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.ui.phone.ViewReportActivity;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ListReportView;

/**
 * @author eyedol
 */
public class ListReportFragment
		extends
		BaseSectionListFragment<ListReportView, ListReportModel, ListReportAdapter> {

	private int mPositionChecked = 0;

	private int mPositionShown = 1;

	private Handler mHandler;

	private MenuItem refresh;

	private CategorySpinnerAdater spinnerArrayAdapter;

	private int filterCategory = 0;

	private CharSequence filterTitle = null;

	private ViewGroup mRootView;

	private ImageButton addReport = null;

	private ImageButton refreshReport = null;

	private ImageButton filterReport = null;

	private boolean refreshState = false;

	private ApiUtils apiUtils;

	private ListFetchedReportAdapter fetchedReportAdapter;

	private ListPendingReportAdapter pendingReportAdapter;

	public ListReportFragment() {
		super(ListReportView.class, ListReportAdapter.class,
				R.layout.list_report, R.menu.list_report, android.R.id.list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		mHandler = new Handler();
		apiUtils = new ApiUtils(getActivity());
		listView.setEmptyView(null);
		fetchedReportAdapter = new ListFetchedReportAdapter(getActivity());
		pendingReportAdapter = new ListPendingReportAdapter(getActivity());
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mPositionChecked = savedInstanceState.getInt("curChoice", 0);
			mPositionShown = savedInstanceState.getInt("shownChoice", -1);
		}

	}

	private View pendingHeader() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.list_pending_header, getListView(), false);
		return viewGroup;
	}

	private View fetchedHeader() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.list_fetched_header, getListView(), false);

		return viewGroup;
	}

	protected View headerView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.list_report_header, getListView(), false);
		TextView textView = (TextView) viewGroup
				.findViewById(R.id.filter_report);
		textView.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable arg0) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!(TextUtils.isEmpty(s.toString()))) {
					filterTitle = s;
					mHandler.post(filterReportList);
				}

			}

		});
		return viewGroup;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("curChoice", mPositionChecked);
		outState.putInt("shownChoice", mPositionShown);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (filterCategory == 0) {
			refreshReportLists();
			showCategories();

		} else {
			reportByCategoryList();
		}

		// upload pending report
		executeUploadTask();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (new RefreshReports(getActivity()).cancel(true)) {
			refreshState = false;
			updateRefreshStatus();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		l.setItemChecked(position, true);
		if (fetchedReportAdapter == adapter.getAdapter(position - 1)) {

			int itemAt = (adapter.getCount() - position);

			launchViewReport((fetchedReportAdapter.getCount() - itemAt) - 1);
		} else if (pendingReportAdapter == adapter.getAdapter(position - 1)) {

			int itemPosition = pendingReportAdapter.getCount() - position;
			int itemAt = (pendingReportAdapter.getCount() - itemPosition) - 1;
			launchAddReport((int) pendingReportAdapter.getItem(itemAt - 1)
					.getId());

		}

	}

	public void setListMapListener(ListMapFragmentListener listener) {

	}

	public void enablePersistentSelection() {
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			refresh = item;
			new RefreshReports(getActivity()).execute((String) null);
			return true;
		} else if (item.getItemId() == R.id.menu_add) {
			launchAddReport(0);
			return true;
		} else if (item.getItemId() == R.id.filter_by) {
			showDropDownNav();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		} else if (item.getItemId() == R.id.app_settings) {
			startActivity(new Intent(getActivity(), Settings.class));
			return true;
		} else if (item.getItemId() == R.id.app_about) {
			startActivity(new Intent(getActivity(), AboutActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refresh the list view with new items
	 */
	final Runnable fetchReportList = new Runnable() {
		public void run() {
			try {
				refreshReportLists();
				showCategories();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * refresh by category id
	 */
	final Runnable fetchReportListByCategory = new Runnable() {
		public void run() {
			try {
				reportByCategoryList();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Filter the list view with new items
	 */
	final Runnable filterReportList = new Runnable() {
		public void run() {
			try {
				filterReportList();
			} catch (Exception e) {
				return;
			}
		}
	};

	private void refreshReportLists() {

		pendingReportAdapter.refresh();
		fetchedReportAdapter.refresh();
		adapter = new ListReportAdapter(getActivity());
		if (!pendingReportAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingReportAdapter);
			// add fetched report
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedReportAdapter);
		} else {
			adapter.addAdapter(fetchedReportAdapter);
		}
		listView.setAdapter(adapter);
	}

	private void filterReportList() {
		fetchedReportAdapter.getFilter().filter(filterTitle);
		pendingReportAdapter.getFilter().filter(filterTitle);
		adapter = new ListReportAdapter(getActivity());
		if (!pendingReportAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingReportAdapter);
			// add fetched report
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedReportAdapter);
		} else {
			adapter.addAdapter(fetchedReportAdapter);
		}
		listView.setAdapter(adapter);

	}

	private void executeUploadTask() {
		if (!pendingReportAdapter.isEmpty()) {
			new UploadTask(getActivity()).execute((String) null);
		}
	}

	private void reportByCategoryList() {
		fetchedReportAdapter.refresh(filterCategory);
		pendingReportAdapter.refresh(filterCategory);
		adapter = new ListReportAdapter(getActivity());
		if (!pendingReportAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingReportAdapter);
			// add fetched report
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedReportAdapter);
		} else {
			adapter.addAdapter(fetchedReportAdapter);
		}
		listView.setAdapter(adapter);

	}

	public void showDropDownNav() {
		showCategories();
		new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.prompt_mesg))
				.setAdapter(spinnerArrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								filterCategory = spinnerArrayAdapter.getTag(
										which).getCategoryId();

								final String all = spinnerArrayAdapter.getTag(
										which).getCategoryTitle();
								view.footerText.setText(all);

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
					launchAddReport(0);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
	private boolean sendOpenGeoSms(ListReportModel r){
		return new OpenGeoSMSSender(getActivity())
		.sendReport(
				Preferences.phonenumber,
				Preferences.openGeoSmsUrl,
				r
				);
	}

	private boolean sendOpenGeoSmsReport(ListReportModel r, int state){
		long id = r.getId();
		OpenGeoSmsDao dao = Database.mOpenGeoSmsDao;
		switch(state){
		case IOpenGeoSmsSchema.STATE_PENDING:
			if ( sendOpenGeoSms(r) ){
				String photos = new UploadPhotoAdapter(getActivity()).pendingPhotos((int) id);
				if ( photos != null && !"".equals(photos)){
					dao.setReportState(id, IOpenGeoSmsSchema.STATE_SENT);
				}else{
					deletePendingReport((int) id);
					dao.deleteReport(id);
				}
				return true;
			}else{
				return false;
			}
		case IOpenGeoSmsSchema.STATE_SENT:
			String photos = new UploadPhotoAdapter(getActivity()).pendingPhotos((int) id);
			if ( photos != null && !"".equals(photos)){

				String url = Preferences.domain + "opengeosms/attach";
				String m = OpenGeoSMSSender.createReport(
						Preferences.openGeoSmsUrl,
						r
						);
				String filename = new UploadPhotoAdapter(getActivity()).pendingPhotos((int) id);

				HashMap<String,String> params = new HashMap<String,String>();
				params.put("m", m);
				params.put("filename", filename);

				ReportsHttpClient c = new ReportsHttpClient(getActivity());

				try {
					if( !c.PostFileUpload(url, params) ){
						return false;
					}
				} catch (IOException e) {
					return false;
				}
			}
			deletePendingReport((int) id);
			dao.deleteReport(id);
			return true;
		}
		return false;
	}
	private List<ListReportModel> mPendingReports;
	private void preparePendingReports(){
		mPendingReports = pendingReportAdapter.pendingReports();
		if (mPendingReports != null) {
			for (ListReportModel report : mPendingReports) {
				long rid = report.getId();
				String categories = pendingReportAdapter
						.fetchCategoriesId((int) rid);
				report.setCategories(categories);
			}
		}
	}
	private boolean uploadPendingReports() {

		boolean retVal = true;
		String dates[];
		String time[];
		StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
		urlBuilder.append("/api");
		if (mPendingReports != null) {
			for (ListReportModel report : mPendingReports) {
				long rid = report.getId();
				int state = Database.mOpenGeoSmsDao.getReportState(rid);
				if (state != IOpenGeoSmsSchema.STATE_NOT_OPENGEOSMS ){
					if ( !sendOpenGeoSmsReport(report, state)){
						retVal = false;
					}
					continue;
				}
				HashMap<String, String> mParams = new HashMap<String, String>();
				mParams.put("task", "report");
				mParams.put("incident_title", report.getTitle());
				mParams.put("incident_description", report.getDesc());

				// dates
				dates = Util
						.formatDate("MMMM dd, yyyy 'at' hh:mm:ss aaa",
								report.getDate(), "MM/dd/yyyy hh:mm a", null,
								Locale.US).split(" ");

				time = dates[1].split(":");
				mParams.put("incident_date", dates[0]);
				mParams.put("incident_hour", time[0]);
				mParams.put("incident_minute", time[1]);
				mParams.put("incident_ampm", dates[2].toLowerCase());

				mParams.put("incident_category", report.getCategories());
				mParams.put("latitude", report.getLatitude());
				mParams.put("longitude", report.getLongitude());
				mParams.put("location_name", report.getLocation());
				mParams.put("person_first", Preferences.firstname);
				mParams.put("person_last", Preferences.lastname);
				mParams.put("person_email", Preferences.email);

				// load filenames
				mParams.put("filename", new UploadPhotoAdapter(getActivity())
						.pendingPhotos((int) report.getId()));

				// upload
				try {
					if (new ReportsHttpClient(getActivity()).PostFileUpload(
							urlBuilder.toString(), mParams)) {
						deletePendingReport((int) report.getId());
					} else {
						retVal = false;
					}
				} catch (IOException e) {
					retVal = false;
				}

			}
		}
		return retVal;
	}

	private void deletePendingReport(int reportId) {

		// make sure it's an existing report
		AddReportModel model = new AddReportModel();
		UploadPhotoAdapter pendingPhoto = new UploadPhotoAdapter(getActivity());
		if (reportId > 0) {
			if (model.deleteReport(reportId)) {
				// delete images
				for (int i = 0; i < pendingPhoto.getCount(); i++) {
					ImageManager.deletePendingPhoto(getActivity(), "/"
							+ pendingPhoto.getItem(i).getPhoto());
				}
				// return to report listing page.
			}
		}
	}

	private void deleteFetchedReport() {
		final List<ListReportModel> items = fetchedReportAdapter
				.fetchedReports();
		for (ListReportModel report : items) {
			if (new ListReportModel().deleteAllFetchedReport(report
					.getReportId())) {
				final List<Photo> photos = new ListPhotoModel()
						.getPhotosByReportId(report.getReportId());

				for (Photo photo : photos) {
					ImageManager.deletePendingPhoto(getActivity(),
							"/" + photo.getPhoto());
				}
			}

		}

	}

	/**
	 * Background progress task for saving Model
	 */
	protected class UploadTask extends ProgressTask {
		public UploadTask(Activity activity) {
			super(activity, R.string.uploading);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			preparePendingReports();
		}


		@Override
		protected Boolean doInBackground(String... args) {

			// delete pending reports
			return uploadPendingReports();

		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				toastLong(R.string.uploaded);
			} else {
				toastLong(R.string.failed);
			}
			refreshReportLists();
			showCategories();
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
					// upload pending reports.
					if (!pendingReportAdapter.isEmpty()) {
						uploadPendingReports();
					}

					// delete everything before updating with a new one
					deleteFetchedReport();

					// fetch categories -- assuming everything will go just
					// right!
					new CategoriesHttpClient(getActivity())
							.getCategoriesFromWeb();

					status = new ReportsHttpClient(getActivity())
							.getAllReportFromWeb();
					return true;
				}

				Thread.sleep(1000);
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_reports);
				} else if (status == 0) {
					refreshReportLists();
					showCategories();
				}
				refreshState = false;
				updateRefreshStatus();
			}
		}
	}

	@Override
	protected void onLoaded(boolean success) {

	}

	/**
	 * Launch Activity to view the details of a report.
	 * 
	 * @param id
	 *            The category id of the selected category.
	 */
	private void launchViewReport(int id) {
		Intent i = new Intent(getActivity(), ViewReportActivity.class);
		i.putExtra("id", id);
		if (filterCategory > 0) {
			i.putExtra("category", filterCategory);
		} else {
			i.putExtra("category", 0);
		}
		startActivityForResult(i, 1);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	/**
	 * Launch Activity for adding new report
	 */
	private void launchAddReport(int id) {
		Intent i = new Intent(getActivity(), AddReportActivity.class);
		i.putExtra("id", id);
		startActivityForResult(i, 2);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}
}