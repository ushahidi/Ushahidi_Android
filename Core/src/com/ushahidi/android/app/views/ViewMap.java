package com.ushahidi.android.app.views;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class ViewMap extends View {

    private TextView title;
    private TextView snippet;
    private ImageView image;
    public ViewMap(MapActivity activity) {
        super(activity);
        
    }
}
