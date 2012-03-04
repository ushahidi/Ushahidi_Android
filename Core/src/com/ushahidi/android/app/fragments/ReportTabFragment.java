
package com.ushahidi.android.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.ListReportActivity;
import com.ushahidi.android.app.activities.ReportMapActivity;
import com.ushahidi.android.app.helpers.LocalActivityManagerFragment;

public class ReportTabFragment extends LocalActivityManagerFragment {

    private TabHost mTabHost;

    private static final String TAG_LIST_REPORT = "list";

    private static final String TAG_MAP_REPORT = "map";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabs_report, container, false);
        mTabHost = (TabHost)view.findViewById(android.R.id.tabhost);

        mTabHost.setup(getLocalActivityManager());

        listListTab();
        listMapTab();
        return view;
    }

    private void listListTab() {
        if (mTabHost != null) {
            mTabHost.addTab(mTabHost.newTabSpec(TAG_LIST_REPORT)
                    .setIndicator(getActivity().getString(R.string.reports))
                    .setContent(new Intent(getActivity(), ListReportActivity.class)));
        }
    }

    private void listMapTab() {
        if (mTabHost != null) {
            mTabHost.addTab(mTabHost.newTabSpec(TAG_MAP_REPORT)
                    .setIndicator(getActivity().getString(R.string.map))
                    .setContent(new Intent(getActivity(), ReportMapActivity.class)));
        }
    }

}
