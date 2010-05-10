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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
	private TableLayout tblLayout;
	private TableRow tblRow;
	
	public ListIncidentTextView( Context context, ListIncidentText listText ) {
		super(context);
		
		this.setOrientation(VERTICAL);
		this.initComponent( context, listText);
		
	}
	
	public void initComponent( Context context, ListIncidentText listText ) {
		this.textLayout = new LinearLayout(context);
		
		this.tblLayout = new TableLayout(context);
		
		this.tblLayout.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		this.tblLayout.setColumnStretchable(1, true);
		this.tblRow =  new TableRow(context);
		this.tblRow.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		textLayout.setOrientation(VERTICAL);
		textLayout.setPadding(0, 2, 0, 2);
		
		this.textLayout.setLayoutParams(
				new TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT)
		);
		
		this.thumbnail = new ImageView(context);
		
		this.thumbnail.setImageURI( listText.getThumbnailUri() );
		
		this.thumbnail.setImageDrawable( listText.getThumbnail() );
		
		thumbnail.setPadding(2, 2, 10, 4);
		thumbnail.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		tblRow.addView(thumbnail);
	
		
		title = new TextView( context);
		
		title.setTextColor(Color.rgb(144, 80, 62));
		title.setTextSize(fontSize);
		title.setSingleLine(false);
		title.setTypeface(Typeface.DEFAULT_BOLD);
		title.setPadding(0, 0, 2, 2);
		title.setText( listText.getTitle() );
		title.setLayoutParams( new TableRow.LayoutParams( 
				TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		textLayout.addView(title, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		iLocation = new TextView( context );
		iLocation.setTextColor(Color.BLACK);
		iLocation.setText(listText.getLocation());
		
		textLayout.addView(iLocation, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		
		date = new TextView( context);
		date.setTextColor(Color.BLACK);
		date.setLayoutParams( new LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, 
				TableRow.LayoutParams.WRAP_CONTENT));
		
		date.setText( listText.getDate() );
		
		textLayout.addView( date, new TableRow.LayoutParams( 
				TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT) );
		
		status = new TextView( context);
		
		//change colored to red if text is not Verified
		if( listText.getStatus() == "Verified" ) {
			status.setTextColor(Color.rgb(41, 142, 40));
		} else {
			status.setTextColor(Color.rgb(237, 0, 0));
		}
		
		status.setTextSize(fontSize);
		status.setText( listText.getStatus() );
		
		
		textLayout.addView( status, new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.FILL_PARENT));
		
		tblRow.addView( textLayout);
		
		this.id = listText.getId();
		
		this.arrow = new ImageView(context);
		
		this.arrow.setImageDrawable( listText.getArrow() );
		
		arrow.setPadding(2, 25, 10, 2);
		
		arrow.setLayoutParams(new TableRow.LayoutParams(
			TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		tblRow.addView(arrow);
		
		tblLayout.addView(tblRow);
		
		addView(tblLayout, new LinearLayout.LayoutParams(
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
