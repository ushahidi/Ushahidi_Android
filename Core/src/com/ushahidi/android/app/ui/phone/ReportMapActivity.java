package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ushahidi.android.app.R;

public class ReportMapActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_map);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
