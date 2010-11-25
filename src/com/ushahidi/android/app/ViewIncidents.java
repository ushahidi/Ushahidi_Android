/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.R;

public class ViewIncidents extends MapActivity {
	private MapView mapView;
	private MapController mapController;
	private GeoPoint defaultLocation;
    private TextView title;
    private TextView body;
    private TextView date;
    private TextView location;
    private TextView category;
    private TextView status;
    private TextView photos;
    private Bundle extras = new Bundle();
    //private Bundle incidentsBundle = new Bundle();
    private String media;
    private String thumbnails [];
    private int id;
    private String reportLatitude;
    private String reportLongitude; 
    private String reportTitle;
    private String reportDescription;
    private static final int VIEW_MAP = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.view_incidents);
        
        mapView = (MapView) findViewById(R.id.loc_map);
        
        Bundle incidents = getIntent().getExtras();
        
        extras = incidents.getBundle("incidents");
        
        id = extras.getInt("id");
        reportTitle = extras.getString("title");
        reportDescription = extras.getString("desc");
        reportLatitude = extras.getString("latitude");
        reportLongitude = extras.getString("longitude");
        String iStatus = Util.toInt(extras.getString("status") ) == 0 ? "No" : "Yes";
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        //title.setTextColor(Color.rgb(144, 80, 62));
		title.setText(extras.getString("title"));
        
        category = (TextView) findViewById(R.id.category);
        category.setTextColor(Color.BLACK);
        category.setText(extras.getString("category"));
        
        
        date = (TextView) findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText( extras.getString("date"));
        
        
        location = (TextView) findViewById(R.id.location);
        location.setTextColor(Color.BLACK);
        location.setText(extras.getString("location"));
        
        body = (TextView) findViewById(R.id.webview);
        body.setTextColor(Color.BLACK);
        body.setText(extras.getString("desc"));
        
        status = (TextView) findViewById( R.id.status);
        //status.setTextColor(Color.rgb(41, 142, 40));
        status.setText(iStatus);
    	
    	media = extras.getString("media");
    	
    	ImageAdapter imageAdapter = new ImageAdapter(this);
    	
    	if( !media.equals("")) {
    		
    		thumbnails = media.split(",");    	
    
        	for( int i = 0; i < thumbnails.length; i++ ) {
        		imageAdapter.mImageIds.add( ImageManager.getImages( thumbnails[i] ) );
        	}
    	} else {
    		photos = (TextView) findViewById(R.id.report_photo);
    		photos.setText("");
    	}
        
        Gallery g = (Gallery) findViewById(R.id.gallery);
        
        g.setAdapter( imageAdapter );
        
        /*viewMap.setOnClickListener( new View.OnClickListener() {  
            
        	public void onClick( View v ) {
				
				incidentsBundle.putInt("id", id);
				reportTitle = extras.getString("title");
		        reportDescription = extras.getString("desc");
		        reportLatitude = extras.getString("latitude");
		        reportLongitude = extras.getString("longitude");
		        
				incidentsBundle.putString("title",reportTitle);
				incidentsBundle.putString("desc", reportDescription);
				incidentsBundle.putString("longitude",reportLongitude);
				incidentsBundle.putString("latitude",reportLatitude);
				incidentsBundle.putString("category", extras.getString("category"));
				incidentsBundle.putString("location", extras.getString("location"));
				incidentsBundle.putString("date", extras.getString("date"));
				incidentsBundle.putString("media", extras.getString("media"));
				incidentsBundle.putString("status", extras.getString("status"));
				
				Intent intent = new Intent( ViewIncidents.this,IncidentMap.class);
				intent.putExtra("report", incidentsBundle);
				startActivityForResult(intent,VIEW_MAP);
				setResult( RESULT_OK, intent );
              
			}
          
		});*/
       
        mapController = mapView.getController();
        defaultLocation = getPoint( Double.parseDouble(reportLongitude), Double.parseDouble(reportLatitude));
		centerLocation(defaultLocation);
    }
    
	private void placeMarker( int markerLatitude, int markerLongitude ) {
		
		Drawable marker = getResources().getDrawable( R.drawable.marker);
		 
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				 marker.getIntrinsicHeight());
		mapView.getController().setZoom(14);

		mapView.setBuiltInZoomControls(true);
		mapView.getOverlays().add(new MapMarker(marker,
				    markerLatitude, markerLongitude));
	}
	
	public GeoPoint getPoint(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
	
	private void centerLocation(GeoPoint centerGeoPoint) {
		
		mapController.animateTo(centerGeoPoint);
		
		//initilaize latitude and longitude for them to be passed to the AddIncident Activity.
		//this.latitude = centerGeoPoint.getLatitudeE6() / 1.0E6;
		//this.longitude = centerGeoPoint.getLongitudeE6() / 1.0E6;
		
		placeMarker(centerGeoPoint.getLatitudeE6(), centerGeoPoint.getLongitudeE6());
	
	}
	
	private class MapMarker extends ItemizedOverlay<OverlayItem> {
		
		private List<OverlayItem> locations =new ArrayList<OverlayItem>();
		private Drawable marker;
		private OverlayItem myOverlayItem;
		private boolean MoveMap = false;
		private long timer;
		
		public MapMarker( Drawable defaultMarker, int LatitudeE6, int LongitudeE6 ) {
			super(defaultMarker);
			this.timer = 0;
			this.marker = defaultMarker;
			
			// create locations of interest
			GeoPoint myPlace = new GeoPoint(LatitudeE6 ,LongitudeE6);
			
			myOverlayItem = new OverlayItem(myPlace, " ", " ");
			
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
		
		
	}
	
    public class ImageAdapter extends BaseAdapter {
    	
    	public Vector<Drawable> mImageIds;
    	private Context mContext;
    	private int mGalleryItemBackground;
    	
    	public ImageAdapter( Context context ){
    		mContext = context;
    		mImageIds = new Vector<Drawable>();
    		
    		TypedArray a = obtainStyledAttributes(R.styleable.PhotoGallery);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.PhotoGallery_android_galleryItemBackground, 0);
            a.recycle();
    		
    	}
    	
    	public int getCount() {
    		return mImageIds.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageDrawable( mImageIds.get( position ) );
			
			i.setScaleType(ImageView.ScaleType.FIT_XY);
            
			i.setLayoutParams(new Gallery.LayoutParams(136, 88));
            
            // The preferred Gallery item background
            i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}
		
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
        
}

