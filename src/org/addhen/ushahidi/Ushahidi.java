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
import android.widget.ImageButton;
import android.widget.Toast;


public class Ushahidi extends Activity {
    /** Called when the activity is first created. */
	private static final int ADD_INCIDENT = Menu.FIRST+1;
	private static final int LIST_INCIDENT = Menu.FIRST+2;
	private static final int INCIDENT_MAP = Menu.FIRST+3;
	private static final int INCIDENT_REFRESH= Menu.FIRST+4;
	private static final int SETTINGS = Menu.FIRST+5;
	private static final int ABOUT = Menu.FIRST+6;
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
        
        //load settings
        if( UshahidiService.domain.length() == 0 ) {
        	UshahidiService.loadSettings(this);
        }
        
        //check if domain has been set
        if(UshahidiService.domain.length() == 0 ) {
        	//means this is a new install or the settings have been corrupted, prompt them!
			final Toast t = Toast.makeText(this,
					"Please enter an instance to track from",
					Toast.LENGTH_LONG);
			t.show();
			return;
        }
        
        listBtn = (Button) findViewById(R.id.incident_list);
        addBtn = (Button) findViewById(R.id.home_add_btn );
        mapBtn = (Button) findViewById(R.id.incident_map);
        
        listBtn.setOnClickListener( new OnClickListener() {
        	public void onClick( View v ){
        		
        		Intent intent = new Intent( Ushahidi.this,ListIncidents.class);
        		startActivityForResult( intent, LIST_INCIDENTS );
        		setResult(RESULT_OK);
				finish();
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
			/*case LIST_INCIDENTS:
				if( resultCode != RESULT_OK ){
					
					break;
				}
				
			break;*/
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
		MenuItem i;
		
		i = menu.add( Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add );
		i.setIcon(R.drawable.ushahidi_add);
		
		i = menu.add( Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list );
		i.setIcon(R.drawable.ushahidi_list);
		  
		i = menu.add( Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map );
		i.setIcon(R.drawable.ushahidi_map);
		  
		
		i = menu.add( Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.menu_sync );
		i.setIcon(R.drawable.ushahidi_refresh);
		  
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings );
		i.setIcon(R.drawable.ushahidi_settings);
		  
		i = menu.add( Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about );
		i.setIcon(R.drawable.ushahidi_settings);
		
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		Intent launchPreferencesIntent;
		switch (item.getItemId()) {
			case LIST_INCIDENT:
				launchPreferencesIntent = new Intent( Ushahidi.this,ListIncidents.class);
        		startActivityForResult( launchPreferencesIntent, LIST_INCIDENTS );
        		setResult(RESULT_OK);
				return true;
		
			case INCIDENT_MAP:
				launchPreferencesIntent = new Intent( Ushahidi.this, ViewIncidents.class);
        		startActivityForResult( launchPreferencesIntent,MAP_INCIDENTS );
				return true;
		
			case ADD_INCIDENT:
				launchPreferencesIntent = new Intent( Ushahidi.this,AddIncident.class);
        		startActivityForResult( launchPreferencesIntent, ADD_INCIDENTS );
        		setResult(RESULT_OK);
				return true;
				
			case SETTINGS:	
				launchPreferencesIntent = new Intent().setClass(Ushahidi.this, Settings.class);
				
				// Make it a subactivity so we know when it returns
				startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
				return true;
		}
		return false;
	}
}