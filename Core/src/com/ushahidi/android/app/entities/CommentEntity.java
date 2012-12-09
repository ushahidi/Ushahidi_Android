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
package com.ushahidi.android.app.entities;

import com.ushahidi.android.app.models.Model;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class CommentEntity extends Model implements IDbEntity {

	private int id;

	private int checkinId;

	private int reportId;

	private int commentId;
	private String commentAuthor;

	private String commentDescription;

	private String commentDate;

	public void addComment(com.ushahidi.java.sdk.api.Comment comment) {
		// get the date pattern

		this.setCommentAuthor(comment.getAuthor());
		this.setCheckinId(comment.getCheckinId());
		this.setCommentDate(Util.datePattern("yyyy-MM-dd HH:mm:ss",
				comment.getDate()));
		this.setCommentDescription(comment.getDescription());
		this.setCommentId(comment.getId());
		this.setReportId(comment.getReportId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.entities.IDbEntity#getDbId()
	 */
	@Override
	public int getDbId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.entities.IDbEntity#setDbId(int)
	 */
	@Override
	public void setDbId(int id) {
		this.id = id;
	}

	public int getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(int checkinId) {
		this.checkinId = checkinId;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getCommentId() {
		return this.commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCommentAuthor() {
		return this.commentAuthor;
	}

	public void setCommentAuthor(String commentAuthor) {
		this.commentAuthor = commentAuthor;
	}

	public String getCommentDescription() {
		return this.commentDescription;
	}

	public void setCommentDescription(String commentDescription) {
		this.commentDescription = commentDescription;
	}

	public String getCommentDate() {
		return this.commentDate;
	}

	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

}
