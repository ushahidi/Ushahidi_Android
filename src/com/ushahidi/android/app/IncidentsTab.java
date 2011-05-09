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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.ushahidi.android.app.checkin.CheckinMap;
import com.ushahidi.android.app.checkin.ListCheckin;

public class IncidentsTab extends TabActivity {

    private TabHost tabHost;

    private Bundle bundle;

    private Bundle extras;

    private Handler mHandler;

    private boolean isCheckinEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_tab);
        mHandler = new Handler();
        bundle = new Bundle();
        extras = this.getIntent().getExtras();

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
    
    public void setTitleFromActivityLabel (int textViewId)
    {
        TextView tv = (TextView) findViewById (textViewId);
        if (tv != null) tv.setText (getTitle ());
    }

}
