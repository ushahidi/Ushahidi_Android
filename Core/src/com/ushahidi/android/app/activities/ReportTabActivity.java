
package com.ushahidi.android.app.activities;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.ViewPager;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ListCheckinListFragment;
import com.ushahidi.android.app.fragments.ListReportListFragment;
import com.ushahidi.android.app.helpers.TabManager;

public class ReportTabActivity extends FragmentActivity {

    ViewPager mViewPager;

    TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_tab);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab listTab = getSupportActionBar().newTab().setText(getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText("Map");
        //ActionBar.Tab adminTab = getSupportActionBar().newTab().setText("Admin");

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mTabManager = new TabManager(ReportTabActivity.this, getSupportActionBar(), mViewPager);
        mTabManager.addTab(listTab, ListReportListFragment.class);
        mTabManager.addTab(mapTab, ListCheckinListFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_report, menu);
        return true;

    }
}
