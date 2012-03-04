
package com.ushahidi.android.app.fragments;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.ReportMapItemizedOverlay;
import com.ushahidi.android.app.ReportMapOverlayItem;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.util.Util;

public class MapFragment<ReportMapItemOverlay> extends BaseFragment {

    private MapView map = null;

    private ListReportModel mListReportModel;

    List<ListReportModel> mReportModel;

    private ReportMapItemizedOverlay<ReportMapOverlayItem> itemOverlay;

    private Handler mHandler;

    private static double latitude;

    private static double longitude;

    private int id = 1;

    private String filterCategory = null;

    public MapFragment() {
        super(R.menu.map_report);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return (new FrameLayout(getActivity()));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListReportModel = new ListReportModel();
        mListReportModel.load(getActivity());
        mReportModel = mListReportModel.getReports(getActivity());
        mHandler = new Handler();
        map = new MapView(getActivity(), getActivity().getString(R.string.google_map_api_key));
        if (mReportModel.size() > 0) {
            if (id > 0) {
                if (!Preferences.deploymentLatitude.equals("0.0")
                        && !Preferences.deploymentLatitude.equals("0.0")) {
                    MapFragment.latitude = Double.parseDouble(Preferences.deploymentLatitude);
                    MapFragment.longitude = Double.parseDouble(Preferences.deploymentLongitude);

                } else {
                    // MapFragment.latitude =
                    // Double.parseDouble(reportLatitude);
                    // MapFragment.longitude =
                    // Double.parseDouble(reportLongitude);

                }
            } else {
                if (!Preferences.deploymentLatitude.equals("0.0")
                        && !Preferences.deploymentLatitude.equals("0.0")) {
                    MapFragment.latitude = Double.parseDouble(Preferences.deploymentLatitude);
                    MapFragment.longitude = Double.parseDouble(Preferences.deploymentLongitude);

                } else {
                    MapFragment.latitude = Double.parseDouble(mReportModel.get(0).getLatitude());
                    MapFragment.longitude = Double.parseDouble(mReportModel.get(0).getLongitude());
                }

            }

            map.setClickable(true);
            map.getController().setCenter(getPoint(MapFragment.latitude, MapFragment.longitude));
            map.setBuiltInZoomControls(true);
            mHandler.post(mMarkersOnMap);
            ((ViewGroup)getView()).addView(map);

        } else {
            toastLong(R.string.no_reports);
        }

    }

    /**
     * Restart the receiving, when we are back on line.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mReportModel.size() == 0) {
            mHandler.post(mMarkersOnMap);
        }
    }

    // put this stuff in a seperate thread
    final Runnable mMarkersOnMap = new Runnable() {
        public void run() {
            populateMap();
        }
    };

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    /**
     * add marker to the map
     */
    public void populateMap() {
        Drawable marker = getResources().getDrawable(R.drawable.map_marker_red);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        itemOverlay = new ReportMapItemizedOverlay<ReportMapOverlayItem>(marker, map, getActivity());
        if (mReportModel != null) {
            for (ListReportModel reportModel : mReportModel) {
                itemOverlay.addOverlay(new ReportMapOverlayItem(getPoint(
                        Double.valueOf(reportModel.getLatitude()),
                        Double.valueOf(reportModel.getLongitude())), reportModel.getTitle(), Util
                        .limitString(reportModel.getDesc(), 30), reportModel.getThumbnail(),
                        reportModel.getId(), filterCategory));
            }
        }
        map.getOverlays().add(itemOverlay);
    }

}
