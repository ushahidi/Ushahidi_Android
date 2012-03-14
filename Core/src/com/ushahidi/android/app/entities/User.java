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

package com.ushahidi.android.app.entities;

/**
 * @author eyedol
 */
public class User implements IDbEntity {

    private int id;

    private String username;

    private String userColor;

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.entities.IDbEntity#getDbId()
     */
    @Override
    public int getDbId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.entities.IDbEntity#setDbId(int)
     */
    @Override
    public void setDbId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserColor() {
        return userColor;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

}
