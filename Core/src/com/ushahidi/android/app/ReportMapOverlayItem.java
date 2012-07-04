package com.ushahidi.android.app;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class ReportMapOverlayItem extends OverlayItem {

	protected String mImage;

	protected long mId;

	protected String mFilterCategory;

	public ReportMapOverlayItem(GeoPoint point, String title, String snippet,
			String image, long id, String filterCategory) {
		super(point, title, snippet);
		this.mImage = image;
		this.mId = id;
		this.mFilterCategory = filterCategory;
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

	public String getFilterCategory() {
		return mFilterCategory;
	}

	public void setFilterCategory(String filterCategory) {
		this.mFilterCategory = filterCategory;
	}

}
