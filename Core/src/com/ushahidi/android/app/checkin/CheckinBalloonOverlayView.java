
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

public class CheckinBalloonOverlayView extends FrameLayout {
    private LinearLayout layout;

    private TextView title;

    private TextView snippet;

    private TextView readmore;

    private Bundle checkinsBundle = new Bundle();
    
    private CheckinMap mMap;
    private Context mContext;
    private List<Checkin> checkins;

    private static final int VIEW_CHECKINS = 1;

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
            final int balloonBottomOffset, final List<Checkin> mCheckins, final int index) {

        super(context);

        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        mMap = iMap;
        mContext = context;
        checkins = mCheckins;
        
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_map_overlay, layout);
        title = (TextView)v.findViewById(R.id.balloon_item_title);
        snippet = (TextView)v.findViewById(R.id.balloon_item_snippet);
        readmore = (TextView)v.findViewById(R.id.balloon_item_readmore);
        readmore.setText(context.getString(R.string.read_more));
        // readmore.setVisibility(GONE);
      
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (checkins != null) {
                    checkinsBundle.putString("name", checkins.get(index).getName());
                    checkinsBundle.putString("message", checkins.get(index).getMsg());
                    checkinsBundle.putString("latitude", checkins.get(index).getLat());
                    checkinsBundle.putString("longitude", checkins.get(index).getLon());
                    checkinsBundle.putString("date", checkins.get(index).getDate());
                    checkinsBundle.putString("photo", checkins.get(index).getImage());
                }

                Intent intent = new Intent(context, CheckinView.class);
                intent.putExtra("checkins", checkinsBundle);
                iMap.startActivityForResult(intent, VIEW_CHECKINS);
                iMap.setResult(Activity.RESULT_OK);

                // Clear popup from the map.
                layout.setVisibility(GONE);

            }

        });

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
    
    private void viewReport(final int index) {
        readmore.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                if (checkins != null) {
                    checkinsBundle.putString("name", checkins.get(index).getName());
                    checkinsBundle.putString("message", checkins.get(index).getMsg());
                    checkinsBundle.putString("latitude", checkins.get(index).getLat());
                    checkinsBundle.putString("longitude", checkins.get(index).getLon());
                    checkinsBundle.putString("date", checkins.get(index).getDate());
                    checkinsBundle.putString("photo", checkins.get(index).getImage());
                }

                Intent intent = new Intent(mContext, CheckinView.class);
                intent.putExtra("checkins", checkinsBundle);
                mMap.startActivityForResult(intent, VIEW_CHECKINS);
                mMap.setResult(Activity.RESULT_OK);

                // Clear popup from the map.
                layout.setVisibility(GONE);
            }
        });
    }

    /**
     * Sets the view data from a given overlay item.
     * 
     * @param item - The overlay item containing the relevant view data (title
     *            and snippet).
     */
    public void setData(OverlayItem item, int i) {
        viewReport(i);
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
