
package com.ushahidi.android.app.database;

/**
 * 
 * @author markov00
 *
 */
public interface ICustomFormSchema {

	public static final String ID = "_id";

	public static final String FORM_ID = "form_id";

	public static final String TITLE = "form_title";

	public static final String DESCRIPTION = "form_desc";

	

	public static final String[] COLUMNS = new String[] { ID, FORM_ID,
			TITLE, DESCRIPTION};

	public static final String TABLE = "customform";

	public static final String CUSTOM_FORM_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE
			+ " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FORM_ID
			+ " INTEGER , "
			+ TITLE
			+ " TEXT NOT NULL, "
			+ DESCRIPTION
			+ " TEXT )";
}
