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

import com.ushahidi.android.app.database.DbEntity;

/**
 * @author eyedol
 *
 */
public class Map implements DbEntity{

    private String name;

    private String desc;

    private String url;

    private String date;

    private String lat;

    private String lon;

    private String catId;

    private String active;
    
    private Long id;
    
    @Override
    public Long getDbId() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public void setDbId(Long id) {
        this.id = id;
        
    }
    
    public String getActive() {
        return this.active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setCatId(String catId) {

        this.catId = catId;
    }

    public String getCatId() {
        return this.catId;
    }

}
