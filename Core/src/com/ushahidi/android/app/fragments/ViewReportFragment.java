
package com.ushahidi.android.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ViewReportModel;

public class ViewReportFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private TextView title;

    private TextView body;

    private TextView date;

    private TextView location;

    private TextView category;

    private TextView status;

    private TextView photos;

    private MapView mapView;

    public ViewReportFragment() {
        //super(R.layout.view_report);

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
