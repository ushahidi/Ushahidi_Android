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

import com.ushahidi.android.app.entities.Media;

/**
 * @author eyedol
 */
public interface IMediaDao {

	// add
	public boolean addMedia(List<Media> media);

	public boolean addMedia(Media media);

	// select
	public List<Media> fetchCheckinPhoto(int checkinId);

	public List<Media> fetchReportPhoto(int reportId);

	public List<Media> fetchReportAudio(int reportId);

	public List<Media> fetchReportNews(int reportId);

	public List<Media> fetchReportVideo(int reportId);

	public List<Media> fetchMedia(String itemType, int itemId, int mediaType,
			int limit);

	// delete
	public boolean deleteAllMedia();

	public boolean deleteReportMediaByIdAndLink(int reportId, String link);

	public boolean deleteMediaByReportId(int reportId);

	public boolean deleteMediaByCheckinId(int checkinId);

	// updates
	public boolean updateMediaByReportId(int reportId);

	public boolean updateMediaByCheckinId(int checkinId);

	// fetch pending report's photo
	public List<Media> fetchPendingReportPhoto(int reportId);

	// fetch pending checkin's photo
	public List<Media> fetchPendingCheckinPhoto(int checkinId);

	// update news
	public boolean deleteReportNews(int reportId);

	public boolean deleteReportPhoto(int reportId);

	public boolean deleteCheckinPhoto(int checkinId);
}
