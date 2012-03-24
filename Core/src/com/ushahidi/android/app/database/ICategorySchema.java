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

	public static final String ID = "_id";

	public static final String TITLE = "category_title";

	public static final String DESCRIPTION = "category_desc";

	public static final String COLOR = "category_color";

	public static final String POSITION = "position";

	public static final String[] COLUMNS = new String[] { ID, TITLE,
			DESCRIPTION, COLOR, POSITION };

	public static final String TABLE = "categories";

	public static final String CATEGORIES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE+ " ("+ ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
			+ TITLE+ " TEXT NOT NULL, "+ DESCRIPTION+ " TEXT, "+ COLOR+ " TEXT, " 
			+ POSITION + " INTEGER " + ")";
}
