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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class AboutNavDrawerItem extends BaseNavDrawerItem {


    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public AboutNavDrawerItem(String title, int iconRes, SherlockFragmentActivity activity) {
        super(NO_ITEM_ID, title, iconRes, NO_COUNTER, null, activity);
    }

    @Override
    public boolean isSelected() {
        return mActivity instanceof AboutActivity;
    }

    @Override
    public void onSelectItem() {
        Util.showAbout(mActivity);
    }

}
