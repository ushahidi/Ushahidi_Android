package org.addhen.ushahidi.data;

public class IncidentsData {
	String incidentTitle = "";
	String incidentDesc = "";
	String incidentLocation = "";
	String incidentDate = "";
	int incidentMode = 0;
	int incidentVerified = 0;
	String incidentLocLatitude = "";
	String incidentLocLongitude = "";
	String incidentCategories = "";
	String incidentMedia = "";
	int incidentId = 0;
	
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
