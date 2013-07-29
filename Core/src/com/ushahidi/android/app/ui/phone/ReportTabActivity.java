
package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;
import android.text.TextUtils;

import com.actionbarsherlock.app.ActionBar;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.helpers.ReportViewPager;
import com.ushahidi.android.app.helpers.TabsAdapter;
import com.ushahidi.android.app.ui.tablet.ListReportFragment;
import com.ushahidi.android.app.ui.tablet.MapFragment;
import com.ushahidi.android.app.views.ReportTabView;

public class ReportTabActivity extends BaseActivity<ReportTabView> {

    /**
     * @param view
     * @param layout
     * @param menu
     */
    public ReportTabActivity() {
        super(ReportTabView.class, R.layout.report_tab, 0, 0, 0);
    }

    private ReportViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        setTitle();
        ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
                getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(
                getString(R.string.map));

        mViewPager = (ReportViewPager) findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(reportsTab, ListReportFragment.class);
        mTabsAdapter.addTab(mapTab, MapFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar()
                .getSelectedNavigationIndex());
    }

    public void setTitle() {
        Preferences.loadSettings(this);
        if ((Preferences.activeMapName != null)
                && (!TextUtils.isEmpty(Preferences.activeMapName))) {

            getSupportActionBar().setTitle(Preferences.activeMapName);
        }
    }

}
