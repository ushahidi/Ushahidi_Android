package com.ushahidi.android.app.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.CheckinMapOverlayItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.ui.phone.ViewCheckinActivity;
import com.ushahidi.android.app.util.ImageViewWorker;

public class CheckinMapBallonOverlayView<Item extends OverlayItem> extends
		BalloonOverlayView<CheckinMapOverlayItem> {

	private TextView title;

	private TextView snippet;

	private static TextView readMore;

	private ImageView image;

	private static Activity mActivity;

	public CheckinMapBallonOverlayView(Context context,
			int balloonBottomOffset, Activity activity) {
		super(context, balloonBottomOffset);
		mActivity = activity;
	}

	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		android.view.View v = inflater.inflate(R.layout.map_balloon_overlay,
				parent);

		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		readMore = (TextView) v.findViewById(R.id.balloon_item_readmore);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);
	}

	public static void viewCheckins(final int id, final int filterUserId) {
		launchViewCheckin(id, filterUserId);

		readMore.setOnClickListener(new OnClickListener() {
			public void onClick(android.view.View view) {
				launchViewCheckin(id, filterUserId);
				
			}
		});

	}

	@Override
	protected void setBalloonData(CheckinMapOverlayItem item, ViewGroup parent) {

		title.setText(item.getTitle());
		snippet.setText(item.getSnippet());
		if (item.getImage() == null) {

			image.setImageResource(R.drawable.report_icon);
					
		} else {
			getPhoto(item.getImage(), image);

		}
	}

	private static void launchViewCheckin(int position, final int filterUserId) {
		Intent i = new Intent(mActivity, ViewCheckinActivity.class);
		i.putExtra("id", position);
		if (filterUserId > 0) {
			i.putExtra("userid", filterUserId);
		} else {
			i.putExtra("userid", 0);
		}
		mActivity.startActivityForResult(i, 0);
		mActivity
				.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);

	}
	
	private void getPhoto(String fileName, ImageView imageView) {
		ImageViewWorker imageWorker = new ImageViewWorker(mActivity);
		imageWorker.setImageFadeIn(true);
		imageWorker.loadImage(fileName, imageView, true, 0);
	}

}
