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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.adapters.ListReportAdapter;
import com.ushahidi.android.app.fragments.BaseListFragment;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.views.ListReportView;

/**
 * @author eyedol
 */
public class ListAdminReportListFragment extends
		BaseListFragment<ListReportView, ListReportModel, ListFetchedReportAdapter> {

	public ListAdminReportListFragment() {
		super(ListReportView.class, ListFetchedReportAdapter.class,
				R.layout.list_report, R.menu.list_report, android.R.id.list);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		// Example of progress queue for executing asynctasks
		// which will execute TaskOne, then TaskTwo
		/*
		 * new ProgressQueue( new TaskOne(this), new TaskTwo(this) ).execute();
		 */
	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// toastShort("onItemClick %d", position);
	}

	/**
	 * Example of a ProgressTask
	 */
	class TaskOne extends ProgressTask {

		public TaskOne(FragmentActivity activity) {
			super(activity, R.string.loading_);
			// pass custom loading message to super call
		}

		@Override
		protected Boolean doInBackground(String... strings) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	/**
	 * Another example of a ProgressTask
	 */
	class TaskTwo extends ProgressTask {

		public TaskTwo(FragmentActivity activity) {
			super(activity, R.string.loading_);
			// pass custom loading message to super call
		}

		@Override
		protected Boolean doInBackground(String... strings) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	@Override
	protected void onLoaded(boolean success) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ushahidi.android.app.fragments.BaseListFragment#headerView()
	 */
	@Override
	protected View headerView() {
		// TODO Auto-generated method stub
		return null;
	}

}