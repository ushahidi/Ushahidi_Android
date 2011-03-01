package com.ushahidi.android.app.checkin;

import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 2/17/11
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckinItemizedOverlay extends ItemizedOverlay{
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public CheckinItemizedOverlay(Drawable drawable) {
        super(boundCenterBottom(drawable));
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
}
