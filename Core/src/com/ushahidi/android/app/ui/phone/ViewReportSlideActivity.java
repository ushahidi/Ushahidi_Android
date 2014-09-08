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

package com.ushahidi.android.app.ui.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.helpers.MapPager;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.ui.tablet.ViewReportFragment;
import com.ushahidi.android.app.views.SlideView;

/**
 * @author eyedol
 */
public class ViewReportSlideActivity extends BaseActivity<SlideView> {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES = 0;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private MapPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private int mCategoryId;

    private int mReportId;

    private String mReportTitle;

    private ListReportModel mReports;

    public ViewReportSlideActivity() {
        super(SlideView.class, R.layout.view_report_slide, R.menu.view_report);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReports = new ListReportModel();
        this.mCategoryId = getIntent().getExtras().getInt("category", 0);
        int pos = getIntent().getExtras().getInt("id", 0);
        if (mCategoryId > 0) {
            mReports.loadReportByCategory(mCategoryId);
        } else {
            mReports.load();
        }

        NUM_PAGES = mReports.getReports().size();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (MapPager) findViewById(R.id.report_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they
                // are dependent
                // on which page is currently active. An alternative approach is
                // to have each
                // fragment expose actions itself (rather than the activity
                // exposing actions),
                // but for simplicity, the activity provides the actions in this one
                supportInvalidateOptionsMenu();
            }
        });

        mPager.setCurrentItem(pos, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_backward) {
            // Go to the previous step in the wizard. If there is no previous
            // step,
            // setCurrentItem will do nothing.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            return true;
        }

        else if (item.getItemId() == R.id.menu_forward) {
            // Advance to the next step in the wizard. If there is no next step,
            // setCurrentItem
            // will do nothing.
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            return true;

        } else if (item.getItemId() == R.id.menu_share) {

            share();
        } else if (item.getItemId() == R.id.menu_comment) {
            mReportId = mReports.getReports().get(mPager.getCurrentItem())
                    .getIncident().getId();
            Intent i = new Intent(this, AddCommentActivity.class);

            i.putExtra("reportid", mReportId);
            startActivityForResult(i, 0);
            overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        }

        return super.onOptionsItemSelected(item);
    }

    private void share() {
        mReportId = mReports.getReports().get(mPager.getCurrentItem())
                .getDbId();
        mReportTitle = mReports.getReports().get(mPager.getCurrentItem())
                .getIncident().getTitle();
        final String reportUrl = Preferences.domain + "reports/view/"
                + mReportId;
        final String shareString = getString(R.string.share_template, " "
                + mReportTitle, "\n" + reportUrl);
        shareText(shareString);

    }

    /**
     * A simple pager adapter that represents  {@link ViewReportFragment}
     * objects, in sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ViewReportFragment.newInstance(position, mCategoryId);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

}
