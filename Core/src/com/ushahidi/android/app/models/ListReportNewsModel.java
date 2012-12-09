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

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.MediaEntity;

/**
 * @author eyedol
 */
public class ListReportNewsModel extends Model {

	private int id;

	private String title;

	private String url;

	private List<MediaEntity> mMedia;

	private List<ListReportNewsModel> mNewsModel;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean load(int reportId) {
		mMedia = Database.mMediaDao.fetchReportNews(reportId);
		if (mMedia != null) {
			return true;
		}
		return false;
	}

	public List<ListReportNewsModel> getNews() {
		mNewsModel = new ArrayList<ListReportNewsModel>();

		if (mMedia != null && mMedia.size() > 0) {
			ListReportNewsModel newsModel = new ListReportNewsModel();
			newsModel.setId(mMedia.get(0).getDbId());
			newsModel.setTitle(mMedia.get(0).getLink());
			newsModel.setUrl(mMedia.get(0).getLink());
			mNewsModel.add(newsModel);

		}

		return mNewsModel;
	}

	public List<ListReportNewsModel> getNewsByReportId(int reportId) {
		mNewsModel = new ArrayList<ListReportNewsModel>();
		mMedia = Database.mMediaDao.fetchReportNews(reportId);
		if (mMedia != null && mMedia.size() > 0) {
			for (MediaEntity item : mMedia) {
				ListReportNewsModel newsModel = new ListReportNewsModel();
				newsModel.setId(item.getDbId());
				newsModel.setTitle(item.getLink());
				newsModel.setUrl(item.getLink());

				mNewsModel.add(newsModel);
			}
		}

		return mNewsModel;
	}

	public int totalReportNews() {
		if (mMedia != null && mMedia.size() > 0) {
			return mMedia.size();
		}
		return 0;
	}

}
