
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
import com.ushahidi.android.app.views.View;

public class ReportTabActivity<V extends View> extends BaseActivity<V> {

	
	public ReportTabActivity() {
		
	}
    /**
	 * @param view
	 * @param layout
	 * @param menu
	 */
	protected ReportTabActivity(Class<V> view, int layout, int menu) {
		super(view, layout, menu);
	}

	private ReportViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.report_tab);
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createMenuDrawer(R.layout.report_tab);
        setTitle();
        ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
                getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(getString(R.string.map));

        mViewPager = (ReportViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(reportsTab, ListReportFragment.class);
        mTabsAdapter.addTab(mapTab, MapFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    public void setTitle() {
		Preferences.loadSettings(this);
		if ((Preferences.activeMapName != null)
				&& (!TextUtils.isEmpty(Preferences.activeMapName))) {

			getSupportActionBar().setTitle(Preferences.activeMapName);
		}
	}

}
