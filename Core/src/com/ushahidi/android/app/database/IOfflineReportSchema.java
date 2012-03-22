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
public interface IOfflineReportSchema {

    public static final String OFFLINE_REPORT_TABLE = "offline_report";

    public static final String ID = "_id";

    public static final String TITLE = "incident_title";

    public static final String DESCRIPTION = "incident_desc";

    public static final String DATE = "incident_date";

    public static final String HOUR = "incident_hour";

    public static final String MINUTE = "incident_minute";

    public static final String AMPM = "incident_ampm";

    public static final String CATEGORIES = "incident_categories";

    public static final String LOCATION_NAME = "incident_loc_name";

    public static final String LATITUDE = "incident_loc_latitude";

    public static final String LONGITUDE = "incident_loc_longitude";

    public static final String PHOTO = "incident_photo";

    public static final String VIDEO = "incident_video";

    public static final String NEWS = "incident_news";

    public static final String FIRST_NAME = "person_first";

    public static final String LAST_NAME = "person_last";

    public static final String EMAIL = "person_email";

    /**
     * Columns of the table that stores off line incidents
     */
    public static final String[] OFFLINE_REPORT_COLUMNS = new String[] {
            ID, TITLE, DESCRIPTION, DATE, HOUR, MINUTE, AMPM, CATEGORIES, LOCATION_NAME, LATITUDE,
            LONGITUDE, PHOTO, VIDEO, NEWS, FIRST_NAME, LAST_NAME, EMAIL
    };

    public static final String OFFLINE_REPORT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + OFFLINE_REPORT_TABLE + " (" + ID + " INTEGER PRIMARY KEY , " + TITLE
            + " TEXT NOT NULL, " + DESCRIPTION + " TEXT, " + DATE + " DATE NOT NULL, " + HOUR
            + " INTEGER, " + MINUTE + " INTEGER, " + AMPM + " TEXT NOT NULL, " + CATEGORIES
            + " TEXT NOT NULL, " + LOCATION_NAME + " TEXT NOT NULL, " + LATITUDE
            + " TEXT NOT NULL, " + LONGITUDE + " TEXT NOT NULL, " + PHOTO + " TEXT, " + VIDEO
            + " TEXT, " + NEWS + " TEXT, " + FIRST_NAME + " TEXT, " + LAST_NAME + " TEXT, " + EMAIL
            + " TEXT " + ")";
}
