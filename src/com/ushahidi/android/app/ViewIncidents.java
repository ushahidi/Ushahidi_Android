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

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import java.util.Vector;

import com.ushahidi.android.app.R;

public class ViewIncidents extends Activity {
	private Button viewMap;
    private TextView title;
    private TextView body;
    private TextView date;
    private TextView location;
    private TextView category;
    private TextView status;
    private Bundle extras = new Bundle();
    private Bundle incidentsBundle = new Bundle();
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
        
        viewMap = (Button) findViewById(R.id.view_map);
        
        Bundle incidents = getIntent().getExtras();
        
        extras = incidents.getBundle("incidents");
        
        id = extras.getInt("id");
        reportTitle = extras.getString("title");
        reportDescription = extras.getString("desc");
        reportLatitude = extras.getString("latitude");
        reportLongitude = extras.getString("longitude");
        String iStatus = Util.toInt(extras.getString("status") ) == 0 ? "Unverified" : "Verified";
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(144, 80, 62));
		title.setText(extras.getString("title"));
        
        category = (TextView) findViewById(R.id.category);
        category.setTextColor(Color.BLACK);
        category.setText(extras.getString("category"));
        
        
        date = (TextView) findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText( Util.joinString("Date: ",extras.getString("date")));
        
        
        location = (TextView) findViewById(R.id.location);
        location.setTextColor(Color.BLACK);
        location.setText(Util.joinString("Location: ", extras.getString("location")));
        
        body = (TextView) findViewById(R.id.webview);
        body.setTextColor(Color.BLACK);
        body.setText(extras.getString("desc"));
        
        status = (TextView) findViewById( R.id.status);
        status.setTextColor(Color.rgb(41, 142, 40));
        status.setText(iStatus);
    	
    	media = extras.getString("media");
    	
    	ImageAdapter imageAdapter = new ImageAdapter(this);
    	
    	if( !media.equals("")) {
    		
    		thumbnails = media.split(",");    	
    
        	for( int i = 0; i < thumbnails.length; i++ ) {
        		imageAdapter.mImageIds.add( ImageManager.getImages( thumbnails[i] ) );
        	}
    	}
        
        Gallery g = (Gallery) findViewById(R.id.gallery);
        
        g.setAdapter( imageAdapter );
        
        viewMap.setOnClickListener( new View.OnClickListener() {  
            
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
          
		});
        
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
        
}

