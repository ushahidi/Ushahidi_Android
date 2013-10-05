package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.CustomFormEntity;

public class CustomFormDao extends DbContentProvider implements ICustomFormDao,
		ICustomFormSchema {

	private Cursor cursor;

	private List<CustomFormEntity> listCustomForm;

	private ContentValues initialValues;

	private static final String SORT_ORDER = FORM_ID + " ASC";

	private static final String GROUP_BY = null;

	public CustomFormDao(SQLiteDatabase db) {
		super(db);
	}
	
	public Cursor fetchAllCustomFormsCursor() {
		Cursor c = super.query(TABLE, COLUMNS, null, null, GROUP_BY, null,
				SORT_ORDER, null);
		return c;
	}

	@Override
	public List<CustomFormEntity> fetchAllCustomForms() {
		cursor = super.query(TABLE, COLUMNS, null, null, GROUP_BY, null,
				SORT_ORDER, null);

		listCustomForm = new ArrayList<CustomFormEntity>();
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustomFormEntity customForm = cursorToEntity(cursor);
				listCustomForm.add(customForm);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listCustomForm;
	}

	@Override
	public List<CustomFormEntity> fetchCustomFormByFormId(int formId) {
		final String sortOrder = FORM_ID;

		final String selectionArgs[] = { String.valueOf(formId) };

		final String selection = FORM_ID + " = ?";

		listCustomForm = new ArrayList<CustomFormEntity>();

		cursor = super.query(TABLE, COLUMNS, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustomFormEntity report = cursorToEntity(cursor);
				listCustomForm.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listCustomForm;
	}

	@Override
	public boolean deleteAllCustomForms() {
		return super.delete(TABLE, null, null) > 0;
	}

	@Override
	public boolean deleteCustomForms(int formId) {
		final String selectionArgs[] = { String.valueOf(formId) };
		final String selection = ID + " = ?";
		return super.delete(TABLE, selection, selectionArgs) > 0;
	}

	@Override
	public boolean addCustomForm(CustomFormEntity customForm) {
		setContentValue(customForm);
		return super.insert(TABLE, getContentValue()) > 0;
	}

	@Override
	public boolean addCustomForms(List<CustomFormEntity> customForms) {
		try {
			mDb.beginTransaction();

			for (CustomFormEntity customForm : customForms) {

				addCustomForm(customForm);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CustomFormEntity cursorToEntity(Cursor cursor) {
		CustomFormEntity customForm = new CustomFormEntity();
		int idIndex;
		int formIdIndex;
		int titleIndex;
		int descriptionIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				customForm.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(FORM_ID) != -1) {
				formIdIndex = cursor.getColumnIndexOrThrow(FORM_ID);
				customForm.setCustomFormId(cursor.getInt(formIdIndex));
			}

			if (cursor.getColumnIndex(TITLE) != -1) {
				titleIndex = cursor.getColumnIndexOrThrow(TITLE);
				customForm.setCustomFormTitle(cursor.getString(titleIndex));
			}

			if (cursor.getColumnIndex(DESCRIPTION) != -1) {
				descriptionIndex = cursor.getColumnIndexOrThrow(DESCRIPTION);
				customForm.setCustomFormDescription(cursor
						.getString(descriptionIndex));
			}
		}

		return customForm;
	}

	private void setContentValue(CustomFormEntity customForm) {
		initialValues = new ContentValues();
		initialValues.put(FORM_ID, customForm.getCustomFormId());
		initialValues.put(TITLE, customForm.getCustomFormTitle());
		initialValues.put(DESCRIPTION, customForm.getCustomFormDescription());
	}

	private ContentValues getContentValue() {
		return initialValues;
	}

}
