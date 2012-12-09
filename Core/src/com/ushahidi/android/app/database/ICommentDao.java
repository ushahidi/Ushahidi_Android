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

import java.util.List;

import com.ushahidi.android.app.entities.CommentEntity;

/**
 * @author eyedol
 * 
 */
public interface ICommentDao {

	// add
	public boolean addComment(List<CommentEntity> comment);

	public boolean addComment(CommentEntity comment);

	// select
	public List<CommentEntity> fetchCheckinComment(int checkinId);

	public List<CommentEntity> fetchReportComment(int reportId);

	// updates
	public boolean updateCheckinByReportId(int reportId);

	public boolean updateCheckinByCheckinId(int checkinId);

	// delete
	public boolean deleteAllComment();

	public boolean deleteCommentByReportId(int reportId);

	public boolean deleteCommentByCheckinId(int checkinId);

}
