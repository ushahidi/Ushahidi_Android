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

package com.ushahidi.android.app.models;

/**
 * Navigation Drawer menu item
 */
public class NavDrawerItem extends Model {

    public static int NO_ITEM_ID = -1;

    public static int NO_COUNTER = -1;

    // Resource id for the title string
    protected int mTitle;
    // Resource id for the icon drawable
    protected int mIconRes;
    // ID for the item for remembering which item was selected
    private int mItemId;

    // counter
    protected int mCounter;

    // The counter background color
    protected String mCounterBgColor;

    /**
     * Creates a NavDrawerItem with the specific id, string resource id and
     * drawable resource id
     */
    public NavDrawerItem(int itemId, int stringRes, int iconRes, int counter, String counterBgColor) {
        mTitle = stringRes;
        mIconRes = iconRes;
        mItemId = itemId;
        mCounter = counter;
        mCounterBgColor = counterBgColor;
    }

    /**
     * Creates a NavDrawerItem with the specific id, string resource id and
     * drawable resource id
     */
    public NavDrawerItem(int itemId, int stringRes, int iconRes, int counter) {
        this(itemId, stringRes, iconRes, counter, null);

    }

    /**
     * Creates a NavDrawerItem with NO_ITEM_ID for it's id for items that
     * shouldn't be remembered between application launches.
     */
    public NavDrawerItem(int stringRes, int iconRes) {
        this(NO_ITEM_ID, stringRes, iconRes, NO_COUNTER);
    }

    /**
     * 
     */

    /**
     * Get's the item's unique ID
     */
    public int getItemId() {
        return mItemId;
    }

    /**
     * Returns the item's string representation (used by ArrayAdapter.getView)
     */
    public String toString() {
        return "";
    }

    /**
     * The resource id to use for the menu item's title
     */
    public int getTitleRes() {
        return mTitle;
    }

    /**
     * The resource id to use for the menu item's icon
     */
    public int getIconRes() {
        return mIconRes;
    }

    /**
     * Get the counter attached to a
     * 
     * @return int
     */
    public int getCounter() {
        return mCounter;
    }

    public String getCounterBgColor() {
        return mCounterBgColor;
    }

}
