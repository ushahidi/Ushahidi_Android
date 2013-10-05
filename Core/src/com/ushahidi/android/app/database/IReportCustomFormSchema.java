package com.ushahidi.android.app.database;

/**
 * Defines schema for custom form values of reports
 * 
 * @author markov00
 */
public interface IReportCustomFormSchema {


	public static final int FETCHED = 0;

	public static final String ID = "_id";

	public static final String FORM_ID = "form_id";
	
	public static final String REPORT_ID = "report_id";

	public static final String CUSTOM_FIELD_ID = "fieldid";

	public static final String CUSTOM_FORM_VALUE = "value";
	
	public static final String CUSTOM_FORM_NAME = "name";

	public static final String PENDING = "pending";

	public static final String TABLE = "reportcustomform";

	public static final String REPORT_CUSTOM_FORM_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FORM_ID
			+ " INTEGER , "
			+ REPORT_ID
			+ " INTEGER , "
			+ CUSTOM_FIELD_ID
			+ " INTEGER, "
			+ CUSTOM_FORM_VALUE
			+ " TEXT, "
			+ CUSTOM_FORM_NAME
			+ " TEXT, "+ PENDING + " INTEGER DEFAULT 0 " + ")";

	public static final String[] COLUMNS = new String[] { ID,FORM_ID,
			REPORT_ID, CUSTOM_FIELD_ID, CUSTOM_FORM_VALUE,CUSTOM_FORM_NAME, PENDING };

}
