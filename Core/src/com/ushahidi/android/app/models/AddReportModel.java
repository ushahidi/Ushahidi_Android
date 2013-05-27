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

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.database.IReportSchema;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportCategory;
import com.ushahidi.android.app.entities.ReportEntity;

/**
 * @author eyedol
 * 
 */
public class AddReportModel extends Model {

	public boolean addPendingReport(ReportEntity report,
			Vector<Integer> category, File[] pendingPhotos, String news) {
		boolean status;
		// add pending reports
		status = Database.mReportDao.addReport(report);
		final String date = Database.mReportDao.getDate(report.getIncident()
				.getDate());
		int id = Database.mReportDao.fetchPendingReportIdByDate(date);
		report.setDbId(id);
		// add category
		if (status) {
			if (category != null && category.size() > 0) {
				for (Integer cat : category) {
					ReportCategory reportCategory = new ReportCategory();
					reportCategory.setCategoryId(cat);
					reportCategory.setReportId(id);
					reportCategory.setStatus(IReportSchema.PENDING);
					Database.mReportCategoryDao
							.addReportCategory(reportCategory);

				}
			}

			// add photos
			if (pendingPhotos != null && pendingPhotos.length > 0) {
				for (File file : pendingPhotos) {
					if (file.exists()) {
						MediaEntity media = new MediaEntity();
						media.setMediaId(0);
						media.setLink(file.getName());

						// get report ID;
						media.setReportId(id);
						media.setType(IMediaSchema.IMAGE);
						Database.mMediaDao.addMedia(media);
					}
				}

			}

			// add news
			if (news != null && news.length() > 0) {

				MediaEntity media = new MediaEntity();
				media.setMediaId(0);
				media.setLink(news);
				// get report ID;
				media.setReportId(id);
				media.setType(IMediaSchema.NEWS);
				Database.mMediaDao.addMedia(media);
			}
		}

		return status;
	}

	public boolean updatePendingReport(int reportId, ReportEntity report,
			Vector<Integer> category, List<PhotoEntity> pendingPhotos,
			String news) {
		boolean status;
		// update pending reports
		status = Database.mReportDao.updatePendingReport(reportId, report);

		// update category
		if (status) {
			if (category != null && category.size() > 0) {
				// delete existing categories. It's easier this way
				Database.mReportCategoryDao.deleteReportCategoryByReportId(
						reportId, IReportSchema.PENDING);

				for (Integer cat : category) {
					ReportCategory reportCategory = new ReportCategory();
					reportCategory.setCategoryId(cat);
					reportCategory.setReportId(reportId);
					reportCategory.setStatus(IReportSchema.PENDING);
					Database.mReportCategoryDao
							.addReportCategory(reportCategory);

				}

			}

			// update photos
			if (pendingPhotos != null && pendingPhotos.size() > 0) {
				// delete existing photo
				Database.mMediaDao.deleteReportPhoto(reportId);
				for (PhotoEntity photo : pendingPhotos) {
					MediaEntity media = new MediaEntity();
					media.setMediaId(0);
					// FIXME:: this is nasty.
					String sections[] = photo.getPhoto().split("/");
					media.setLink(sections[1]);

					// get report ID
					media.setReportId(reportId);
					media.setType(IMediaSchema.IMAGE);
					Database.mMediaDao.addMedia(media);
				}

			}

			// add news
			if (news != null && news.length() > 0) {
				// delete existing news item
				Database.mMediaDao.deleteReportNews(reportId);
				MediaEntity media = new MediaEntity();
				media.setMediaId(0);
				media.setLink(news);
				// get report ID;
				media.setReportId(reportId);
				media.setType(IMediaSchema.NEWS);
				Database.mMediaDao.addMedia(media);

			}
		}
		return status;
	}

	public ReportEntity fetchPendingReportById(int reportId) {
		return Database.mReportDao.fetchPendingReportIdById(reportId);
	}

	public List<ReportCategory> fetchReportCategories(int reportId, int status) {
		return Database.mReportCategoryDao.fetchReportCategoryByReportId(
				reportId, status);
	}

	public List<MediaEntity> fetchReportNews(int reportId) {
		return Database.mMediaDao.fetchReportNews(reportId);
	}

	public boolean deleteReport(int reportId) {
		// delete report
		Database.mReportDao.deletePendingReportById(reportId);

		// delete categories
		Database.mReportCategoryDao.deleteReportCategoryByReportId(reportId,
				IReportSchema.PENDING);

		// delete media
		Database.mMediaDao.deleteMediaByReportId(reportId);
		return true;
	}
}
