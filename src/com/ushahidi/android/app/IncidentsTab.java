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

package com.ushahidi.android.app;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.ushahidi.android.app.checkin.CheckinActivity;
import com.ushahidi.android.app.checkin.CheckinMap;
import com.ushahidi.android.app.checkin.ListCheckin;

public class IncidentsTab extends TabActivity {

    private TabHost tabHost;

    private Bundle bundle;

    private Bundle extras;

    private TextView activityTitle;

    private static final int VIEW_SEARCH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tab);
        new Handler();
        bundle = new Bundle();
        extras = this.getIntent().getExtras();

        activityTitle = (TextView)findViewById(R.id.title_text);

        tabHost = getTabHost();

        // load preferences
        checkinEnabled();

        tabHost.setCurrentTab(0);

        // set tab colors
        setTabColor(tabHost);

        // set tab colors on tab change as well
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {

            public void onTabChanged(String arg0) {
                setTabColor(tabHost);
            }

        });

        if (extras != null) {
            bundle = extras.getBundle("tab");
            tabHost.setCurrentTab(bundle.getInt("tab_index"));
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void checkinEnabled() {

        UshahidiPref.loadSettings(IncidentsTab.this);
        if (UshahidiPref.isCheckinEnabled == 1) {
            setTitle(getString(R.string.tab_item_checkin));
            if (activityTitle != null)
                activityTitle.setText(getString(R.string.tab_item_checkin));
            tabHost.addTab(tabHost
                    .newTabSpec("list_checkins")
                    .setIndicator(getString(R.string.tab_item_report_list),
                            getResources().getDrawable(R.drawable.ushahidi_tab_reports_selected))
                    .setContent(new Intent(IncidentsTab.this, ListCheckin.class)));

            // checkins
            tabHost.addTab(tabHost
                    .newTabSpec("checkin")
                    .setIndicator(getString(R.string.tab_item_report_map),
                            getResources().getDrawable(R.drawable.ushahidi_tab_checkin_selected))
                    .setContent(new Intent(IncidentsTab.this, CheckinMap.class)));
        } else {
            if (activityTitle != null)
                activityTitle.setText(getTitle());
            // List of reports
            tabHost.addTab(tabHost
                    .newTabSpec("list_reports")
                    .setIndicator(getString(R.string.tab_item_report_list),
                            getResources().getDrawable(R.drawable.ushahidi_tab_reports_selected))
                    .setContent(new Intent(IncidentsTab.this, ListIncidents.class)));

            // Reports map
            tabHost.addTab(tabHost
                    .newTabSpec("map")
                    .setIndicator(getString(R.string.tab_item_report_map),
                            getResources().getDrawable(R.drawable.ushahidi_tab_map_selected))
                    .setContent(new Intent(IncidentsTab.this, IncidentMap.class)));
        }

    }

    public static void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            // unselected
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#404041"));

            TextView tv = (TextView)tabhost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title); // Unselected Tabs
            tv.setTextColor(Color.parseColor("#ffffff"));

        }

        // selected
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundColor(Color.parseColor("#8A1F03"));
        TextView tv = (TextView)tabhost.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(Color.parseColor("#ffffff"));
    }

    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    protected void goHome(Context context) {
        final Intent intent = new Intent(context, Ushahidi.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void onSearchDeployments(View v) {
        Intent intent = new Intent(IncidentsTab.this, DeploymentSearch.class);
        startActivityForResult(intent, VIEW_SEARCH);
        setResult(RESULT_OK);
    }
    
    public void onAddReport(View v) {
        UshahidiPref.loadSettings(IncidentsTab.this);
        if (UshahidiPref.isCheckinEnabled == 1) {
            Intent checkinActivityIntent = new Intent().setClass(IncidentsTab.this,
                    CheckinActivity.class);
            startActivity(checkinActivityIntent);
            setResult(RESULT_OK);
            
        } else {
            Intent intent = new Intent(IncidentsTab.this, AddIncident.class);
            startActivityForResult(intent, 0);
            setResult(RESULT_OK);
        }
    }

}
