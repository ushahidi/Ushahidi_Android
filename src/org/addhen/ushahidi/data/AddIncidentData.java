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

package org.addhen.ushahidi.data;

public class AddIncidentData {
	private String addIncidentTitle;
	private String addIncidentDesc;
	private String addIncidentDate;
	private int addIncidentHour;
	private int addIncidentMinute;
	private String addIncidentAmPm;
	private String addIncidentCategories;
	private String addIncidentLocName;
	private String addIncidentLocLatitude;
	private String addIncidentLocLongitude;
	private String addIncidentPhoto;
	private String addIncidentVideo;
	private String addIncidentNews;
	private String personFirst;
	private String personLast;
	private String personEmail;
	
	
	public AddIncidentData() {
		addIncidentTitle = "";
		addIncidentDesc = "";
		addIncidentDate = "";
		addIncidentHour = 0;
		addIncidentMinute = 0;
		addIncidentAmPm = "";
		addIncidentCategories = "";
		addIncidentLocName = "";
		addIncidentLocLatitude = "";
		addIncidentLocLongitude = "";
		addIncidentPhoto = "";
		addIncidentVideo = "";
		addIncidentNews = "";
		personFirst = "";
		personLast = "";
		personEmail = "";
	}
	
	//getters and setters 
	public String getIncidentTitle() {
		return this.addIncidentTitle;
	}
	
	public void setIncidentTitle( String title ) {
		this.addIncidentTitle = title;
	}
	
	public String getIncidentDesc() {
		return this.addIncidentDesc;
	}
	
	public void setIncidentDesc(String description) {
		this.addIncidentDesc = description;
	}
	
	public String getIncidentDate() {
		return this.addIncidentDate;
	}
	
	public void setIncidentDate( String date ) {
		this.addIncidentDate = date;
	}
	
	public int getIncidentMinute() {
		return this.addIncidentMinute;
	}
	
	public void setIncidentMinute(int minute) {
		this.addIncidentMinute = minute;
	}
	
	public int getIncidentHour() {
		return this.addIncidentHour;
	}
	
	public void setIncidentHour(int hour) {
		this.addIncidentHour = hour;
	}
	
	public String getIncidentAmPm() {
		return this.addIncidentAmPm;
	}
	
	public void setIncidentAmPm( String amPm ) {
		this.addIncidentAmPm = amPm;
	}
	
	public String getIncidentCategories() {
		return this.addIncidentCategories;
	}
	
	public void setIncidentCategories( String categories ) {
		this.addIncidentCategories = categories;
	}
	
	public String getIncidentLocName() {
		return this.addIncidentLocName;
	}
	
	public void setIncidentLocName(String locationName) {
		this.addIncidentLocName = locationName;
	}
	
	public String getIncidentLocLatitude() {
		return this.addIncidentLocLatitude;
	}
	
	public void setIncidentLocLatitude( String latitude ) {
		this.addIncidentLocLatitude = latitude;
	}
	
	public String getIncidentLocLongitude() {
		return this.addIncidentLocLongitude;
	}
	
	public void setIncidentLocLongitude( String longitude ) {
		this.addIncidentLocLongitude = longitude;
	}
	
	public String getIncidentPhoto() {
		return this.addIncidentPhoto;
	}
	
	public void setIncidentPhoto( String photo ) {
		this.addIncidentPhoto = photo;
	}
	
	public String getIncidentVideo() {
		return this.addIncidentVideo;
	}
	
	public void setIncidentVideo( String video) {
		this.addIncidentVideo = video;
	}
	
	public String getIncidentNews() {
		return this.addIncidentNews;
	}
	
	public void setIncidentNews(String news) {
		this.addIncidentNews = news;
	} 
	
	public String getPersonFirst() {
		return this.personFirst;
	}
	
	public void setPersonFirst( String firstName ) {
		this.personFirst =  firstName;
	}
	
	public String getPersonLast() {
		return this.personLast;
	}
	
	public void setPersonLast( String lastName ) {
		this.personLast = lastName;
	}
	
	public String getPersonEmail() {
		return this.personEmail;
	}
	
	public void setPersonEmail( String email ) {
		this.personEmail = email;
	}
}
