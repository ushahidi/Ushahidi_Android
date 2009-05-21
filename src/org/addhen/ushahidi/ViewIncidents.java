package org.addhen.ushahidi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class ViewIncidents extends Activity {
	private ImageView mImageView;
    private TextView title;
    private TextView body;
    private TextView date;
    private TextView location;
    private TextView category;
    private TextView status;
    
    private Bundle extras = new Bundle();
    private String media;
    private String thumbnails [];
    private final String PREFS_NAME = "Ushahidi";
    private Drawable d;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.view_incidents);
        
        mImageView = (ImageView) findViewById(R.id.img);
        
        Bundle incidents = getIntent().getExtras();
        
        extras = incidents.getBundle("incidents");
        
        d = null;
        
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
        date.setText( Util.joinString("Date:",extras.getString("date")));
        
        
        location = (TextView) findViewById(R.id.location);
        location.setTextColor(Color.BLACK);
        location.setText(extras.getString("location"));
        
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
    
    		d = ImageManager.getImages( thumbnails[0]);
        
        	mImageView = (ImageView) findViewById(R.id.img);
        
        	for( int i = 0; i < thumbnails.length; i++ ) {
        		imageAdapter.mImageIds.add( ImageManager.getImages( thumbnails[i] ) );
        	}
    	}
        
        Drawable drawable = getResources().getDrawable(R.drawable.ushahidi_icon); 
        
        mImageView.setImageDrawable(
        	d == null ? drawable : d
        );
       
          
        Gallery g = (Gallery) findViewById(R.id.gallery);
        
        g.setAdapter( imageAdapter );
        
        
        
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
    	
    	public Vector<Drawable> mImageIds;
    	private Context mContext;
    	
    	public ImageAdapter( Context context ){
    		mContext = context;
    		mImageIds = new Vector<Drawable>();
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
            
			i.setLayoutParams(new Gallery.LayoutParams(180, 88));

			return i;
		}
		
    }
        
}

