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


/**
 * @author eyedol
 */
public class CategoryNavMenuItem extends BaseNavDrawerItem {

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public CategoryNavMenuItem(int itemId, String title, String counterBgColor) {
        super(itemId, title, NO_ICON_RES_ID, NO_COUNTER, counterBgColor);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.ui.navdrawer.BaseNavDrawerItem#onSelectItem()
     */
    @Override
    public void onSelectItem() {
        // TODO Auto-generated method stub
    }

}
