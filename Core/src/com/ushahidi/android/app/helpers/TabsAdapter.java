
package com.ushahidi.android.app.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

public class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener,
        ActionBar.TabListener {

    private final Context mContext;

    private final ActionBar mActionBar;

    private final ReportViewPager mViewPager;

    private final ArrayList<String> mTabs = new ArrayList<String>();

    public TabsAdapter(FragmentMapActivity activity, ActionBar actionBar, ReportViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mActionBar = actionBar;
        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clss) {
        mTabs.add(clss.getName());
        mActionBar.addTab(tab.setTabListener(this));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, mTabs.get(position), null);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {

        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

}
