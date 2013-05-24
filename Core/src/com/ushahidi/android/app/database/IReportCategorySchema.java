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

	public static String TABLE = "report_category";

	public static String ID = "_id";

	public static String REPORT_ID = "report_id";

	public static String CATEGORY_ID = "category_id";

	public static String PENDING = "pending";

	public static final String REPORT_CATEGORY_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CATEGORY_ID
			+ " INTEGER NOT NULL, "
			+ REPORT_ID
			+ " INTEGER NOT NULL, " + PENDING + " INTEGER NOT NULL )";

	public static final String[] COLUMNS = new String[] { ID, CATEGORY_ID,
			REPORT_ID, CATEGORY_ID, PENDING };

}
