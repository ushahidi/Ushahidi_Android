package org.addhen.ushahidi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.addhen.ushahidi.net.UshahidiHttpClient;
import org.addhen.ushahidi.net.UshahidiHttpClient.Category;
import org.addhen.ushahidi.net.UshahidiHttpClient.IncidentData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class ListIncidents extends Activity 
{
	
	/** Called when the activity is first created. */
	private ListView listIncidents = null;
	private ListIncidentAdapter ila = new ListIncidentAdapter( this );
	private static final int LIST_INCIDENT = Menu.FIRST+1;
	private static final int MAP_INCIDENT = Menu.FIRST+2;
	private static final int ADD_INCIDENT = Menu.FIRST+3;
	private static final int VIEW_INCIDENT = 0;
	private static final int USHAHIDI = 1;
	private static final int DIALOG_NETWORK_ERROR = 1;
	private static final int DIALOG_LOADING_INCIDENTS = 2;
	private static final int DIALOG_EMPTY_INCIDENTS = 3;
	private static final int LIST_INCIDENTS = 0;
	private Spinner spinner = null;
	private ArrayAdapter spinnerArrayAdapter = null;
	private String URL = "" ;
	private String settingsURL = "";
	private int col = 5;
	private String incidentDetails[][];
	private String incidents[][];
	private Bundle incidentsMap = new Bundle();
	private final Handler mHandler = new Handler();
	private String PREFS_NAME = "Ushahidi";
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.list_incidents );
        
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settingsURL = settings.getString("Domain", "");
    	this.setURL( settingsURL );
    	incidents = this.getIncidentDetails();
        listIncidents = (ListView) findViewById( R.id.view_incidents );
        
        listIncidents.setOnItemClickListener( new OnItemClickListener(){	
			
        	public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				if( incidents.length != 0 ) {
					incidentsMap.putString( "title", incidents[position][0] );
					incidentsMap.putString( "body", incidents[position][1] );
					incidentsMap.putString( "category", incidents[position][2] );
					incidentsMap.putString( "location", incidents[position][3] );
					incidentsMap.putString( "thumbnail", incidents[position][4] );
				
					Intent intent = new Intent( ListIncidents.this,ViewIncidents.class);
					intent.putExtra("incidents", incidentsMap);
					startActivityForResult(intent,VIEW_INCIDENT);
					setResult( RESULT_OK, intent );
	                finish();
				}
        		//Log.i("Incidents", incidents[position][0]);
			}
        	
        });
        spinner = (Spinner) findViewById(R.id.incident_cat);
        this.showCategories();
        //mHandler.post( mDisplayCategories );
        //mHandler.post( mDisplayIncidents );
        
        this.showIncidents("DEATHS");
        //this.setIncidentDetails( iIncidentDetails );
        incidents = this.getIncidentDetails();
    }
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_NETWORK_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Network error!");
                dialog.setMessage("Network error, please ensure you are connected to the internet");
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent( ListIncidents.this,Ushahidi.class);
						startActivityForResult( intent,USHAHIDI );
						setResult( RESULT_OK );
		                finish();
					}
        		});
                dialog.setCancelable(false);
                return dialog;
            }
            
            case DIALOG_LOADING_INCIDENTS: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Loading incidents");
                dialog.setMessage("Please wait while incidents are loaded...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
            
            case DIALOG_EMPTY_INCIDENTS: {
            	AlertDialog dialog = (new AlertDialog.Builder(this)).create();
            	dialog.setTitle("No incidents!");
            	dialog.setMessage("No incident available for this category, please select " +
            			"a new category to filter by.");
            	dialog.setButton2("Ok", new Dialog.OnClickListener() {
            		public void onClick( DialogInterface dialog, int which ) {
            			dialog.dismiss();
            		}
            	});
            	dialog.setCancelable(false);
                return dialog;
            }
            
        }
        return null;
    }
	
	final Runnable mDisplayIncidents = new Runnable() {
		public void run() {
			showDialog(DIALOG_LOADING_INCIDENTS);
			try{
				dismissDialog( DIALOG_LOADING_INCIDENTS );
			} catch(Exception e){
				return;	//means that the dialog is not showing, ignore please!
			}
		}
	};
	
	final Runnable mDisplayCategories = new Runnable() {
		public void run() {
			showCategories();
			try{
				//dismissDialog( DIALOG_LOADING_INCIDENTS );
			} catch(Exception e){
				return;	//means that the dialog is not showing, ignore please!
			}
		}
	};
	
	final Runnable mDisplayNetworkError = new Runnable(){
		public void run(){
			showDialog(DIALOG_NETWORK_ERROR);
		}
	};
	
	final Runnable mDisplayIncidentsLoading = new Runnable() {
		public void run() {
			showDialog(DIALOG_LOADING_INCIDENTS);
		}
	};
	
	final Runnable mDisplayEmptyIncident = new Runnable() {
		public void run() {
			showDialog(DIALOG_EMPTY_INCIDENTS);
		}
	};
	
	public void setURL( String URL ) {
		// set the directory where ushahidi photos are stored
		String photoDir = "/media/uploads/";
		this.URL = URL+photoDir;
	}
	
	public String getURL() {
		return this.URL;
	}

  //menu stuff
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
		menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, "List Incident");
		menu.add(Menu.NONE, MAP_INCIDENT, Menu.NONE, "Map Incident");
		menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, "Add Incident");
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
			case LIST_INCIDENT:
				//TODO
				return(true);
		
			case MAP_INCIDENT:
				//TODO
				return(true);
		
			case ADD_INCIDENT:
				//TODO
				return(true);
		
		}
		return(false);
	}
	
	public void showIncidents( String cat ) {
		
		String url = settingsURL+"/api";
		
		try{
			List<IncidentData> incidentData = new UshahidiHttpClient().getIncidents( url,cat );
			
		String body = "";
		
		String iIncidentDetails[][] = new String[incidentData.size()][col];
		int i = 0;
		ila.removeItems();
		/*for (IncidentData data : incidentData ) {
			body = data.getTitle()+"\nLocation: "+data.iLocation+"\nCategory: "+
				data.iCategory+"\n";
			
			ila.addItem( new ListIncidentText( 
				data.getThumbnail() != "" ?	imageOperations( getURL()+data.getThumbnail(),
						data.getThumbnail()):
					getResources().getDrawable( R.drawable.ushahidi_icon) ,body ) );
			
			iIncidentDetails[i][0] = data.getTitle();
			iIncidentDetails[i][1] = data.iBody;
			iIncidentDetails[i][2] = data.iCategory;
			iIncidentDetails[i][3] = data.iLocation;
			iIncidentDetails[i][4] = data.getThumbnail();
			i++;	
		}*/
		setIncidentDetails( iIncidentDetails ); 
		listIncidents.setAdapter( ila );
			
		}catch( Exception e){
			mHandler.post( mDisplayNetworkError );
		}
		
	}
	
	public void setIncidentDetails( String iIncidentDetails[][]){
		incidentDetails = iIncidentDetails;
	}
	
	public String[][] getIncidentDetails(){
		return incidentDetails;
	}
	
	@SuppressWarnings("unchecked")
	public void showCategories() {
		String url = settingsURL+"/api";
		try{
		List<Category> category = new UshahidiHttpClient().getCategories( url );
		Vector<String> vector = new Vector<String>();
		
		for( Category data : category ) {
			vector.add( data.getCTitle() );
		}
		
		spinnerArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, vector );
		
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(spinnerListener);
		}catch( Exception e ){
			Log.d("URL Exception",e.toString());
			mHandler.post(mDisplayNetworkError);
		}
	}
	
	//spinner listener
	Spinner.OnItemSelectedListener spinnerListener =
	    new Spinner.OnItemSelectedListener() {
		
	      @SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView parent, View v, int position, long id) {
	    	  showDialog(DIALOG_LOADING_INCIDENTS);
	    	  showIncidents( parent.getSelectedItem().toString() );
	    	  dismissDialog(DIALOG_LOADING_INCIDENTS);
	      }

	      @SuppressWarnings("unchecked")
		public void onNothingSelected(AdapterView parent) { }

	                  
	}; 
	
	// As drawable.  
	public static Drawable imageOperations(String url, String saveFilename) {
		try {
			InputStream is = (InputStream) fetch(url);
			Drawable d = Drawable.createFromStream(is, saveFilename);
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Fetch image from the given URL
	private static Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content; 
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch( requestCode ) {
			case LIST_INCIDENTS:
				if( resultCode != RESULT_OK ){
					break;
				}
				//showDialog( DIALOG_LOADING_INCIDENTS );	
				break;
        }
    }
	
}
