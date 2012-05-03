package com.ushahidi.android.app;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.views.BalloonOverlayView;
import com.ushahidi.android.app.views.ReportMapBallonOverlayView;

public class ReportMapItemizedOverlay<Item extends OverlayItem> extends
		MapItemizedOverlay<ReportMapOverlayItem> {

	private ArrayList<ReportMapOverlayItem> items = new ArrayList<ReportMapOverlayItem>();

	private Activity mActivity;

	private Drawable marker;

	public ReportMapItemizedOverlay(Drawable marker, MapView mapView,
			Activity activity) {
		super(boundCenter(marker), mapView);
		mActivity = activity;
		this.marker = marker;
	}

	public void addOverlay(ReportMapOverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected ReportMapOverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	protected boolean onBalloonTap(int index, ReportMapOverlayItem item) {

		ReportMapBallonOverlayView.viewReports(index, item.getFilterCategory());

		return true;
	}

	@Override
	protected BalloonOverlayView<ReportMapOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new ReportMapBallonOverlayView<ReportMapOverlayItem>(
				getMapView().getContext(), getBalloonBottomOffset(), mActivity);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

}
