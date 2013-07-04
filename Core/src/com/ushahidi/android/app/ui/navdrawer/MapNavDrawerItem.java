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

import android.content.Context;
import android.content.Intent;

import com.ushahidi.android.app.ui.phone.ListMapActivity;
import com.ushahidi.android.app.ui.tablet.DashboardActivity;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class MapNavDrawerItem extends BaseNavDrawerItem {

    private Intent mIntent;

    private Context mContext;

    /**
     * @param stringRes
     * @param iconRes
     */
    public MapNavDrawerItem(String title, int iconRes, Context context) {
        super(NO_ITEM_ID, title, iconRes, NO_COUNTER, null);
        mContext = context;
    }

    @Override
    public void onSelectItem() {

        // TODO Auto-generated method stub
        if (Util.isTablet(mContext)) {

            mIntent = new Intent(mContext, DashboardActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        } else {
            mIntent = new Intent(mContext, ListMapActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        }
    }

}
