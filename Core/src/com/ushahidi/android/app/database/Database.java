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

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.Util;

public class Database {

	private static final String TAG = "UshahidiDatabase";

	private DatabaseHelper mDbHelper;

	private SQLiteDatabase mDb;

	public static final String DATABASE_NAME = "ushahidi_db";

	private static final int DATABASE_VERSION = 17;

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

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + " which destroys all old data");

			List<String> addIncidentColumns;
			List<String> checkinsColums;
			List<String> usersColumns;
			List<String> deploymentColumns;
			List<String> categoriesColumns;
			try {
				// upgrade incident table
				Log.i("Upgrading", "Upgrading incidents table");
				dropColumn(db, IReportSchema.INCIDENTS_TABLE_CREATE,
						IReportSchema.INCIDENTS_TABLE, new String[] {
								IReportSchema.INCIDENT_CATEGORIES,
								IReportSchema.INCIDENT_MEDIA,
								IReportSchema.INCIDENT_IMAGE, "is_unread" });

				// upgrade category table
				Log.i("Upgrading", "Upgrading categories table");
				dropColumn(db, ICategorySchema.CATEGORIES_TABLE_CREATE,
						ICategorySchema.TABLE, new String[] { "is_unread" });

				// upgrade add incident table
				Log.i("Upgrading", "Upgrading offline incidents table");
				// drop the offline report table. It's no longer in use
				db.execSQL("DROP TABLE IF EXISTS "
						+ IOfflineReportSchema.OFFLINE_REPORT_TABLE);

				db.execSQL(IOfflineReportSchema.OFFLINE_REPORT_TABLE_CREATE);
				addIncidentColumns = Database.getColumns(db,
						IOfflineReportSchema.OFFLINE_REPORT_TABLE);
				db.execSQL("ALTER TABLE "
						+ IOfflineReportSchema.OFFLINE_REPORT_TABLE
						+ " RENAME TO temp_"
						+ IOfflineReportSchema.OFFLINE_REPORT_TABLE);
				db.execSQL(IOfflineReportSchema.OFFLINE_REPORT_TABLE_CREATE);
				addIncidentColumns.retainAll(Database.getColumns(db,
						IOfflineReportSchema.OFFLINE_REPORT_TABLE));
				String addIncidentCols = Database.join(addIncidentColumns, ",");
				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						IOfflineReportSchema.OFFLINE_REPORT_TABLE,
						addIncidentCols, addIncidentCols,
						IOfflineReportSchema.OFFLINE_REPORT_TABLE));
				db.execSQL("DROP TABLE IF EXISTS temp_"
						+ IOfflineReportSchema.OFFLINE_REPORT_TABLE);

				// upgrade checkin table
				Log.i("Upgrading", "Upgrading checkins table");
				db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
				checkinsColums = Database.getColumns(db,
						ICheckinSchema.CHECKINS_TABLE);
				db.execSQL("ALTER TABLE " + ICheckinSchema.CHECKINS_TABLE
						+ " RENAME TO temp_" + ICheckinSchema.CHECKINS_TABLE);
				db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
				checkinsColums.retainAll(Database.getColumns(db,
						ICheckinSchema.CHECKINS_TABLE));
				String checkinsCols = Database.join(checkinsColums, ",");

				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						ICheckinSchema.CHECKINS_TABLE, checkinsCols,
						checkinsCols, ICheckinSchema.CHECKINS_TABLE));
				db.execSQL("DROP TABLE IF EXISTS temp_"
						+ ICheckinSchema.CHECKINS_TABLE);

				// upgrade checkin media table
				Log.i("Upgrading", "Upgrading checkin media table");
				dropColumn(db, IMediaSchema.MEDIA_TABLE_CREATE,
						IMediaSchema.TABLE, new String[] {
								"media_thumbnail_link", "media_medium_link" });

				// upgrade checkin users table
				Log.i("Upgrading", "Upgrading checkin users table");
				db.execSQL(IUserSchema.USER_TABLE_CREATE);
				usersColumns = Database.getColumns(db, IUserSchema.USER_TABLE);
				db.execSQL("ALTER TABLE " + IUserSchema.USER_TABLE
						+ " RENAME TO temp_" + IUserSchema.USER_TABLE);
				db.execSQL(IUserSchema.USER_TABLE_CREATE);
				usersColumns.retainAll(Database.getColumns(db,
						IUserSchema.USER_TABLE));
				String usersCols = Database.join(usersColumns, ",");
				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						IUserSchema.USER_TABLE, usersCols, usersCols,
						IUserSchema.USER_TABLE));
				db.execSQL("DROP TABLE IF EXISTS temp_"
						+ IUserSchema.USER_TABLE);

				// upgrade categories table
				db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
				categoriesColumns = Database.getColumns(db,
						ICategorySchema.TABLE);
				db.execSQL("ALTER TABLE " + ICategorySchema.TABLE
						+ " RENAME TO temp_" + ICategorySchema.TABLE);
				db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
				usersColumns.retainAll(Database.getColumns(db,
						ICategorySchema.TABLE));
				String categoriesCols = Database.join(categoriesColumns, ",");
				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						ICategorySchema.TABLE, categoriesCols, categoriesCols,
						ICategorySchema.TABLE));
				db.execSQL("DROP TABLE IF EXISTS temp_" + ICategorySchema.TABLE);

				// upgrade deployment table
				deploymentColumns = Database.getColumns(db, IMapSchema.TABLE);
				db.execSQL("ALTER TABLE " + IMapSchema.TABLE
						+ " RENAME TO temp_" + IMapSchema.TABLE);

				db.execSQL("DROP TABLE IF EXISTS " + IMapSchema.TABLE);

				db.execSQL(IMapSchema.MAP_TABLE_CREATE);
				deploymentColumns.retainAll(Database.getColumns(db,
						IMapSchema.TABLE));

				String deploymentCols = Database.join(deploymentColumns, ",");
				db.execSQL(String.format(
						"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
						IMapSchema.TABLE, deploymentCols, deploymentCols,
						IMapSchema.TABLE));

				db.execSQL("DROP TABLE IF EXISTS temp_" + IMapSchema.TABLE);

				// create missing tables
				db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
				db.execSQL(ICommentSchema.COMMENT_TABLE_CREATE);

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
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public boolean clearData() {

		// deleteAllIncidents();
		// deleteAllCategories();
		// deleteUsers();
		// deleteAllCheckins();
		// deleteCheckinMedia();
		// delete all files
		Util.rmDir(Preferences.savePath);
		return true;

	}

	public boolean clearReports() {

		// deleteAllIncidents();
		deleteAllCategories();
		// deleteUsers();
		// deleteAllCheckins();
		deleteCheckinMedia();
		// delete all files
		Util.rmDir(Preferences.savePath);
		return true;

	}

	public boolean deleteAllCategories() {
		Log.i(TAG, "Deleting all categories");
		// return mDb.delete(CATEGORIES_TABLE, null, null) > 0;
		return true;
	}

	public boolean deleteCategory(int id) {
		Log.i(TAG, "Deleteing all Category by id " + id);
		// return mDb.delete(CATEGORIES_TABLE, CATEGORY_ID + "=" + id, null) >
		// 0;
		return true;
	}

	public boolean deleteCheckinMedia() {
		Log.i(TAG, "Deleting all Media Checkins");
		return mDb.delete(IMediaSchema.TABLE, null, null) > 0;
	}

}
