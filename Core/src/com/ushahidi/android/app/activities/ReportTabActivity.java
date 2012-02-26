
package com.ushahidi.android.app.activities;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ListAdminReportListFragment;
import com.ushahidi.android.app.fragments.ListCheckinListFragment;
import com.ushahidi.android.app.fragments.ListReportListFragment;
import com.ushahidi.android.app.helpers.TabsAdapter;

public class ReportTabActivity extends FragmentActivity {

    private ViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_tab);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
                getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(getString(R.string.map));
        ActionBar.Tab adminTab = getSupportActionBar().newTab().setText(getString(R.string.admin));

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(reportsTab, ListReportListFragment.class);
        mTabsAdapter.addTab(mapTab, ListCheckinListFragment.class);
        mTabsAdapter.addTab(adminTab, ListAdminReportListFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }


}
