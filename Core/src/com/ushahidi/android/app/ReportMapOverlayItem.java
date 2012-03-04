
package com.ushahidi.android.app;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class ReportMapOverlayItem extends OverlayItem {

    protected Drawable mImage;

    protected long mId;

    protected String mFilterCategory;

    public ReportMapOverlayItem(GeoPoint point, String title, String snippet, Drawable image,
            long id, String filterCategory) {
        super(point, title, snippet);
        this.mImage = image;
        this.mId = id;
        this.mFilterCategory = filterCategory;
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable image) {
        this.mImage = image;
    }
    
    public long getId(){
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
