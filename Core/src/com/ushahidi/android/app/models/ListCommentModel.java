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
package com.ushahidi.android.app.models;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Comment;

/**
 * @author eyedol
 * 
 */
public class ListCommentModel extends Comment {

	private List<Comment> mCommentModel;

	public boolean load(int reportId) {
		mCommentModel = Database.mCommentDao.fetchReportComment(reportId);
		if (mCommentModel != null) {
			return true;
		}
		return false;
	}

	public boolean loadCheckinComment(int checkinId) {
		mCommentModel = Database.mCommentDao.fetchCheckinComment(checkinId);
		if (mCommentModel != null) {
			return true;
		}
		return false;
	}

	public List<ListCommentModel> getComments() {
		final List<ListCommentModel> comments = new ArrayList<ListCommentModel>();
		if (mCommentModel != null && mCommentModel.size() > 0) {

			ListCommentModel comment = new ListCommentModel();
			comment.setDbId(mCommentModel.get(0).getDbId());
			comment.setCommentAuthor(mCommentModel.get(0).getCommentAuthor());
			comment.setCommentDescription(mCommentModel.get(0)
					.getCommentDescription());
			comment.setReportId(mCommentModel.get(0).getReportId());
			comment.setCommentId(mCommentModel.get(0).getCommentId());
			comment.setCommentDate(mCommentModel.get(0).getCommentDate());
			comments.add(comment);

		}

		return comments;
	}

	public int totalComments() {
		if (mCommentModel != null && mCommentModel.size() > 0) {
			return mCommentModel.size();
		}
		return 0;
	}

	public List<ListCommentModel> getCommentsByReportId(int reportId) {
		final List<ListCommentModel> comments = new ArrayList<ListCommentModel>();
		mCommentModel = new ArrayList<Comment>();
		mCommentModel = Database.mCommentDao.fetchReportComment(reportId);
		if (mCommentModel != null && mCommentModel.size() > 0) {

			for (Comment item : mCommentModel) {
				ListCommentModel comment = new ListCommentModel();
				comment.setDbId(item.getDbId());
				comment.setCommentAuthor(item.getCommentAuthor());
				comment.setCommentDescription(item.getCommentDescription());
				comment.setReportId(item.getReportId());
				comment.setCommentId(item.getCommentId());
				comment.setCommentDate(item.getCommentDate());
				comments.add(comment);
			}

		}

		return comments;
	}

	public List<ListCommentModel> getCommentsByCheckintId(int checkinId) {
		final List<ListCommentModel> comments = new ArrayList<ListCommentModel>();
		mCommentModel = new ArrayList<Comment>();
		mCommentModel = Database.mCommentDao.fetchCheckinComment(checkinId);
		if (mCommentModel != null && mCommentModel.size() > 0) {

			for (Comment item : mCommentModel) {
				ListCommentModel comment = new ListCommentModel();
				comment.setDbId(item.getDbId());
				comment.setCommentAuthor(item.getCommentAuthor());
				comment.setCommentDescription(item.getCommentDescription());
				comment.setCheckinId(item.getCheckinId());
				comment.setCommentId(item.getCommentId());
				comment.setCommentDate(item.getCommentDate());
				comments.add(comment);
			}

		}

		return comments;
	}
	
	public boolean deleteComments() {
		if( Database.mCommentDao.deleteAllComment() ) {
			Log.i("ListCommentModel","Comment deleted");
			return true;
		}
		
		Log.i("ListCommentModel","Comment deletetion failed!");
		
		return false;
	}

	@Override
	public boolean load() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.models.Model#save()
	 */
	@Override
	public boolean save() {
		return false;
	}

}
