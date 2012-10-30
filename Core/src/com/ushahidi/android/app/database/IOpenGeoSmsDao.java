package com.ushahidi.android.app.database;


public interface IOpenGeoSmsDao {

	public int getReportState(long reportId);
	public boolean setReportState(long reportId, int state);
	public boolean addReport(long reportId);
	public boolean deleteReport(long reportId);
	public boolean deleteReports();

}
