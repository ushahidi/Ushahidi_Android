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
public interface IUserSchema {

    public static final String USER_TABLE = "users";
    
    public static final String USER_ID = "_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_COLOR = "user_color";
    
    public static final String USER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE
    + " (" + USER_ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " + USER_NAME
    + " TEXT NOT NULL, " + USER_COLOR + " TEXT" + ")";

    public static final String[] USER_COLUMNS = new String[] {
            USER_ID, USER_NAME, USER_COLOR
    };
}
