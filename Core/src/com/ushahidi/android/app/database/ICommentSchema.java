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
 * @author eyedol
 * 
 */
public interface ICommentSchema {
	public static final String TABLE = "comment";

	public static final String ID = "_id";

	public static final String COMMENT_ID = "comment_id";

	public static final String REPORT_ID = "report_id";

	public static final String CHECKIN_ID = "checkin_id";

	public static final String COMMENT_AUTHOR = "comment_author";

	public static final String COMMENT_DESCRIPTION = "comment_description";

	public static final String COMMENT_DATE = "comment_date";

	public static final String[] COMMENT_COLUMN = new String[] { ID,

	COMMENT_ID, REPORT_ID, CHECKIN_ID, COMMENT_AUTHOR, COMMENT_DESCRIPTION,
			COMMENT_DATE };

	public static final String COMMENT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COMMENT_ID
			+ " INTEGER , "
			+ CHECKIN_ID
			+ " INTEGER, "
			+ REPORT_ID
			+ " INTEGER, "
			+ COMMENT_DATE
			+ " DATE NOT NULL, "
			+ COMMENT_AUTHOR
			+ " TEXT, "
			+ COMMENT_DESCRIPTION + " TEXT " + ")";
}
