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
 */
public interface ICheckinSchema {

	public static final String CHECKINS_TABLE = "checkins";

	public static final String ID = "_id";

	public static final String CHECKIN_ID = "checkin_id";

	public static final String CHECKIN_USER_ID = "user_id";

	public static final String CHECKIN_MESG = "checkin_mesg";

	public static final String CHECKIN_DATE = "checkin_date";

	public static final String CHECKIN_LOC_NAME = "checki_loc_name";

	public static final String CHECKIN_LOC_LATITUDE = "checkin_loc_latitude";

	public static final String CHECKIN_LOC_LONGITUDE = "checkin_loc_longitude";

	// Checkins messages
	public static final String[] CHECKINS_COLUMNS = new String[] { ID,
			CHECKIN_ID, CHECKIN_USER_ID, CHECKIN_MESG, CHECKIN_DATE,
			CHECKIN_LOC_NAME, CHECKIN_LOC_LATITUDE, CHECKIN_LOC_LONGITUDE };

	public static final String CHECKINS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ CHECKINS_TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CHECKIN_ID
			+ " INTEGER , "
			+ CHECKIN_USER_ID
			+ " INTEGER, "
			+ CHECKIN_MESG
			+ " TEXT NOT NULL, "
			+ CHECKIN_DATE
			+ " DATE NOT NULL, "
			+ CHECKIN_LOC_NAME
			+ " TEXT NOT NULL, "
			+ CHECKIN_LOC_LATITUDE
			+ " TEXT NOT NULL, "
			+ CHECKIN_LOC_LONGITUDE
			+ " TEXT NOT NULL"
			+ ")";

}
