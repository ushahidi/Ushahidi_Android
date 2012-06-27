package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.MenuItem;

import com.ushahidi.android.app.R;

public class CheckinMapActivity extends FragmentMapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkin_map);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
