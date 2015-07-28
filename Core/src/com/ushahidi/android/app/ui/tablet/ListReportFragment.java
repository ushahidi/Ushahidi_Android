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

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.activities.WeatherMapActivity;
import com.ushahidi.android.app.adapters.BaseListReportAdapter;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.adapters.CustomFormAdapter;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.ListPendingReportAdapter;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.api.CategoriesApi;
import com.ushahidi.android.app.api.CustomFormApi;
import com.ushahidi.android.app.api.ReportsApi;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IOpenGeoSmsSchema;
import com.ushahidi.android.app.database.OpenGeoSmsDao;
import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportCustomFormEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.fragments.BaseSectionListFragment;
import com.ushahidi.android.app.models.AddReportModel;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.opengeosms.OpenGeoSMSSender;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.ui.phone.AdminActivity;
import com.ushahidi.android.app.ui.phone.ViewReportSlideActivity;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ListReportView;
import com.ushahidi.java.sdk.api.Incident;
import com.ushahidi.java.sdk.api.Person;
import com.ushahidi.java.sdk.api.ReportFields;
import com.ushahidi.java.sdk.api.json.Response;
import com.ushahidi.java.sdk.net.content.Body;
import com.ushahidi.java.sdk.net.content.FileBody;

/**
 * @author eyedol
 */
