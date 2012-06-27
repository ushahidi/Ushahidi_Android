package com.ushahidi.android.app.ui.tablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.helpers.LocalActivityManagerFragment;
import com.ushahidi.android.app.ui.phone.ReportMapActivity;

public class ReportTabFragment extends LocalActivityManagerFragment {

	private TabHost mTabHost;

	private static final String TAG_LIST_REPORT = "list";

	private static final String TAG_MAP_REPORT = "map";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.tabs_report, container, false);
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup(getLocalActivityManager());
		listListTab();
		listMapTab();
		return view;
	}


	private void listListTab() {
		if (mTabHost != null) {
			addTab(TAG_LIST_REPORT, R.drawable.list, ListReportFragmentActivity.class);

		}
	}

	private void listMapTab() {
		if (mTabHost != null) {
			addTab(TAG_MAP_REPORT, R.drawable.map, ReportMapActivity.class);
		}
	}

	private void addTab(String indicator, int drawableId, Class<?> c) {
		TabHost tabHost = mTabHost;
		TabHost.TabSpec spec = tabHost.newTabSpec(indicator);
		Intent intent = new Intent(getActivity(), c);
		View tabIndicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab_indicator, tabHost.getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	static ReportTabFragment newInstance() {
		ReportTabFragment f = new ReportTabFragment();

		return f;
	}
}
