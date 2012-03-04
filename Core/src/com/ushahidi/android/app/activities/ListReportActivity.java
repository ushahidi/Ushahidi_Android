package com.ushahidi.android.app.activities;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ViewReportFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListReportActivity extends FragmentActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.list_report_tab);                          
    }
    
}
