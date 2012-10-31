package com.ushahidi.android.app.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OpenGeoSmsDao implements IOpenGeoSmsDao, IOpenGeoSmsSchema{

	private SQLiteDatabase mDb;

	public OpenGeoSmsDao(SQLiteDatabase db){
		mDb = db;
	}
	private static String[] array(String...strs){
		return strs;
	}
	private static String[] array(long i){
		return new String[]{ String.valueOf(i) };
	}

	@Override
	public int getReportState(long reportId) {
		Cursor c = mDb.query(
			TABLE,
			array(STATE),
			WHERE,
			array(reportId),
			null,
			null,
			null
		);
		if ( c.getCount() < 1 ){
			
			//free resources
			c.close();
			return STATE_NOT_OPENGEOSMS;
		}
		c.moveToFirst();
		final int reportState = c.getInt(0);
		
		//free resources
		c.close();
		
		return reportState;
	}
	private static final String WHERE=REPORT_ID+"=?";
	@Override
	public boolean addReport(long reportId) {
		ContentValues cv = new ContentValues();
		cv.put(REPORT_ID, reportId);
		cv.put(STATE, STATE_PENDING);
		return mDb.insert(TABLE, null, cv) != -1;
	}

	@Override
	public boolean setReportState(long reportId, int state) {
		switch(state){
		case STATE_PENDING:
		case STATE_SENT:
			ContentValues cv = new ContentValues();
			cv.put(STATE, state);
			return mDb.update(TABLE, cv, WHERE, array(reportId)) > 0;
		default:
			return false;
		}

	}

	@Override
	public boolean deleteReport(long reportId) {
		return mDb.delete(TABLE, WHERE, array(reportId)) > 0;
	}
	@Override
	public boolean deleteReports() {
		mDb.delete(TABLE, null, null);
		return true;
	}

}
