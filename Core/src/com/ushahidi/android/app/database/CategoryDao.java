package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.Category;

public class CategoryDao extends DbContentProvider implements ICategoryDao,
		ICategorySchema {

	private Cursor cursor;

	private List<Category> listCategory;

	private ContentValues initialValues;

	public CategoryDao(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public List<Category> fetchAllCategories() {
		final String sortOrder = POSITION + " DESC";
		cursor = super.query(TABLE, COLUMNS, null, null, sortOrder);
		listCategory = new ArrayList<Category>();
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Category category = cursorToEntity(cursor);
				listCategory.add(category);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listCategory;
	}

	@Override
	public List<Category> fetchAllCategoryTitles() {
		final String sortOrder = POSITION + " DESC";
		final String columns[] = { TITLE, POSITION };
		listCategory = new ArrayList<Category>();
		cursor = super.query(TABLE, columns, null, null, sortOrder);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {

				Category category = cursorToEntity(cursor);

				listCategory.add(category);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listCategory;

	}

	@Override
	public boolean deleteAllCategories() {
		return super.delete(TABLE, null, null) > 0;

	}

	@Override
	public boolean deleteCategory(long id) {
		final String selectionArgs[] = { String.valueOf(id) };
		final String selection = ID + " = ?";

		return super.delete(TABLE, selection, selectionArgs) > 0;

	}

	@Override
	public boolean addCategory(Category category) {
		// set values
		setContentValue(category);
		return super.insert(TABLE, getContentValue()) > 0;
	}

	@Override
	public boolean addCategories(List<Category> categories) {
		try {
			mDb.beginTransaction();

			for (Category category : categories) {

				addCategory(category);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Category cursorToEntity(Cursor cursor) {
		Category category = new Category();
		int titleIndex;
		int idIndex;
		int colorIndex;
		int positionIndex;
		int descriptionIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				category.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(TITLE) != -1) {
				titleIndex = cursor.getColumnIndexOrThrow(TITLE);
				category.setCategoryTitle(cursor.getString(titleIndex));
			}

			if (cursor.getColumnIndex(COLOR) != -1) {
				colorIndex = cursor.getColumnIndexOrThrow(COLOR);
				category.setCategoryColor(cursor.getString(colorIndex));
			}

			if (cursor.getColumnIndex(POSITION) != -1) {
				positionIndex = cursor.getColumnIndexOrThrow(POSITION);
				category.setCategoryPosition(Integer.valueOf(cursor
						.getString(positionIndex)));
			}

			if (cursor.getColumnIndex(DESCRIPTION) != -1) {
				descriptionIndex = cursor.getColumnIndexOrThrow(DESCRIPTION);
				category.setCategoryDescription(cursor
						.getString(descriptionIndex));
			}
		}

		return category;
	}

	private void setContentValue(Category category) {
		initialValues = new ContentValues();
		initialValues.put(ID, category.getDbId());
		initialValues.put(TITLE, category.getCategoryTitle());
		initialValues.put(DESCRIPTION, category.getCategoryDescription());
		initialValues.put(COLOR, category.getCategoryColor());
		initialValues.put(POSITION, category.getCategoryPosition());

	}

	private ContentValues getContentValue() {
		return initialValues;
	}

}
