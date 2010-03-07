package org.addhen.ushahidi;
 
 
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
 
import android.location.Geocoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;
 
public class IncidentMap extends MapActivity {
	private MapView mapView = null;
	private static final int HOME = Menu.FIRST+1;
	private static final int LIST_INCIDENT = Menu.FIRST+2;
	private static final int INCIDENT_ADD = Menu.FIRST+3;
	private static final int INCIDENT_REFRESH= Menu.FIRST+4;
	private static final int SETTINGS = Menu.FIRST+5;
	private static final int ABOUT = Menu.FIRST+6;
	private static final int GOTOHOME = 0;
	private static final int ADD_INCIDENTS = 1;
	private static final int LIST_INCIDENTS = 2;
	private static final int REQUEST_CODE_SETTINGS = 1;
	private static final int REQUEST_CODE_ABOUT = 2;
	private static final int DIALOG_MESSAGE = 0;
	private static final int  DIALOG_LOADING_INCIDENTS = 1;
 
	private static double latitude;
	private static double longitude;
	private Handler mHandler;
	private MapController ushMapController = null;
	public static Geocoder gc;
	private Vector<String> vectorCategories = new Vector<String>();
	private Spinner spinner = null;
	private ArrayAdapter<String> spinnerArrayAdapter;
	private boolean doUpdates = true;
	private List<IncidentsData> mNewIncidents;
	private Integer index;
	private List<IncidentsData> mOldIncidents;
	private List<CategoriesData> mNewCategories;
	private Bundle extras;
	private int id;
	private String reportLatitude;
    private String reportLongitude; 
    private String reportTitle;
    private String reportDescription;
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_map);
 
		spinner = (Spinner) findViewById(R.id.incident_cat);
		mapView = (MapView) findViewById(R.id.map);
		index = new Integer(0);
		mOldIncidents = new ArrayList<IncidentsData>();
		mNewIncidents  = showIncidents("All");
		
		Bundle incidents = getIntent().getExtras();
		
		if( incidents != null ){
			extras = incidents.getBundle("report");
			id = extras.getInt("id");
			reportTitle = extras.getString("title");
			reportDescription = extras.getString("desc");
			reportLatitude = extras.getString("latitude");
			reportLongitude = extras.getString("longitude");
		}
		
		if( mNewIncidents.size() > 0 ) {
			if( id > 0 ) {
				IncidentMap.latitude = Double.parseDouble( reportLatitude );
				IncidentMap.longitude = Double.parseDouble( reportLongitude );
			} else {
				IncidentMap.latitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLatitude());
				IncidentMap.longitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLongitude());
				
			}
			
			mapView.getController().setCenter(getPoint(IncidentMap.latitude,
					IncidentMap.longitude));
			
			mapView.getController().setZoom(12);

			mapView.setBuiltInZoomControls(true);
 
			mHandler = new Handler();
			
			Drawable marker =getResources().getDrawable(R.drawable.marker);  
 
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
			mapView.getOverlays().add(new SitesOverlay(marker,mapView));
		} else {
			 Toast.makeText(IncidentMap.this, "There are no reports to be shown",
						Toast.LENGTH_LONG).show();
		}
 
		final Thread tr = new Thread() {
		      @Override
		      public void run() {
		    	  //mNewIncidents  = showIncidents("All");
		        showCategories();
 
		      }
		};
		tr.start();
        
	}
 
 
 	@Override
	protected boolean isRouteDisplayed() {
		return(false);
	}
 
 // get incidents from the db
 	  public List<IncidentsData> showIncidents( String by ) {
 
 		  Cursor cursor;
 		  String title;
		  String date;
		  String description;
		  String location;
		  String categories;
		  String media;
 
 		  if( by.equals("All")) 
 			  cursor = UshahidiApplication.mDb.fetchAllIncidents();
 		  else
 			  cursor = UshahidiApplication.mDb.fetchIncidentsByCategories(by);
 		  
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
 			  
 			  
 			  do {
 				  
 				  IncidentsData incidentData = new IncidentsData();
 				  mOldIncidents.add( incidentData );
 				  
 				  int id = Util.toInt(cursor.getString(idIndex));
 				  incidentData.setIncidentId(id);
 				  
 				  title = Util.capitalizeString(cursor.getString(titleIndex));
 				  incidentData.setIncidentTitle(title);
 				  
 				  description = cursor.getString(descIndex);
 				  incidentData.setIncidentDesc(description);
 				  
 				  categories = cursor.getString(categoryIndex);
 				  incidentData.setIncidentCategories(categories);
 				  
 				  location = cursor.getString(locationIndex);
 				  incidentData.setIncidentLocLongitude(location);
 				  
 				  date = Util.joinString("Date: ",cursor.getString(dateIndex));
 				  incidentData.setIncidentDate(cursor.getString(dateIndex));			  
 				  
 				  media = cursor.getString(mediaIndex);
 				  incidentData.setIncidentMedia(media);
 				  
 				  
 				  incidentData.setIncidentVerified(Util.toInt(cursor.getString(verifiedIndex) ));
 				  
 				  incidentData.setIncidentLocLatitude(cursor.getString(latitudeIndex));
 				  incidentData.setIncidentLocLongitude(cursor.getString(longitudeIndex));
 				  
 				  
 			  } while (cursor.moveToNext());
 		  }
 	    
 		  cursor.close();
 		  return mOldIncidents;
 	    
 	  }
 	
 	/**
	 * Restart the receiving, when we are back on line.
	 */
	@Override
	public void onResume() {
		super.onResume();
		this.doUpdates = true;
 
	}
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	        /* switch( requestCode ) {
	       case INCIDENTS_MAP:
	         if( resultCode != RESULT_OK ){
	           break;
	         }
	         mHandler.post(mDisplayIncidents);
	         mHandler.post(mDisplayCategories);
	         
	         //mark all incidents as read
	         UshahidiApplication.mDb.markAllIncidentssRead();  
	         break;
	         }*/
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
 
	final Runnable mDisplayNetworkError = new Runnable(){
		public void run(){
			showDialog(DIALOG_MESSAGE);
		}
	};
 
	final Runnable mDisplayCategories = new Runnable() {
		public void run() {
			showCategories();
		}
	};
 
	final Runnable mDisplayIncidents = new Runnable() {
		public void run() {
			dismissDialog(DIALOG_LOADING_INCIDENTS);
			try{
				//plotIncidentsOnMap();
			} catch(Exception e){
				return;	//means that the dialog is not showing, ignore please!
			}
		}
	};
    
    protected Dialog onCreateDialog(int id, String message, String title) {
        switch (id) {
            case DIALOG_MESSAGE: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
        		});
                dialog.setCancelable(false);
                return dialog;
            }
        }
        return null;
    }
 
	private void populateMenu(Menu menu) {
		MenuItem i;i = menu.add( Menu.NONE, HOME, Menu.NONE, R.string.menu_home );
		i.setIcon(R.drawable.ushahidi_home);
 
		i = menu.add( Menu.NONE, INCIDENT_ADD, Menu.NONE, R.string.incident_menu_add);
		i.setIcon(R.drawable.ushahidi_add);
 
		i = menu.add( Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list );
		i.setIcon(R.drawable.ushahidi_list);
 
 
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
			intent = new Intent( IncidentMap.this,Ushahidi.class);
				startActivityForResult( intent, GOTOHOME );
				return true;
	    	case INCIDENT_REFRESH:
	    		//TODO 
	    		//retrieveIncidentsAndCategories();
	    		//mHandler.post(mDisplayIncidents);
	        return(true);
 
	      case LIST_INCIDENT:
	    	 intent = new Intent( IncidentMap.this, ListIncidents.class);
	  		startActivityForResult( intent,LIST_INCIDENTS );
	        return(true);
 
	      case INCIDENT_ADD:
	    	intent = new Intent( IncidentMap.this, AddIncident.class);
	  		startActivityForResult(intent, ADD_INCIDENTS  );
	        return(true);
 
	      case ABOUT:
				intent = new Intent( IncidentMap.this,About.class);
	    		startActivityForResult( intent, REQUEST_CODE_ABOUT );
	    		setResult(RESULT_OK);
				return true;  
 
	      case SETTINGS:
	    	  intent = new Intent( IncidentMap.this,  Settings.class);
 
	    	  // Make it a subactivity so we know when it returns
	    	  startActivityForResult( intent, REQUEST_CODE_SETTINGS );
	    	  return( true );
	    }
	    return false;
	}
 
	public GeoPoint getPoint(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
 
	 @SuppressWarnings("unchecked")
	  public void showCategories() {
		  Cursor cursor = UshahidiApplication.mDb.fetchAllCategories();
		  vectorCategories.clear();
		  vectorCategories.add("All");
		  if (cursor.moveToFirst()) {
			  int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CATEGORY_TITLE);
			  do {
				  vectorCategories.add( 
						  Util.capitalizeString(cursor.getString(titleIndex).toLowerCase()));
 
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
		   mNewIncidents  = showIncidents(vectorCategories.get(position));
		   mapView.invalidate();
	   }
 
	   @SuppressWarnings("unchecked")
	    public void onNothingSelected(AdapterView parent) { }
 
 
	  };
 
	  private class SitesOverlay extends UshahidiItemizedOverlay<OverlayItem> {  
		  private ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();  
		  private Context context;  
 
		  public SitesOverlay(Drawable marker, MapView mapView) {  
			  super(boundCenter(marker),mapView, IncidentMap.this,mNewIncidents, extras);  
			  context =  mapView.getContext();  
			  
			  if( id > 0 ) {
				  IncidentMap.latitude = Double.parseDouble( reportLatitude);
				  IncidentMap.longitude = Double.parseDouble( reportLongitude);

				  items.add(new OverlayItem(getPoint(IncidentMap.latitude, IncidentMap.longitude),  
						  reportTitle, Util.limitString(reportDescription,30)));
			  }else {
			  
				  for( IncidentsData incidentData : mNewIncidents ) {
					  IncidentMap.latitude = Double.parseDouble( incidentData.getIncidentLocLatitude());
					  IncidentMap.longitude = Double.parseDouble( incidentData.getIncidentLocLongitude());
 
					  items.add(new OverlayItem(getPoint(IncidentMap.latitude, IncidentMap.longitude),  
						  incidentData.getIncidentTitle(), Util.limitString(incidentData.getIncidentDesc(),30)));
 
				  }
			  }
 
			  populate();  
		  }  
 
		  @Override  
		  protected OverlayItem createItem(int i) {  
			  return items.get(i);  
		  }    
 
		  @Override  
		  protected boolean onBalloonTap(int i) {
			  return true;
		  }  
 
		  @Override  
		  public int size() {  
			  return(items.size());  
		  }  
	}
}