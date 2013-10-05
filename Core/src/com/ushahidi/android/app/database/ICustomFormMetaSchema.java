package com.ushahidi.android.app.database;


/**
 * 
 * @author markov00
 * 
 */
public interface ICustomFormMetaSchema {

	public static final String ID = "_id";

	public static final String FORM_ID = "form_id";
	public static final String FIELD_ID = "field_id";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DEFAULT_VALUES = "default_values";
	public static final String REQUIRED = "required";
	public static final String MAX_LEN = "max_len";
	public static final String IS_DATE = "is_date";
	public static final String IS_PUBLIC_VISIBLE = "is_public_visible";
	public static final String IS_PUBLIC_SUBMIT = "is_public_submit";

	public static final String[] COLUMNS = new String[] { ID, FORM_ID,
			FIELD_ID, NAME, TYPE, DEFAULT_VALUES, REQUIRED, MAX_LEN, IS_DATE,
			IS_PUBLIC_VISIBLE, IS_PUBLIC_SUBMIT };

	public static final String TABLE = "customformmeta";

	public static final String CUSTOM_FORM_META_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FORM_ID + " INTEGER , "
			+ FIELD_ID + " INTEGER , "
			+ NAME + " TEXT , "
			+ TYPE + " INTEGER , "
			+ DEFAULT_VALUES + " TEXT , "
			+ REQUIRED + " INTEGER , "
			+ MAX_LEN + " INTEGER , "
			+ IS_DATE + " INTEGER , "
			+ IS_PUBLIC_VISIBLE + " INTEGER , "
			+ IS_PUBLIC_SUBMIT + " INTEGER "
			+" )";
}