public class ListReportFragment
		extends
		BaseSectionListFragment<ListReportView, ReportEntity, BaseListReportAdapter> {

	private int mPositionChecked = 0;

	private int mPositionShown = 1;

	private Handler mHandler;

	private MenuItem refresh;

	private CategorySpinnerAdater spinnerArrayAdapter;

	private int filterCategory = 0;

	private CharSequence filterTitle = null;

	private boolean refreshState = false;

	private ListFetchedReportAdapter fetchedReportAdapter;

	private ListPendingReportAdapter pendingReportAdapter;

	public ListReportFragment() {
		super(ListReportView.class, BaseListReportAdapter.class,
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

	@Override
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
					filterTitle = s.toString();
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
			// showCategories();

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
					.getDbId());

		}

	}

	public void setListMapListener(ListMapFragmentListener listener) {

	}

	public void enablePersistentSelection() {
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(this.menu, menu);
		if (TextUtils.isEmpty(getString(R.string.deployment_url))) {
			menu.findItem(R.id.menu_admin).setVisible(false);
			menu.findItem(R.id.menu_about).setVisible(false);
			menu.findItem(R.id.menu_settings).setVisible(false);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			refresh = item;
			new RefreshReports(getActivity()).execute((String) null);
			return true;
		} else if (item.getItemId() == R.id.menu_add_report) {
			launchAddReport(0);
			return true;
		} else if (item.getItemId() == R.id.filter_by) {
			showDropDownNav();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		} else if (item.getItemId() == R.id.menu_about) {
			Util.showAbout(getSherlockActivity());
			return true;
		} else if (item.getItemId() == R.id.menu_admin) {
			startActivityZoomIn(new Intent(getActivity(), AdminActivity.class));
			return true;
		} else if (item.getItemId() == R.id.menu_settings) {
			startActivityZoomIn(new Intent(getActivity(), Settings.class));
			return true;
		} else if (item.getItemId() == R.id.show_weather){
			Intent i = new Intent(getActivity(), WeatherMapActivity.class);
			startActivity(i);
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

	/**
	 * Refresh both pending and fetched reports list
	 */
	public void refreshReportLists() {

		pendingReportAdapter.refresh();
		fetchedReportAdapter.refresh();
		adapter = new BaseListReportAdapter(getActivity());
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

	/**
	 * Filter through the report list by report title
	 */
	private void filterReportList() {
		fetchedReportAdapter.getFilter().filter(filterTitle);
		pendingReportAdapter.getFilter().filter(filterTitle);
	}

	private void executeUploadTask() {
		if (!pendingReportAdapter.isEmpty()) {
			new UploadTask(getActivity()).execute((String) null);
		}
	}

	private void reportByCategoryList() {
		fetchedReportAdapter.refresh(filterCategory);
		pendingReportAdapter.refresh(filterCategory);
		adapter = new BaseListReportAdapter(getActivity());
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
								CategoryEntity selected = spinnerArrayAdapter
										.getTag(which);
								String title = selected.getCategoryTitle();

								if ((title != null)
										&& (!TextUtils.isEmpty(title))
										&& (title != getActivity().getString(
												R.string.all_categories))) {

									mHandler.post(fetchReportListByCategory);
									if (selected.getParentId() != 0) {
										ListReportModel mListReportModel = new ListReportModel();
										CategoryEntity parent = mListReportModel
												.getParentCategory(selected
														.getParentId());
										if (parent != null)
											title = parent.getCategoryTitle()
													+ ": " + title;
									}
								} else {
									mHandler.post(fetchReportList);
								}
								view.footerText.setText(title);

								dialog.dismiss();
							}
						}).create().show();
	}

	public void showCategories() {
		spinnerArrayAdapter = new CategorySpinnerAdater(getActivity());
		spinnerArrayAdapter.refresh();
	}

	private void updateRefreshStatus() {

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

	private boolean sendOpenGeoSms(ReportEntity r) {
		return new OpenGeoSMSSender(getActivity()).sendReport(
				Preferences.phonenumber, Preferences.openGeoSmsUrl, r);
	}

	private boolean sendOpenGeoSmsReport(ReportEntity r, int state) {
		long id = r.getDbId();
		OpenGeoSmsDao dao = Database.mOpenGeoSmsDao;
		switch (state) {
		case IOpenGeoSmsSchema.STATE_PENDING:
			if (sendOpenGeoSms(r)) {
				List<File> photos = new UploadPhotoAdapter(getActivity())
						.pendingPhotos((int) id);
				if ((photos != null) && (photos.size() > 0)) {
					dao.setReportState(id, IOpenGeoSmsSchema.STATE_SENT);
				} else {
					deletePendingReport((int) id);
					dao.deleteReport(id);
				}
				return true;
			} else {
				return false;
			}
		case IOpenGeoSmsSchema.STATE_SENT:
			List<File> photos = new UploadPhotoAdapter(getActivity())
					.pendingPhotos((int) id);
			if ((photos != null) && (photos.size() > 0)) {

				String url = Preferences.domain + "opengeosms/attach";
				String m = OpenGeoSMSSender.createReport(
						Preferences.openGeoSmsUrl, r);

				Body body = new Body();
				body.addField("m", m);
				for (File file : photos) {
					body.addField("filename", new FileBody(file));
				}

				ReportsApi report = new ReportsApi();
				if (!report.upload(url, body)) {
					return false;
				}

			}
			deletePendingReport((int) id);
			dao.deleteReport(id);
			return true;
		}
		return false;
	}

	private List<ReportEntity> mPendingReports;

	private void preparePendingReports() {
		mPendingReports = pendingReportAdapter.pendingReports();
		if (mPendingReports != null) {
			for (ReportEntity report : mPendingReports) {
				long rid = report.getDbId();

				report.setCategories(pendingReportAdapter
						.fetchCategoriesId((int) rid));
			}
		}
	}

	private boolean uploadPendingReports() {

		boolean retVal = true;

		ReportFields fields = new ReportFields();
		Incident incident = new Incident();
		ReportsApi reportApi = new ReportsApi();
		if (mPendingReports != null) {
			for (ReportEntity report : mPendingReports) {
				long rid = report.getDbId();
				
				int state = Database.mOpenGeoSmsDao.getReportState(rid);
				if (state != IOpenGeoSmsSchema.STATE_NOT_OPENGEOSMS) {
					if (!sendOpenGeoSmsReport(report, state)) {
						retVal = false;
					}
					continue;
				}

				// Set the incident details
				incident.setTitle(report.getIncident().getTitle());
				incident.setDescription(report.getIncident().getDescription());
				incident.setDate(report.getIncident().getDate());

				incident.setLatitude(report.getIncident().getLatitude());
				incident.setLongitude(report.getIncident().getLongitude());
				incident.setLocationName(report.getIncident().getLocationName());
				fields.fill(incident);

				// Set person details
				if ((!TextUtils.isEmpty(Preferences.fileName))
						&& (!TextUtils.isEmpty(Preferences.lastname))
						&& (!TextUtils.isEmpty(Preferences.email))) {
					fields.setPerson(new Person(Preferences.firstname,
							Preferences.lastname, Preferences.email));
				}

				// Add categories
				fields.addCategory(report.getCategories());

				// Add photos
				List<File> photos = new UploadPhotoAdapter(getActivity())
						.pendingPhotos((int) report.getDbId());
				if (photos != null && photos.size() > 0)
					fields.addPhotos(photos);
				
				//Add custom forms values
				
				List<ReportCustomFormEntity> pendingCustomForms = Database.mReportCustomFormDao.fetchPendingReportCustomForms(report.getDbId());
				if(pendingCustomForms!= null && pendingCustomForms.size() > 0){
					Map<String, String> fieldMap = CustomFormAdapter.convertEntityToMap(pendingCustomForms);
					fields.addCustomFields(fieldMap);
					fields.setFormId(String.valueOf(pendingCustomForms.get(0).getFormId()));
				}
				
				

				// Upload
				Response response = reportApi.submitReport(fields);
				if (response != null) {
					if (response.getErrorCode() == 0) {
						deletePendingReport((int) report.getDbId());
					} else {
						retVal = false;
					}
				} else {
					retVal = false;
//					deletePendingReport((int) report.getDbId());
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
				
			}
		}
	}

	private void deleteFetchedReport() {
		final List<ReportEntity> items = fetchedReportAdapter.fetchedReports();
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
		if (Database.mReportCustomFormDao.deleteAllReportCustomForms()) {
			new Util().log( "Report CustomForms deleted");
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
				if (Util.isConnected(getActivity())) {
					// upload pending reports.
					if (!pendingReportAdapter.isEmpty()) {
						uploadPendingReports();
					}

					// delete everything before updating with a new one
					deleteFetchedReport();

					boolean reportFetched = new ReportsApi().saveReports(getActivity());
					
					if(reportFetched){//fetch also customforms values
						List<ReportEntity> reports = Database.mReportDao.fetchAllReports();
						new CustomFormApi().fetchReportCustomFormList(reports);
					}
						
					//TODO adding CONSTANT status values	
					status = reportFetched ? 0 : 99;
					// fetch categories -- assuming everything will go just
					new CategoriesApi().getCategoriesList();
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
					if (filterCategory > 0) {
						reportByCategoryList();
					} else {
						refreshReportLists();
					}
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
		Intent i = new Intent(getActivity(), ViewReportSlideActivity.class);
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
