package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.views.View;

public class ReportMapActivity<V extends View> extends BaseActivity<V> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createMenuDrawer(R.layout.report_map);
	}

}
