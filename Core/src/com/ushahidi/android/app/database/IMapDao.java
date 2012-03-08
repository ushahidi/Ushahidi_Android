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

import java.util.List;

import android.database.Cursor;

import com.ushahidi.android.app.entities.Map;

public interface IMapDao {

    public List<Map> fetchMapById(long id);

    public void setActiveDeployment(long id);

    public boolean deleteMapById(long id);

    public boolean deleteAllMap();

    public boolean updateMap(Map map);

    public boolean addMaps(List<Map> map);

    public boolean addMap(Map map);

    public List<Map> fetchAllMaps();
    
    public List<Map> fetchMap(Cursor cursor);

    public List<Map> fetchMapByIdAndUrl(long id, String Url);

}
