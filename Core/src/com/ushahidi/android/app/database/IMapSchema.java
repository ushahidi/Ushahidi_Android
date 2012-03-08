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

public interface IMapSchema {

    // Deployments
    public static final String MAP_ID = "_id";

    public static final String MAP_NAME = "name";

    public static final String MAP_URL = "url";

    public static final String MAP_DESC = "desc";

    public static final String MAP_CAT_ID = "cat_id";

    public static final String MAP_LATITUDE = "latitude";

    public static final String MAP_LONGITUDE = "longitude";

    public static final String MAP_DATE = "discovery_date";

    public static final String MAP_ACTIVE = "deployment_active"; // 1 4
                                                                        // active,
                                                                        // 0 4
                                                                        // inactive

    // Deployments
    public static final String[] MAP_COLUMNS = new String[] {
            MAP_ID, MAP_NAME, MAP_URL, MAP_DESC, MAP_CAT_ID,
            MAP_ACTIVE, MAP_LATITUDE, MAP_LONGITUDE, MAP_DATE
    };

    public static final String MAP_TABLE = "deployment";

    public static final String MAP_TABLE_CREATE = "CREATE VIRTUAL TABLE " + MAP_TABLE
            + " USING fts3 (" + MAP_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
            + MAP_CAT_ID + " INTEGER, " + MAP_ACTIVE + " INTEGER, " + MAP_NAME
            + " TEXT NOT NULL, " + MAP_DATE + " DATE NOT NULL, " + MAP_DESC
            + " TEXT NOT NULL, " + MAP_URL + " TEXT NOT NULL, " + MAP_LATITUDE
            + " TEXT NOT NULL, " + MAP_LONGITUDE + " TEXT NOT NULL" + ")";

}
