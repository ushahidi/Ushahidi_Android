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
import java.util.Vector;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Report;
import com.ushahidi.android.app.entities.ReportCategory;
import com.ushahidi.android.app.util.Util;

public class AddCheckinModel extends Model {

	public boolean addPendingCheckin(Checkin checkin, File[] pendingPhotos) {
		boolean status;
		// add pending reports
		status = Database.mCheckin.addCheckin(checkin);
		//int id = Database.mCheckin.
		int id = 0;
		// add photos
		if (pendingPhotos != null && pendingPhotos.length > 0) {
			for (File file : pendingPhotos) {
				if (file.exists()) {
					Media media = new Media();
					media.setMediaId(0);
					media.setLink(file.getName());

					// get report ID;
					media.setCheckinId(id);
					media.setType(IMediaSchema.IMAGE);
					Database.mMediaDao.addMedia(media);
				}
			}

		}

		return status;
	}

	public boolean deleteCheckin(int checkinId) {
		// delete checkin
		Database.mCheckin.deletePendingCheckinById(checkinId);

		// delete media
		Database.mMediaDao.deleteMediaByCheckinId(checkinId);
		return true;
	}

	public Checkin fetchPendingCheckinById(int checkinId) {
		return Database.mCheckin.fetchPendingCheckinById(checkinId);
	}

	@Override
	public boolean load() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}
