package com.ushahidi.android.app;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CheckinMapOverlayItem extends OverlayItem {

	protected String mImage;

	protected long mId;

	protected int  mFilterUserId;

	public CheckinMapOverlayItem(GeoPoint point, String title, String snippet,
			String image, long id, int filterUserId) {
		super(point, title, snippet);
		this.mImage = image;
		this.mId = id;
		this. mFilterUserId = filterUserId;
	}

	public String getImage() {
		return mImage;
	}

	public void setImage(String image) {
		this.mImage = image;
	}

	public long getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public int getFilterUserId() {
		return this.mFilterUserId;
	}

	public void setFilterCategory(int filterUserId) {
		this. mFilterUserId = filterUserId;
	}

}
