package org.addhen.ushahidi.data;

import java.io.File;

import org.addhen.ushahidi.data.UshahidiDatabase.Incidents;
import org.addhen.ushahidi.data.UshahidiDatabase.Locations;
import org.addhen.ushahidi.data.UshahidiDatabase.Categories;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class UshahidiDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "UshahidiDatabasHelper";
	private SQLiteDatabase mDatabase;
	private boolean mIsInitializing = false;
	private boolean mUseExternalStorage = false;
	
	private static final String DATABASE_DIRECTORY = "ushahidi";
	private static final String DATABASE_NAME = "ushahidi.sqlite";
	private static final int DATABASE_VERSION = 7;
	private static final String CATEGORIES_TABLE_NAME = "categories";
	private static final String LOCATION_TABLE_NAME = "location";
	private static final String INCIDENTS_TABLE_NAME = "incidents";

	public UshahidiDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		mUseExternalStorage = sp.getBoolean("storage_use_external", false);
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (!mUseExternalStorage) {
			return super.getWritableDatabase();
		}

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			throw new SQLiteDiskIOException("Cannot access external storage: not mounted");
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			throw new SQLiteDiskIOException("Cannot access external storage: mounted read only");
		}

		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase;
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		boolean success = false;
		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			File storage = Environment.getExternalStorageDirectory();
			String path = storage.getAbsolutePath();
			File dir = new File(path, DATABASE_DIRECTORY);
			dir.mkdir();
			File file = new File(dir.getAbsolutePath(), DATABASE_NAME);
			db = SQLiteDatabase.openOrCreateDatabase(file, null);
			int version = db.getVersion();
			if (version != DATABASE_VERSION) {
				db.beginTransaction();
				try {
					if (version == 0) {
						onCreate(db);
					} else {
						onUpgrade(db, version, DATABASE_VERSION);
					}
					db.setVersion(DATABASE_VERSION);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}
			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try { mDatabase.close(); } catch (Exception e) { }
				}
				mDatabase = db;
			} else {
				if (db != null) db.close();
			}
		}
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (!mUseExternalStorage) {
			return super.getReadableDatabase();
		}

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			throw new SQLiteDiskIOException("Cannot access external storage: not mounted");
		}

		if (mDatabase != null && mDatabase.isOpen()) {
			return mDatabase;
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		try {
			return getWritableDatabase();
		} catch (SQLiteException e) {
			Log.e(TAG, "Couldn't open " + DATABASE_NAME + " for writing (will try read-only):", e);
		}

		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			File storage = Environment.getExternalStorageDirectory();
			String path = storage.getAbsolutePath();
			File dir = new File(path, DATABASE_DIRECTORY);
			dir.mkdir();
			File file = new File(dir.getAbsolutePath(), DATABASE_NAME);
			db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
			if (db.getVersion() != DATABASE_VERSION) {
				throw new SQLiteException("Can't upgrade read-only database from version " + db.getVersion() + " to " + DATABASE_VERSION + ": " + file.getAbsolutePath());
			}
			onOpen(db);
			Log.w(TAG, "Opened " + DATABASE_NAME + " in read-only mode");
			mDatabase = db;
			return mDatabase;
		} finally {
			mIsInitializing = false;
			if (db != null && db != mDatabase) db.close();
		}
	}

	@Override
	public synchronized void close() {
		super.close();
		if (mUseExternalStorage && mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating tables");
		// create categories table
		db.execSQL("CREATE TABLE " + CATEGORIES_TABLE_NAME + " ("
				+ Categories._ID + " INTEGER PRIMARY KEY,"
				+ Categories.CATEGORY_ID + " INTEGER,"
				+ Categories.CATEGORY_TITLE + " TEXT," 
				+ Categories.CATEGORY_DESC + " TEXT," 
				+ Categories.CATEGORY_DESC + " TEXT"
				+ ");");
		
		//create location tables
		db.execSQL("CREATE TABLE " + LOCATION_TABLE_NAME + " ("
				+ Locations._ID + " INTEGER PRIMARY KEY," 
				+ Locations.LOCATION_ID + " INTEGER," 
				+ Locations.LOCATION_NAME + " TEXT," 
				+ Locations.LOCATION_LATITUDE + " TEXT,"
				+ Locations.LOCATION_LONGITUDE + " TEXT"
				+ ");");
		//create incident table
		db.execSQL("CREATE TABLE " + INCIDENTS_TABLE_NAME + " ("
				+ Incidents._ID + " INTEGER PRIMARY KEY," 
				+ Incidents.INCIDENT_ID + " INTEGER," 
				+ Incidents.INCIDENT_TITLE + "TEXT,"
				+ Incidents.INCIDENT_DESC + "TEXT,"
				+ Incidents.INCIDENT_MODE + "INTEGER,"
				+ Incidents.INCIDENT_VERIFIED + "INTEGER,"
				+ Incidents.INCIDENT_LOC_NAME + " TEXT,"
				+ Incidents.INCIDENT_LOC_LATITUDE + " TEXT,"
				+ Incidents.INCIDENT_LOC_LONGITUDE + " TEXT,"
				+ Incidents.INCIDENT_CATEGORIES + " TEXT,"
				+ Incidents.INCIDENT_MEDIA + " TEXT" 
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion + " to version " + newVersion);
		if (oldVersion < 7) {
			db.beginTransaction();
			try {
				/*
				 * Upgrading tweets table:
				 *  - Add column TWEET_TYPE
				 *
				db.execSQL("CREATE TEMPORARY TABLE " + TWEETS_TABLE_NAME + "_backup ("
						+ Tweets._ID + " INTEGER PRIMARY KEY," 
						+ Tweets.AUTHOR_ID + " TEXT," 
						+ Tweets.MESSAGE + " TEXT," 
						+ Tweets.SOURCE + " TEXT,"
						+ Tweets.IN_REPLY_TO_STATUS_ID + " INTEGER,"
						+ Tweets.IN_REPLY_TO_AUTHOR_ID + " TEXT,"
						+ Tweets.SENT_DATE + " INTEGER," 
						+ Tweets.CREATED_DATE + " INTEGER"
						+ ");");
				db.execSQL("INSERT INTO " + TWEETS_TABLE_NAME + "_backup SELECT " + Tweets._ID + ", " + Tweets.AUTHOR_ID + ", " + Tweets.MESSAGE + ", " + Tweets.SOURCE + ", " + Tweets.IN_REPLY_TO_AUTHOR_ID + ", " + Tweets.IN_REPLY_TO_STATUS_ID + ", " + Tweets.SENT_DATE + ", " + Tweets.CREATED_DATE + " FROM " + TWEETS_TABLE_NAME + ";");
				db.execSQL("DROP TABLE " + TWEETS_TABLE_NAME + ";");
				db.execSQL("CREATE TABLE " + TWEETS_TABLE_NAME + " ("
						+ Tweets._ID + " INTEGER PRIMARY KEY," 
						+ Tweets.AUTHOR_ID + " TEXT," 
						+ Tweets.MESSAGE + " TEXT," 
						+ Tweets.SOURCE + " TEXT,"
						+ Tweets.TWEET_TYPE + " INTEGER,"
						+ Tweets.IN_REPLY_TO_STATUS_ID + " INTEGER,"
						+ Tweets.IN_REPLY_TO_AUTHOR_ID + " TEXT,"
						+ Tweets.SENT_DATE + " INTEGER," 
						+ Tweets.CREATED_DATE + " INTEGER"
						+ ");");
				db.execSQL("INSERT INTO " + TWEETS_TABLE_NAME + " SELECT " + Tweets._ID + ", " + Tweets.AUTHOR_ID + ", " + Tweets.MESSAGE + ", " + Tweets.SOURCE + ", " + Tweets.TWEET_TYPE_TWEET + ", " + Tweets.IN_REPLY_TO_AUTHOR_ID + ", " + Tweets.IN_REPLY_TO_STATUS_ID + ", " + Tweets.SENT_DATE + ", " + Tweets.CREATED_DATE + " FROM " + TWEETS_TABLE_NAME + "_backup;");
				db.execSQL("DROP TABLE " + TWEETS_TABLE_NAME + "_backup;");

				/*
				 * Upgrading users table:
				 *  - Add column AVATAR_IMAGE
				 *
				db.execSQL("CREATE TEMPORARY TABLE " + USERS_TABLE_NAME + "_backup ("
						+ Users._ID + " INTEGER PRIMARY KEY," 
						+ Users.AUTHOR_ID + " TEXT," 
						+ Users.CREATED_DATE + " INTEGER," 
						+ Users.MODIFIED_DATE + " INTEGER"
						+ ");");
				db.execSQL("INSERT INTO " + USERS_TABLE_NAME + "_backup SELECT " + Users._ID + ", " + Users.AUTHOR_ID + ", " + Users.CREATED_DATE + ", " + Users.MODIFIED_DATE + " FROM " + USERS_TABLE_NAME + ";");
				db.execSQL("DROP TABLE " + USERS_TABLE_NAME + ";");
				db.execSQL("CREATE TABLE " + USERS_TABLE_NAME + " ("
						+ Users._ID + " INTEGER PRIMARY KEY," 
						+ Users.AUTHOR_ID + " TEXT," 
						+ Users.AVATAR_IMAGE + " BLOB," 
						+ Users.CREATED_DATE + " INTEGER," 
						+ Users.MODIFIED_DATE + " INTEGER"
						+ ");");
				db.execSQL("INSERT INTO " + USERS_TABLE_NAME + " SELECT " + Users._ID + ", " + Users.AUTHOR_ID + ", null, " + Users.CREATED_DATE + ", " + Users.MODIFIED_DATE + " FROM " + USERS_TABLE_NAME + "_backup;");
				db.execSQL("DROP TABLE " + USERS_TABLE_NAME + "_backup;");
				db.setTransactionSuccessful();
				Log.d(TAG, "Successfully upgraded database from version " + oldVersion + " to version " + newVersion + ".");
			} catch (SQLException e) {
				Log.e(TAG, "Could not upgrade database from version " + oldVersion + " to version " + newVersion, e);
			} finally {
				db.endTransaction();
			}
		}
	}*/
}
