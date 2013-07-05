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

import com.ushahidi.android.app.Settings;

/**
 * @author eyedol
 */
public class SettingsNavDrawerItem extends BaseNavDrawerItem {

    private Context mContext;

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public SettingsNavDrawerItem(String title, int resIconId, Context context) {
        super(NO_ITEM_ID, title, resIconId, NO_COUNTER, null, null);
        mContext = context;
    }

    @Override
    public boolean isSelected() {
        return mContext instanceof Settings;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.ui.navdrawer.BaseNavDrawerItem#onSelectItem()
     */
    @Override
    public void onSelectItem() {
        if (!isSelected()) {
            Intent mIntent = new Intent(mContext, Settings.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mContext.startActivity(mIntent);
        }
    }

}
