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

package com.ushahidi.android.app.ui.phone;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseMapViewActivity;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.models.ViewReportModel;
import com.ushahidi.android.app.services.FetchReportsComments;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.views.ViewReportView;

/**
 * @author eyedol
 */
public class ViewReportActivity extends
		BaseMapViewActivity<ViewReportView, ViewReportModel> {

	private ListReportModel reports;

	private List<ListReportModel> report;

	private ListFetchedReportAdapter reportAdapter;

	private int position;

	private int categoryId;

	private int reportId;

	private String reportTitle;

	private Intent fetchReportComments;

	public ViewReportActivity() {
		super(ViewReportView.class, R.layout.view_report, R.menu.view_report,
				R.id.loc_map);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reports = new ListReportModel();
		this.categoryId = getIntent().getExtras().getInt("category", 0);
		this.position = getIntent().getExtras().getInt("id", 0);

		if (categoryId > 0) {
			reports.loadReportByCategory(categoryId);
		} else {
			reports.load();
		}

		initReport(this.position);
		fetchComments();

	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(fetchBroadcastReceiver, new IntentFilter(
				SyncServices.FETCH_REPORT_COMMENTS_SERVICES_ACTION));
		stopLocating();
	}

	public void onPause() {
		super.onPause();
		try {
			unregisterReceiver(fetchBroadcastReceiver);
		} catch (IllegalArgumentException e) {
		}
	}

	private void fetchComments() {
		registerReceiver(fetchBroadcastReceiver, new IntentFilter(
				SyncServices.FETCH_REPORT_COMMENTS_SERVICES_ACTION));

		fetchReportComments = new Intent(this, FetchReportsComments.class);
		fetchReportComments.putExtra("reportid", reportId);
		startService(fetchReportComments);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_forward) {

			if (report != null) {
				position++;
				if (!(position > (report.size() - 1))) {
					initReport(position);
					view.goNext();
				} else {
					position = report.size() - 1;
				}
			}
			return true;

		} else if (item.getItemId() == R.id.menu_backward) {

			if (report != null) {
				position--;
				if ((position < (report.size() - 1)) && (position != -1)) {
					initReport(position);
					view.goPrevious();
				} else {
					position = 0;
				}
			}
			return true;

		} else if (item.getItemId() == R.id.menu_share) {
			share();
		} else if (item.getItemId() == R.id.menu_comment) {
			Intent i = new Intent(ViewReportActivity.this,
					AddCommentActivity.class);

			i.putExtra("reportid", reportId);
			startActivityForResult(i, 0);
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}

		return super.onOptionsItemSelected(item);
	}

	private String fetchCategories(int reportId) {
		reportAdapter = new ListFetchedReportAdapter(this);
		return reportAdapter.fetchCategories(reportId);
	}

	private void initReport(int position) {
		report = reports.getReports(this);

		if (report != null) {
			reportId = (int) report.get(position).getReportId();

			reportTitle = report.get(position).getTitle();

			view.setBody(report.get(position).getDesc());
			view.setCategory(fetchCategories(reportId));
			view.setLocation(report.get(position).getLocation());
			view.setDate(report.get(position).getDate());
			view.setTitle(report.get(position).getTitle());
			view.setStatus(report.get(position).getStatus());
			view.setListNews((int) reportId);
			view.setListPhotos((int) reportId);
			view.setListVideos((int) reportId);
			view.setListComments(reportId);
			view.getListPhotos().setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent i = new Intent(ViewReportActivity.this,
							ViewReportPhotoActivity.class);
					i.putExtra("reportid", reportId);
					i.putExtra("position", 0);
					startActivityForResult(i, 0);
					overridePendingTransition(R.anim.home_enter,
							R.anim.home_exit);
				}
			});
			view.getListNews().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(ViewReportActivity.this,
									ViewReportNewsActivity.class);
							i.putExtra("reportid", reportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							overridePendingTransition(R.anim.home_enter,
									R.anim.home_exit);
						}
					});

			view.getListVideos().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(ViewReportActivity.this,
									ViewReportVideoActivity.class);
							i.putExtra("reportid", reportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							overridePendingTransition(R.anim.home_enter,
									R.anim.home_exit);
						}
					});

			view.getListComments().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(ViewReportActivity.this,
									ListReportCommentActivity.class);
							i.putExtra("reportid", reportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							overridePendingTransition(R.anim.home_enter,
									R.anim.home_exit);
						}
					});

			centerLocationWithMarker(getPoint(
					Double.parseDouble(report.get(position).getLatitude()),
					Double.parseDouble(report.get(position).getLongitude())));
			view.mapView.setBuiltInZoomControls(false);
			int page = position;
			this.setTitle(page + 1);
		}
	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (report != null)
			title.append(report.size());
		setActionBarTitle(title.toString());
	}

	private void share() {
		final String reportUrl = Preferences.domain + "reports/view/"
				+ reportId;
		final String shareString = getString(R.string.share_template, " "
				+ reportTitle, "\n" + reportUrl);
		shareText(shareString);

	}

	@Override
	public void onLocationChanged(Location arg0) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.MapUserLocation#locationChanged(double,
	 * double)
	 */
	@Override
	protected void locationChanged(double latitude, double longitude) {

	}

	private BroadcastReceiver fetchBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 4);
				stopService(fetchReportComments);
				view.dialog.cancel();
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_comment);
				} else if (status == 0) {
					log("successfully fetched comments");
					view.setListComments(reportId);
				}
			}

			try {
				unregisterReceiver(fetchBroadcastReceiver);
			} catch (IllegalArgumentException e) {
			}
		}
	};

}