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
 * Defines the schema for media table.
 * 
 * @author eyedol
 */
public interface IMediaSchema {

    public static final String MEDIA_TABLE = "checkin_media";

    // Checkins media
    public static final String MEDIA_ID = "_id";

    public static final String MEDIA_CHECKIN_ID = "media_checkin_id";

    public static final String MEDIA_REPORT_ID = "media_report_id";

    public static final String MEDIA_TYPE = "media_type";

    public static final String MEDIA_THUMBNAIL = "media_thumbnail_link";

    public static final String MEDIA_MEDIUM_SIZE = "media_medium_link";

    // Checkin Media
    public static final String[] MEDIA_COLUMNS = new String[] {
            MEDIA_ID, MEDIA_CHECKIN_ID, MEDIA_REPORT_ID, MEDIA_TYPE, MEDIA_THUMBNAIL,
            MEDIA_MEDIUM_SIZE
    };

    public static final String MEDIA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + MEDIA_TABLE
            + " (" + MEDIA_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + MEDIA_CHECKIN_ID
            + " INTEGER, " + MEDIA_REPORT_ID + " INTEGER, " + MEDIA_TYPE + " INTEGER, "
            + MEDIA_THUMBNAIL + " TEXT, " + MEDIA_MEDIUM_SIZE + " TEXT" + ")";

}
