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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.entities.Category;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Report;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListReportModel extends Model {

	public List<Report> mReports;

	public List<ListReportModel> reportModel;

	// FIXME:: I need to fix this to use the report entity instead.
	private long id;

	private int reportId;

	private String title;

	private String date;

	private String status;

	private String thumbnail;

	private Drawable arrow;

	private Uri thumbnailUri;

	private String description;

	private String location;

	private String media;

	private String categories;

	private String latitude;

	private String longitude;

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	public void setThumbnailUri(Uri uri) {
		this.thumbnailUri = uri;
	}

	public Uri getThumbnailUri() {
		return this.thumbnailUri;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setDate(String date) {

		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setDesc(String description) {
		this.description = description;
	}

	public String getDesc() {
		return this.description;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return this.location;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getMedia() {
		return this.media;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getCategories() {
		return this.categories;
	}

	public void setArrow(Drawable arrow) {
		this.arrow = arrow;
	}

	public Drawable getArrow() {
		return this.arrow;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getReportId() {
		return this.reportId;
	}

	@Override
	public boolean load() {
		mReports = Database.mReportDao.fetchAllReports();

		if (mReports != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean save() {
		return false;
	}

	public boolean loadReportById(long id) {
		mReports = Database.mReportDao.fetchReportById(id);

		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadPendingReports() {
		mReports = Database.mReportDao.fetchAllPendingReports();
		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadPendingReportsByCategory(int categoryId) {

		mReports = Database.mReportDao
				.fetchPendingReportByCategoryId(categoryId);
		if (mReports != null) {
			return true;
		}
		return false;
	}

	public boolean loadReportByCategory(int categoryId) {
		mReports = Database.mReportDao.fetchReportByCategoryId(categoryId);

		if (mReports != null) {
			return true;
		}
		return false;
	}

	public List<ListReportModel> getReports(Context context) {
		reportModel = new ArrayList<ListReportModel>();
		String d = null;
		if (mReports != null && mReports.size() > 0) {
			for (Report item : mReports) {
				ListReportModel listReportModel = new ListReportModel();
				listReportModel.setId(item.getDbId());
				listReportModel.setReportId(item.getReportId());
				listReportModel
						.setTitle(Util.capitalizeString(item.getTitle()));
				listReportModel.setDesc(Util.capitalizeString(item
						.getDescription()));
				listReportModel.setDate(Util
						.formatDate("yyyy-MM-dd HH:mm:ss",
								item.getReportDate(),
								"MMMM dd, yyyy 'at' hh:mm:ss aaa"));
				final String status = Util.toInt(item.getVerified()) == 0 ? context
						.getString(R.string.unverified) : context
						.getString(R.string.verified);
				listReportModel.setStatus(Util.capitalizeString(status));
				listReportModel.setLocation(Util.capitalizeString(item
						.getLocationName()));
				listReportModel.setLatitude(item.getLatitude());
				listReportModel.setLongitude(item.getLongitude());
				listReportModel.setArrow(context.getResources().getDrawable(
						R.drawable.arrow));
				listReportModel.setCategories(item.getCategories());
				listReportModel.setMedia(item.getMedia());

				if (item.getReportId() == 0) {
					// get pending reports images
					d = getImage(item.getDbId());

				} else {
					// get fetched reports images
					d = getImage(item.getReportId());
				}
				listReportModel.setThumbnail(d);

				reportModel.add(listReportModel);
			}

		}
		return reportModel;
	}

	public List<Category> getCategories(Context context) {
		return Database.mCategoryDao.fetchAllCategoryTitles();

	}

	public List<Category> getCategoriesByReportId(int reportId) {

		return Database.mCategoryDao.fetchCategoryByReportId(reportId);
	}

	private String getImage(int reportId) {

		List<Media> sMedia = Database.mMediaDao.fetchMedia(
				IMediaSchema.REPORT_ID, reportId, IMediaSchema.IMAGE, 1);
		if (sMedia != null && sMedia.size() > 0) {
			return sMedia.get(0).getLink();
			// return ImageManager.getThumbnails(context,
			// sMedia.get(0).getLink());

		}
		// return context.getResources().getDrawable(R.drawable.report_icon);
		return null;
	}

	/**
	 * Deletes all fetched reports.
	 * 
	 * @param reportId
	 *            The id of the report to be deleted.
	 * 
	 * @return boolean
	 */
	public boolean deleteAllFetchedReport(int reportId) {

		// delete fetched reports
		if (Database.mReportDao.deleteReportById(reportId)) {
			Log.i("ListReportModel", "Report deleted");
		}

		// delete categories
		if (Database.mReportCategoryDao
				.deleteReportCategoryByReportId(reportId)) {
			Log.i("Report", "Report deleted");
		}

		if (Database.mCategoryDao.deleteAllCategories()) {
			Log.i("Category: ", "Category deleted");
		}

		// delete media
		if (Database.mMediaDao.deleteReportPhoto(reportId)) {
			Log.i("Media", "Media deleted");
		}
		return true;
	}

	public boolean deleteReport() {
		// delete fetched reports
		if (Database.mReportDao.deleteAllReport()) {
			Log.i("ListReportModel", "Report deleted");
		}

		// delete categories
		if (Database.mReportCategoryDao.deleteAllReportCategory()) {
			Log.i("Report", "Report deleted");
		}

		if (Database.mCategoryDao.deleteAllCategories()) {
			Log.i("Category: ", "Category deleted");
		}

		// delete media
		if (Database.mMediaDao.deleteAllMedia()) {
			Log.i("Media", "Media deleted");
		}
		return true;
	}
}
