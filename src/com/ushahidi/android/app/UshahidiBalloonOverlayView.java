
package com.ushahidi.android.app;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.data.IncidentsData;

public class UshahidiBalloonOverlayView extends FrameLayout {
    private LinearLayout layout;

    private TextView title;

    private TextView readmore;

    private TextView snippet;

    private Bundle incidentsBundle = new Bundle();

    private List<IncidentsData> mNewIncidents;

    private IncidentMap mMap;

    private Context mContext;

    private static final int VIEW_INCIDENT = 1;

    // public int index = 0;

    /**
     * Create a new BalloonOverlayView.
     * 
     * @credits - http://github.com/jgilfelt/android-mapviewballoons/
     * @param context - The activity context.
     * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
     *            when rendering this view.
     * @author Jeff Gilfelt
     */
    public UshahidiBalloonOverlayView(final IncidentMap iMap, final Context context,
            final int balloonBottomOffset, final List<IncidentsData> incidentsData,
            final int thisIndex) {

        super(context);

        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        mNewIncidents = incidentsData;
        mMap = iMap;
        mContext = context;

        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_map_overlay, layout);
        title = (TextView)v.findViewById(R.id.balloon_item_title);
        snippet = (TextView)v.findViewById(R.id.balloon_item_snippet);
        readmore = (TextView)v.findViewById(R.id.balloon_item_readmore);
        
        readmore.setText(context.getString(R.string.read_more));

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

    private void viewReports(final int index) {
        readmore.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                incidentsBundle.putString("title", mNewIncidents.get(index).getIncidentTitle());
                incidentsBundle.putString("desc", mNewIncidents.get(index).getIncidentDesc());
                incidentsBundle.putString("category", mNewIncidents.get(index)
                        .getIncidentCategories());
                incidentsBundle.putString("location", mNewIncidents.get(index)
                        .getIncidentLocation());
                incidentsBundle.putString("latitude", mNewIncidents.get(index)
                        .getIncidentLocLatitude());
                incidentsBundle.putString("longitude", mNewIncidents.get(index)
                        .getIncidentLocLongitude());
                incidentsBundle.putString("date", mNewIncidents.get(index).getIncidentDate());
                incidentsBundle.putString("media", mNewIncidents.get(index).getIncidentThumbnail());
                incidentsBundle.putString("image", mNewIncidents.get(index).getIncidentImage());
                incidentsBundle.putString("status", ""
                        + mNewIncidents.get(index).getIncidentVerified());

                Intent intent = new Intent(mContext, ViewIncidents.class);
                intent.putExtra("incidents", incidentsBundle);

                mMap.startActivityForResult(intent, VIEW_INCIDENT);
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
    public void setData(OverlayItem item, final int i) {
        viewReports(i);
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
