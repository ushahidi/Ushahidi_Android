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

import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ViewSwitcher;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseMapViewActivity;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.models.ViewReportModel;
import com.ushahidi.android.app.views.ViewReportView;

/**
 * @author eyedol
 */
public class ViewReportActivity extends
		BaseMapViewActivity<ViewReportView, ViewReportModel> implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

	private ListReportModel reports;

	private List<ListReportModel> report;

	private int position;

	private Bundle photosBundle;

	private int categoryId;

	public ViewReportActivity() {
		super(ViewReportView.class, R.layout.view_report, R.menu.view_report,
				R.id.loc_map);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reports = new ListReportModel();
		view = new ViewReportView(this);
		photosBundle = new Bundle();

		this.categoryId = getIntent().getExtras().getInt("category",0);
		this.position = getIntent().getExtras().getInt("id", 0);

		if (categoryId ==0) {
			reports.loadReportByCategory(categoryId);
		}else{
			reports.load();
		}
		initReport(this.position);

	}

	private void previewImage(int position) {
		// FIXME redo this

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
				} else {
					position = 0;
				}
			}
			return true;

		}

		return super.onOptionsItemSelected(item);
	}

	private void initReport(int position) {
		report = reports.getReports(this);

		if (report != null) {
			view.setBody(report.get(position).getDesc());
			view.setCategory(report.get(position).getCategories());
			view.setLocation(report.get(position).getLocation());
			view.setDate(report.get(position).getDate());
			view.setTitle(report.get(position).getTitle());
			view.setStatus(report.get(position).getStatus());
			view.setListNews((int) report.get(position).getId());
			view.setListPhotos((int) report.get(position).getId());
			view.setListVideos((int) report.get(position).getId());
			
			centerLocationWithMarker(getPoint(
					Double.parseDouble(report.get(position).getLatitude()),
					Double.parseDouble(report.get(position).getLongitude())));
			int page = position;
			this.setTitle(page + 1);
		}
		//animate views
		view.showViews();
	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (report != null)
			title.append(report.size());
		setActionBarTitle(title.toString());
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}

	@Override
	public View makeView() {

		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

}