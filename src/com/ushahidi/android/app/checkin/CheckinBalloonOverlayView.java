
package com.ushahidi.android.app.checkin;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.data.IncidentsData;

public class CheckinBalloonOverlayView extends FrameLayout {
    private LinearLayout layout;

    private TextView title;

    private TextView snippet;

    private TextView readmore;
    /**
     * Create a new BalloonOverlayView.
     * 
     * @credits - http://github.com/jgilfelt/android-mapviewballoons/
     * @param context - The activity context.
     * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
     *            when rendering this view.
     * @author Jeff Gilfelt
     */
    public CheckinBalloonOverlayView(final CheckinMap iMap, final Context context,
            final int balloonBottomOffset, final int index) {

        super(context);

        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_map_overlay, layout);
        title = (TextView)v.findViewById(R.id.balloon_item_title);
        snippet = (TextView)v.findViewById(R.id.balloon_item_snippet);
        readmore = (TextView)v.findViewById(R.id.balloon_item_readmore);
        readmore.setVisibility(GONE);
        ImageView close = (ImageView)v.findViewById(R.id.close_img_button);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                layout.setVisibility(GONE);
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;

        addView(layout, params);

    }

    /**
     * Sets the view data from a given overlay item.
     * 
     * @param item - The overlay item containing the relevant view data (title
     *            and snippet).
     */
    public void setData(OverlayItem item) {

        layout.setVisibility(VISIBLE);
        if (item.getTitle() != null) {
            title.setVisibility(VISIBLE);
            title.setText(item.getTitle());
        } else {
            title.setVisibility(GONE);
        }
        if (item.getSnippet() != null) {
            snippet.setVisibility(VISIBLE);
            snippet.setText(item.getSnippet());
        } else {
            snippet.setVisibility(GONE);
        }

    }

}
