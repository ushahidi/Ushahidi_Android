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

import java.text.SimpleDateFormat;
import java.util.Date;

public interface IMapSchema {

	// Deployments
	public static final String ID = "_id ";

	public static final String MAP_ID = "map_id";

	public static final String NAME = "name";

	public static final String URL = "url";

	public static final String DESC = "desc";

	public static final String CAT_ID = "cat_id";

	public static final String LATITUDE = "latitude";

	public static final String LONGITUDE = "longitude";

	public static final String DATE = "discovery_date";

	public static final String ACTIVE = "deployment_active"; // 1 4
																// active,
																// 0 4
																// inactive

	// default map stuff
	public static final int DEMO_ID = 0;

	public static final int DEMO_CAT_ID = 0;

	public static final int DEMO_ACTIVE = 0;

	public static final String DEMO_DESC = "Ushahidi Demo";

	public static final String DEMO_NAME = "Demo";

	public static final String DEMO_DATE = (new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss")).format(new Date());

	public static final String DEMO_URL = "http://demo.ushahidi.com";

	public static final String DEMO_LATITUDE = "0.0";

	public static final String DEMO_LONGITUDE = "0.0";
	// Deployments
	public static final String[] MAP_COLUMNS = new String[] { ID, MAP_ID, NAME,
			URL, DESC, CAT_ID, ACTIVE, LATITUDE, LONGITUDE, DATE };

	public static final String TABLE = "deployment";

	public static final String MAP_TABLE_CREATE = "CREATE VIRTUAL TABLE "
			+ TABLE + " USING fts3 (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MAP_ID + " INTEGER, "
			+ CAT_ID + " INTEGER, " + ACTIVE + " INTEGER, " + NAME
			+ " TEXT NOT NULL, " + DATE + " DATE NOT NULL, " + DESC
			+ " TEXT NOT NULL, " + URL + " TEXT NOT NULL, " + LATITUDE
			+ " TEXT NOT NULL, " + LONGITUDE + " TEXT NOT NULL" + ")";

	public static final String DEFAULT_MAP_CREATE = String
			.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (%d,%d,%d,'%s','%s','%s','%s','%s','%s')",
					TABLE, MAP_ID, CAT_ID, ACTIVE, NAME, DATE, DESC, URL,
					LATITUDE, LONGITUDE, DEMO_ID, DEMO_CAT_ID, DEMO_ACTIVE,
					DEMO_NAME, DEMO_DATE, DEMO_DESC, DEMO_URL, DEMO_LATITUDE,
					DEMO_LONGITUDE);

}
