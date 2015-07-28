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

package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class Database {

	private static final String TAG = "UshahidiDatabase";

	private DatabaseHelper mDbHelper;

	private SQLiteDatabase mDb;

	public static final String DATABASE_NAME = "ushahidi_db";

	private static final int DATABASE_VERSION = 20;

	private final Context mContext;

	public static ReportDao mReportDao; // Report table

	public static CategoryDao mCategoryDao; // Category table

	public static MapDao mMapDao; // Map aka deployment table

	public static ReportCategoryDao mReportCategoryDao; // ReportCategory table

	public static MediaDao mMediaDao; // Media table

	public static OfflineReportDao mOfflineReport; // Offline reports

	public static CheckinDao mCheckin; // checkins

	public static UserDao mUserDao; // user

	public static CommentDao mCommentDao; // comment

	public static OpenGeoSmsDao mOpenGeoSmsDao;
	
	public static CustomFormDao mCustomFormDao; // Custom form table
	public static CustomFormMetaDao mCustomFormMetaDao; // Custom form table
	public static ReportCustomFormDao mReportCustomFormDao;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
			db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
			if (!doesVirtualTableExists(db, IMapSchema.TABLE)) {
				// create map aka deployment table
				db.execSQL(IMapSchema.MAP_TABLE_CREATE);
			}

			// create default map
			// TODO:: check if default map is set.
			db.execSQL(IMapSchema.DEFAULT_MAP_CREATE);
			db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
			db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
			db.execSQL(IUserSchema.USER_TABLE_CREATE);
			db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
			db.execSQL(IOfflineReportSchema.OFFLINE_REPORT_TABLE_CREATE);
			db.execSQL(ICommentSchema.COMMENT_TABLE_CREATE);
			db.execSQL(IOpenGeoSmsSchema.OPENGEOSMS_TABLE_CREATE);
			db.execSQL(ICustomFormSchema.CUSTOM_FORM_TABLE_CREATE);
			db.execSQL(ICustomFormMetaSchema.CUSTOM_FORM_META_TABLE_CREATE);
			db.execSQL(IReportCustomFormSchema.REPORT_CUSTOM_FORM_TABLE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + " which destroys all old data");

			List<String> reportCategoryColumns;

			try {

				// upgrade report category
				db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
				reportCategoryColumns = Database.getColumns(db,
						IReportCategorySchema.TABLE);
				
				db.execSQL("ALTER TABLE " + IReportCategorySchema.TABLE
						+ " RENAME TO temp_" + IReportCategorySchema.TABLE);
				db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
				
				reportCategoryColumns.retainAll(Database.getColumns(db,
						IReportCategorySchema.TABLE));
				String reportsCategoryCols = Database.join(
						reportCategoryColumns, ",");
				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						IReportCategorySchema.TABLE, reportsCategoryCols,
						reportsCategoryCols, IReportCategorySchema.TABLE));
				db.execSQL("DROP TABLE IF EXISTS temp_"
						+ IReportCategorySchema.TABLE);

				onCreate(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Credits http://goo.gl/7kOpU
	 * 
	 * @param db
	 * @param tableName
	 * @return
	 */
	public static List<String> getColumns(SQLiteDatabase db, String tableName) {
		List<String> ar = null;
		Cursor c = null;

		try {
			c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
			}

		} catch (Exception e) {
			Log.v(tableName, e.getMessage(), e);
			e.printStackTrace();
		} finally {
			if (c != null)
				c.close();
		}
		return ar;
	}

	public static String join(List<String> list, String delim) {
		StringBuilder buf = new StringBuilder();
		int num = list.size();
		for (int i = 0; i < num; i++) {
			if (i != 0)
				buf.append(delim);
			buf.append((String) list.get(i));
		}
		return buf.toString();
	}

	public Database(Context context) {
		this.mContext = context;
	}

	public static List<String> getTableColumns(SQLiteDatabase db,
			String tableName) {
		ArrayList<String> columns = new ArrayList<String>();
		String cmd = "pragma table_info(" + tableName + ");";
		Cursor cur = db.rawQuery(cmd, null);

		while (cur.moveToNext()) {
			columns.add(cur.getString(cur.getColumnIndex("name")));
		}
		cur.close();

		return columns;
	}

	// Credits:
	// http://udinic.wordpress.com/2012/05/09/sqlite-drop-column-support/
	private static void dropColumn(SQLiteDatabase db, String createTableCmd,
			String tableName, String[] colsToRemove) {

		List<String> updatedTableColumns = getTableColumns(db, tableName);
		// Remove the columns we don't want anymore from the table's list of
		// columns
		updatedTableColumns.removeAll(Arrays.asList(colsToRemove));

		String columnsSeperated = TextUtils.join(",", updatedTableColumns);

		db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName
				+ "_old;");

		// Creating the table on its new format (no redundant columns)
		db.execSQL(createTableCmd);

		// Populating the table with the data
		db.execSQL("INSERT INTO " + tableName + "(" + columnsSeperated
				+ ") SELECT " + columnsSeperated + " FROM " + tableName
				+ "_old;");
		db.execSQL("DROP TABLE " + tableName + "_old;");
	}

	public static boolean doesVirtualTableExists(SQLiteDatabase db,
			String tableName) {

		Cursor cursor = db
				.rawQuery(
						String.format(
								"SELECT DISTINCT tbl_name from sqlite_master where tbl_name ='%s'",
								tableName), null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public Database open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		mDb = mDbHelper.getWritableDatabase();
		mReportDao = new ReportDao(mDb);
		mCategoryDao = new CategoryDao(mDb);
		mMapDao = new MapDao(mDb);
		mMediaDao = new MediaDao(mDb);
		mReportCategoryDao = new ReportCategoryDao(mDb);
		mOfflineReport = new OfflineReportDao(mDb);
		mCheckin = new CheckinDao(mDb);
		mUserDao = new UserDao(mDb);
		mCommentDao = new CommentDao(mDb);
		mOpenGeoSmsDao = new OpenGeoSmsDao(mDb);
		mCustomFormDao = new CustomFormDao(mDb);
		mCustomFormMetaDao = new CustomFormMetaDao(mDb);
		mReportCustomFormDao = new ReportCustomFormDao(mDb);
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

}
