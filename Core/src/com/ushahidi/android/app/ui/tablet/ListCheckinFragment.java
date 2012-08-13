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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.ListCheckinAdapter;
import com.ushahidi.android.app.adapters.ListFetchedCheckinAdapter;
import com.ushahidi.android.app.adapters.ListPendingCheckinAdapter;
import com.ushahidi.android.app.adapters.UserSpinnerAdater;
import com.ushahidi.android.app.fragments.BaseSectionListFragment;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.services.FetchCheckins;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.ui.phone.AddCheckinActivity;
import com.ushahidi.android.app.ui.phone.ViewCheckinActivity;
import com.ushahidi.android.app.views.ListCheckinView;

/**
 * @author eyedol
 */
public class ListCheckinFragment
		extends
		BaseSectionListFragment<ListCheckinView, ListCheckinModel, ListCheckinAdapter> {

	private ListFetchedCheckinAdapter fetchedAdapter;

	private ListPendingCheckinAdapter pendingAdapter;

	private Handler mHandler;

	private MenuItem refresh;

	private UserSpinnerAdater spinnerArrayAdapter;

	private int filterUserId = 0;

	private CharSequence filterTitle = null;

	private boolean refreshState = false;

	public ProgressDialog dialog;

	private Intent fetchCheckins;

	private ViewGroup mRootView;

	private ImageButton addCheckin = null;

	private ImageButton refreshCheckin = null;

	private ImageButton filterCheckin = null;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.list_checkin, null);
		addCheckin = (ImageButton) mRootView.findViewById(R.id.add_checkin_btn);
		refreshCheckin = (ImageButton) mRootView
				.findViewById(R.id.refresh_checkin_btn);
		filterCheckin = (ImageButton) mRootView
				.findViewById(R.id.filter_by_users);

		if (addCheckin != null) {
			addCheckin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					launchAddCheckin(0);
				}

			});
		}

		if (refreshCheckin != null) {
			refreshCheckin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fetchCheckins();
				}

			});
		}

		if (filterCheckin != null) {
			filterCheckin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDropDownNav();
				}
			});
		}

		return mRootView;
	}

	private void fetchCheckins() {
		getActivity().registerReceiver(fetchBroadcastReceiver,
				new IntentFilter(SyncServices.FETCH_CHECKIN_SERVICES_ACTION));
		refreshState = true;
		updateRefreshStatus();
		fetchCheckins = new Intent(getActivity(), FetchCheckins.class);
		getActivity().startService(fetchCheckins);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(fetchBroadcastReceiver,
				new IntentFilter(SyncServices.FETCH_CHECKIN_SERVICES_ACTION));
		refreshCheckinList();
		if (filterUserId == 0) {
			refreshCheckinList();
			showUsers();
		} else {
			refreshCheckinByUserList();
		}

	}

	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(fetchBroadcastReceiver);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh_checkin) {
			refresh = item;
			fetchCheckins();
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
		}else if (item.getItemId() == R.id.app_settings) {
			startActivity(new Intent(getActivity(), Settings.class));

			return true;
		} else if (item.getItemId() == R.id.app_about) {
			startActivity(new Intent(getActivity(), AboutActivity.class));

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
								view.footerText.setText(all);

								if ((all != null)
										&& (!TextUtils.isEmpty(all))
										&& (all != getActivity().getString(
												R.string.all_users))) {

									mHandler.post(fetchCheckinListByUser);

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

	private BroadcastReceiver fetchBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 4);
				getActivity().stopService(fetchCheckins);
				refreshState = false;
				updateRefreshStatus();

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
			}

			try {
				getActivity().unregisterReceiver(fetchBroadcastReceiver);
			} catch (IllegalArgumentException e) {
				log("IllegalArgumentException", e);
			}
		}
	};

}