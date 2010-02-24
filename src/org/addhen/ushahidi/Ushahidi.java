package org.addhen.ushahidi;

import java.io.IOException;
import java.util.List;

import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.net.Categories;
import org.addhen.ushahidi.net.Incidents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class Ushahidi extends Activity {
    /** Called when the activity is first created. */
	private static final int ADD_INCIDENT = Menu.FIRST+1;
	private static final int LIST_INCIDENT = Menu.FIRST+2;
	private static final int INCIDENT_MAP = Menu.FIRST+3;
	private static final int INCIDENT_REFRESH = Menu.FIRST+4;
	private static final int SETTINGS = Menu.FIRST+5;
	private static final int ABOUT = Menu.FIRST+6;
	private static final int SYNC = Menu.FIRST+7;
	
	private static final int LIST_INCIDENTS = 0;
	private static final int MAP_INCIDENTS = 1;
	private static final int ADD_INCIDENTS = 2;
	
	private static final int REQUEST_CODE_SETTINGS = 1;
	private static final int REQUEST_CODE_ABOUT = 2; 
	private static final int DIALOG_PROMPT = 0;
	private static final int DIALOG_PROGRESS = 1;
	
	private static final int MAX_PROGRESS = 100;
    
    private ProgressDialog mProgressDialog;
    private int mProgress;
    private List<IncidentsData> mNewIncidents;
	private List<CategoriesData> mNewCategories;
    
	private Handler mHandler;
	
	private Button listBtn;
	private Button addBtn;
	private Button mapBtn;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.main );
        mHandler = new Handler();
        //load settings
        if( UshahidiService.domain.length() == 0 ) {
        	UshahidiService.loadSettings(this);
        }
        
        //check if domain has been set
        if(UshahidiService.domain.length() == 0 ) {
        	
        	//means this is a new install or the settings have been corrupted, prompt them!
			mHandler.post(mDisplayPrompt);
			return;
			
        }
        
        listBtn = (Button) findViewById(R.id.incident_list);
        addBtn = (Button) findViewById(R.id.home_add_btn );
        mapBtn = (Button) findViewById(R.id.incident_map);
        
        listBtn.setOnClickListener( new View.OnClickListener() {
        	public void onClick( View v ){
        		
        		Intent intent = new Intent( Ushahidi.this,ListIncidents.class);
        		startActivityForResult( intent, LIST_INCIDENTS );
        		setResult(RESULT_OK);
				
        	}
        });
        
        mapBtn.setOnClickListener( new View.OnClickListener() {
        	public void onClick( View v) {
        		Intent intent = new Intent( Ushahidi.this, IncidentMap.class);
        		startActivityForResult( intent, MAP_INCIDENTS );
        		setResult(RESULT_OK);
        	}
        });
        
        addBtn.setOnClickListener( new View.OnClickListener()  {
        	public void onClick( View v ) {
        		Intent intent = new Intent( Ushahidi.this, AddIncident.class);
        		startActivityForResult(intent, ADD_INCIDENTS );
        		setResult(RESULT_OK);
        	}
        });
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                    mProgressDialog.dismiss();
                } else {
                    mProgress++;
                    mProgressDialog.incrementProgressBy(1);
                    mHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
       
	}
	
	 @Override
	 protected Dialog onCreateDialog(int id) {
		 switch (id) {
	     	case DIALOG_PROMPT: {
	     		AlertDialog dialog = (new AlertDialog.Builder(this)).create();
	     		dialog.setTitle("Ushahidi Setup");
	            dialog.setMessage("Setup an ushahidi instance.");
	            dialog.setButton2("Ok", new Dialog.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int which) {
	            		Intent launchPreferencesIntent = new Intent().setClass(Ushahidi.this, 
	            				Settings.class);
	    				
	    				// Make it a subactivity so we know when it returns
	    				startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
							dialog.dismiss();						
						}
	        		});
	                dialog.setCancelable(false);
	                return dialog;
	     	}
	     	
	     	case DIALOG_PROGRESS:
	            mProgressDialog = new ProgressDialog(Ushahidi.this);
	            mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
	            mProgressDialog.setTitle(R.string.ushahidi_sync);
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            mProgressDialog.setMax(MAX_PROGRESS);
	            
	            return mProgressDialog;
		 }
		 return null;
	  }
	 
	 final Runnable mRetrieveNewIncidents = new Runnable() {
		  public void run() {
		  try {
			  if( Util.isConnected(Ushahidi.this)) {
		   
				  if(Categories.getAllCategoriesFromWeb() ) {
					  mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
				  }
				  
				  if(Incidents.getAllIncidentsFromWeb()){
					  mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
				  }
				  
				  UshahidiApplication.mDb.addCategories(mNewCategories, false);
				  UshahidiApplication.mDb.addIncidents(mNewIncidents, false);
				  
			  } else {
				  Toast.makeText(Ushahidi.this, R.string.internet_connection, Toast.LENGTH_LONG).show();
			  }
		  	} catch (IOException e) {
				//means there was a problem getting it
		  	}
		  }
	  }; 
	 
	final Runnable mDisplayPrompt = new Runnable(){
		public void run(){
			showDialog(DIALOG_PROMPT);
		}
	};
	
	
	
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
		  
		
		i = menu.add( Menu.NONE, SYNC, Menu.NONE, R.string.menu_sync );
		i.setIcon(R.drawable.ushahidi_refresh);
		  
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings );
		i.setIcon(R.drawable.ushahidi_settings);
		  
		i = menu.add( Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about );
		i.setIcon(R.drawable.ushahidi_about);
		
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
				launchPreferencesIntent = new Intent( Ushahidi.this, IncidentMap.class);
        		startActivityForResult( launchPreferencesIntent,MAP_INCIDENTS );
        		setResult(RESULT_OK);
				return true;
		
			case ADD_INCIDENT:
				launchPreferencesIntent = new Intent( Ushahidi.this,AddIncident.class);
        		startActivityForResult( launchPreferencesIntent, ADD_INCIDENTS );
        		setResult(RESULT_OK);
				return true;
				
			case ABOUT:
				launchPreferencesIntent = new Intent( Ushahidi.this,About.class);
	    		startActivityForResult( launchPreferencesIntent, REQUEST_CODE_ABOUT );
	    		setResult(RESULT_OK);
				return true;
				
			case SETTINGS:	
				launchPreferencesIntent = new Intent().setClass(Ushahidi.this, Settings.class);
				
				// Make it a subactivity so we know when it returns
				startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
				setResult(RESULT_OK);
				return true;
				
			case SYNC:
				showDialog(DIALOG_PROGRESS);
				mHandler.post(mRetrieveNewIncidents);
	            mProgress = 0;
	            mProgressDialog.setProgress(0);
	            mHandler.sendEmptyMessage(0);
	            return true;
	            
		}
		return false;
	}
}