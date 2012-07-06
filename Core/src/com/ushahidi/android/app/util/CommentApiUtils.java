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

package com.ushahidi.android.app.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Comment;

/**
 * Handle processing of the JSON string as returned from the HTTP request. Main
 * deals with reports related HTTP request.
 * 
 * @author eyedol
 */
public class CommentApiUtils {

	private JSONObject jsonObject;

	private boolean processingResult;

	public CommentApiUtils(String jsonString) {
		processingResult = true;
		new Util().log("JSONString: " + jsonString);
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			new Util().log("JSONException", e);
			processingResult = false;
		}
	}

	private JSONObject getCommentsObject() {
		try {
			return jsonObject.getJSONObject("payload");
		} catch (JSONException e) {
			return new JSONObject();
		}
	}

	private JSONArray getCommentsArray() {
		try {
			return getCommentsObject().getJSONArray("comments");
		} catch (JSONException e) {
			return new JSONArray();
		}
	}

	public List<Comment> getCommentsList(Context context) {
		new Util().log("Save comments");
		if (processingResult) {
			List<Comment> commentList = new ArrayList<Comment>();
			JSONArray commentArray = getCommentsArray();
			int id = 0;
			if (commentArray != null && commentArray.length() > 0) {
				for (int i = 0; i < commentArray.length(); i++) {
					Comment currentComment = new Comment();
					try {
						id = commentArray.getJSONObject(i)
								.getJSONObject("comment").getInt("id");
						currentComment.setCommentId(id);
						currentComment.setReportId(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getInt("incident_id"));
						currentComment.setCommentAuthor(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_author"));
						currentComment.setCommentDate(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_date"));
						currentComment.setCommentDescription(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_description"));

					} catch (JSONException e) {
						new Util().log("JSONException", e);
						processingResult = false;
						return null;
					}
					commentList.add(currentComment);
				}

				return commentList;
			}

		}
		return null;
	}

	public List<Comment> getCheckinCommentsList(Context context) {
		new Util().log("Save comments");
		if (processingResult) {
			List<Comment> commentList = new ArrayList<Comment>();
			JSONArray commentArray = getCommentsArray();
			int id = 0;
			if (commentArray != null && commentArray.length() > 0) {
				for (int i = 0; i < commentArray.length(); i++) {
					Comment currentComment = new Comment();
					try {
						id = commentArray.getJSONObject(i)
								.getJSONObject("comment").getInt("id");
						currentComment.setCommentId(id);
						currentComment.setCheckinId(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getInt("checkin_id"));
						currentComment.setCommentAuthor(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_author"));
						currentComment.setCommentDate(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_date"));
						currentComment.setCommentDescription(commentArray
								.getJSONObject(i).getJSONObject("comment")
								.getString("comment_description"));

					} catch (JSONException e) {
						new Util().log("JSONException", e);
						processingResult = false;
						return null;
					}
					commentList.add(currentComment);
				}

				return commentList;
			}

		}
		return null;
	}

	// Save checkins into database
	public boolean saveComments(Context context) {
		List<Comment> comments = getCommentsList(context);

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

	// Save checkins into database
	public boolean saveCheckinsComments(Context context) {
		List<Comment> comments = getCheckinCommentsList(context);
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
