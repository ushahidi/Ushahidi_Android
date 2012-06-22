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

import com.ushahidi.android.app.entities.Comment;

/**
 * @author eyedol
 * 
 */
public class CommentDao extends DbContentProvider implements ICommentDao,
		ICommentSchema {

	private Cursor cursor;

	private List<Comment> listComment;

	private ContentValues initialValues;

	/**
	 * @param db
	 */
	public CommentDao(SQLiteDatabase db) {
		super(db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#addComment(java.util.List)
	 */
	@Override
	public boolean addComment(List<Comment> comments) {
		try {
			mDb.beginTransaction();

			for (Comment comment : comments) {

				addComment(comment);
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
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#addComment(com.ushahidi
	 * .android.app.entities.Comment)
	 */
	@Override
	public boolean addComment(Comment comment) {
		// set values
		setContentValue(comment);
		return super.insert(TABLE, getContentValue()) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#fetchCheckinComment(int)
	 */
	@Override
	public List<Comment> fetchCheckinComment(int checkinId) {
		listComment = new ArrayList<Comment>();
		final String sortOrder = COMMENT_DATE + " DESC";
		final String selection = CHECKIN_ID + " = " + checkinId;
		cursor = super.query(TABLE, COMMENT_COLUMN, selection, null, sortOrder);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Comment comment = cursorToEntity(cursor);
				listComment.add(comment);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listComment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#fetchReportComment(int)
	 */
	@Override
	public List<Comment> fetchReportComment(int reportId) {
		listComment = new ArrayList<Comment>();
		final String sortOrder = COMMENT_DATE + " DESC";
		final String selection = REPORT_ID + " = " + reportId;
		cursor = super.query(TABLE, COMMENT_COLUMN, selection, null, sortOrder);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Comment comment = cursorToEntity(cursor);
				listComment.add(comment);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return listComment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#updateCheckinByReportId
	 * (int)
	 */
	@Override
	public boolean updateCheckinByReportId(int reportId) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#updateCheckinByCheckinId
	 * (int)
	 */
	@Override
	public boolean updateCheckinByCheckinId(int checkinId) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.ICommentDao#deleteAllComment()
	 */
	@Override
	public boolean deleteAllComment() {
		return super.delete(TABLE, null, null) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#deleteCommentByReportId
	 * (int)
	 */
	@Override
	public boolean deleteCommentByReportId(int reportId) {
		final String selectionArgs[] = { String.valueOf(reportId) };
		final String selection = REPORT_ID + " =? ";
		return super.delete(TABLE, selection, selectionArgs) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.database.ICommentDao#deleteCommentByCheckinId
	 * (int)
	 */
	@Override
	public boolean deleteCommentByCheckinId(int checkinId) {
		final String selectionArgs[] = { String.valueOf(checkinId) };
		final String selection = CHECKIN_ID + " =? ";
		return super.delete(TABLE, selection, selectionArgs) > 0;
	}

	private void setContentValue(Comment comment) {
		initialValues = new ContentValues();
		initialValues.put(COMMENT_ID, comment.getCommentId());
		initialValues.put(REPORT_ID, comment.getReportId());
		initialValues.put(CHECKIN_ID, comment.getCheckinId());
		initialValues.put(COMMENT_AUTHOR, comment.getCommentAuthor());
		initialValues.put(COMMENT_DATE, comment.getCommentDate());
		initialValues.put(COMMENT_DESCRIPTION, comment.getCommentDescription());
	}

	private ContentValues getContentValue() {
		return initialValues;
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
	protected Comment cursorToEntity(Cursor cursor) {
		Comment comment = new Comment();
		int idIndex;
		int reportIdIndex;
		int checkinIdIndex;
		int commentIdIndex;
		int commentAuthorIndex;
		int commentDateIndex;
		int commentDescriptionIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				comment.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(REPORT_ID) != -1) {
				reportIdIndex = cursor.getColumnIndexOrThrow(REPORT_ID);
				comment.setReportId(cursor.getInt(reportIdIndex));
			}

			if (cursor.getColumnIndex(CHECKIN_ID) != -1) {
				checkinIdIndex = cursor.getColumnIndexOrThrow(CHECKIN_ID);
				comment.setCheckinId(cursor.getInt(checkinIdIndex));
			}

			if (cursor.getColumnIndex(COMMENT_AUTHOR) != -1) {
				commentAuthorIndex = cursor
						.getColumnIndexOrThrow(COMMENT_AUTHOR);
				comment.setCommentAuthor(cursor.getString(commentAuthorIndex));
			}

			if (cursor.getColumnIndex(COMMENT_DESCRIPTION) != -1) {
				commentDescriptionIndex = cursor
						.getColumnIndexOrThrow(COMMENT_DESCRIPTION);
				comment.setCommentDescription(cursor
						.getString(commentDescriptionIndex));
			}

			if (cursor.getColumnIndex(COMMENT_DATE) != -1) {
				commentDateIndex = cursor.getColumnIndexOrThrow(COMMENT_DATE);
				comment.setCommentDate(cursor.getString(commentDateIndex));
			}

			if (cursor.getColumnIndex(COMMENT_ID) != -1) {
				commentIdIndex = cursor.getColumnIndexOrThrow(COMMENT_ID);
				comment.setCommentId(cursor.getInt(commentIdIndex));
			}

		}
		return comment;
	}

}
