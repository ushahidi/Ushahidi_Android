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

import com.ushahidi.android.app.util.Util;

public class Report implements IDbEntity {

	private int id;

	private String title;

	private String description;

	private String date;

	private int mode;

	private int verified;

	private String locationname;

	private double latitude;

	private double longitude;

	private String categories;

	private String media;

	private String image;

	private int reportId = 0;

	private int pending = 0;

	public Report() {

	}

	public void addReport(com.ushahidi.java.sdk.api.Incident i) {
		this.title = i.getTitle();
		this.description = i.getDescription();
		this.date = Util.datePattern("yyyy-MM-dd HH:mm:ss", i.getDate());
		this.mode = i.getMode();
		this.verified = i.getVerified();
		this.locationname = i.getLocationName();
		this.latitude = i.getLatitude();
		this.longitude = i.getLongitude();
		this.reportId = i.getId();
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

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getVerified() {
		return verified;
	}

	public void setVerified(int verified) {
		this.verified = verified;
	}

	public String getLocationName() {
		return locationname;
	}

	public void setLocationName(String locationname) {
		this.locationname = locationname;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getMedia() {
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

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getReportId() {
		return this.reportId;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getPending() {
		return this.pending;
	}
}
