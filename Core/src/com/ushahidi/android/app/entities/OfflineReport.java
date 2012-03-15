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

public class OfflineReport implements IDbEntity {

    private int id;

    private String title;

    private String description;

    private String date;

    private int hour;

    private int minute;

    private String amPm;

    private String categories;

    private String locationName;

    private String latitude;

    private String longitude;

    private String photo;

    private String video;

    private String news;

    private String firstName;

    private String lastName;

    private String email;

    // getters and setters
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getAmPm() {
        return this.amPm;
    }

    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVideo() {
        return this.video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getNews() {
        return this.news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
}
