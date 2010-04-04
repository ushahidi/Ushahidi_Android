package org.addhen.ushahidi;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
 
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
	private static final int HOME = Menu.FIRST+1;
	private static final int ADD_INCIDENT = Menu.FIRST+2;
	private static final int INCIDENT_MAP = Menu.FIRST+3;
	private static final int INCIDENT_REFRESH= Menu.FIRST+4;
	private static final int SETTINGS = Menu.FIRST+5;
	private static final int ABOUT = Menu.FIRST+6;
	private static final int GOTOHOME = 0;
	private static final int POST_INCIDENT = 1;
	private static final int INCIDENTS_MAP = 2;
	private static final int VIEW_INCIDENT = 3;
	private static final int REQUEST_CODE_SETTINGS = 1;
	private static final int REQUEST_CODE_ABOUT = 2;
	private Spinner spinner = null;
	private ArrayAdapter<String> spinnerArrayAdapter;
	private Bundle incidentsBundle = new Bundle();
	private final Handler mHandler = new Handler();
	public static UshahidiDatabase mDb;
  
	private List<IncidentsData> mOldIncidents;
	private Vector<String> vectorCategories = new Vector<String>();
  
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView( R.layout.list_incidents );
       
		listIncidents = (ListView) findViewById( R.id.view_incidents );
        
		mOldIncidents = new ArrayList<IncidentsData>();
		listIncidents.setOnItemClickListener( new OnItemClickListener(){  
      
			public void onItemClick(AdapterView<?> arg0, View view, int position,
        		  long id) {
				
				incidentsBundle.putInt("id", mOldIncidents.get(position).getIncidentId());
				incidentsBundle.putString("title",mOldIncidents.get(position).getIncidentTitle());
				incidentsBundle.putString("desc", mOldIncidents.get(position).getIncidentDesc());
				incidentsBundle.putString("longitude",mOldIncidents.get(position).getIncidentLocLongitude());
				incidentsBundle.putString("latitude",mOldIncidents.get(position).getIncidentLocLatitude());
				incidentsBundle.putString("category", mOldIncidents.get(position).getIncidentCategories());
				incidentsBundle.putString("location", mOldIncidents.get(position).getIncidentLocation());
				incidentsBundle.putString("date", mOldIncidents.get(position).getIncidentDate());
				incidentsBundle.putString("media", mOldIncidents.get(position).getIncidentMedia());
				incidentsBundle.putString("status", ""+mOldIncidents.get(position).getIncidentVerified());
          
				Intent intent = new Intent( ListIncidents.this,ViewIncidents.class);
				intent.putExtra("incidents", incidentsBundle);
				startActivityForResult(intent,VIEW_INCIDENT);
				setResult( RESULT_OK, intent );
              
			}
          
		});
		
		spinner = (Spinner) findViewById(R.id.incident_cat);
		
		mHandler.post(mDisplayIncidents);
		mHandler.post(mDisplayCategories);
		
		//mark all incidents as read
		UshahidiApplication.mDb.markAllIncidentssRead();
		UshahidiApplication.mDb.markAllCategoriesRead();
		
	}
  
	@Override
	protected void onResume(){
		super.onResume();
	}
  
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
  
	final Runnable mDisplayIncidents = new Runnable() {
		public void run() {
			setProgressBarIndeterminateVisibility(true);
			showIncidents("All");
			showCategories();
			try{
				setProgressBarIndeterminateVisibility(false);
			} catch(Exception e){
				return;  //means that the dialog is not showing, ignore please!
			}
		}
	};
  
	final Runnable mDisplayCategories = new Runnable() {
		public void run() {
			showCategories();
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
		MenuItem i;i = menu.add( Menu.NONE, HOME, Menu.NONE, R.string.menu_home );
		i.setIcon(R.drawable.ushahidi_home);
		
		i = menu.add( Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add);
		i.setIcon(R.drawable.ushahidi_add);
		  
		i = menu.add( Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map );
		i.setIcon(R.drawable.ushahidi_map);
		  
		
		i = menu.add( Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.incident_menu_refresh );
		i.setIcon(R.drawable.ushahidi_refresh);
		  
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings );
		i.setIcon(R.drawable.ushahidi_settings);
		  
		i = menu.add( Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about );
		i.setIcon(R.drawable.ushahidi_about);
	  
	}
  
	private boolean applyMenuChoice(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
    		case HOME:
    			intent = new Intent( ListIncidents.this,Ushahidi.class);
    			startActivityForResult( intent, GOTOHOME );
    			return true;
    		case INCIDENT_REFRESH:
    			ReportsTask reportsTask = new ReportsTask();
	            reportsTask.appContext = this;
	            reportsTask.execute();
    			return(true);
    
    		case INCIDENT_MAP:
    			incidentsBundle.putInt("tab_index", 1);
    			intent = new Intent( ListIncidents.this, IncidentsTab.class);
    			intent.putExtra("tab", incidentsBundle);
    			startActivityForResult( intent,INCIDENTS_MAP );
    			return(true);
    
    		case ADD_INCIDENT:
    			intent = new Intent( ListIncidents.this, AddIncident.class);
    			startActivityForResult(intent, POST_INCIDENT  );
    			return(true);
     
    		case ABOUT:
    			intent = new Intent( ListIncidents.this,About.class);
    			startActivityForResult( intent, REQUEST_CODE_ABOUT );
    			setResult(RESULT_OK);
    			return true;  
        
    		case SETTINGS:
    			intent = new Intent( ListIncidents.this,  Settings.class);
			
    			// Make it a subactivity so we know when it returns
    			startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    			return(true);
        
		}
		return(false);
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
			} else if( result == 0 ) {
				showIncidents("All");
				showCategories();
				setProgressBarIndeterminateVisibility(false);
			}
		}

		
	}
  
	// get incidents from the db
	public void showIncidents( String by ) {
    
		Cursor cursor;
		if( by.equals("All")) 
			cursor = UshahidiApplication.mDb.fetchAllIncidents();
		else
			cursor = UshahidiApplication.mDb.fetchIncidentsByCategories(by);
	  
			String title;
			String status;
			String date;
			String description;
			String location;
			String categories;
			String media;
	
			String thumbnails [];
			Drawable d = null;
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
				
				int latitudeIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LATITUDE);
				
				int longitudeIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LONGITUDE);
				
				ila.removeItems();
				ila.notifyDataSetChanged();
				mOldIncidents.clear();
				
				do {
			  
					IncidentsData incidentData = new IncidentsData();
					mOldIncidents.add( incidentData );
			  
					int id = Util.toInt(cursor.getString(idIndex));
					incidentData.setIncidentId(id);
					incidentData.setIncidentLocLatitude(cursor.getString(latitudeIndex));
					incidentData.setIncidentLocLongitude(cursor.getString(longitudeIndex));
					title = Util.capitalizeString(cursor.getString(titleIndex));
					incidentData.setIncidentTitle(title);
			  
					description = cursor.getString(descIndex);
					incidentData.setIncidentDesc(description);
			  
					categories = cursor.getString(categoryIndex);
					incidentData.setIncidentCategories(categories);
			  
					location = cursor.getString(locationIndex);
					incidentData.setIncidentLocation(location);
					
					//TODO format the date to the appropriate format
					date = Util.formatDate("yyyy-MM-dd hh:mm:ss", cursor.getString(dateIndex), "MMMM dd, yyyy 'at' hh:mm:ss aaa" );
					
					incidentData.setIncidentDate(date);			  
			  
					media = cursor.getString(mediaIndex);
					incidentData.setIncidentMedia(media);
					thumbnails = media.split(",");
			  
					//TODO make the string readable from the string resource
					status = Util.toInt(cursor.getString(verifiedIndex) ) == 0 ? "Unverified" : "Verified";
					incidentData.setIncidentVerified(Util.toInt(cursor.getString(verifiedIndex) ));
			  
					//TODO do a proper check for thumbnails
					d = ImageManager.getImages( thumbnails[0]);
			  
					ila.addItem( new ListIncidentText( d == null ? getResources().getDrawable( R.drawable.ushahidi_report_icon):d, 
							title, date, 
							status,
							description,
							location,
							media,
							categories, 
							id,
							getResources().getDrawable( R.drawable.ushahidi_arrow)) );
			  
				} while (cursor.moveToNext());
			}
    
			cursor.close();
			ila.notifyDataSetChanged();
			listIncidents.setAdapter( ila );
	}
  
  
	@SuppressWarnings("unchecked")
	public void showCategories() {
		Cursor cursor = UshahidiApplication.mDb.fetchAllCategories();
		UshahidiApplication.mDb.fetchCategoriesCount();
		
		vectorCategories.clear();
		vectorCategories.add("All");
		if (cursor.moveToFirst()) {
			int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CATEGORY_TITLE);
			do {
				vectorCategories.add( cursor.getString(titleIndex));
			}while( cursor.moveToNext() );
		}
		cursor.close();
		spinnerArrayAdapter = new ArrayAdapter(this,
			  android.R.layout.simple_spinner_item, vectorCategories );
		    
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
	  
		spinner.setOnItemSelectedListener(spinnerListener);
	  
	}
  
	//spinner listener
	Spinner.OnItemSelectedListener spinnerListener =
		new Spinner.OnItemSelectedListener() {
    
		@SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView parent, View v, int position, long id) {
			showIncidents(vectorCategories.get(position));
		}
 
		@SuppressWarnings("unchecked")
		public void onNothingSelected(AdapterView parent) { }
	};
  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}
  
}