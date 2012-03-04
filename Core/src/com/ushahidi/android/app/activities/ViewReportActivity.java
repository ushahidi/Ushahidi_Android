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
import com.ushahidi.android.app.fragments.ListMapFragmentListener;
import com.ushahidi.android.app.fragments.ListReportFragment;
import com.ushahidi.android.app.views.ViewReportView;

/**
 * @author eyedol
 */
public class ViewReportActivity extends BaseActivity<ViewReportView> implements
        ListMapFragmentListener {

    private boolean detailsInline = false;

    protected ViewReportActivity() {
        super(ViewReportView.class, R.layout.dashboard_items, R.menu.view_report);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListReportFragment reports = (ListReportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.list_report_fragment);

        reports.setListMapListener(this);

        Fragment f = getSupportFragmentManager().findFragmentById(
                R.id.list_map_fragment);

        detailsInline = (f != null && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE));

        if (detailsInline) {
            reports.enablePersistentSelection();
        } else if (f != null) {
            f.getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapSelected(int mapId) {
        Intent i = new Intent(this, ListReportActivity.class);

        // i.putExtra(DetailsActivity.EXTRA_URL, url);
        startActivity(i);
    }
}
