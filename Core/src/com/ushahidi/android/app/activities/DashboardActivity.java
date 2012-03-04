
package com.ushahidi.android.app.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentMapActivity;
import android.util.Log;
import android.view.View;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ListMapFragment;
import com.ushahidi.android.app.fragments.ListMapFragmentListener;
import com.ushahidi.android.app.fragments.ListReportFragment;

public class DashboardActivity extends FragmentMapActivity implements ListMapFragmentListener {

    private boolean detailsInline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_items);

        ListMapFragment maps = (ListMapFragment)getSupportFragmentManager().findFragmentById(
                R.id.list_map_fragment);
        maps.setListMapListener(this);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.list_report_fragment);

        detailsInline = (f != null && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));

        if (detailsInline) {
            log("enable persistent selection is on ");
            maps.enablePersistentSelection();
        } else if (f != null) {
            log("no persistent selection");
            f.getView().setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapSelected(int mapId) {
        log("message id" + mapId);
        if (detailsInline) {

            ListReportFragment f = ((ListReportFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.list_report_fragment));
            f.mListReportAdapter.refresh(this);
            f.mListReportView.getPullToRefreshListView().setAdapter(f.mListReportAdapter);
        } else {
            Intent i = new Intent(this, ReportTabActivity.class);
            startActivity(i);
        }

    }

    protected void log(String message) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        if (MainApplication.LOGGING_MODE)
            Log.e(getClass().getName(), message, ex);
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
}
