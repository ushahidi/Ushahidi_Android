/**
 * Code inspired from how AndTweet handles its database
 */
package org.addhen.ushahidi.data;

import java.net.SocketTimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.addhen.ushahidi.net.Connection;
import org.addhen.ushahidi.net.ConnectionException;
import org.addhen.ushahidi.net.ConnectionUnavailableException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

/**
 * Handles the updating of the category table
 * @author henryaddo
 *
 */
public class Categories {
	
	private static final String TAG = "Categories";

	private ContentResolver mContentResolver;
	private Connection mConnection;
	
	private int mNewCategories;
	private long mLastCategoryId = 0;
	
	public Categories(ContentResolver contentResolver, long lastCategoryId ) {
		mContentResolver = contentResolver;
		mLastCategoryId = lastCategoryId;
	}
	
	/**
	 * Load categories
	 * 
	 * @throws ConnectionException
	 * @throws JSONException
	 * @throws SQLiteConstraintException
	 * @throws ConnectionAuthenticationException
	 * @throws ConnectionUnavailableException
	 * @throws SocketTimeoutException
	 */
	public void loadCategories() throws ConnectionException, JSONException, 
		SQLiteConstraintException, 
		ConnectionUnavailableException, SocketTimeoutException{
		
		mNewCategories = 0;
		
		if( mLastCategoryId > 0 ) {
			
			//TODO connection constructor 
			mConnection = new Connection( mLastCategoryId );
		} else {
			mConnection = new Connection();
		}
		
		// get the category for the json object
		JSONArray jArr = mConnection.getCategories();
		
		for( int i = 0; i < jArr.length(); i++ ) {
			JSONObject jo = jArr.getJSONObject(i);
			long lastId = jo.getLong("id");
			
			if( lastId > mLastCategoryId ) {
				mLastCategoryId = lastId;
			}
			
			// insert new categories
			insertNewCategories( jo );
		}
		if( mNewCategories > 0 ) {
			mContentResolver.notifyChange(UshahidiDatabase.Categories.CONTENT_URI, null);
		}
		
		
	}
	
	/**
	 * Insert a record from a JSON object.
	 * 
	 * @param jo
	 * @return Uri
	 * @throws JSONException
	 * @throws SQLiteConstraintException
	 */
	public Uri insertNewCategories(JSONObject jo) throws JSONException, SQLiteConstraintException {
		ContentValues values = new ContentValues();

		// Construct the Uri to existing record
		Long lastCategoryId = Long.parseLong(jo.getString("id"));
		Uri aCategoryUri = ContentUris.withAppendedId(UshahidiDatabase.Categories.CONTENT_URI, lastCategoryId);

		values.put(UshahidiDatabase.Categories.CATEGORY_ID, lastCategoryId.toString());
		values.put(UshahidiDatabase.Categories.CATEGORY_TITLE, jo.getString("title"));

		values.put(UshahidiDatabase.Categories.CATEGORY_DESC, jo.getString("description"));
		values.put(UshahidiDatabase.Categories.CATEGORY_COLOR, jo.getString("color"));

		if ((mContentResolver.update(aCategoryUri, values, null, null)) == 0) {
			mContentResolver.insert(UshahidiDatabase.Categories.CONTENT_URI, values);
			mNewCategories++;
		}
		return aCategoryUri;
	}

	/**
	 * Insert a record from a JSON object.
	 * 
	 * @param jo
	 * @param notify
	 * @return Uri
	 * @throws JSONException
	 * @throws SQLiteConstraintException
	 */
	public Uri insertNewCategories(JSONObject jo, boolean notify) throws JSONException, SQLiteConstraintException {
		Uri aCategoryUri = insertNewCategories(jo);
		if (notify) mContentResolver.notifyChange(aCategoryUri, null);
		return aCategoryUri;
	}
	
	/**
	 * TODO think of something better to use to prune old records
	 * Prune old records from the database to keep the size down.
	 * 
	 * @param sinceTimestamp
	 * @return int
	 */
	public int pruneOldRecords(long sinceTimestamp) {
		return 0;
	}
	
	public int newCount() {
		return mNewCategories;
	}

	public long lastId() {
		return mLastCategoryId;
	}
}
