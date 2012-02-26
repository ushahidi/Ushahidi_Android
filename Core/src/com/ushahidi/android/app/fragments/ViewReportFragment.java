
package com.ushahidi.android.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ViewReportModel;

public class ViewReportFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        ViewSwitcher.ViewFactory {

    private TextView title;

    private TextView body;

    private TextView date;

    private TextView location;

    private TextView category;

    private TextView status;

    private TextView photos;

    private MapView mapView;
    
    private ImageSwitcher mSwitcher;

    private ViewGroup mRootView;

    public ViewReportFragment() {
        //super(R.menu.view_report);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup)inflater.inflate(R.layout.view_report, null);
       // mapView = (MapView)mRootView.findViewById(R.id.loc_map);
        title = (TextView)mRootView.findViewById(R.id.title);
        category = (TextView)mRootView.findViewById(R.id.category);
        date = (TextView)mRootView.findViewById(R.id.date);
        location = (TextView)mRootView.findViewById(R.id.location);
        body = (TextView)mRootView.findViewById(R.id.webview);
        status = (TextView)mRootView.findViewById(R.id.status);
        photos = (TextView)mRootView.findViewById(R.id.report_photo);
        mSwitcher = (ImageSwitcher)mRootView.findViewById(R.id.switcher);
        return mRootView;
    }

    public static ViewReportFragment newInstance(int index) {
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);

        ViewReportFragment f = new ViewReportFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}
