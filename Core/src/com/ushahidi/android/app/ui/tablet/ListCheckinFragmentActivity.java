package com.ushahidi.android.app.ui.tablet;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ushahidi.android.app.R;

public class ListCheckinFragmentActivity extends FragmentActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.list_checkin_tab); 
    }
    
}
