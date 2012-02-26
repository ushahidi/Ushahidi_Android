/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.ListReportListFragment;
import com.ushahidi.android.app.fragments.ListReportListFragmentListener;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.views.ViewReportView;

/**
 * @author eyedol
 */
public class ViewReportActivity extends BaseActivity<ViewReportView> implements
        ListReportListFragmentListener {

    private boolean detailsInline = false;

    protected ViewReportActivity() {
        super(ViewReportView.class, R.layout.list_report_details, R.menu.view_report);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListReportListFragment reports = (ListReportListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_reports_list);

        reports.setListReportListListener(this);

        Fragment f = getSupportFragmentManager().findFragmentById(
                R.id.view_reports_details_fragment);

        detailsInline = (f != null && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));

        if (detailsInline) {
            reports.enablePersistentSelection();
        } else if (f != null) {
            f.getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onReportSelected(ListReportModel report) {
        Intent i = new Intent(this, ReportActivity.class);

        // i.putExtra(DetailsActivity.EXTRA_URL, url);
        startActivity(i);
    }
}
