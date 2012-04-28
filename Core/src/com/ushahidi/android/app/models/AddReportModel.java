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
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Report;
import com.ushahidi.android.app.entities.ReportCategory;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class AddReportModel extends Model {

	public boolean addPendingReport(Report report, Vector<String> category,
			File[] pendingPhotos, String news) {
		boolean status;
		// add pending reports
		status = Database.mReportDao.addReport(report);
		int id = Database.mReportDao.fetchPendingReportIdByDate(report
				.getReportDate());

		// add category
		if (status) {
			if (category != null && category.size() > 0) {
				for (String cat : category) {
					ReportCategory reportCategory = new ReportCategory();
					reportCategory.setCategoryId(Util.toInt(cat));
					reportCategory.setReportId(id);
					Database.mReportCategoryDao
							.addReportCategory(reportCategory);

				}
			}

			// add photos
			if (pendingPhotos != null && pendingPhotos.length > 0) {
				for (File file : pendingPhotos) {
					if (file.exists()) {
						Media media = new Media();
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
				Media media = new Media();
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

	public boolean updatePendingReport(int reportId, Report report,
			Vector<String> category, File[] pendingPhotos, String news) {
		boolean status;
		// update pending reports
		status = Database.mReportDao.updatePendingReport(reportId, report);
		// update category
		if (status) {
			if (category != null && category.size() > 0) {
				if (Database.mReportCategoryDao
						.deleteReportCategoryByReportId(reportId)) {
					// FIXME:: optimize this
					// delete existing categories. It's easier this way
					for (String cat : category) {
						ReportCategory reportCategory = new ReportCategory();
						reportCategory.setCategoryId(Util.toInt(cat));
						reportCategory.setReportId(reportId);
						Database.mReportCategoryDao
								.addReportCategory(reportCategory);

					}

				}
			}

			// add photos
			/*if (pendingPhotos != null && pendingPhotos.length > 0) {
				for (File file : pendingPhotos) {
					if (file.exists()) {
						Media media = new Media();
						media.setMediaId(0);
						media.setLink(file.getName());

						// get report ID;
						media.setReportId(reportId);
						media.setType(IMediaSchema.IMAGE);
						Database.mMediaDao.addMedia(media);
					} else {
						//delete from database.
						Database.mMediaDao.deleteReportMediaByIdAndLink(reportId, file.getName());
					}
				}

			}*/

			// add news
			if (news != null && news.length() > 0) {
				Media media = new Media();
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

	public Report fetchPendingReportById(int reportId) {
		return Database.mReportDao.fetchPendingReportIdById(reportId);
	}

	public List<ReportCategory> fetchReportCategories(int reportId) {
		return Database.mReportCategoryDao
				.fetchReportCategoryByReportId(reportId);
	}

	public List<Media> fetchReportNews(int reportId) {
		return Database.mMediaDao.fetchReportNews(reportId);
	}

	@Override
	public boolean load() {
		return false;
	}

	@Override
	public boolean save() {
		return false;
	}
}
