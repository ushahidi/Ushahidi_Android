package org.addhen.ushahidi;
	 

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
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
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import android.location.Address;
import android.location.Geocoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;

public class IncidentMap extends MapActivity {
	MapView map = null;
	private static final int LIST_INCIDENT = Menu.FIRST+1;
	private static final int MAP_INCIDENT = Menu.FIRST+2;
	private static final int ADD_INCIDENT = Menu.FIRST+3;
	
	private static final int DIALOG_NETWORK_ERROR = 0;
	private static final int  DIALOG_LOADING_INCIDENTS = 1;
	
	private String settingsURL = "";
	private int col = 5;
	private Double latitude;
	private Double longitude;
	private double lat;
	private double lon;
	private String incidentDetails[][];
	private String incidents[][];
	private Handler mHandler;
	private List<List<Address>> addresses;
	private List<Address> foundAddresses;
	public static Geocoder gc;
	private List<GeoPoint> points;
	private Drawable marker;
	private Vector<String> vectorCategories = new Vector<String>();
	private Spinner spinner = null;
	private ArrayAdapter<String> spinnerArrayAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_map);
		
		spinner = (Spinner) findViewById(R.id.incident_cat);
        
        gc = new Geocoder(this);
        
        mHandler = new Handler();
        
        addresses = new ArrayList<List<Address>>();
        points = new ArrayList<GeoPoint>();
        
        marker = getResources().getDrawable(R.drawable.ushahidi_marker);
        
		//this.setMap();
		
		showDialog(DIALOG_LOADING_INCIDENTS);
		final Thread tr = new Thread() {
			@Override
			public void run() {
				//incidentDetails = showIncidents("DEATHS");
				geoCodeLocations();
				mHandler.post(mDisplayCategories);
				mHandler.post(mDisplayIncidents);
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
		if (keyCode == KeyEvent.KEYCODE_S) {
			map.setSatellite(!map.isSatellite());
			return(true);
		}
		else if (keyCode == KeyEvent.KEYCODE_Z) {
			map.displayZoomControls(true);
			return(true);
		}
		
		return(super.onKeyDown(keyCode, event));
	}

	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
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
				plotIncidentsOnMap();
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
		menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, "List Incident");
		menu.add(Menu.NONE, MAP_INCIDENT, Menu.NONE, "Map Incident");
		menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, "Add Incident");
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
			case LIST_INCIDENT:
				//getListView().setDividerHeight(8);
				return(true);
		
			case MAP_INCIDENT:
				//getListView().setDividerHeight(16);
				return(true);
		
			case ADD_INCIDENT:
				//getListView().setDividerHeight(24);
				return(true);
		
		}
		return(false);
	}
	
	public String[][] showIncidents( String cat ) {
		
		String url = settingsURL+"/api";
		String iIncidentDetails[][] = null;
		try{
			List<IncidentsData> incidentData = new ArrayList<IncidentsData>();
			
		String body = "";
		iIncidentDetails = new String[incidentData.size()][col];
		
		int i = 0;
		for (IncidentsData data : incidentData ) {
			//TODO get the data needed.
			
			/*body = data.getTitle()+"\nLocation: "+data.iLocation+"\nCategory: "+
				data.iCategory+"\n";			
			iIncidentDetails[i][0] = data.getTitle();
			iIncidentDetails[i][1] = data.iBody;
			iIncidentDetails[i][2] = data.iCategory;
			iIncidentDetails[i][3] = data.iLocation;
			iIncidentDetails[i][4] = data.getThumbnail();
			i++;*/	
		}
		
		}catch( Exception e){
			mHandler.post(mDisplayNetworkError);
		}
		return iIncidentDetails;
	}
	
	 @SuppressWarnings("unchecked")
	  public void showCategories() {
		  Cursor cursor = UshahidiApplication.mDb.fetchAllCategories();
		  
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
		   showIncidents(vectorCategories.get(position));
	   }
	 
	   @SuppressWarnings("unchecked")
	    public void onNothingSelected(AdapterView parent) { }
	 
	  
	  };
		
	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items=new ArrayList<OverlayItem>();
		private Drawable marker=null;
		private Geocoder gc;
		private List<Address> foundAdd;
		public SitesOverlay(Drawable marker,List<GeoPoint> points, String[][]locations) {
			super(marker);
			
			this.marker = marker;
			
			
			try{
			for( int i =0; i < locations.length; i++ ) {	
				//TODO get the latitude and longitude from the database.
				
					/*GeoPoint geopoint = new  GeoPoint(latitude.intValue() , 
							longitude.intValue());
					OverlayItem overlayItem = new OverlayItem( geopoint,
							locations[i][0],locations[i][1]+"\n"+locations[i][2]);
					items.add( overlayItem );*/
					//latitude = (-1.2811737395587102 * 1000000);
					//longitude = ( 36.815185546875 * 1000000);
					//points.add(new GeoPoint( latitude.intValue() , longitude.intValue() ) );
					
					items.add(new OverlayItem(new GeoPoint( latitude.intValue() , 
							longitude.intValue()),
							locations[i][0],locations[i][1]+"\n"+locations[i][2]));
					
				//}
			}
			}catch(Exception e) {
				Log.i("Address not found ", e.toString());
			}
			
			/*int i = 0;
			for( GeoPoint point: points ){
				items.add(new OverlayItem(point,
					locations[i][0],locations[i][2]+"\n"+locations[i][3]+"\n"+
						point.getLatitudeE6()+"--"+point.getLongitudeE6()+"\n"+
						points.size()));
				i++;
			}*/
			
			/*items.add(new OverlayItem(getPoint(-1.2811737395587102, 36.815185546875),
					" ", "Nairobi, Kenya"));*/
			
			populate();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}
		
		//draw marker on the map
		@Override
		public void draw(Canvas canvas, MapView mapView,
											boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			boundCenterBottom(marker);
		}
 		
		@Override
		protected boolean onTap(int i) {
			Toast.makeText(IncidentMap.this,
			items.get(i).getSnippet(),
			Toast.LENGTH_SHORT).show();
			
			return(true);
		}
		
		@Override
		public int size() {
			return(items.size());
		}
	}
	
	private void plotIncidentsOnMap() {
		if(points.size() == 0 ){
			
			mHandler.post(mDisplayNetworkError);
		} else {
			for (int i = 0; i < addresses.size(); ++i) {
				foundAddresses = addresses.get(i);
				for( Address address: foundAddresses){
					
					lat = address.getLatitude();
					lon = address.getLongitude();
					latitude = (lat * 1000000);
					longitude = (lon * 1000000);
					points.add(new GeoPoint( latitude.intValue() , longitude.intValue() ) );
					
				}
			}
			setMap( points, incidents );
			//setMap(points,incidents);
		}
	}
	
	
	private void setMap( List<GeoPoint> points, String[][]locations ) {
		
		map = (MapView)findViewById(R.id.map);
		
		map.getController().setCenter(points.get(0));
		
		map.getController().setZoom(17);
		
		ViewGroup zoom=(ViewGroup)findViewById(R.id.zoom);
		
		zoom.addView(map.getZoomControls());
		
		
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		
		//map.getOverlays().add(new SitesOverlay(marker));
		map.getOverlays().add(new SitesOverlay(marker,points,incidents));

	}
	
	private void geoCodeLocations() {
		incidents = showIncidents("DEATHS");
		
		try {
			for( int i =0; i < incidents.length; i++ ) {	
				foundAddresses = gc.getFromLocationName( incidents[i][3]+",kenya", 5);
				for( int w =0; w < foundAddresses.size(); w++ ) {
					Address address = foundAddresses.get(w);
					
					lat = address.getLatitude();
					lon = address.getLongitude();
					latitude = (lat * 1000000);
					longitude = ( lon * 1000000 );
					//latitude = (-1.2811737395587102 * 1000000);
					//longitude = ( 36.815185546875 * 1000000);
					points.add(new GeoPoint( latitude.intValue() , longitude.intValue() ) );
					
				}
			}
		} catch (Exception e) {
		
			// TODO Auto-generated catch block
			Log.i("Address Not Found", e.toString());
		}
	}
}