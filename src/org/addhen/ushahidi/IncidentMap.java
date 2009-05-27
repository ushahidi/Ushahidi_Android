package org.addhen.ushahidi;
	 

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Paint.Style;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.addhen.ushahidi.AddIncident.MyLocationListener;
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
	private static final int DIALOG_NETWORK_ERROR = 0;
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
	private List<IncidentsData> mOldIncidents;
	private List<CategoriesData> mNewCategories;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_map);
		
		spinner = (Spinner) findViewById(R.id.incident_cat);
		mapView = (MapView) findViewById(R.id.map);
		
		mOldIncidents = new ArrayList<IncidentsData>();
		mNewIncidents  = showIncidents("All");
		
		IncidentMap.latitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLatitude());
		IncidentMap.longitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLongitude());
		mapView.getController().setCenter(getPoint(IncidentMap.latitude,
				IncidentMap.longitude));
		mapView.getController().setZoom(17);

		ViewGroup zoom = (ViewGroup)findViewById(R.id.zoom);

		zoom.addView(mapView.getZoomControls());
		
		mHandler = new Handler();
		//mHandler.post(mDisplayCategories);
		Drawable marker =getResources().getDrawable(R.drawable.ushahidi_circle);  
		  
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		mapView.getOverlays().add(new SitesOverlay(marker)); 
		
		final Thread tr = new Thread() {
		      @Override
		      public void run() {
		        //incidentDetails = showIncidents("DEATHS");
		        showCategories();
		       
		      }
		};
		tr.start();
        
	}
	
	
 	@Override
	protected boolean isRouteDisplayed() {
		return(false);
	}
	
 	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
 		if (keyCode == KeyEvent.KEYCODE_I) {
	    	// Zoom not closer than possible
        	this.ushMapController.zoomIn();
	    	//this.myMapController.zoomInFixing(Math.min(21, this.myMapView.getZoomLevel() + 1));
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_O) {
	    	// Zoom not farer than possible 
        	this.ushMapController.zoomOut();
	    	//this.myMapController.zoomInFixing(Math.max(1, this.myMapView.getZoomLevel() - 1),0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_T) {
        	// Switch to satellite view
            mapView.setSatellite(true);
            
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_M) {
        	// Switch to satellite view
            mapView.setSatellite(false);
            
            return true;
        }
 		
        return false;
	}
 	
 // get incidents from the db
 	  public List<IncidentsData> showIncidents( String by ) {
 
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
			showDialog(DIALOG_NETWORK_ERROR);
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
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_NETWORK_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Network error!");
                dialog.setMessage("Please ensure you are connected to the internet.");
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
        		});
                dialog.setCancelable(false);
                return dialog;
            }
            case DIALOG_LOADING_INCIDENTS: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Translating...");
                dialog.setMessage("Please wait while I translate for you.");
                dialog.setIndeterminate(true);
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
		i.setIcon(R.drawable.ushahidi_settings);
	  
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
	    	 intent = new Intent( IncidentMap.this, IncidentMap.class);
	  		startActivityForResult( intent,LIST_INCIDENTS );
	        return(true);
	    
	      case INCIDENT_ADD:
	    	intent = new Intent( IncidentMap.this, AddIncident.class);
	  		startActivityForResult(intent, ADD_INCIDENTS  );
	        return(true);
	        
	      case SETTINGS:
	    	  intent = new Intent( IncidentMap.this,  Settings.class);
				
	    	  // Make it a subactivity so we know when it returns
	    	  startActivityForResult( intent, REQUEST_CODE_SETTINGS );
	    	  return( true );
	    }
	    return false;
	}
	
	private GeoPoint getPoint(double lat, double lon) {
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
		   //showIncidents(vectorCategories.get(position));
	   }
	 
	   @SuppressWarnings("unchecked")
	    public void onNothingSelected(AdapterView parent) { }
	 
	  
	  };
	  
	  private class SitesOverlay extends ItemizedOverlay<OverlayItem> {  
		  private List<OverlayItem> items=new ArrayList<OverlayItem>();  
		  private Drawable marker=null;  
		    
		  public SitesOverlay(Drawable marker) {  
			  super(marker);  
			  this.marker=marker;  
		  
			  mNewIncidents  = showIncidents("All");
		  
			  for( IncidentsData incidentData : mNewIncidents ) {
				  IncidentMap.latitude = Double.parseDouble( incidentData.getIncidentLocLatitude());
				  IncidentMap.longitude = Double.parseDouble( incidentData.getIncidentLocLongitude());
		  
				  items.add(new OverlayItem(getPoint(latitude, longitude),  
						  incidentData.getIncidentTitle(), incidentData.getIncidentDesc()));
		  
			  }
		    
			  populate();  
		  }  
		    
		  @Override  
		  protected OverlayItem createItem(int i) {  
			  return items.get(i);  
		  }  
		    
		  @Override  
		  public void draw(Canvas canvas, MapView mapView, boolean shadow) {  
			  super.draw(canvas, mapView, shadow);  
		    
			  boundCenterBottom(marker);  
		  }  
		    
		  @Override  
		  protected boolean onTap(int i) {  
			  Toast.makeText(IncidentMap.this, items.get(i).getSnippet(), Toast.LENGTH_SHORT).show();  
		    
			  return(true);  
		  }  
		    
		  @Override  
		  public int size() {  
			  return(items.size());  
		  }  
	}
}