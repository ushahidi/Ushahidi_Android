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

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseMapViewActivity;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.models.ViewCheckinModel;
import com.ushahidi.android.app.services.FetchCheckinsComments;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.views.ViewCheckinView;

/**
 * @author eyedol
 * 
 */
public class ViewCheckinActivity extends
		BaseMapViewActivity<ViewCheckinView, ViewCheckinModel> {

	private ListCheckinModel checkinModel;

	private List<ListCheckinModel> listCheckin;

	private int position;

	private int userId;

	private int checkinId;

	private Intent fetchCheckinComments;

	public ViewCheckinActivity() {
		super(ViewCheckinView.class, R.layout.view_checkin,
				R.menu.view_checkin, R.id.checkin_loc_map);
		checkinModel = new ListCheckinModel();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.userId = getIntent().getExtras().getInt("userid", 0);
		this.position = getIntent().getExtras().getInt("id", 0);

		if (userId > 0) {
			checkinModel.loadCheckinByUser(userId);
		} else {
			checkinModel.load();
		}

		// because of header view, decrease position by one
		initCheckin(this.position);
		fetchComments();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_forward) {

			if (listCheckin != null) {
				position++;
				if (!(position > (listCheckin.size() - 1))) {
					initCheckin(position);
					view.goNext();
				} else {
					position = listCheckin.size() - 1;
				}
			}
			return true;

		} else if (item.getItemId() == R.id.menu_backward) {

			if (listCheckin != null) {
				position--;
				if ((position < (listCheckin.size() - 1)) && (position != -1)) {
					initCheckin(position);
					view.goPrevious();
				} else {
					position = 0;
				}
			}
			return true;

		} else if (item.getItemId() == R.id.menu_comment) {
			Intent i = new Intent(ViewCheckinActivity.this,
					AddCommentActivity.class);

			i.putExtra("checkinid", checkinId);
			startActivityForResult(i, 0);
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(fetchBroadcastReceiver, new IntentFilter(
				SyncServices.FETCH_CHECKIN_COMMENTS_SERVICES_ACTION));
		view.commentAdapter.refreshCheckinComment(checkinId);
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
				SyncServices.FETCH_CHECKIN_COMMENTS_SERVICES_ACTION));

		fetchCheckinComments = new Intent(this, FetchCheckinsComments.class);
		fetchCheckinComments.putExtra("checkinid", checkinId);
		startService(fetchCheckinComments);
	}

	private void initCheckin(int position) {
		listCheckin = checkinModel.getCheckins(this);

		if (listCheckin != null) {
			checkinId = (int) listCheckin.get(position).getCheckinId();
			if (view != null) {
				view.setTitle(listCheckin.get(position).getUsername());
				view.setBody(listCheckin.get(position).getMessage());
				view.setDate(listCheckin.get(position).getDate());
				view.setListPhotos((int) checkinId);
				view.setListComments(checkinId);
				view.getListPhotos().setOnClickListener(
						new OnClickListener() {

							public void onClick(View v) {
								Intent i = new Intent(ViewCheckinActivity.this,
										ViewReportPhotoActivity.class);
								i.putExtra("reportid", checkinId);
								i.putExtra("position", 0);
								startActivityForResult(i, 0);
								overridePendingTransition(R.anim.home_enter,
										R.anim.home_exit);
							}
						});
						

				view.setListComments((int) checkinId);
				view.getListComments().setOnItemClickListener(
						new OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View v, int pos, long id) {
								Intent i = new Intent(ViewCheckinActivity.this,
										ListCheckinCommentActivity.class);
								i.putExtra("checkinid", checkinId);
								i.putExtra("position", pos);
								startActivityForResult(i, 0);
								overridePendingTransition(R.anim.home_enter,
										R.anim.home_exit);
							}
						});

				centerLocationWithMarker(getPoint(
						Double.parseDouble(listCheckin.get(position)
								.getLocationLatitude()),
						Double.parseDouble(listCheckin.get(position)
								.getLocationLongitude())));
				view.mapView.setBuiltInZoomControls(false);
				int page = position;
				this.setTitle(page + 1);
			}
		}
	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (listCheckin != null)
			title.append(listCheckin.size());
		setActionBarTitle(title.toString());
	}

	public void onLocationChanged(Location location) {
	}

	public void onProviderDisabled(String provider) {
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
	protected void locationChanged(double latitude, double longitude) {

	}

	private BroadcastReceiver fetchBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 4);
				stopService(fetchCheckinComments);
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_comment);
				} else if (status == 0) {
					log("successfully fetched comments");
					view.setListComments(checkinId);
				}
			}

			try {
				unregisterReceiver(fetchBroadcastReceiver);
			} catch (IllegalArgumentException e) {
			}
		}
	};

}
