package org.addhen.ushahidi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class LocationMap extends MapActivity {
	private MapView mapView = null;
	private MapController mapController;
	public static Geocoder gc;
	private GeoPoint defaultLocation;
	private double latitude;
	private double longitude;
	private List<IncidentsData> mNewIncidents;
	private List<IncidentsData> mOldIncidents;
	private EditText locationName;
	//private Button btnSearch;
	private Button btnBack;
	private Bundle bundle = new Bundle();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_map);
		
		mapView = (MapView) findViewById(R.id.location_map);
		locationName = (EditText) findViewById(R.id.location_name);
		
		//btnSearch = (Button) findViewById(R.id.btn_search);
		
		//centre map
		/*btnSearch.setOnClickListener( new View.OnClickListener(){
			public void onClick( View v ) {
				getLatLon( locationName.getText().toString());
			}
		});*/
		
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener( new View.OnClickListener(){
			public void onClick( View v ) {
				
				bundle.putDouble("latitude", latitude);
				bundle.putDouble("longitude", longitude);
				bundle.putString("location", locationName.getText().toString());
				
				Intent intent = new Intent( LocationMap.this,AddIncident.class);
				intent.putExtra("locations",bundle);
				startActivityForResult(intent,1);
				setResult( RESULT_OK, intent );
				finish();
			}
		});
		
		mapController = mapView.getController();
		
		mOldIncidents = new ArrayList<IncidentsData>();
		mNewIncidents  = showIncidents("All");
		
		latitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLatitude());
		longitude = Double.parseDouble( mNewIncidents.get(0).getIncidentLocLongitude());
		
		defaultLocation = getPoint( latitude, longitude);
		centerLocation(defaultLocation);
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void placeMarker( int markerLatitude, int markerLongitude ) {
		Drawable marker=getResources().getDrawable( R.drawable.marker);
		 
		 marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				 marker.getIntrinsicHeight());
		mapView.getController().setZoom(12);

		mapView.setBuiltInZoomControls(true);
		mapView.getOverlays().add(new MapMarker(marker,
				    markerLatitude, markerLongitude));
	}
	
	public GeoPoint getPoint(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
	
	private void centerLocation(GeoPoint centerGeoPoint) {
		mapController.animateTo(centerGeoPoint);

		/*locationName.setText("Longitude: "+
				String.valueOf((float)centerGeoPoint.getLongitudeE6()/1000000) + "Latitude: "+
				String.valueOf((float)centerGeoPoint.getLatitudeE6()/1000000));*/
		
		//myLatitude.setText("Latitude: "+
				//String.valueOf((float)centerGeoPoint.getLatitudeE6()/1000000));
		
		latitude = centerGeoPoint.getLongitudeE6()/1000000;
		longitude = centerGeoPoint.getLatitudeE6()/1000000;
		
		locationName.setText(getLocationName(centerGeoPoint.getLongitudeE6()/1000000 , 
				centerGeoPoint.getLongitudeE6()/1000000));
		
		placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());
	 }
	
	/**
	 * get the real location name from
	 * the latitude and longitude.
	 */
	private String getLocationName( double lat, double lon ) {
		//
	    //  Write the location name.
	    //
	    try {
	        Geocoder geo = new Geocoder(this, Locale.getDefault());
	        List<Address> addresses = geo.getFromLocation(lat, lon, 1);
	        if (addresses.size() > 0) {
	        	return addresses.get(0).getLocality();
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace(); 
	    }
		return "";
	}
	
	/**
	 * Get latitude and logitude
	 * @param String locName: the location name
	 * @return void
	 *
	private void getLatLon( String locName ) {
		double lat = 0;
		double lon = 0;
		try {
	        Geocoder geo = new Geocoder(this, Locale.getDefault());
	        List<Address> addresses = geo.getFromLocationName(locName, 1);
	        
	        if (addresses.size() > 0) {
	        	lat = addresses.get(0).getLatitude();
	        	lon = addresses.get(0).getLongitude();
	        	centerLocation(getPoint(lat,lon));
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace(); 
	    }
	    
	}*/
	
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
	
	private class MapMarker extends ItemizedOverlay<OverlayItem> {
		
		private List<OverlayItem> locations =new ArrayList<OverlayItem>();
		private Drawable marker;
		private OverlayItem myOverlayItem;
		private boolean MoveMap;
		
		public MapMarker( Drawable defaultMarker, int LatitudeE6, int LongitudeE6 ) {
			super(defaultMarker);
			
			this.marker = defaultMarker;
			
			// create locations of interest
			GeoPoint myPlace = new GeoPoint(LatitudeE6,LongitudeE6);
			
			myOverlayItem = new OverlayItem(myPlace, "Location ", "Location");
			
			locations.add(myOverlayItem);
			   
			populate();
			   
		}
		@Override
		protected OverlayItem createItem(int i) {
			return locations.get(i);
		}

		@Override
		public int size() {
			return locations.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView,
				boolean shadow) {
			super.draw(canvas, mapView, shadow);
		   
			boundCenterBottom(marker);
		}

		@Override
		public boolean onTouchEvent(MotionEvent motionEvent, MapView mapview) {
			
		   int Action = motionEvent.getAction();
		   if (Action == MotionEvent.ACTION_UP){
		 
		    if(!MoveMap)
		    {
		     Projection proj = mapView.getProjection();
		     GeoPoint loc = proj.fromPixels((int)motionEvent.getX(), (int)motionEvent.getY());
		              
		     //remove the last marker
		     mapView.getOverlays().remove(0);
		              
		     centerLocation(loc);
		    }
		    
		   }
		   else if (Action == MotionEvent.ACTION_DOWN){
		    
		    MoveMap = false;

		   }
		   else if (Action == MotionEvent.ACTION_MOVE){    
		    MoveMap = true;
		   }

		   return super.onTouchEvent(motionEvent, mapview);
		   //return false;
		  }
	}
}
