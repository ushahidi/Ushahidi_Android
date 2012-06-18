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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.views.ListCommentView;
import com.ushahidi.android.app.models.ListCommentModel;
import com.ushahidi.android.app.activities.BaseListActivity;
import com.ushahidi.android.app.adapters.ListCommentAdapter;

/**
 * @author eyedol
 * 
 */
public class ListCommentActivity extends
		BaseListActivity<ListCommentView, ListCommentModel, ListCommentAdapter> {

	private int checkinId = 0;

	private int reportId = 0;

	/**
	 * @param view
	 * @param adapter
	 * @param layout
	 * @param menu
	 * @param listView
	 */
	protected ListCommentActivity() {
		super(ListCommentView.class, ListCommentAdapter.class,
				R.layout.list_comment, R.menu.list_comment, android.R.id.list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		Intent i;
		i = new Intent(this, AddCommentActivity.class);
		if (Preferences.isCheckinEnabled == 1) {
			checkinId = adapter.getItem(position).getCheckinId();
			i.putExtra("checkinid", checkinId);
		} else {
			reportId = adapter.getItem(position).getReportId();
			i.putExtra("reportid", reportId);
		}
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

}
