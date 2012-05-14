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

import com.ushahidi.android.app.models.Model;

/**
 * @author eyedol
 */
public class User extends Model implements IDbEntity {

    private int id;

    private String username;

    private String color;

    private int userId;
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
    
    public int getUserId() {
    	return this.userId;
    }
    
    public void setUserId(int userId) {
    	this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
    	if (color.startsWith("#")) {
			this.color = color;
		} else {
			this.color = "#" + color;
		}
    }

	/* (non-Javadoc)
	 * @see com.ushahidi.android.app.models.Model#load()
	 */
	@Override
	public boolean load() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ushahidi.android.app.models.Model#save()
	 */
	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
