package org.addhen.ushahidi;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
	private TableLayout textLayout;
	public ListIncidentTextView( Context context, ListIncidentText listText ) {
		super(context);
		
		this.setOrientation(HORIZONTAL);
		//this.setBackgroundColor(R.color.light_yellow);
		this.initComponent( context, listText);
		
	}
	
	public void initComponent( Context context, ListIncidentText listText ) {
		this.textLayout = new TableLayout(context);
		this.textLayout.setPadding(5, 5, 10, 2);
		this.textLayout.setLayoutParams(
				new LayoutParams(
						LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.FILL_PARENT)
		);
		
		this.thumbnail = new ImageView(context);
		
		this.thumbnail.setImageURI( listText.getThumbnailUri() );
		
		this.thumbnail.setImageDrawable( listText.getThumbnail() );
		
		thumbnail.setPadding(5, 8, 5, 2);
		
		addView(thumbnail, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		
		title = new TextView( context);
		title.setTextColor(R.color.wine);
		title.setTextSize(fontSize);
		//title.setPadding(5, 5, 10, 2);
		title.setText( listText.getTitle() );
		
		textLayout.addView(title, 0);
		
		date = new TextView( context);
		date.setTextColor(Color.BLACK);
		date.setText( listText.getDate() );
		
		textLayout.addView( date, 1 );
		
		status = new TextView( context);
		status.setTextColor(new ColorStateList( 
				new int[][] { 
						new int[] { android.R.attr.state_selected }, 
						new int[0], 
						}, new int[] { 
						R.color.deep_green, 
						R.color.deep_green, 
						} 
						));
		status.setTextSize(fontSize);
		//status.setPadding(5, 5, 10, 2);
		status.setText( listText.getStatus() );
		
		
		textLayout.addView( status, 2);
		
		addView( textLayout, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
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
