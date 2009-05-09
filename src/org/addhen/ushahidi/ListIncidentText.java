package org.addhen.ushahidi;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ListIncidentText {
	private String title;
	private String date;
	private String status;
	private int id;
	private Drawable thumbnail;
	private Uri thumbnailUri;
	private boolean isSelectable;
	
	public ListIncidentText(Drawable thumbnail, String title, String date, String status, int id) {
		this.thumbnail = thumbnail;
		this.title = title;
		this.date = date;
		this.status = status;
		this.id = id;
	}
	
	public ListIncidentText(Uri uri, String title, String date, String status, int id) {
		this.thumbnailUri = uri;
		this.title = title;
		this.date = date;
		this.status = status;
		this.id = id;
	}
	
	public boolean isSelectable() {
		return isSelectable;
	}
	
	public void setSelectable( boolean selectable ){
		isSelectable = selectable;
	}
	
	public void setThumbnail( Drawable thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public Drawable getThumbnail() {
		return this.thumbnail;
	}
	
	public void setThumbnailUri( Uri uri ) {
		this.thumbnailUri = uri;
	}
	
	public Uri getThumbnailUri() {
		return this.thumbnailUri;
	}
	
	public void setTitle( String title ) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setDate( String date ) {
		this.date = date;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public void setStatus( String status ) {
		this.status = status;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setId( int id ) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
}
