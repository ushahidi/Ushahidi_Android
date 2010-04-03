package org.addhen.ushahidi;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.Intent;


public class IncidentsTab extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("list_reports")
                .setIndicator(" ",getResources().getDrawable(R.drawable.ushahidi_list))
                .setContent(new Intent(this, ListIncidents.class)));

        tabHost.addTab(tabHost.newTabSpec("map")
                .setIndicator(" ",getResources().getDrawable(R.drawable.ushahidi_map))
                .setContent(new Intent(this, IncidentMap.class)));
        
    }
}
