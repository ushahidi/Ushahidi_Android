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

/**
 * Defines the schema for report category table.
 * 
 * @author eyedol
 */
public interface IReportCategorySchema {

    public static String REPORT_CATEGORY_TABLE = "report_table";

    public static String REPORT_CATEGORY_ID = "_id";

    public static String REPORT_CATEGORY_REPORT_ID = "report_id";

    public static String REPORT_CATEGORY_CATEGORY_ID = "category_id";

    public static final String REPORT_CATEGORY_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + REPORT_CATEGORY_TABLE + " (" + REPORT_CATEGORY_ID
            + " INTEGER PRIMARY KEY AUTO_INCREMENT, " + REPORT_CATEGORY_ID + "INTEGER NOT NULL, "
            + REPORT_CATEGORY_REPORT_ID + "INTEGER NOT NULL )";

    public static final String[] REPORT_CATEGORY_COLUMNS = new String[] {
            REPORT_CATEGORY_TABLE, REPORT_CATEGORY_ID, REPORT_CATEGORY_REPORT_ID,
            REPORT_CATEGORY_CATEGORY_ID
    };

}
