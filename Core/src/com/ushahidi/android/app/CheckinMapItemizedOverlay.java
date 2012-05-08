package com.ushahidi.android.app;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.views.BalloonOverlayView;
import com.ushahidi.android.app.views.CheckinMapBallonOverlayView;
import com.ushahidi.android.app.views.ReportMapBallonOverlayView;

public class CheckinMapItemizedOverlay<Item extends OverlayItem> extends
		MapItemizedOverlay<CheckinMapOverlayItem> {

	private ArrayList<CheckinMapOverlayItem> items = new ArrayList<CheckinMapOverlayItem>();

	private Activity mActivity;

	private Drawable marker;

	public CheckinMapItemizedOverlay(Drawable marker, MapView mapView,
			Activity activity) {
		super(boundCenter(marker), mapView);
		mActivity = activity;
		this.marker = marker;
	}

	public void addOverlay(CheckinMapOverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected CheckinMapOverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CheckinMapOverlayItem item) {

		ReportMapBallonOverlayView.viewReports(index, item.getFilterCategory());

		return true;
	}

	@Override
	protected BalloonOverlayView<CheckinMapOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CheckinMapBallonOverlayView<CheckinMapOverlayItem>(
				getMapView().getContext(), getBalloonBottomOffset(), mActivity);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

}
