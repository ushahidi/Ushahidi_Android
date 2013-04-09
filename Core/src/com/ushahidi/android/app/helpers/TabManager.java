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

package com.ushahidi.android.app.helpers;

import java.util.ArrayList;

import android.content.Context;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class TabManager extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener, ActionBar.TabListener {
	private final Context mContext;

	private final ActionBar mActionBar;

	private final ViewPager mViewPager;

	private final ArrayList<String> mTabs = new ArrayList<String>();

	public TabManager(FragmentActivity activity, ActionBar actionBar,
			ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mActionBar = actionBar;
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss) {
		mTabs.add(clss.getName());
		mActionBar.addTab(tab.setTabListener(this));
		notifyDataSetChanged();
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	public void onPageSelected(int position) {
		// TODO Auto-generated method stub

	}

	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public Fragment getItem(int position) {
		return Fragment.instantiate(mContext, mTabs.get(position), null);
	}

	@Override
	public int getCount() {

		return mTabs.size();
	}

}
