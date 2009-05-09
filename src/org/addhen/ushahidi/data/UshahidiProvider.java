package org.addhen.ushahidi.data;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import org.addhen.ushahidi.data.UshahidiDatabase;
import org.addhen.ushahidi.data.UshahidiDatabaseHelper;
import org.addhen.ushahidi.data.UshahidiDatabase.Categories;
import org.addhen.ushahidi.data.UshahidiDatabase.Locations;
import org.addhen.ushahidi.data.UshahidiDatabase.Incidents;


public class UshahidiProvider extends ContentProvider {
	private static final String CATEGORIES_TABLE_NAME = "categories";
	private static final String LOCATIONS_TABLE_NAME = "location";
	private static final String INCIDENTS_TABLE_NAME = "incidents";
	
	private static final UriMatcher sUriMatcher;
	private UshahidiDatabaseHelper mOpenHelper;

	private static HashMap<String, String> sIncidentsProjectionMap;
	private static HashMap<String, String> sLocationsProjectionMap;
	private static HashMap<String, String> sCategoriesProjectionMap;

	private static final int INCIDENTS = 1;
	private static final int INCIDENTS_ID = 2;
	private static final int LOCATIONS = 3;
	private static final int LOCATIONS_ID = 4;
	private static final int CATEGORIES = 5;
	private static final int CATEGORIES_ID = 6;
	
	/**
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new UshahidiDatabaseHelper(getContext());
		return (mOpenHelper == null) ? false : true;
	}
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case INCIDENTS:
			count = db.delete(INCIDENTS_TABLE_NAME, selection, selectionArgs);
			break;

		case INCIDENTS_ID:
			String incidentID = uri.getPathSegments().get(1);
			
			count = db.delete(INCIDENTS_TABLE_NAME, Incidents._ID + "="+ incidentID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		case CATEGORIES:
			count = db.delete(CATEGORIES_TABLE_NAME, selection, selectionArgs);
			break;

		case CATEGORIES_ID:
			String categoryID = uri.getPathSegments().get(1);
			
			count = db.delete(CATEGORIES_TABLE_NAME, Categories._ID + "="+ categoryID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		case LOCATIONS:
			count = db.delete(LOCATIONS_TABLE_NAME, selection, selectionArgs);
			break;

		case LOCATIONS_ID:
			String locationID = uri.getPathSegments().get(1);
			
			count = db.delete(LOCATIONS_TABLE_NAME, Locations._ID + "="+ locationID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INCIDENTS:
			return Incidents.CONTENT_TYPE;

		case INCIDENTS_ID:
			return Incidents.CONTENT_ITEM_TYPE;

		case CATEGORIES:
			return Categories.CONTENT_TYPE;

		case CATEGORIES_ID:
			return Categories.CONTENT_ITEM_TYPE;

		case LOCATIONS:
			return Locations.CONTENT_TYPE;

		case LOCATIONS_ID:
			return Locations.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		String table;
		String nullColumnHack;
		Uri contentUri;

		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		switch (sUriMatcher.match(uri)) {
		case INCIDENTS:
			table = INCIDENTS_TABLE_NAME;
			nullColumnHack = Incidents.INCIDENT_ID;
			contentUri = Incidents.CONTENT_URI;
			break;

		case CATEGORIES:
			
			break;

		case LOCATIONS:
			
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return null;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case INCIDENTS:
			count = db.update(INCIDENTS_TABLE_NAME, values, selection, selectionArgs);
			break;

		case INCIDENTS_ID:
			String incidentID = uri.getPathSegments().get(1);
			
			count = db.update(INCIDENTS_TABLE_NAME, values, Incidents._ID + "="+ incidentID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		case CATEGORIES:
			count = db.update(CATEGORIES_TABLE_NAME, values, selection, selectionArgs);
			break;

		case CATEGORIES_ID:
			String categoryID = uri.getPathSegments().get(1);
			
			count = db.update(CATEGORIES_TABLE_NAME, values, Categories._ID + "="+ categoryID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);
			break;

		case LOCATIONS:
			count = db.update(LOCATIONS_TABLE_NAME, values, selection, selectionArgs);
			break;

		case LOCATIONS_ID:
			String locationID = uri.getPathSegments().get(1);
			
			count = db.update(LOCATIONS_TABLE_NAME, values, Locations._ID + "="+ locationID
					+(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
					selectionArgs);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return count;
	}
	
	// Static Definitions for UriMatcher and Projection Maps
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, INCIDENTS_TABLE_NAME, INCIDENTS);
		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, INCIDENTS_TABLE_NAME + "/#", INCIDENTS_ID);

		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, LOCATIONS_TABLE_NAME, LOCATIONS);
		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, LOCATIONS_TABLE_NAME + "/#", LOCATIONS_ID);

		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, CATEGORIES_TABLE_NAME, CATEGORIES);
		sUriMatcher.addURI(UshahidiDatabase.AUTHORITY, CATEGORIES_TABLE_NAME + "/#", CATEGORIES_ID);

		sIncidentsProjectionMap = new HashMap<String, String>();
		sIncidentsProjectionMap.put(Incidents._ID, Incidents._ID);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_ID, Incidents.INCIDENT_ID);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_TITLE,Incidents.INCIDENT_TITLE);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_DESC, Incidents.INCIDENT_DESC);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_MODE , Incidents.INCIDENT_MODE );
		sIncidentsProjectionMap.put(Incidents.INCIDENT_VERIFIED, Incidents.INCIDENT_VERIFIED);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_LOC_NAME, Incidents.INCIDENT_LOC_NAME);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_LOC_LATITUDE, Incidents.INCIDENT_LOC_LATITUDE);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_LOC_LONGITUDE, Incidents.INCIDENT_LOC_LONGITUDE);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_CATEGORIES, Incidents.INCIDENT_CATEGORIES);
		sIncidentsProjectionMap.put(Incidents.INCIDENT_MEDIA, Incidents.INCIDENT_MEDIA);

		sCategoriesProjectionMap = new HashMap<String, String>();
		sCategoriesProjectionMap.put(Categories._ID, Categories._ID);
		sCategoriesProjectionMap.put(Categories.CATEGORY_ID, Categories.CATEGORY_ID);
		sCategoriesProjectionMap.put(Categories.CATEGORY_DESC, Categories.CATEGORY_DESC);
		sCategoriesProjectionMap.put(Categories.CATEGORY_COLOR, Categories.CATEGORY_COLOR);

		sLocationsProjectionMap = new HashMap<String, String>();
		sLocationsProjectionMap.put(Locations._ID, Locations._ID);
		sLocationsProjectionMap.put(Locations.LOCATION_ID, Locations.LOCATION_ID);
		sLocationsProjectionMap.put(Locations.LOCATION_NAME, Locations.LOCATION_NAME);
		sLocationsProjectionMap.put(Locations.LOCATION_LATITUDE, Locations.LOCATION_LATITUDE);
		sLocationsProjectionMap.put(Locations.LOCATION_LONGITUDE,Locations.LOCATION_LONGITUDE);
	}
	
}
