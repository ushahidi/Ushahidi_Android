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

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.User;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListCheckinModel extends Checkin {

	List<Checkin> mCheckins;

	@Override
	public boolean load() {
		mCheckins = Database.mCheckin.fetchAllCheckins();
		if (mCheckins != null) {
			return true;
		}

		return false;
	}

	public boolean loadCheckinByUser(int userid) {
		mCheckins = Database.mCheckin.fetchCheckinsByUserId(userid);
		if (mCheckins != null) {
			return true;
		}
		return false;
	}

	public boolean loadPendingCheckin() {
		mCheckins = Database.mCheckin.fetchAllCheckins();
		if (mCheckins != null) {
			return true;
		}

		return false;
	}

	public boolean loadPendingCheckinByUser(int userid) {
		mCheckins = Database.mCheckin.fetchPendingCheckinsByUserId(userid);
		if (mCheckins != null) {
			return true;
		}
		return false;
	}

	public List<ListCheckinModel> getCheckins(Context context) {
		final List<ListCheckinModel> checkins = new ArrayList<ListCheckinModel>();
		Drawable d = null;

		if (mCheckins != null && mCheckins.size() > 0) {
			for (Checkin item : mCheckins) {
				ListCheckinModel listCheckin = new ListCheckinModel();
				listCheckin.setDbId(item.getDbId());
				listCheckin.setCheckinId(item.getCheckinId());
				listCheckin.setUsername(getUsername(context, item.getUserId()));
				listCheckin.setDate(Util.formatDate("yyyy-MM-dd hh:mm:ss",
						item.getDate(), "MMMM dd, yyyy 'at' hh:mm:ss aaa"));
				listCheckin.setLocationLatitude(item.getLocationLatitude());
				listCheckin.setLocationLongitude(item.getLocationLongitude());
				listCheckin.setLocationName(item.getLocationName());
				listCheckin.setMessage(item.getMessage());
				listCheckin.setUserId(item.getUserId());

				// set thumbnails
				if (item.getCheckinId() == 0) {
					// get pending reports images
					d = getImage(context, item.getDbId());

				} else {
					// get fetched reports images
					d = getImage(context, item.getCheckinId());
				}

				if (d != null) {

					listCheckin.setThumbnail(d);
				} else {
					listCheckin.setThumbnail(context.getResources()
							.getDrawable(R.drawable.report_icon));
				}

				checkins.add(listCheckin);
			}
		}
		return checkins;
	}

	private Drawable getImage(Context context, int checkinId) {

		List<Media> sMedia = Database.mMediaDao.fetchMedia(
				IMediaSchema.CHECKIN_ID, checkinId, IMediaSchema.IMAGE, 1);
		if (sMedia != null && sMedia.size() > 0) {
			return ImageManager.getThumbnails(context, sMedia.get(0).getLink());
		}
		return context.getResources().getDrawable(R.drawable.report_icon);
	}

	private String getUsername(Context context, int userId) {
		List<User> sUser = Database.mUserDao.fetchUsersById(userId);
		if (sUser != null && sUser.size() > 0) {
			return sUser.get(0).getUsername();
		}
		return context.getText(R.string.unknown).toString();
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
