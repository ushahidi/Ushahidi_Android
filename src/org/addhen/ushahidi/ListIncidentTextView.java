package org.addhen.ushahidi;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class ListIncidentTextView extends LinearLayout{
	private TextView title;
	private TextView iLocation;
	private TextView date;
	private TextView status;
	private ImageView thumbnail;
	private ImageView arrow;
	private String description;
	private String categories;
	private String media;
	private String location;
	private int id;
	private float fontSize = 13.5f;
	private LinearLayout textLayout;
	public ListIncidentTextView( Context context, ListIncidentText listText ) {
		super(context);
		
		this.setOrientation(HORIZONTAL);
		this.initComponent( context, listText);
		
	}
	
	public void initComponent( Context context, ListIncidentText listText ) {
		this.textLayout = new LinearLayout(context);
		textLayout.setOrientation(VERTICAL);
		textLayout.setPadding(0, 2, 0, 2);
		this.textLayout.setLayoutParams(
				new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT)
		);
		
		this.thumbnail = new ImageView(context);
		
		this.thumbnail.setImageURI( listText.getThumbnailUri() );
		
		this.thumbnail.setImageDrawable( listText.getThumbnail() );
		
		thumbnail.setPadding(2, 2, 10, 4);
		thumbnail.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		addView(thumbnail, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		
		title = new TextView( context);
		title.setLayoutParams( new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		title.setTextColor(Color.rgb(144, 80, 62));
		title.setTextSize(fontSize);
		title.setSingleLine(false);
		title.setTypeface(Typeface.DEFAULT_BOLD);
		title.setText( listText.getTitle() );
		
		textLayout.addView(title, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		iLocation = new TextView( context );
		iLocation.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		iLocation.setTextColor(Color.BLACK);
		iLocation.setText(listText.getLocation());
		iLocation.setSingleLine(false);
		textLayout.addView(iLocation, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		date = new TextView( context);
		date.setTextColor(Color.BLACK);
		date.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		date.setText( listText.getDate() );
		date.setSingleLine(false);
		textLayout.addView( date, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) );
		
		status = new TextView( context);
		
		//change colored to red if text is not Verified
		if( listText.getStatus() == "Verified" ) {
			status.setTextColor(Color.rgb(41, 142, 40));
		} else {
			status.setTextColor(Color.rgb(237, 0, 0));
		}
		
		status.setTextSize(fontSize);
		status.setText( listText.getStatus() );
		
		textLayout.addView( status, new LayoutParams( LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		addView( textLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		this.id = listText.getId();
		
		this.arrow = new ImageView(context);
		
		this.arrow.setImageDrawable( listText.getArrow() );
		
		arrow.setPadding(20, 25, 2, 2);
		
		arrow.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		addView(arrow, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void setThumbnail( Drawable thumbnail) {
		this.thumbnail.setImageDrawable(thumbnail);
	}
	
	public void setThumbnailUri( Uri uri ) {
		this.thumbnail.setImageURI(uri);
	}
	
	public void setTitle( String title ) {
		this.title.setText(title);
	}
	
	public void setDate( String date ) {
		this.date.setText(date);
	}
	
	public void setStatus( String status ) {
		this.status.setText( status );
	}
	
	public void setDesc( String description ) {
		this.description = description;
	}
	
	public void setCategories( String categories ) {
		this.categories = categories;
	}
	
	public void setLocation( String location ) {
		this.location = location;
	}
	
	public void setMedia( String media ) {
		this.media = media;
	}
	
	public void setId( int id ) {
		this.id = id;
	}
	
	public void setArrow( Drawable arrow) {
		this.arrow.setImageDrawable(arrow);
	}
	
}