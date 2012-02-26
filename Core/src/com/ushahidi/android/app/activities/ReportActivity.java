package com.ushahidi.android.app.activities;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ViewReportFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReportActivity extends Fragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
     /* ViewReportFragment details
        =(ViewReportFragment)getSupportFragmentManager()
                              .findFragmentById(R.id.view_fragment_reports);*/
      
      //details.loadUrl(getIntent().getStringExtra(EXTRA_URL));                           
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_report_details, null);
    }

}
