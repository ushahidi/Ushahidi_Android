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
import android.util.Log;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.Util;

public class Database {

	/**
	 * Group of constants that specify the different columns in the offline
	 * incident table and there corresponding column indexes
	 */
	public static final int ADD_INCIDENT_ID_INDEX = 0;

	public static final int ADD_INCIDENT_TITLE_INDEX = 1;

	public static final int ADD_INCIDENT_DESC_INDEX = 2;

	public static final int ADD_INCIDENT_DATE_INDEX = 3;

	public static final int ADD_INCIDENT_HOUR_INDEX = 4;

	public static final int ADD_INCIDENT_MINUTE_INDEX = 5;

	public static final int ADD_INCIDENT_AMPM_INDEX = 6;

	public static final int ADD_INCIDENT_CATEGORIES_INDEX = 7;

	public static final int INCIDENT_LOC_NAME_INDEX = 8;

	public static final int INCIDENT_LOC_LATITUDE_INDEX = 9;

	public static final int INCIDENT_LOC_LONGITUDE_INDEX = 10;

	public static final int ADD_INCIDENT_PHOTO_INDEX = 11;

	public static final int ADD_INCIDENT_VIDEO_INDEX = 12;

	public static final int ADD_INCIDENT_NEWS_INDEX = 13;

	public static final int ADD_PERSON_FIRST_INDEX = 14;

	public static final int ADD_PERSON_LAST_INDEX = 15;

	public static final int ADD_PERSON_EMAIL_INDEX = 16;

	/**
     */

	private static final String TAG = "UshahidiDatabase";

	private DatabaseHelper mDbHelper;

	private SQLiteDatabase mDb;

	public static final String DATABASE_NAME = "ushahidi_db";

	private static final int DATABASE_VERSION = 16;

	// NOTE: the incident ID is used as the row ID.
	// Furthermore, if a row already exists, an insert will replace
	// the old row upon conflict.

	private final Context mContext;

	public static ReportDao mReportDao; // Report table

	public static CategoryDao mCategoryDao; // Category table

	public static MapDao mMapDao; // Map aka deployment table

	public static ReportCategoryDao mReportCategoryDao; // ReportCategory table

	public static MediaDao mMediaDao; // Media table

	public static OfflineReportDao mOfflineReport; // Offline reports

	public static CheckinDao mCheckin; // checkins

	public static UserDao mUserDao; // user

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
			db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
			// create map aka deployment table
			db.execSQL(IMapSchema.MAP_TABLE_CREATE);
			db.execSQL(IReportCategorySchema.REPORT_CATEGORY_TABLE_CREATE);
			db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
			db.execSQL(IUserSchema.USER_TABLE_CREATE);
			db.execSQL(ICheckinSchema.CHECKINS_TABLE_CREATE);
			db.execSQL(IOfflineReportSchema.OFFLINE_REPORT_TABLE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + " which destroys all old data");
			List<String> incidentsColumns;
			List<String> categoriesColumns;
			List<String> addIncidentColumns;
			List<String> checkinsColums;
			List<String> checkinsMediaColums;
			List<String> usersColumns;
			// List<String> deploymentColumns;

			// upgrade incident table
			db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
			incidentsColumns = Database.getColumns(db,
					IReportSchema.INCIDENTS_TABLE);
			db.execSQL("ALTER TABLE " + IReportSchema.INCIDENTS_TABLE
					+ " RENAME TO temp_" + IReportSchema.INCIDENTS_TABLE);
			db.execSQL(IReportSchema.INCIDENTS_TABLE_CREATE);
			incidentsColumns.retainAll(Database.getColumns(db,
					IReportSchema.INCIDENTS_TABLE));
			String cols = Database.join(incidentsColumns, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
					IReportSchema.INCIDENTS_TABLE, cols, cols,
					IReportSchema.INCIDENTS_TABLE));
			db.execSQL("DROP TABLE IF EXISTS temp_"
					+ IReportSchema.INCIDENTS_TABLE);

			// upgrade category table
			db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
			categoriesColumns = Database.getColumns(db, ICategorySchema.TABLE);
			db.execSQL("ALTER TABLE " + ICategorySchema.TABLE
					+ " RENAME TO temp_" + ICategorySchema.TABLE);
			db.execSQL(ICategorySchema.CATEGORIES_TABLE_CREATE);
			categoriesColumns.retainAll(Database.getColumns(db,
					ICategorySchema.TABLE));
			String catsCols = Database.join(categoriesColumns, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
					ICategorySchema.TABLE, catsCols, catsCols,
					ICategorySchema.TABLE));
			db.execSQL("DROP TABLE IF EXISTS temp_" + ICategorySchema.TABLE);

			// upgrade add incident table
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
					IOfflineReportSchema.OFFLINE_REPORT_TABLE, addIncidentCols,
					addIncidentCols, IOfflineReportSchema.OFFLINE_REPORT_TABLE));
			db.execSQL("DROP TABLE IF EXISTS temp_"
					+ IOfflineReportSchema.OFFLINE_REPORT_TABLE);

			// upgrade checkin table
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
					ICheckinSchema.CHECKINS_TABLE, checkinsCols, checkinsCols,
					ICheckinSchema.CHECKINS_TABLE));
			db.execSQL("DROP TABLE IF EXISTS temp_"
					+ ICheckinSchema.CHECKINS_TABLE);

			// upgrade checkin media table
			db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
			checkinsMediaColums = Database.getColumns(db, IMediaSchema.TABLE);
			db.execSQL("ALTER TABLE " + IMediaSchema.TABLE + " RENAME TO temp_"
					+ IMediaSchema.TABLE);
			db.execSQL(IMediaSchema.MEDIA_TABLE_CREATE);
			checkinsMediaColums.retainAll(Database.getColumns(db,
					IMediaSchema.TABLE));
			String checkinsMediaCols = Database.join(checkinsMediaColums, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s FROM temp_%s",
					IMediaSchema.TABLE, checkinsMediaCols, checkinsMediaCols,
					IMediaSchema.TABLE));
			db.execSQL("DROP TABLE IF EXISTS temp_" + IMediaSchema.TABLE);

			// upgrade checkin users table
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
			db.execSQL("DROP TABLE IF EXISTS temp_" + IUserSchema.USER_TABLE);

			// upgrade deployment table
			db.execSQL("DROP TABLE IF EXISTS " + IMapSchema.TABLE);
			onCreate(db);
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
