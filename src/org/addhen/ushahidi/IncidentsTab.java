package org.addhen.ushahidi;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.content.Intent;


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
        tabHost.addTab(tabHost.newTabSpec("list_reports")
        		.setIndicator("List ",getResources().getDrawable(R.drawable.ushahidi_list))
                .setContent(new Intent(this, ListIncidents.class)));

        tabHost.addTab(tabHost.newTabSpec("map")
                .setIndicator("Map ",getResources().getDrawable(R.drawable.ushahidi_map))
                .setContent(new Intent(this, IncidentMap.class)));
        tabHost.setCurrentTab(0);
        if( extras != null ) {
        	bundle = extras.getBundle("tab");
        	tabHost.setCurrentTab(bundle.getInt("tab_index"));
        }
        
    }
    
}
