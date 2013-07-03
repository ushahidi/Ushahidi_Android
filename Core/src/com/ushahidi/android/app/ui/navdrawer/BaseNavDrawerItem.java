/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
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
 *****************************************************************************/

package com.ushahidi.android.app.ui.navdrawer;

import com.ushahidi.android.app.models.NavDrawerItem;

import android.view.View;

/**
 * @author eyedol
 */
public abstract class BaseNavDrawerItem extends NavDrawerItem {

    /**
     * @param itemId
     * @param title
     * @param iconRes
     * @param counter
     * @param counterBgColor
     */
    public BaseNavDrawerItem(int itemId, String title, int iconRes, int counter,
            String counterBgColor) {
        super(itemId, title, iconRes, counter, counterBgColor);
    }

    public void onConfigureView(View view) {
    };

    public abstract void onSelectItem();

    public void selectItem() {
        onSelectItem();
    }

    /**
     * Determines if the item has an id for remembering the last selected item
     */
    public boolean hasItemId() {
        return getItemId() != NO_ITEM_ID;
    }

    /**
     * Allows the menu item to do additional manipulation to the view
     */
    public void configureView(View v) {
        onConfigureView(v);
    }

    /**
     * Determines if the item is selected. Default is always false.
     */
    public Boolean isSelected() {
        return false;
    }

    /**
     * Determines if the menu item should be displayed in the menu. Default is
     * always true.
     */
    public Boolean isVisible() {
        return true;
    };
}
