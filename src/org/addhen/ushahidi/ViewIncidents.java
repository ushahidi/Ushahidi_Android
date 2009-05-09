package org.addhen.ushahidi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewIncidents extends Activity {
	private ImageView mImageView;
    private TextView title;
    private TextView body;
    private TextView date;
    private TextView location;
    private TextView category;
    private TextView status;
    
    private Bundle incidents = new Bundle();
    private String URL;
    private final String PREFS_NAME = "Ushahidi";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.view_incidents);
        
        mImageView = (ImageView) findViewById(R.id.img);
        
        title = (TextView) findViewById(R.id.title);
        title.setTextColor(Color.BLUE);
		title.setText("Uhuru Fires still strong - Uhuru, Kenay");
        
        category = (TextView) findViewById(R.id.category);
        category.setTextColor(Color.BLACK);
        category.setText("Category: DEATH, PROPERTY LOSS");
        
        
        date = (TextView) findViewById(R.id.date);
        date.setTextColor(Color.BLACK);
        date.setText("Date:2009-04-17");
        
        
        location = (TextView) findViewById(R.id.location);
        location.setTextColor(Color.BLACK);
        location.setText("Uhuru, Kenya");
        
        body = (TextView) findViewById(R.id.webview);
        body.setTextColor(Color.BLACK);
        body.setText("On top of that, like what happened in Gaza, is that you can get the OSM community to help build out more detailed maps as you go along, something none of the big mapping suppliers (Google, MSFT, Yahoo) can/will do. When you get a chance, compare the OSM view of Gaza to the one done by MSFT and Google.");
        
        status = (TextView) findViewById( R.id.status);
        status.setTextColor(Color.GREEN);
        status.setText("VERIFIED");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	this.setURL( settings.getString("Domain", "") );
        
        Bundle extras = getIntent().getExtras();
        
        mImageView = (ImageView) findViewById(R.id.img);
        
        Drawable drawable = getResources().getDrawable(R.drawable.ushahidi_globe); 
        
        mImageView.setImageDrawable(
        	drawable
        );
       
          
        Gallery g = (Gallery) findViewById(R.id.gallery);
        
        g.setAdapter(new ImageAdapter(this) );
        
        
        
    }
    
    public void setURL( String URL ) {
		// set the directory where ushahidi photos are stored
		String photoDir = "/media/uploads/";
		this.URL = URL+photoDir;
	}
	
	public String getURL() {
		return this.URL;
	}
    
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
    
    public class ImageAdapter extends BaseAdapter {
        
    	public ImageAdapter( Context context ){
    		mContext = context;
    	}
    	public int getCount() {
    		return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageDrawable( 
					getResources().getDrawable( mImageIds[position] ) );
			
			i.setScaleType(ImageView.ScaleType.FIT_XY);
            
			i.setLayoutParams(new Gallery.LayoutParams(180, 88));

			return i;
		}
		
		private Context mContext;

        private Integer[] mImageIds = {
        		R.drawable.ushahidi_icon,
        		R.drawable.ushahidi_icon,
        		R.drawable.ushahidi_icon,
        		R.drawable.ushahidi_icon,
        };
    }
        
}

