package com.ushahidi.android.app.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.IDbEntity;

public abstract class DbContentProvider {

	public SQLiteDatabase mDb;

	public int delete(String tableName, String selection, String[] selectionArgs) {
		return mDb.delete(tableName, selection, selectionArgs);
	}

	public long insert(String tableName, ContentValues values) {
		return mDb.insert(tableName, null, values);
	}

	protected abstract <T extends IDbEntity> T cursorToEntity(Cursor cursor);

	public DbContentProvider(SQLiteDatabase db) {

		this.mDb = db;

	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {

		final Cursor cursor = mDb.query(tableName, columns, selection,
				selectionArgs, null, null, sortOrder);

		return cursor;
	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String sortOrder, String limit) {

		return mDb.query(tableName, columns, selection, selectionArgs, null,
				null, sortOrder, limit);
	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {

		return mDb.query(tableName, columns, selection, selectionArgs, groupBy,
				having, orderBy, limit);
	}

	public int update(String tableName, ContentValues values, String selection,
			String[] selectionArgs) {
		return mDb.update(tableName, values, selection, selectionArgs);
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return mDb.rawQuery(sql, selectionArgs);
	}

}
