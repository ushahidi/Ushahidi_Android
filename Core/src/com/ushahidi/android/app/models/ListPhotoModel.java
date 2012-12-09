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
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.MediaEntity;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.util.PhotoUtils;

/**
 * @author eyedol
 */
public class ListPhotoModel extends Model {

	private List<MediaEntity> mMedia;

	private List<PhotoEntity> mPhotoModel;

	private static final String FETCHED = "fetched/";

	private static final String PENDING = "pending/";

	public boolean load(int reportId) {
		mMedia = Database.mMediaDao.fetchReportPhoto(reportId);
		if (mMedia != null) {
			return true;
		}
		return false;
	}
	
	public boolean loadCheckinPhoto(int checkinId) {
		mMedia = Database.mMediaDao.fetchCheckinPhoto(checkinId);
		if (mMedia != null) {
			return true;
		}
		return false;
	}

	public List<PhotoEntity> getPhotos() {
		mPhotoModel = new ArrayList<PhotoEntity>();

		if (mMedia != null && mMedia.size() > 0) {
			PhotoEntity photo = new PhotoEntity();
			photo.setDbId(mMedia.get(0).getDbId());
			photo.setPhoto(mMedia.get(0).getLink());
			mPhotoModel.add(photo);

		}

		return mPhotoModel;
	}

	public List<PhotoEntity> getPendingPhotos(Context context) {
		mPhotoModel = new ArrayList<PhotoEntity>();
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(context);
		if (pendingPhotos != null && pendingPhotos.length > 0) {
			int id = 0;
			for (File file : pendingPhotos) {
				if (file.exists()) {
					id += 1;
					PhotoEntity photo = new PhotoEntity();
					photo.setDbId(id);
					photo.setPhoto(PENDING + file.getName());
					mPhotoModel.add(photo);
				}
			}

		}

		return mPhotoModel;
	}

	public List<PhotoEntity> getPhotosByReportId(int reportId) {

		mPhotoModel = new ArrayList<PhotoEntity>();
		mMedia = Database.mMediaDao.fetchReportPhoto(reportId);
		if (mMedia != null && mMedia.size() > 0) {

			for (MediaEntity item : mMedia) {
				PhotoEntity photo = new PhotoEntity();
				photo.setDbId(item.getDbId());
				photo.setPhoto(item.getLink());
				mPhotoModel.add(photo);
			}

		}

		return mPhotoModel;
	}

	public List<PhotoEntity> getPhotosByCheckinId(int checkinId) {

		mPhotoModel = new ArrayList<PhotoEntity>();
		mMedia = Database.mMediaDao.fetchCheckinPhoto(checkinId);
		if (mMedia != null && mMedia.size() > 0) {

			for (MediaEntity item : mMedia) {
				PhotoEntity photo = new PhotoEntity();
				photo.setDbId(item.getDbId());
				photo.setPhoto(item.getLink());
				mPhotoModel.add(photo);
			}

		}

		return mPhotoModel;
	}

	public List<PhotoEntity> getPendingPhotosByReportId(int reportId) {

		mPhotoModel = new ArrayList<PhotoEntity>();
		mMedia = Database.mMediaDao.fetchPendingReportPhoto(reportId);
		if (mMedia != null && mMedia.size() > 0) {

			for (MediaEntity item : mMedia) {
				PhotoEntity photo = new PhotoEntity();
				photo.setDbId(item.getDbId());
				photo.setPhoto(FETCHED + item.getLink());
				mPhotoModel.add(photo);
			}

		}

		return mPhotoModel;
	}

	public List<PhotoEntity> getPendingPhotosByCheckinId(int checkinId) {

		mPhotoModel = new ArrayList<PhotoEntity>();
		mMedia = Database.mMediaDao.fetchPendingCheckinPhoto(checkinId);
		if (mMedia != null && mMedia.size() > 0) {

			for (MediaEntity item : mMedia) {
				PhotoEntity photo = new PhotoEntity();
				photo.setDbId(item.getDbId());
				photo.setPhoto(FETCHED + item.getLink());
				mPhotoModel.add(photo);
			}

		}

		return mPhotoModel;
	}

	public int totalReportPhoto() {
		if (mMedia != null && mMedia.size() > 0) {
			return mMedia.size();
		}
		return 0;
	}

	public String getImage(Context context, String path) {
		return path;
	}

}
