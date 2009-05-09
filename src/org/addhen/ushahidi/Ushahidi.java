package org.addhen.ushahidi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Ushahidi extends Activity {
    /** Called when the activity is first created. */
	private static final int INCIDENT_LIST  = Menu.FIRST+1;
	private static final int INCIDENT_MAP = Menu.FIRST+2;
	private static final int INCIDENT_ADD = Menu.FIRST+3;
	private static final int SETTINGS = Menu.FIRST+4;
	private static final int LIST_INCIDENTS = 0;
	private static final int MAP_INCIDENTS = 1;
	private static final int ADD_INCIDENTS = 2;
	private static final int REQUEST_CODE_SETTINGS = 1;
	
	private Button listBtn;
	private Button addBtn;
	private Button mapBtn;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.main );
        
        listBtn = (Button) findViewById(R.id.incident_list);
        addBtn = (Button) findViewById(R.id.home_add_btn );
        mapBtn = (Button) findViewById(R.id.incident_map);
        
        listBtn.setOnClickListener( new OnClickListener() {
        	public void onClick( View v ){
        		
        		Intent intent = new Intent( Ushahidi.this,ListIncidents.class);
        		startActivityForResult( intent, LIST_INCIDENTS );
        		setResult(RESULT_OK);
				//finish();
        	}
        });
        
        mapBtn.setOnClickListener( new OnClickListener() {
        	public void onClick( View v) {
        		Intent intent = new Intent( Ushahidi.this, ViewIncidents.class);
        		startActivityForResult( intent,MAP_INCIDENTS );
        	}
        });
        
        addBtn.setOnClickListener( new OnClickListener()  {
        	public void onClick( View v ) {
        		Intent intent = new Intent( Ushahidi.this,AddIncident.class);
        		startActivityForResult(intent, ADD_INCIDENTS );
        	}
        });
       
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch( requestCode ) {
			case REQUEST_CODE_SETTINGS:
				if( resultCode != RESULT_OK ){
					break;
				}
			break;
			case LIST_INCIDENTS:
				if( resultCode != RESULT_OK ){
					
					break;
				}
				
			break;
		}
	}
	
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
		menu.add(Menu.NONE, INCIDENT_LIST, Menu.NONE, "Incident List");
		menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, "Incident Map");
		menu.add(Menu.NONE, INCIDENT_ADD, Menu.NONE, "Add Incident");
		menu.add(Menu.NONE, SETTINGS, Menu.NONE, "Settings");
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		Intent launchPreferencesIntent;
		switch (item.getItemId()) {
			case INCIDENT_LIST:
				//write code to show incident list
				return true;
		
			case INCIDENT_MAP:
				//TODO write code to show incident map
				return true;
		
			case INCIDENT_ADD:
				//TODO write code to add incident
				return true;
				
			case SETTINGS:	
				//launchPreferencesIntent = new Intent(this, Settings.class);
				// Make it a subactivity so we know when it returns
				//startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
				return true;
		}
		return false;
	}
}