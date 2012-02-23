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

package com.ushahidi.android.app.database;

public interface ICategorySchema {

    public static final String CATEGORY_ID = "_id";

    public static final String CATEGORY_TITLE = "category_title";

    public static final String CATEGORY_DESC = "category_desc";

    public static final String CATEGORY_COLOR = "category_color";

    public static final String CATEGORY_IS_UNREAD = "is_unread";

    public static final String CATEGORY_POS = "position";

    public static final String[] CATEGORIES_COLUMNS = new String[] {
            CATEGORY_ID, CATEGORY_TITLE, CATEGORY_DESC, CATEGORY_COLOR, CATEGORY_IS_UNREAD,
            CATEGORY_POS
    };

    public static final String CATEGORIES_TABLE = "categories";

    public static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + CATEGORIES_TABLE + " (" + CATEGORY_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + CATEGORY_TITLE + " TEXT NOT NULL, " + CATEGORY_DESC + " TEXT, " + CATEGORY_COLOR
            + " TEXT, " + CATEGORY_IS_UNREAD + " BOOLEAN NOT NULL, " + CATEGORY_POS + " INTEGER "
            + ")";
}
