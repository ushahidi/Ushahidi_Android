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

    public static final String TABLE = "checkin_media";

    // Checkins media
    public static final String ID = "_id";

    public static final String CHECKIN_ID = "media_checkin_id";

    public static final String REPORT_ID = "media_report_id";

    public static final String TYPE = "media_type";

    public static final String LINK = "media_link";

    // Checkin Media
    public static final String[] MEDIA_COLUMNS = new String[] {
            ID, CHECKIN_ID, REPORT_ID, TYPE, LINK
    };

    public static final String MEDIA_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
            + ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + CHECKIN_ID + " INTEGER, "
            + REPORT_ID + " INTEGER, " + TYPE + " INTEGER, " + LINK + " TEXT " + ")";
    
    public static final int IMAGE = 1;

    public static final int VIDEO = 2;

    public static final int AUDIO = 3;

    public static final int NEWS = 4;

}
