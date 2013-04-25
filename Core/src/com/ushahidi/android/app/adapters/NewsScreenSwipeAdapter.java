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
package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ushahidi.android.app.ui.tablet.ViewReportNewsFragment;

/**
 * Page adapter for photo swipe screen
 */
public class NewsScreenSwipeAdapter extends FragmentPagerAdapter {

	private Context mContext;

	private int mTotalPage;
	
	private int mReportId;

	/**
	 * @param fm
	 */
	public NewsScreenSwipeAdapter(FragmentManager fm, Context context,
			int reportId, int totalPage) {
		super(fm);
		this.mContext = context;
		this.mReportId = reportId;
		this.mTotalPage = totalPage;
		
	}

	@Override
	public Fragment getItem(int position) {
		return ViewReportNewsFragment.newInstance(position, mReportId);
	}

	@Override
	public String getPageTitle(int position) {
		return ViewReportNewsFragment.getTitle(mContext, position, mTotalPage);
	}

	@Override
	public int getCount() {
		return mTotalPage;
	}

}
