package com.ushahidi.android.app.database;

/**
 * Defines schema for reports aka incidents table
 * 
 * @author eyedol
 */
public interface IReportSchema {

	public static final int PENDING = 1;

	public static final int FETCHED = 0;

	public static final String ID = "_id";

	public static final String INCIDENT_ID = "incident_id";

	public static final String INCIDENT_TITLE = "incident_title";

	public static final String INCIDENT_DESC = "incident_desc";

	public static final String INCIDENT_DATE = "incident_date";

	public static final String INCIDENT_MODE = "incident_mode";

	public static final String INCIDENT_VERIFIED = "incident_verified";

	public static final String INCIDENT_LOC_NAME = "incident_loc_name";

	public static final String INCIDENT_LOC_LATITUDE = "incident_loc_latitude";

	public static final String INCIDENT_LOC_LONGITUDE = "incident_loc_longitude";

	public static final String INCIDENT_CATEGORIES = "incident_categories";

	public static final String INCIDENT_MEDIA = "incident_media";

	public static final String INCIDENT_IMAGE = "incident_image";

	public static final String INCIDENT_PENDING = "pending";

	public static final String INCIDENTS_TABLE = "incidents";

	public static final String INCIDENTS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ INCIDENTS_TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ INCIDENT_ID
			+ " INTEGER , "
			+ INCIDENT_TITLE
			+ " TEXT NOT NULL, "
			+ INCIDENT_DESC
			+ " TEXT, "
			+ INCIDENT_DATE
			+ " DATE NOT NULL, "
			+ INCIDENT_MODE
			+ " INTEGER, "
			+ INCIDENT_VERIFIED
			+ " INTEGER, "
			+ INCIDENT_LOC_NAME
			+ " TEXT NOT NULL, "
			+ INCIDENT_LOC_LATITUDE
			+ " TEXT NOT NULL, "
			+ INCIDENT_LOC_LONGITUDE
			+ " TEXT NOT NULL, "
			+ INCIDENT_PENDING + " INTEGER DEFAULT 0 " + ")";

	public static final String[] INCIDENTS_COLUMNS = new String[] { ID,
			INCIDENT_ID, INCIDENT_TITLE, INCIDENT_DESC, INCIDENT_DATE,
			INCIDENT_MODE, INCIDENT_VERIFIED, INCIDENT_LOC_NAME,
			INCIDENT_LOC_LATITUDE, INCIDENT_LOC_LONGITUDE, INCIDENT_PENDING };

}
