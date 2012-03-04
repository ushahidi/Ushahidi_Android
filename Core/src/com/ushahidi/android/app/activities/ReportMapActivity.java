package com.ushahidi.android.app.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;

import com.ushahidi.android.app.R;

public class ReportMapActivity extends FragmentMapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.report_map);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

}
