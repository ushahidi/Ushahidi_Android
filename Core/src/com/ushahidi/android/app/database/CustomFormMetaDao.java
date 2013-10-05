package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.CustomFormMetaEntity;

public class CustomFormMetaDao extends DbContentProvider implements
		ICustomFormMetaDao, ICustomFormMetaSchema {

	private Cursor cursor;

	private List<CustomFormMetaEntity> listCustomForm;


	private static final String SORT_ORDER = FORM_ID + " ASC";

	private static final String GROUP_BY = null;

	public CustomFormMetaDao(SQLiteDatabase db) {
		super(db);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CustomFormMetaEntity cursorToEntity(Cursor cursor) {
		CustomFormMetaEntity customForm = new CustomFormMetaEntity();

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				customForm.setDbId(cursor.getInt(cursor
						.getColumnIndexOrThrow(ID)));
			}
			if (cursor.getColumnIndex(FORM_ID) != -1) {
				customForm.setFormId(cursor.getInt(cursor
						.getColumnIndexOrThrow(FORM_ID)));
			}
			if (cursor.getColumnIndex(FIELD_ID) != -1) {
				customForm.setFieldId(cursor.getInt(cursor
						.getColumnIndexOrThrow(FIELD_ID)));
			}
			if (cursor.getColumnIndex(NAME) != -1) {
				customForm.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(NAME)));
			}
			if (cursor.getColumnIndex(TYPE) != -1) {
				customForm.setType(cursor.getInt(cursor
						.getColumnIndexOrThrow(TYPE)));
			}
			if (cursor.getColumnIndex(REQUIRED) != -1) {
				customForm.setRequired(cursor.getInt(cursor
						.getColumnIndexOrThrow(REQUIRED)));
			}
			if (cursor.getColumnIndex(DEFAULT_VALUES) != -1) {
				customForm.setDefaultValues(cursor.getString(cursor
						.getColumnIndexOrThrow(DEFAULT_VALUES)));
			}
			if (cursor.getColumnIndex(IS_DATE) != -1) {
				customForm.setIsDate(cursor.getInt(cursor
						.getColumnIndexOrThrow(IS_DATE)));
			}
			if (cursor.getColumnIndex(IS_PUBLIC_VISIBLE) != -1) {
				customForm.setIsPublicVisible(cursor.getInt(cursor
						.getColumnIndexOrThrow(IS_PUBLIC_VISIBLE)));
			}
			if (cursor.getColumnIndex(IS_PUBLIC_SUBMIT) != -1) {
				customForm.setIsPublicSubmit(cursor.getInt(cursor
						.getColumnIndexOrThrow(IS_PUBLIC_SUBMIT)));
			}
			if (cursor.getColumnIndex(MAX_LEN) != -1) {
				customForm.setMaxLen(cursor.getInt(cursor
						.getColumnIndexOrThrow(MAX_LEN)));
			}
		}

		return customForm;
	}

	private ContentValues createContentValue(CustomFormMetaEntity form) {
		ContentValues values = new ContentValues();
		values.put(FIELD_ID, form.getFieldId());
		values.put(FORM_ID, form.getFormId());
		values.put(NAME, form.getName());
		values.put(TYPE, form.getType());
		values.put(REQUIRED, form.getRequired());
		values.put(DEFAULT_VALUES, form.getDefaultValues());
		values.put(IS_DATE, form.getIsDate());
		values.put(IS_PUBLIC_SUBMIT, form.getIsPublicSubmit());
		values.put(IS_PUBLIC_VISIBLE, form.getIsPublicVisible());
		values.put(MAX_LEN, form.getMaxLen());
		return values;
	}


	@Override
	public List<CustomFormMetaEntity> fetchAllCustomFormMetas() {
		cursor = super.query(TABLE, COLUMNS, null, null, GROUP_BY, null,
				SORT_ORDER, null);

		listCustomForm = new ArrayList<CustomFormMetaEntity>();
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustomFormMetaEntity customForm = cursorToEntity(cursor);
				listCustomForm.add(customForm);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listCustomForm;
	}

	@Override
	public List<CustomFormMetaEntity> fetchCustomFormMetaByFormId(int formId) {
		final String sortOrder = FORM_ID;

		final String selectionArgs[] = { String.valueOf(formId) };

		final String selection = FORM_ID + " = ?";

		listCustomForm = new ArrayList<CustomFormMetaEntity>();

		cursor = super.query(TABLE, COLUMNS, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustomFormMetaEntity report = cursorToEntity(cursor);
				listCustomForm.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listCustomForm;
	}

	@Override
	public boolean deleteAllCustomFormMetas() {
		return super.delete(TABLE, null, null) > 0;
	}

	@Override
	public boolean deleteCustomFormMetas(int formId) {
		final String selectionArgs[] = { String.valueOf(formId) };
		final String selection = ID + " = ?";
		return super.delete(TABLE, selection, selectionArgs) > 0;
	}

	@Override
	public boolean addCustomFormMeta(CustomFormMetaEntity customForm) {
		ContentValues cv = createContentValue(customForm);
		System.out.println(cv);
		return super.insert(TABLE, cv) > 0;
	}

	@Override
	public boolean addCustomFormMetas(List<CustomFormMetaEntity> customForms) {
		try {
			mDb.beginTransaction();

			for (CustomFormMetaEntity customForm : customForms) {

				addCustomFormMeta(customForm);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

}
