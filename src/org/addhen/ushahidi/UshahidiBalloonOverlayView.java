package org.addhen.ushahidi;

import java.util.List;

import org.addhen.ushahidi.data.IncidentsData;
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


public class UshahidiBalloonOverlayView extends FrameLayout {
	private LinearLayout layout;
	private TextView title;
	private TextView readmore;
	private TextView snippet;
	private Bundle incidentsBundle = new Bundle();
	private static final int VIEW_INCIDENT = 1;
	
	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @credits - http://github.com/jgilfelt/android-mapviewballoons/
	 * 
	 * @param context - The activity context.
	 * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
	 * when rendering this view.
	 * 
	 * @author Jeff Gilfelt
	 */
	public UshahidiBalloonOverlayView( final IncidentMap iMap,
			final Context context, 
			final int balloonBottomOffset,
			final List<IncidentsData> mNewIncidents, 
			final int index,
			final Bundle extras) {

		super(context);

		setPadding(10, 0, 10, balloonBottomOffset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_map_overlay, layout);
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		readmore = (TextView) v.findViewById(R.id.balloon_item_readmore);
		
		
		readmore.setText("Read more..");
		readmore.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {
				
				if( extras != null ) {
					
					incidentsBundle.putString("title",extras.getString("title"));
					incidentsBundle.putString("desc", extras.getString("desc"));
					incidentsBundle.putString("category", extras.getString("category"));
					incidentsBundle.putString("location", extras.getString("location"));
					incidentsBundle.putString("date", extras.getString("date"));
					incidentsBundle.putString("media", extras.getString("media"));
					incidentsBundle.putString("status", ""+extras.getString("status"));
					
				} else {
					incidentsBundle.putString("title",mNewIncidents.get(index).getIncidentTitle());
					incidentsBundle.putString("desc", mNewIncidents.get(index).getIncidentDesc());
					incidentsBundle.putString("category", mNewIncidents.get(index).getIncidentCategories());
					incidentsBundle.putString("location", mNewIncidents.get(index).getIncidentLocation());
					incidentsBundle.putString("date", mNewIncidents.get(index).getIncidentDate());
					incidentsBundle.putString("media", mNewIncidents.get(index).getIncidentMedia());
					incidentsBundle.putString("status", ""+mNewIncidents.get(index).getIncidentVerified());
				}
	        	Intent intent = new Intent( context, ViewIncidents.class);
				intent.putExtra("incidents", incidentsBundle);
				
				iMap.startActivityForResult(intent,VIEW_INCIDENT);
				iMap.setResult(iMap.RESULT_OK );
				
			}
		} );
		
		ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout.setVisibility(GONE);
			}
		});

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);

	}
	
	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item - The overlay item containing the relevant view data 
	 * (title and snippet). 
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
