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

package com.ushahidi.android.app.ui.navdrawer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.ui.tablet.AboutFragment;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class AboutNavDrawerItem extends BaseNavDrawerItem {

    private Intent mIntent;

    SherlockFragmentActivity activity;

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public AboutNavDrawerItem(String title, int iconRes, SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, NO_COUNTER, null);

    }

    @Override
    public void onSelectItem() {
        if (Util.isTablet(activity.getApplicationContext())) {
            showAboutDialog();
        } else {
            mIntent = new Intent(activity.getApplicationContext(), AboutActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(mIntent);
        }

    }

    public void showAboutDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out,
                R.anim.slide_right_in, R.anim.slide_right_out);
        ft.addToBackStack(null);

        // Create and show the dialog.
        AboutFragment newFragment = AboutFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

}
