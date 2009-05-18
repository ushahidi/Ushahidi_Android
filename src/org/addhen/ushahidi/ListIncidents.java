package org.addhen.ushahidi;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
 
import org.addhen.ushahidi.net.Categories;
import org.addhen.ushahidi.net.Incidents;
import org.addhen.ushahidi.net.UshahidiHttpClient;
import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.photostream.UserTask;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
  private ArrayAdapter spinnerArrayAdapter;
  private Bundle incidentsBundle = new Bundle();
  private final Handler mHandler = new Handler();
  private String incidentDetails[][];
  private static final String TAG = "ListIncidents";
  public static UshahidiDatabase mDb;
  private static final String LAUNCH_ACTION = "org.addhen.ushahidi.INCIDENTS";
  private static final String NEW_INCIDENTS_ACTION = "org.addhen.ushahidi.NEW";
  
  private List<IncidentsData> mNewIncidents;
  private List<CategoriesData> mNewCategories;
 
  private static final String EXTRA_TEXT = "text";
  private UserTask<Void, Void, RetrieveResult> mRetrieveTask;
  
  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView( R.layout.list_incidents );
       
        listIncidents = (ListView) findViewById( R.id.view_incidents );
        
        mDb = new UshahidiDatabase(this);
	    mDb.open();
        
        listIncidents.setOnItemClickListener( new OnItemClickListener(){  
      
          public void onItemClick(AdapterView<?> arg0, View view, int position,
          long id) {
        	  //TODO show view incident details the user clicks on the list
        	  Log.i(TAG, "incident id "+mNewIncidents.get(position).getIncidentId());
        	  incidentsBundle.putString("title",mNewIncidents.get(position).getIncidentTitle());
        	  incidentsBundle.putString("desc", mNewIncidents.get(position).getIncidentDesc());
        	  incidentsBundle.putString("category", mNewIncidents.get(position).getIncidentCategories());
        	  incidentsBundle.putString("location", mNewIncidents.get(position).getIncidentLocation());
        	  incidentsBundle.putString("date", mNewIncidents.get(position).getIncidentDate());
        	  incidentsBundle.putString("media", mNewIncidents.get(position).getIncidentMedia());
        	  incidentsBundle.putString("status", ""+mNewIncidents.get(position).getIncidentVerified());
          
        	  Intent intent = new Intent( ListIncidents.this,ViewIncidents.class);
				intent.putExtra("incidents", incidentsBundle);
				startActivityForResult(intent,VIEW_INCIDENT);
				setResult( RESULT_OK, intent );
              finish();
          }
          
        });
        spinner = (Spinner) findViewById(R.id.incident_cat);
        if( UshahidiService.AutoFetch ) {
          try {
            mHandler.post(mDisplayIncidentsLoading);
            if(org.addhen.ushahidi.net.Incidents.getAllIncidentsFromWeb()) {
              UshahidiService.saveSettings(this);
            }
            mHandler.post(mDismissLoading);
          }catch(IOException e) {
            mHandler.post(mDismissLoading);
            mHandler.post(mDisplayNetworkError);
            return;
          }
        }
        mHandler.post(mDisplayIncidents);
        //mark all incidents as read
        mDb.markAllIncidentssRead();
    }
  
  public static Intent createIntent(Context context) {
   Intent intent = new Intent(LAUNCH_ACTION);
   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
   return intent;
   }
  
  public static Intent createNewIncidentsIntent(String text) {
   Intent intent = new Intent(NEW_INCIDENTS_ACTION);
   intent.putExtra(EXTRA_TEXT, text);
 
   return intent;
   }
  
  	private void retrieveIncidentsAndCategories() {
  		
  		try {
  			
  			if(Incidents.getAllIncidentsFromWeb()){
				   mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
			}
			   
  			if(Categories.getAllCategoriesFromWeb() ) {
  				Log.i(TAG,"Refreshing...");   
  				mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
  				Log.i(TAG,"Done Refreshing");
  			}
  		} catch (IOException e) {
				//means there was a problem getting it
  		}
 
  		mDb.addIncidents(mNewIncidents, false);
	    	
  		mDb.addCategories(mNewCategories, false);
	    
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
      //showDialog(DIALOG_LOADING_INCIDENTS);
    	setProgressBarIndeterminateVisibility(true);
    	retrieveIncidentsAndCategories();
      
    	showIncidents();
    	showCategories();
      //showIncidents();
      try{
    	  setProgressBarIndeterminateVisibility(false);
    	  //setProgressBarIndeterminateVisibility(false);
      } catch(Exception e){
        return;  //means that the dialog is not showing, ignore please!
      }
    }
  };
  
  final Runnable mDisplayCategories = new Runnable() {
    public void run() {
      showCategories();
      try{
        //dismissDialog( DIALOG_LOADING_INCIDENTS );
      } catch(Exception e){
        return;  //means that the dialog is not showing, ignore please!
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
  
  final Runnable mDismissLoading = new Runnable(){
    public void run(){
      try{
        dismissDialog(DIALOG_LOADING_INCIDENTS);        
      } catch(IllegalArgumentException e){
        return;  //means that the dialog is not showing, ignore please!
      }
    }
  };

 
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
  
  // get incidents from the db
  public void showIncidents() {
    
	  Cursor cursor = mDb.fetchAllIncidents();
	  String status;
	  String date;
	  String description;
	  String location;
	  String categories;
	  String media;
	  int i = 0;
	  
	  //iIncidentDetails[][] = new String[IncidentsData.size()][6];
	  
	  if (cursor.moveToFirst()) {
		  int idIndex = cursor.getColumnIndexOrThrow( 
				  UshahidiDatabase.INCIDENT_ID);
		  int titleIndex = cursor.getColumnIndexOrThrow(
				  UshahidiDatabase.INCIDENT_TITLE);
		  int dateIndex = cursor.getColumnIndexOrThrow(
				  UshahidiDatabase.INCIDENT_DATE);
		  int verifiedIndex = cursor.getColumnIndexOrThrow(
				  UshahidiDatabase.INCIDENT_VERIFIED);
		  int locationIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_NAME);
		  
		  int descIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_DESC);
		  
		  int categoryIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_CATEGORIES);
		  
		  int mediaIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_MEDIA);
		  
		  ila.removeItems();
		  
		  do {
			  int id = Util.toInt(cursor.getString(idIndex));
			  
			  //TODO make the string readable from the string resource
			  status = Util.toInt(cursor.getString(verifiedIndex) ) == 0 ? "Unverified" : "Verified";
			  
			  date = Util.joinString("Date: ",cursor.getString(dateIndex));
			  
			  description = cursor.getString(descIndex);
			  
			  location = cursor.getString(locationIndex);
			  categories = cursor.getString(categoryIndex);
			  media= cursor.getString(mediaIndex);
			  
			  
			  ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_icon), 
					  cursor.getString(titleIndex), date, 
					  	status,description,location,media,categories, id) );
			  
		  } while (cursor.moveToNext());
	  }
    
	  cursor.close();
	  listIncidents.setAdapter( ila );
    
  }
  
  @SuppressWarnings("unchecked")
  public void showCategories() {
	  Cursor cursor = mDb.fetchAllCategories();
	  
	  Vector<String> vector = new Vector<String>();
	  vector.add("All");
	  if (cursor.moveToFirst()) {
		  int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CATEGORY_TITLE);
		  do {
			  vector.add( cursor.getString(titleIndex).toLowerCase());
		  }while( cursor.moveToNext() );
	  }
	  cursor.close();
	  spinnerArrayAdapter = new ArrayAdapter(this,
			  android.R.layout.simple_spinner_item, vector );
		    
	  spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  spinner.setAdapter(spinnerArrayAdapter);
	  
	  spinner.setOnItemSelectedListener(spinnerListener);
	  
  }
  
  //spinner listener
  Spinner.OnItemSelectedListener spinnerListener =
   new Spinner.OnItemSelectedListener() {
    
   @SuppressWarnings("unchecked")
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
      showDialog(DIALOG_LOADING_INCIDENTS);
      showIncidents();
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
        mHandler.post(mDisplayIncidents);
        //mark all incidents as read
        mDb.markAllIncidentssRead();  
        break;
        }
    }
  
  	private void doRetrieve() {
	    Log.i(TAG, "Attempting retrieve.");

	    if (mRetrieveTask != null
	        && mRetrieveTask.getStatus() == UserTask.Status.RUNNING) {
	      Log.w(TAG, "Already retrieving.");
	    } else {
	      mRetrieveTask = new RetrieveTask().execute();
	    }
	  }

	  private void onRetrieveBegin() {
		  Log.i(TAG,"Refreshing...");
	  }


	  private enum RetrieveResult {
	    OK, IO_ERROR, AUTH_ERROR, CANCELLED
	  }
	
	//retrieve incidents
	private class RetrieveTask extends UserTask<Void, Void, RetrieveResult> {
	    @Override
	    public void onPreExecute() {
	      onRetrieveBegin();
	    }

	    @Override
	    public RetrieveResult doInBackground(Void... params) {
	    	try {
				   if(Incidents.getAllIncidentsFromWeb()){
					   mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
				   }
				   
				   if(Categories.getAllCategoriesFromWeb() ) {
					   mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
				   }
		    	} catch (IOException e) {
					//means there was a problem getting it
		    	}
	    

		    	if (isCancelled()) {
		    		return RetrieveResult.CANCELLED;
		    	}

		    	mDb.addIncidents(mNewIncidents, false);
	      
		    	if (isCancelled()) {
		    		return RetrieveResult.CANCELLED;
		    	}

		    	return RetrieveResult.OK;
	    	}

	  }
  
}