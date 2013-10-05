package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.ReportCustomFormEntity;

/**
 * Define the methods for interacting with the report custom forms table.
 * 
 * @author markov00
 */
public class ReportCustomFormDao extends DbContentProvider implements IReportCustomFormDao, IReportCustomFormSchema {

	public ReportCustomFormDao(SQLiteDatabase db) {
		super(db);
	}

	@Override
	protected ReportCustomFormEntity cursorToEntity(Cursor cursor) {
		ReportCustomFormEntity cf = new ReportCustomFormEntity();

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				cf.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
			}
			if (cursor.getColumnIndex(FORM_ID) != -1) {
				cf.setFormId(cursor.getInt(cursor.getColumnIndexOrThrow(FORM_ID)));
			}

			if (cursor.getColumnIndex(REPORT_ID) != -1) {
				cf.setReportId(cursor.getInt(cursor.getColumnIndexOrThrow(REPORT_ID)));
			}

			if (cursor.getColumnIndex(CUSTOM_FIELD_ID) != -1) {
				cf.setFieldId(cursor.getInt(cursor.getColumnIndexOrThrow(CUSTOM_FIELD_ID)));
			}

			if (cursor.getColumnIndex(CUSTOM_FORM_VALUE) != -1) {
				cf.setValue(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOM_FORM_VALUE)));
			}
			if (cursor.getColumnIndex(CUSTOM_FORM_NAME) != -1) {
				cf.setFieldName(cursor.getString(cursor.getColumnIndexOrThrow(CUSTOM_FORM_NAME)));
			}
		}
		return cf;
	}

	private ContentValues buildContentValues(ReportCustomFormEntity cf) {
		ContentValues cValues = new ContentValues();
		cValues.put(FORM_ID, cf.getFormId());
		cValues.put(REPORT_ID, cf.getReportId());
		cValues.put(PENDING, cf.getPending());
		cValues.put(CUSTOM_FIELD_ID, cf.getFieldId());
		cValues.put(CUSTOM_FORM_VALUE, cf.getValue());
		cValues.put(CUSTOM_FORM_NAME, cf.getFieldName());
		return cValues;
	}

	@Override
	public boolean addReportCustomForm(List<ReportCustomFormEntity> customForms) {
		try {
			mDb.beginTransaction();
			
			for (ReportCustomFormEntity cf : customForms) {
				addReportCustomForm(cf);
			}
			
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	@Override
	public boolean addReportCustomForm(ReportCustomFormEntity customForm) {
		return super.insert(TABLE, buildContentValues(customForm)) > 0;
	}

	@Override
	public List<ReportCustomFormEntity> fetchReportCustomForms(int reportId) {
		List<ReportCustomFormEntity> list = new ArrayList<ReportCustomFormEntity>();

		final String selectionArgs[] = { String.valueOf(reportId) };
		final String selection = REPORT_ID + " =?";
		
		Cursor cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				ReportCustomFormEntity cf = cursorToEntity(cursor);
				list.add(cf);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return list;
	}

	@Override
	public List<ReportCustomFormEntity> fetchPendingReportCustomForms(int reportId) {
		List<ReportCustomFormEntity> list = new ArrayList<ReportCustomFormEntity>();

		final String selectionArgs[] = { String.valueOf(reportId), "1"};
		final String selection = REPORT_ID + " =? AND "+PENDING+" =?";
		
		Cursor cursor = super.query(TABLE, COLUMNS, selection, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				ReportCustomFormEntity cf = cursorToEntity(cursor);
				list.add(cf);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return list;
	}

	@Override
	public boolean deleteAllReportCustomForms() {
		return super.delete(TABLE, null, null) > 0;
	}

	@Override
	public boolean deleteReportCustomFormsByReportId(int reportId) {
		final String selectionArgs[] = { String.valueOf(reportId) };
		final String selection = REPORT_ID + " =?";
		return super.delete(TABLE, selection, selectionArgs) > 0;
	}

}
