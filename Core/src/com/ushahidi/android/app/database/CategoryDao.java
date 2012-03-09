
package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.Category;

public class CategoryDao extends DbContentProvider implements ICategoryDao, ICategorySchema {

    private Cursor cursor;

    private List<Category> listCategory;

    private ContentValues initialValues;

    public CategoryDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Category> fetchAllCategories() {
        final String sortOrder = CATEGORY_POS + " DESC";
        cursor = super.query(CATEGORIES_TABLE, CATEGORIES_COLUMNS, null, null, sortOrder);
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
        final String sortOrder = CATEGORY_POS + " DESC";
        final String columns[] = {
                CATEGORY_TITLE, CATEGORY_POS
        };
        listCategory = new ArrayList<Category>();
        cursor = super.query(CATEGORIES_TABLE, columns, null, null, sortOrder);
        
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
        return super.delete(CATEGORIES_TABLE, null, null) > 0;

    }

    @Override
    public boolean deleteCategory(long id) {
        final String selectionArgs[] = {
            String.valueOf(id)
        };
        final String selection = CATEGORY_ID + " = ?";

        return super.delete(CATEGORIES_TABLE, selection, selectionArgs) > 0;

    }

    @Override
    public boolean addCategory(Category category) {
        // set values
        setContentValue(category);
        return super.insert(CATEGORIES_TABLE, getContentValue()) > 0;
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
            if (cursor.getColumnIndex(CATEGORY_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(CATEGORY_ID);
                category.setDbId(Long.valueOf(cursor.getString(idIndex)));
            }

            if (cursor.getColumnIndex(CATEGORY_TITLE) != -1) {
                titleIndex = cursor.getColumnIndexOrThrow(CATEGORY_TITLE);
                category.setCategoryTitle(cursor.getString(titleIndex));
            }

            if (cursor.getColumnIndex(CATEGORY_COLOR) != -1) {
                colorIndex = cursor.getColumnIndexOrThrow(CATEGORY_COLOR);
                category.setCategoryColor(cursor.getString(colorIndex));
            }

            if (cursor.getColumnIndex(CATEGORY_POS) != -1) {
                positionIndex = cursor.getColumnIndexOrThrow(CATEGORY_POS);
                category.setCategoryPosition(Integer.valueOf(cursor.getString(positionIndex)));
            }

            if (cursor.getColumnIndex(CATEGORY_DESC) != -1) {
                descriptionIndex = cursor.getColumnIndexOrThrow(CATEGORY_DESC);
                category.setCategoryDescription(cursor.getString(descriptionIndex));
            }
        }

        return category;
    }

    protected void setContentValue(Category category) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CATEGORY_ID, category.getCategoryId());
        initialValues.put(CATEGORY_TITLE, category.getCategoryTitle());
        initialValues.put(CATEGORY_DESC, category.getCategoryDescription());
        initialValues.put(CATEGORY_COLOR, category.getCategoryColor());
        initialValues.put(CATEGORY_POS, category.getCategoryPosition());
        initialValues.put(CATEGORY_IS_UNREAD, true);
    }

    protected ContentValues getContentValue() {
        return initialValues;
    }

}
