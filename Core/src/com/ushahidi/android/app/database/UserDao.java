/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.UserEntity;

/**
 * @author eyedol
 */
public class UserDao extends DbContentProvider implements IUserSchema, IUserDao {

	private Cursor cursor;

	private List<UserEntity> listUser;

	private ContentValues initialValues;

	/**
	 * @param db
	 */
	public UserDao(SQLiteDatabase db) {
		super(db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.IUserDao#fetchUsersById(int)
	 */
	@Override
	public List<UserEntity> fetchUsersById(int userId) {
		final String selectionArgs[] = { String.valueOf(userId) };

		final String selection = USER_ID + " = ?";

		listUser = new ArrayList<UserEntity>();

		cursor = super.query(USER_TABLE, USER_COLUMNS, selection,
				selectionArgs, USER_NAME);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				UserEntity user = cursorToEntity(cursor);
				listUser.add(user);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listUser;
	}

	@Override
	public List<UserEntity> fetchUsers() {

		listUser = new ArrayList<UserEntity>();

		cursor = super.query(USER_TABLE, USER_COLUMNS, null,
				null, ID);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				UserEntity user = cursorToEntity(cursor);
				listUser.add(user);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.IUserDao#addUser(com.ushahidi.android
	 * .app.entities.User)
	 */
	@Override
	public boolean addUser(UserEntity user) {
		// set values
		setContentValue(user);
		return super.insert(USER_TABLE, getContentValue()) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.IUserDao#addUser(java.util.List)
	 */
	@Override
	public boolean addUser(List<UserEntity> users) {
		try {
			mDb.beginTransaction();

			for (UserEntity user : users) {

				addUser(user);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.IUserDao#deleteAllUsers()
	 */
	@Override
	public boolean deleteAllUsers() {
		return super.delete(USER_TABLE, null, null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.DbContentProvider#cursorToEntity(android
	 * .database.Cursor)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected UserEntity cursorToEntity(Cursor cursor) {

		UserEntity user = new UserEntity();

		int idIndex;
		int userIdIndex;
		int usernameIndex;
		int userColorIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				user.setDbId(cursor.getInt(idIndex));
			}
			if (cursor.getColumnIndex(USER_ID) != -1) {
				userIdIndex = cursor.getColumnIndexOrThrow(USER_ID);
				user.setUserId(cursor.getInt(userIdIndex));
			}

			if (cursor.getColumnIndex(USER_COLOR) != -1) {
				userColorIndex = cursor.getColumnIndexOrThrow(USER_COLOR);
				user.setColor(cursor.getString(userColorIndex));
			}

			if (cursor.getColumnIndex(USER_COLOR) != -1) {
				usernameIndex = cursor.getColumnIndexOrThrow(USER_NAME);
				user.setUsername(cursor.getString(usernameIndex));
			}

		}
		return user;
	}

	private void setContentValue(UserEntity user) {
		initialValues = new ContentValues();
		initialValues.put(USER_ID, user.getUserId());
		initialValues.put(USER_NAME, user.getUsername());
		initialValues.put(USER_COLOR, user.getColor());
	}

	private ContentValues getContentValue() {
		return initialValues;
	}

}
