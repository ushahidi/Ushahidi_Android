package org.addhen.ushahidi;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.Intent;


public class IncidentsTab extends TabActivity {
	private Bundle bundle;
	private Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        bundle = new Bundle();
		extras = this.getIntent().getExtras();
		
        final TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("list_reports")
        		.setIndicator("List ",getResources().getDrawable(R.drawable.ushahidi_list))
                .setContent(new Intent(this, ListIncidents.class)));

        tabHost.addTab(tabHost.newTabSpec("map")
                .setIndicator("Map ",getResources().getDrawable(R.drawable.ushahidi_map))
                .setContent(new Intent(this, IncidentMap.class)));
        
        if( extras != null ) {
        	bundle = extras.getBundle("tab");
        	tabHost.setCurrentTab(bundle.getInt("tab_index"));
        }
        
    }
}
