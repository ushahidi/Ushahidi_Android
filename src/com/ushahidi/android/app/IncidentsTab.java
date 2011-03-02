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
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.content.Intent;
import com.ushahidi.android.app.checkin.CheckinMap;

public class IncidentsTab extends TabActivity {

    private TabHost tabHost;

    private Bundle bundle;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        bundle = new Bundle();
        extras = this.getIntent().getExtras();

        tabHost = getTabHost();

        // List of reports
        tabHost.addTab(tabHost
                .newTabSpec("list_reports")
                .setIndicator("List",
                        getResources().getDrawable(R.drawable.ushahidi_tab_list_selected))
                .setContent(new Intent(this, ListIncidents.class)));

        // Reports map
        tabHost.addTab(tabHost
                .newTabSpec("map")
                .setIndicator("Map",
                        getResources().getDrawable(R.drawable.ushahidi_tab_map_selected))
                .setContent(new Intent(this, IncidentMap.class)));

        // Checkins map
        // TODO: Place the checkins map here
        tabHost.addTab(tabHost
                .newTabSpec("checkin")
                .setIndicator("Checkin",
                        getResources().getDrawable(R.drawable.ushahidi_tab_map_selected))
                .setContent(new Intent(this, CheckinMap.class)));

        tabHost.setCurrentTab(0);

        if (extras != null) {
            bundle = extras.getBundle("tab");
            tabHost.setCurrentTab(bundle.getInt("tab_index"));
        }

    }

}
