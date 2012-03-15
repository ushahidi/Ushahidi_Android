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


public class Report implements IDbEntity {

    private int id;

    private String title;

    private String description;

    private String date;

    private String mode;

    private String verified;

    private String locationname;

    private String latitude;

    private String longitude;

    private String categories;

    private String media;

    private String image;

    public Report() {

    }

    public Report(String title, String description, String reportdate, String mode,
            String verified, String locationame, String latitude, String longitude,
            String categories, String media, String image) {
        this.title = title;
        this.description = description;
        this.date = reportdate;
        this.mode = mode;
        this.verified = verified;
        this.locationname = locationame;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categories = categories;
        this.media = media;
        this.image = image;

    }

    @Override
    public int getDbId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReportDate() {
        return date;
    }

    public void setReportDate(String reportdate) {
        this.date = reportdate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getLocationName() {
        return locationname;
    }

    public void setLocationName(String locationname) {
        this.locationname = locationname;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getCategories() {
        return categories;
    }
    
    public void setCategories(String categories){
        this.categories = categories;
    }
    
    public String getMedia(){
        return this.media;
    }
    
    public void setMedia(String media) {
        this.media = media;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public void setDbId(int id) {
        this.id = id;
    }
}
