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

	private static final String SORT_ORDER = POSITION+" ASC";

	private static final String GROUP_BY = null;

	public CategoryDao(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public List<Category> fetchAllCategories() {
		cursor = super.query(TABLE, COLUMNS, null, null, GROUP_BY, null,
				SORT_ORDER, null);

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

		final String columns[] = { ID, CATEGORY_ID, TITLE, COLOR, POSITION,
				PARENT_ID };
		final String selection = PARENT_ID +" = ?";
		listCategory = new ArrayList<Category>();

		cursor = super.query(TABLE, columns, selection, new String[]{String.valueOf(0)}, GROUP_BY, null,
				SORT_ORDER, null);

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
	
	public List<Category> fetchChildrenCategories(int parentId) {

		final String columns[] = { ID, CATEGORY_ID, TITLE, COLOR, POSITION,
				PARENT_ID };
		final String selection = PARENT_ID +" = ?";
		listCategory = new ArrayList<Category>();
		
		cursor = super.query(TABLE, columns, selection, new String[]{String.valueOf(parentId)}, GROUP_BY, null,
				SORT_ORDER, null);

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
	public List<Category> fetchCategoryByReportId(int reportId) {

		final String sql = "SELECT *" + " FROM " + TABLE
				+ " category INNER JOIN " + IReportCategorySchema.TABLE
				+ " categories ON category." + CATEGORY_ID + " = categories."
				+ IReportCategorySchema.CATEGORY_ID + " WHERE categories."
				+ IReportCategorySchema.REPORT_ID + " =? " + " GROUP BY "
				+ PARENT_ID + " ORDER BY  " + POSITION + " DESC";
		listCategory = new ArrayList<Category>();
		cursor = super.rawQuery(sql, new String[] { String.valueOf(reportId) });

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
	public boolean deleteCategory(int id) {
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
		int categoryIdIndex;
		int parentIdIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				category.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(CATEGORY_ID) != -1) {
				categoryIdIndex = cursor.getColumnIndexOrThrow(CATEGORY_ID);
				category.setCategoryId(cursor.getInt(categoryIdIndex));
			}

			if (cursor.getColumnIndex(PARENT_ID) != -1) {
				parentIdIndex = cursor.getColumnIndexOrThrow(PARENT_ID);
				category.setParentId(cursor.getInt(parentIdIndex));
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
		initialValues.put(CATEGORY_ID, category.getCategoryId());
		initialValues.put(PARENT_ID, category.getParentId());
		initialValues.put(TITLE, category.getCategoryTitle());
		initialValues.put(DESCRIPTION, category.getCategoryDescription());
		initialValues.put(COLOR, category.getCategoryColor());
		initialValues.put(POSITION, category.getCategoryPosition());

	}

	private ContentValues getContentValue() {
		return initialValues;
	}

}
