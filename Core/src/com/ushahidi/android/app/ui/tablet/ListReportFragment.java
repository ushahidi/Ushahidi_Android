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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.CategorySpinnerAdater;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.ListPendingReportAdapter;
import com.ushahidi.android.app.adapters.ListReportAdapter;
import com.ushahidi.android.app.fragments.BaseSectionListFragment;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.net.CategoriesHttpClient;
import com.ushahidi.android.app.net.ReportsHttpClient;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AddReportActivity;
import com.ushahidi.android.app.ui.phone.ViewReportActivity;
import com.ushahidi.android.app.util.ApiUtils;
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

	private TextView categories;

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
		fetchedReportAdapter = new ListFetchedReportAdapter(getActivity());
		pendingReportAdapter = new ListPendingReportAdapter(getActivity());

		// add pending upload
		if (pendingReportAdapter.getCount() > 0) {
			adapter.addView(pendingHeader());
			adapter.addAdapter(pendingReportAdapter);
			// add fetched report
			adapter.addView(fetchedHeader());
		}
		adapter.addAdapter(fetchedReportAdapter);
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
		categories = (TextView) viewGroup
				.findViewById(R.id.list_header_fetched);
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
			mHandler.post(fetchReportList);
		} else {
			mHandler.post(fetchReportListByCategory);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		l.setItemChecked(position, true);
		if( fetchedReportAdapter == adapter.getAdapter(position) ) {
			//fetchedReportAdapter.getItem
			//toastLong("item at: "+fetchedReportAdapter.getItem(position).getTitle()+" ID: "+id);
			launchViewReport(position);
		}
		log("getPositionForSection: "+adapter.getPositionForSection(position));
		log("getSectionForPosition: "+adapter.getSectionForPosition(position));
		log("getItemPosition:1  "+adapter.getSectionForPosition(adapter.getPositionForSection(position)));
		log("getItemSection:2 "+adapter.getPositionForSection(adapter.getSectionForPosition(position)));
		
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
			launchAddReport();
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
		fetchedReportAdapter.refresh();
		pendingReportAdapter.refresh();
	}

	private void filterReportList() {
		fetchedReportAdapter.getFilter().filter(filterTitle);
		pendingReportAdapter.getFilter().filter(filterTitle);
	}

	private void reportByCategoryList() {
		fetchedReportAdapter.refresh(filterCategory);
		pendingReportAdapter.refresh(filterCategory);
	}

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
										which).getCategoryId();
								final String all = spinnerArrayAdapter.getTag(
										which).getCategoryTitle();
								view.footerText.setText(all);
								if (categories != null)
									categories.setText(all);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
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
					refreshReportLists();
					showCategories();
					refreshState = false;
					updateRefreshStatus();
				}
			}
		}
	}

	@Override
	protected void onLoaded(boolean success) {

	}

	public void launchViewReport(int id) {
		Intent i = new Intent(getActivity(), ViewReportActivity.class);
		i.putExtra("id", id);
		if (filterCategory > 0) {
			i.putExtra("category", filterCategory);
		} else {
			i.putExtra("category", 0);
		}
		startActivityForResult(i, 0);
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	public void launchAddReport() {
		Intent i = new Intent(getActivity(), AddReportActivity.class);
		startActivityForResult(i, 1);
	}
}