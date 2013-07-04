
package com.ushahidi.android.app.ui.tablet;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.ui.phone.ReportMapActivity;
import com.ushahidi.android.app.ui.phone.ReportTabActivity;

public class DashboardActivity
        extends BaseActivity<com.ushahidi.android.app.views.View> implements
        ListMapFragmentListener,
        ActionBar.OnNavigationListener {

    private boolean detailsInline = false;

    private SpinnerAdapter mSpinnerAdapter;

    private ListMapFragment maps;

    private static final int DIALOG_DISTANCE = 0;

    private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

    private static final int DIALOG_ADD_DEPLOYMENT = 2;

    public DashboardActivity() {
        super(com.ushahidi.android.app.views.View.class, R.layout.dashboard_items, 0,
                R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNavDrawer();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        Preferences.loadSettings(this);
        mSpinnerAdapter = ArrayAdapter
                .createFromResource(this, R.array.nav_list,
                        android.R.layout.simple_spinner_dropdown_item);

        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        maps = (ListMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.list_map_fragment);
        maps.setListMapListener(this);

        // check if we have a frame to embed list fragment
        Fragment f = getSupportFragmentManager().findFragmentById(
                R.id.show_report_fragment);

        detailsInline = (f != null && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));

        if (detailsInline) {
            maps.enablePersistentSelection();
        } else if (f != null) {
            f.getView().setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapSelected() {
        if (detailsInline) {

            ((ListReportFragment) getSupportFragmentManager().findFragmentById(
                    R.id.show_report_fragment)).refreshReportLists();

        } else {

            Intent i = new Intent(this, ReportTabActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.home_enter, R.anim.home_exit);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.dashboard, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_report_map) {
            Intent launchIntent;
            launchIntent = new Intent(this, ReportMapActivity.class);
            startActivityZoomIn(launchIntent);
            setResult(RESULT_OK);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.ActionBar.OnNavigationListener#
     * onNavigationItemSelected(int, long)
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        // add map is selected
        if (itemPosition == 1) {
            maps.edit = false;
            maps.createDialog(DIALOG_ADD_DEPLOYMENT);
            return true;
        } else if (itemPosition == 2) { // find map around me
            maps.createDialog(DIALOG_DISTANCE);
            return true;
        } else if (itemPosition == 3) { // clear all map
            maps.createDialog(DIALOG_CLEAR_DEPLOYMENT);
            return true;
        }
        return false;
    }
}
