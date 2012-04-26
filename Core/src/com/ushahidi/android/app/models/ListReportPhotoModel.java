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
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListReportPhotoModel extends Model {

	private List<Media> mMedia;

	private List<Photo> mPhotoModel;

	@Override
	public boolean load() {
		return false;
	}

	public boolean load(int reportId) {
		mMedia = Database.mMediaDao.fetchReportPhoto(reportId);
		if (mMedia != null) {
			return true;
		}
		return false;
	}

	public List<Photo> getPhotos() {
		mPhotoModel = new ArrayList<Photo>();

		if (mMedia != null && mMedia.size() > 0) {
			Photo photo = new Photo();
			photo.setDbId(mMedia.get(0).getDbId());
			photo.setPhoto(mMedia.get(0).getLink());
			mPhotoModel.add(photo);

		}

		return mPhotoModel;
	}

	public List<Photo> getPendingPhotos(Context context) {
		mPhotoModel = new ArrayList<Photo>();
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(context);
		if (pendingPhotos != null && pendingPhotos.length > 0) {
			int id = 0;
			for (File file : pendingPhotos) {
				if (file.exists()) {
					id += 1;
					Photo photo = new Photo();
					photo.setDbId(id);
					photo.setPhoto(file.getName());
					mPhotoModel.add(photo);
				}
			}

		}

		return mPhotoModel;
	}

	public List<Photo> getPhotosByReportId(int reportId) {

		mPhotoModel = new ArrayList<Photo>();
		mMedia = Database.mMediaDao.fetchReportPhoto(reportId);
		Log.i("ListReportPhotoModel ", "Photo: " + reportId);
		if (mMedia != null && mMedia.size() > 0) {

			for (Media item : mMedia) {
				Photo photo = new Photo();
				photo.setDbId(item.getDbId());
				photo.setPhoto(item.getLink());
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

	public Drawable getImage(Context context, String path) {
		return ImageManager.getDrawables(context, path,
				Util.getScreenWidth(context));
	}

	@Override
	public boolean save() {
		return false;
	}

}
