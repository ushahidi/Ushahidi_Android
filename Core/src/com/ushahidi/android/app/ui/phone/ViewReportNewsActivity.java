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

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.adapters.NewsScreenSwipeAdapter;
import com.ushahidi.android.app.models.ListReportNewsModel;
import com.ushahidi.android.app.views.SlideView;

/**
 * @author eyedol
 */
public class ViewReportNewsActivity extends BaseActivity<SlideView> {

    private ListReportNewsModel mNews;

    private int position;

    private int reportId;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES = 0;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private ViewPager mPager;

    public ViewReportNewsActivity() {
        super(SlideView.class, R.layout.screen_slide, R.menu.view_media);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mNews = new ListReportNewsModel();

        this.reportId = getIntent().getExtras().getInt("reportid", 0);
        this.position = getIntent().getExtras().getInt("position", 0);

        mNews.load(reportId);
        NUM_PAGES = mNews.totalReportNews();

        mPager = (ViewPager) findViewById(R.id.screen_pager);
        mPager.setAdapter(getAdapter());
        mPager.setCurrentItem(position, true);

    }

    public PagerAdapter getAdapter() {
        return new NewsScreenSwipeAdapter(getSupportFragmentManager(), this,
                reportId, NUM_PAGES);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_forward) {

            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            return true;

        } else if (item.getItemId() == R.id.menu_backward) {

            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            return true;

        } else if (item.getItemId() == R.id.menu_share) {
            share(mNews.getNews().get(mPager.getCurrentItem()).getUrl());
        }

        return super.onOptionsItemSelected(item);
    }

    private void share(String url) {
        final String shareString = getString(R.string.share_template, " ",
                " \n" + url);
        shareText(shareString);
    }
}
