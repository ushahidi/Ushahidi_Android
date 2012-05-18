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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItem;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListCheckinAdapter;
import com.ushahidi.android.app.adapters.ListFetchedCheckinAdapter;
import com.ushahidi.android.app.adapters.ListPendingCheckinAdapter;
import com.ushahidi.android.app.adapters.UploadPhotoAdapter;
import com.ushahidi.android.app.adapters.UserSpinnerAdater;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.fragments.BaseSectionListFragment;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.models.ListReportPhotoModel;
import com.ushahidi.android.app.net.CheckinHttpClient;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddCheckinActivity;
import com.ushahidi.android.app.ui.phone.ViewCheckinActivity;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ListCheckinView;

/**
 * @author eyedol
 */
public class ListCheckinFragment
		extends
		BaseSectionListFragment<ListCheckinView, ListCheckinModel, ListCheckinAdapter> {

	private ListFetchedCheckinAdapter fetchedAdapter;

	private ListPendingCheckinAdapter pendingAdapter;

	private ApiUtils apiUtils;

	private ViewGroup mRootView;

	private Handler mHandler;

	private MenuItem refresh;

	private UserSpinnerAdater spinnerArrayAdapter;

	private int filterUserId = 0;

	private CharSequence filterTitle = null;

	private boolean refreshState = false;

	public ListCheckinFragment() {
		super(ListCheckinView.class, ListCheckinAdapter.class,
				R.layout.list_checkin, R.menu.list_checkin, android.R.id.list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		mHandler = new Handler();
		apiUtils = new ApiUtils(getActivity());
		listView.setEmptyView(null);
		fetchedAdapter = new ListFetchedCheckinAdapter(getActivity());
		pendingAdapter = new ListPendingCheckinAdapter(getActivity());

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
				R.layout.list_checkin_header, getListView(), false);
		TextView textView = (TextView) viewGroup
				.findViewById(R.id.filter_checkins);
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
					mHandler.post(filterCheckinList);
				} else {
					mHandler.post(fetchCheckinList);
				}

			}

		});
		return viewGroup;
	}

	private void executeUploadTask() {
		if (!pendingAdapter.isEmpty()) {
			new UploadTask(getActivity()).execute((String) null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshCheckinList();
		if (filterUserId == 0) {
			refreshCheckinList();
			showUsers();

		} else {
			refreshCheckinByUserList();
		}

		// upload pending checkin
		executeUploadTask();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh_checkin) {
			refresh = item;
			new FetchCheckins(getActivity()).execute((String) null);
			return true;
		} else if (item.getItemId() == R.id.menu_add_checkin) {
			launchAddCheckin(0);
			return true;
		} else if (item.getItemId() == R.id.menu_filter_by_users) {
			showDropDownNav();

			return true;
		} else if (item.getItemId() == android.R.id.home) {
			getActivity().finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showUsers() {
		spinnerArrayAdapter = new UserSpinnerAdater(getActivity());
		spinnerArrayAdapter.refresh();
	}

	private void refreshCheckinList() {
		fetchedAdapter.refresh();
		pendingAdapter.refresh();
		adapter = new ListCheckinAdapter(getActivity());
		if (!pendingAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingAdapter);

			// add fetched checkin
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedAdapter);
		} else {
			adapter.addAdapter(fetchedAdapter);
		}

		listView.setAdapter(adapter);
	}

	private void refreshCheckinByUserList() {
		fetchedAdapter.refresh(filterUserId);
		pendingAdapter.refresh(filterUserId);
		adapter = new ListCheckinAdapter(getActivity());

		if (!pendingAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingAdapter);

			// add fetched checkin
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedAdapter);
		} else {
			adapter.addAdapter(fetchedAdapter);
		}

		listView.setAdapter(adapter);
	}

	private void filterChecinList() {
		fetchedAdapter.getFilter().filter(filterTitle);
		pendingAdapter.getFilter().filter(filterTitle);
		adapter = new ListCheckinAdapter(getActivity());

		if (!pendingAdapter.isEmpty()) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingAdapter);
			// add fetched checkin
			adapter.addView(fetchedHeader());
			adapter.addAdapter(fetchedAdapter);
		} else {
			adapter.addAdapter(fetchedAdapter);
		}

		listView.setAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		l.setItemChecked(position, true);
		if (fetchedAdapter == adapter.getAdapter(position - 1)) {
			int itemAt = (adapter.getCount() - position);
			launchViewCheckin((fetchedAdapter.getCount() - itemAt) - 1);
		} else if (pendingAdapter == adapter.getAdapter(position - 1)) {
			int itemPosition = pendingAdapter.getCount() - position;
			int itemAt = (pendingAdapter.getCount() - itemPosition) - 1;
			launchAddCheckin((int) pendingAdapter.getItem(itemAt - 1).getDbId());

		}

	}

	/**
	 * refresh by user id
	 */
	final Runnable fetchCheckinListByUser = new Runnable() {
		public void run() {
			try {
				refreshCheckinByUserList();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * refresh by category id
	 */
	final Runnable fetchCheckinList = new Runnable() {
		public void run() {
			try {
				refreshCheckinList();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Filter the list view with new items
	 */
	final Runnable filterCheckinList = new Runnable() {
		public void run() {
			try {
				filterChecinList();
			} catch (Exception e) {
				return;
			}
		}
	};

	private void showDropDownNav() {
		showUsers();
		new AlertDialog.Builder(getActivity())
				.setTitle(getActivity().getString(R.string.prompt_mesg))
				.setAdapter(spinnerArrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								filterUserId = spinnerArrayAdapter
										.getTag(which).getUserId();

								final String all = spinnerArrayAdapter.getTag(
										which).getUsername();
								view. footerText.setText(all);

								if ((all != null)
										&& (!TextUtils.isEmpty(all))
										&& (all != getActivity().getString(
												R.string.all_users))) {

									mHandler.post(filterCheckinList);

								} else {
									mHandler.post(fetchCheckinList);
								}

								dialog.dismiss();
							}
						}).create().show();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void updateRefreshStatus() {
		if (refresh != null) {
			if (refreshState)
				refresh.setActionView(R.layout.indeterminate_progress_action);
			else
				refresh.setActionView(null);
		}

	}

	private void deletePendingCheckin(int checkinId) {
		// make sure it's an existing report
		AddCheckinModel model = new AddCheckinModel();
		UploadPhotoAdapter pendingPhoto = new UploadPhotoAdapter(getActivity());
		if (checkinId > 0) {
			if (model.deleteCheckin(checkinId)) {
				// delete images
				for (int i = 0; i < pendingPhoto.getCount(); i++) {
					ImageManager.deletePendingPhoto(getActivity(), "/"
							+ pendingPhoto.getItem(i).getPhoto());
				}
				// return to report listing page.
			}
		}
	}

	private boolean uploadPendingCheckin() {
		List<ListCheckinModel> items = pendingAdapter.pendingCheckin();
		StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
		urlBuilder.append("/api");
		if (items != null) {
			for (ListCheckinModel checkin : items) {
				final HashMap<String, String> mParams = new HashMap<String, String>();
				mParams.put("task", "checkin");
				mParams.put("action", "ci");
				mParams.put("mobileid", Util.IMEI(getActivity()));
				mParams.put("lat", checkin.getLocationLatitude());
				mParams.put("lon", checkin.getLocationLongitude());
				mParams.put("message", checkin.getMessage());
				mParams.put("firstname", Preferences.firstname);
				mParams.put("lastname", Preferences.lastname);
				mParams.put("email", Preferences.email);
				final String photo = new UploadPhotoAdapter(getActivity())
						.pendingPhotos((int) checkin.getCheckinId());

				// load filenames
				if (!TextUtils.isEmpty(photo)) {
					mParams.put("filename", photo);
				}
				// upload
				try {
					if (new CheckinHttpClient(getActivity()).PostFileUpload(
							urlBuilder.toString(), mParams)) {
						deletePendingCheckin((int) checkin.getDbId());
						return true;
					}
					return false;
				} catch (IOException e) {
					return false;
				}
			}
		}
		return false;
	}

	private void deleteFetchedCheckin() {
		final List<ListCheckinModel> items = fetchedAdapter
				.fetchedCheckins();
		for (ListCheckinModel checkin : items) {
			if (new ListReportModel().deleteAllFetchedReport(checkin
					.getCheckinId())) {
				final List<Photo> photos = new ListReportPhotoModel()
						.getPhotosByCheckinId(checkin.getCheckinId());

				for (Photo photo : photos) {
					ImageManager.deletePendingPhoto(getActivity(),
							"/" + photo.getPhoto());
				}
			}

		}
	}

	/**
	 * Example of a ProgressTask
	 */
	class FetchCheckins extends ProgressTask {

		protected Integer status = 4; // there is no internet

		public FetchCheckins(Activity activity) {
			super(activity, R.string.loading_);
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
					if (!pendingAdapter.isEmpty()) {
						uploadPendingCheckin();
					}

					// delete everything before updating with a new one
					deleteFetchedCheckin();

					status = new CheckinHttpClient(getActivity())
							.getAllCheckinFromWeb();
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				log("fetching checkins");
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_checkin);
				} else if (status == 0) {
					log("successfully fetched checkins");
					refreshCheckinList();
					showUsers();
				}
				refreshState = false;
				updateRefreshStatus();
			}
		}
	}

	/**
	 * Upload pending checkins
	 */
	class UploadTask extends ProgressTask {

		public UploadTask(Activity activity) {
			super(activity, R.string.uploading);
			// pass custom loading message to super call
		}

		@Override
		protected Boolean doInBackground(String... strings) {

			return uploadPendingCheckin();

		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				toastLong(R.string.uploaded);
				refreshCheckinList();
				showUsers();
			} else {
				toastLong(R.string.failed);
			}
		}
	}

	@Override
	protected void onLoaded(boolean success) {
		// TODO Auto-generated method stub

	}

	/**
	 * Launch Activity to view the details of a report.
	 * 
	 * @param id
	 *            The category id of the selected category.
	 */
	private void launchViewCheckin(int id) {
		Intent i = new Intent(getActivity(), ViewCheckinActivity.class);
		i.putExtra("id", id);
		log("userId " + filterUserId);
		if (filterUserId > 0) {
			i.putExtra("userid", filterUserId);
		} else {
			i.putExtra("userid", 0);
		}
		startActivityForResult(i, 1);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	/**
	 * Launch Activity for adding new report
	 */
	private void launchAddCheckin(int id) {
		Intent i = new Intent(getActivity(), AddCheckinActivity.class);
		i.putExtra("id", id);
		startActivityForResult(i, 2);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}
}