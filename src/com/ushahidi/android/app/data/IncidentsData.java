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

package com.ushahidi.android.app.data;

public class IncidentsData {
	private String incidentTitle = "";
	private String incidentDesc = "";
	private String incidentLocation = "";
	private String incidentDate = "";
	private int incidentMode = 0;
	private int incidentVerified = 0;
	private String incidentLocLatitude = "";
	private String incidentLocLongitude = "";
	private String incidentCategories = "";
	private String incidentMedia = "";
	private int incidentId = 0;
	
	public IncidentsData() {
		
	}
	
	public int getIncidentId() {
		return incidentId;
	}
	
	public void setIncidentId( int id ) {
		this.incidentId = id;
	}
	
	public String getIncidentTitle() {
		return incidentTitle;
	}

	public void setIncidentTitle(String title ) {
		this.incidentTitle = title;
	}
	
	public String getIncidentDesc() {
		return incidentDesc;
	}
	
	public void setIncidentDesc( String description) {
		this.incidentDesc = description;
	}
	
	public String getIncidentMedia() {
		return incidentMedia;
	}
	
	public void setIncidentMedia(String media ) {
		this.incidentMedia = media;
	}
	
	public String getIncidentCategories() {
		return incidentCategories;
	}
	
	public void setIncidentCategories( String categories) {
		this.incidentCategories = categories;
	}
	
	public String getIncidentLocation() {
		return this.incidentLocation;
	}
	
	public void setIncidentLocation( String location ) {
		this.incidentLocation =  location;
	}
	
	public String getIncidentLocLatitude() {
		return incidentLocLatitude;
	}
	
	public void setIncidentLocLatitude(String latitude ) {
		this.incidentLocLatitude = latitude;
	}
	
	public String getIncidentLocLongitude() {
		return incidentLocLongitude;
	}
	
	public void setIncidentLocLongitude(String longitude ) {
		this.incidentLocLongitude = longitude;
	}
	
	public int getIncidentVerified() {
		return incidentVerified;
	}
	
	public void setIncidentVerified( int id ) {
		this.incidentVerified = id;
	}
	
	public int getIncidentMode() {
		return incidentMode;
	}
	
	public void setIncidentMode( int id ) {
		this.incidentMode = id;
	}
	
	public String getIncidentDate() {
		return incidentDate;
	}
	
	public void setIncidentDate( String date) {
		this.incidentDate = date;
	}

}
