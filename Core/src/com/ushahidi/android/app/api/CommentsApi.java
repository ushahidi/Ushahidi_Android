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
package com.ushahidi.android.app.api;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Comment;
import com.ushahidi.android.app.net.UshahidiClient;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.UshahidiException;
import com.ushahidi.java.sdk.api.CommentFields;
import com.ushahidi.java.sdk.api.json.Response;
import com.ushahidi.java.sdk.api.tasks.CommentsTask;

/**
 * @author eyedol
 * 
 */
public class CommentsApi {

	private boolean processingResult;

	private List<Comment> comments;

	private CommentsTask task;

	public CommentsApi() {
		processingResult = true;
		comments = new ArrayList<Comment>();
		task = UshahidiClient.ushahidiApi.factory.createCommentTask();
	}

	/**
	 * Save reports comment
	 * 
	 * @param context
	 * @return
	 */
	public List<Comment> getCommentsList(int reportId) {
		new Util().log("Save comments");
		if (processingResult) {
			try {
				for (com.ushahidi.java.sdk.api.Comment c : task
						.reportId(reportId)) {
					Comment comment = new Comment();
					comment.addComment(c);
					comments.add(comment);
				}
			} catch (UshahidiException e) {
				e.printStackTrace();
			}
		}
		return comments;

	}

	/**
	 * Submit a comment to an existing report.
	 * 
	 * @return The response from the server.
	 */
	public Response submit(CommentFields comment) {
		return task.submit(comment);
	}

	public List<Comment> getCheckinCommentsList() {
		new Util().log("Save comments");
		if (processingResult) {
			try {
				for (com.ushahidi.java.sdk.api.Comment c : task.all()) {
					Comment comment = new Comment();
					comment.addComment(c);
					comments.add(comment);
				}
			} catch (UshahidiException e) {
				e.printStackTrace();
			}
		}
		return comments;
	}

	// Save report comments into database
	public boolean saveComments(int reportId) {
		List<Comment> comments = getCommentsList(reportId);

		if (comments != null && comments.size() > 0) {
			// remove existing comments
			for (Comment comment : comments) {
				Database.mCommentDao.deleteCommentByReportId(comment
						.getReportId());
			}
			return Database.mCommentDao.addComment(comments);
		}

		return false;
	}

	// Save checkins comments into database
	public boolean saveCheckinsComments() {
		List<Comment> comments = getCheckinCommentsList();
		if (comments != null && comments.size() > 0) {
			// remove existing comments
			for (Comment comment : comments) {
				Database.mCommentDao.deleteCommentByCheckinId(comment
						.getCheckinId());
			}
			return Database.mCommentDao.addComment(comments);
		}

		return false;
	}

}
