package org.addhen.ushahidi;

import android.app.TabActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.content.Context;
import android.content.Intent;


public class IncidentsTab extends TabActivity {
	
	private static final int HOME = Menu.FIRST+1;
	private static final int ADD_INCIDENT = Menu.FIRST+2;
	private static final int SETTINGS = Menu.FIRST+3;
	private static final int ABOUT = Menu.FIRST+4;
	private static final int SYNC = Menu.FIRST+5;

	private static final int REQUEST_CODE_GOTOHOME = 0;
	private static final int REQUEST_CODE_ADD_REPORTS = 1;
	private static final int REQUEST_CODE_SETTINGS = 2;
	private static final int REQUEST_CODE_ABOUT = 3;
	
	private static final int DIALOG_PROMPT = 0;
	private static final int DIALOG_ERROR = 1;
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
        
        if( extras != null ) {
        	bundle = extras.getBundle("tab");
        	tabHost.setCurrentTab(bundle.getInt("tab_index"));
        }
        
    }
    
  /*menu stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		populateMenu(menu);
	}
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
 
		return(super.onCreateOptionsMenu(menu));
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		applyMenuChoice(item);
 
		return(applyMenuChoice(item) ||
				super.onOptionsItemSelected(item));
	}
 
	public boolean onContextItemSelected(MenuItem item) {
 
		return(applyMenuChoice(item) ||
        super.onContextItemSelected(item));
	}
  
	private void populateMenu(Menu menu) {
		MenuItem i;i = menu.add( Menu.NONE, HOME, Menu.NONE, R.string.menu_home );
		i.setIcon(R.drawable.ushahidi_home);
		
		i = menu.add( Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add);
		i.setIcon(R.drawable.ushahidi_add);
		  
		i = menu.add( Menu.NONE, SYNC, Menu.NONE, R.string.incident_menu_refresh );
		i.setIcon(R.drawable.ushahidi_refresh);
		  
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings );
		i.setIcon(R.drawable.ushahidi_settings);
		  
		i = menu.add( Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about );
		i.setIcon(R.drawable.ushahidi_about);
	  
	}
    
    private boolean applyMenuChoice(MenuItem item) {
		Intent launchIntent;
		switch (item.getItemId()) {
			case HOME:
				launchIntent = new Intent(IncidentsTab.this,Ushahidi.class);
				startActivityForResult( launchIntent, REQUEST_CODE_GOTOHOME );
				return true;
 
			case ADD_INCIDENT:
				launchIntent = new Intent( IncidentsTab.this,AddIncident.class);
        		startActivityForResult( launchIntent, REQUEST_CODE_ADD_REPORTS );
        		setResult(RESULT_OK);
				return true;
 
			case ABOUT:
				launchIntent = new Intent( IncidentsTab.this,About.class);
	    		startActivityForResult( launchIntent, REQUEST_CODE_ABOUT );
	    		setResult(RESULT_OK);
				return true;
 
			case SETTINGS:	
				launchIntent = new Intent().setClass(IncidentsTab.this, Settings.class);
 
				// Make it a subactivity so we know when it returns
				startActivityForResult(launchIntent, REQUEST_CODE_SETTINGS);
				setResult(RESULT_OK);
				return true;
 
			case SYNC:
	            ReportsTask reportsTask = new ReportsTask();
	            reportsTask.appContext = this;
	            reportsTask.execute();
	            return true;
 
		}
		return false;
	}
    
  //thread class
	private class ReportsTask extends AsyncTask <Void, Void, Integer> {
		
		protected Integer status;
		protected Context appContext;
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);

		}
		
		@Override 
		protected Integer doInBackground(Void... params) {
			status = Util.processReports(appContext);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if( result == 4 ){
				Util.showToast(appContext, R.string.internet_connection);
			}
			
			setProgressBarIndeterminateVisibility(false);
		}

		
	}*/
    
}
