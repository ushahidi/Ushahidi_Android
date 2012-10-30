package com.ushahidi.android.app.database;

public interface IOpenGeoSmsSchema {
	public static final String ID = "_id";
	public static final String REPORT_ID = "report_id";
	public static final String STATE = "state";
	public static final int STATE_NOT_OPENGEOSMS = -1;
	public static final int STATE_PENDING = 0;
	public static final int STATE_SENT = 1;
	public static final String TABLE = "opengeosms";
	public static final String OPENGEOSMS_TABLE_CREATE =
		"CREATE TABLE IF NOT EXISTS " + TABLE +
		"(" +
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			REPORT_ID + " INTEGER NOT NULL, " +
			STATE + " INTEGER NOT NULL" +
		")";
}
