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
	private TextView date;
	private TextView status;
	private ImageView thumbnail;
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
		
		this.textLayout.setLayoutParams(
				new LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)
		);
		
		this.thumbnail = new ImageView(context);
		
		this.thumbnail.setImageURI( listText.getThumbnailUri() );
		
		this.thumbnail.setImageDrawable( listText.getThumbnail() );
		
		thumbnail.setPadding(2, 2, 10, 4);
		thumbnail.setLayoutParams(new LayoutParams(48,48));
		
		addView(thumbnail, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		
		title = new TextView( context);
		title.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		title.setTextColor(Color.rgb(144, 80, 62));
		title.setTextSize(fontSize);
		
		title.setTypeface(Typeface.DEFAULT_BOLD);
		//title.setPadding(5, 5, 10, 2);
		title.setText( listText.getTitle() );
		
		textLayout.addView(title, 0);
		
		date = new TextView( context);
		date.setTextColor(Color.BLACK);
		date.setLayoutParams( new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		date.setText( listText.getDate() );
		
		textLayout.addView( date, 1 );
		
		status = new TextView( context);
		status.setTextColor(Color.rgb(41, 142, 40));
		status.setTextSize(fontSize);
		status.setText( listText.getStatus() );
		
		
		textLayout.addView( status, 2);
		
		addView( textLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		this.id = listText.getId();
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
	
	public void setId( int id ) {
		this.id = id;
	}
}
