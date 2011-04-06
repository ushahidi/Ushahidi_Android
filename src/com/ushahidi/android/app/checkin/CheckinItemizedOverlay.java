
package com.ushahidi.android.app.checkin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.data.IncidentsData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/17/11 Time: 2:45 PM To change
 * this template use File | Settings | File Templates.
 */
public class CheckinItemizedOverlay<Item> extends ItemizedOverlay<OverlayItem> {
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    private MapView mapView;

    private CheckinBalloonOverlayView balloonView;

    private CheckinMap iMap;

    private List<Checkin> mCheckins;

    private Bundle extras;

    private int viewOffset;

    private View clickRegion;

    private MapController mc = null;

    public CheckinItemizedOverlay(Drawable drawable) {
        super(boundCenter(drawable));
    }

    public CheckinItemizedOverlay(Drawable marker, MapView mapView, CheckinMap iMap,
            List<Checkin> checkins, Bundle extras) {
        super(marker);

        this.mapView = mapView;
        this.viewOffset = 32;
        this.iMap = iMap;
        this.mc = mapView.getController();
        this.mCheckins = checkins;
        this.extras = extras;
    }

    /**
     * Set the horizontal distance between the marker and the bottom of the
     * information balloon. The default is 0 which works well for center bounded
     * markers. If your marker is center-bottom bounded, call this before adding
     * overlay items to ensure the balloon hovers exactly above the marker.
     * 
     * @param pixels - The padding between the center point and the bottom of
     *            the information balloon.
     */
    public void setBalloonBottomOffset(int pixels) {
        viewOffset = pixels;
    }

    /**
     * Override this method to handle a "tap" on a balloon. By default, does
     * nothing and returns false.
     * 
     * @param index - The index of the item whose balloon is tapped.
     * @return true if you handled the tap, otherwise false.
     */
    protected boolean onBalloonTap(int index) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#onTap(int)
     */
    @Override
    protected final boolean onTap(int index) {
        boolean isRecycled;
        final int thisIndex;
        GeoPoint point;
        thisIndex = index;
        point = createItem(index).getPoint();

        if (balloonView == null) {

            balloonView = new CheckinBalloonOverlayView(this.iMap, mapView.getContext(),
                    viewOffset, mCheckins, thisIndex, extras);
            clickRegion = balloonView.findViewById(R.id.balloon_inner_layout);
            isRecycled = false;

        } else {
            isRecycled = true;
        }

        balloonView.setVisibility(View.GONE);

        balloonView.setData(createItem(index));

        MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER);

        params.mode = MapView.LayoutParams.MODE_MAP;

        setBalloonTouchListener(thisIndex);
        balloonView.setVisibility(View.VISIBLE);

        if (isRecycled) {
            balloonView.setLayoutParams(params);
        } else {
            mapView.addView(balloonView, params);
        }

        mc.animateTo(point);
        return true;
    }

    /**
     * Sets the onTouchListener for the balloon being displayed, calling the
     * overridden onBalloonTap if implemented.
     * 
     * @param thisIndex - The index of the item whose balloon is tapped.
     */
    private void setBalloonTouchListener(final int thisIndex) {

        try {
            @SuppressWarnings("unused")
            Method m = this.getClass().getDeclaredMethod("onBalloonTap", int.class);

            clickRegion.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    View l = ((View)v.getParent()).findViewById(R.id.balloon_main_layout);
                    Drawable d = l.getBackground();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        int[] states = {
                            android.R.attr.state_pressed
                        };
                        if (d.setState(states)) {
                            d.invalidateSelf();
                        }
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        int newStates[] = {};
                        if (d.setState(newStates)) {
                            d.invalidateSelf();
                        }
                        // call overridden method
                        onBalloonTap(thisIndex);
                        return true;
                    } else {
                        return false;
                    }

                }
            });

        } catch (SecurityException e) {
            return;
        } catch (NoSuchMethodException e) {
            // method not overridden - do nothing
            return;
        }
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
