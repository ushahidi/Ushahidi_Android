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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseListActivity;
import com.ushahidi.android.app.adapters.CommentAdapter;
import com.ushahidi.android.app.models.ListCommentModel;
import com.ushahidi.android.app.services.FetchCheckinsComments;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.views.ListCommentView;

/**
 * @author eyedol
 * 
 */
public class ListCheckinCommentActivity extends
		BaseListActivity<ListCommentView, ListCommentModel, CommentAdapter> {

	private int checkinId = 0;
	
	private Intent fetchCheckinComments;

	/**
	 * @param view
	 * @param adapter
	 * @param layout
	 * @param menu
	 * @param listView
	 */
	public ListCheckinCommentActivity() {
		super(ListCommentView.class, CommentAdapter.class,
				R.layout.list_comment, R.menu.list_comment, android.R.id.list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkinId = getIntent().getExtras().getInt("checkinid");
		fetchComments();
		adapter.refreshCheckinComment(checkinId);
	}

	public void onResume() {
		super.onResume();
		adapter.refreshCheckinComment(checkinId);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;

		} else if (item.getItemId() == R.id.menu_comment) {
			goToAddComment(checkinId);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 *      .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {

		checkinId = adapter.getItem(position).getCheckinId();
	}

	private void goToAddComment(int checkinId) {
		Intent i;
		i = new Intent(this, AddCommentActivity.class);
		i.putExtra("checkinid", checkinId);

		startActivityForResult(i, 0);
		overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
		setResult(RESULT_OK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.activities.BaseListActivity#onLoaded(boolean)
	 */
	@Override
	protected void onLoaded(boolean success) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.activities.BaseListActivity#headerView()
	 */
	@Override
	protected View headerView() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BroadcastReceiver fetchBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 4);
				view.dialog.cancel();
				if (status == 4) {
					toastLong(R.string.internet_connection);
				} else if (status == 110) {
					toastLong(R.string.connection_timeout);
				} else if (status == 100) {
					toastLong(R.string.could_not_fetch_comment);
				} else if (status == 0) {
					log("successfully fetched comments");
					//refreshCheckinList();
					//showUsers();
				}
			}

			try {
				unregisterReceiver(fetchBroadcastReceiver);
			} catch (IllegalArgumentException e) {
			}
			stopService(fetchCheckinComments);
		}
	};

}
