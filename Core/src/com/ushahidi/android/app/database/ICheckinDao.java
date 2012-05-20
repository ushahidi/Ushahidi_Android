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

import com.ushahidi.android.app.entities.Checkin;

/**
 * @author eyedol
 * 
 */
public interface ICheckinDao {

	public List<Checkin> fetchAllCheckins();

	public List<Checkin> fetchAllPendingCheckins();

	public List<Checkin> fetchCheckinsByUserId(int userId);

	public List<Checkin> fetchPendingCheckinsByUserId(int userId);

	public Checkin fetchPendingCheckinById(int checkinId);
	
	public int fetchPendingCheckinIdByDate(String date);

	// delete checkin by id
	public boolean deletePendingCheckinById(int checkinId);

	// delete checkin by id
	public boolean deleteCheckinById(int checkinId);

	public boolean deleteAllCheckins();

	public boolean addCheckin(Checkin checkin);

	public boolean addCheckins(List<Checkin> checkins);

	// update pending checkin
	public boolean updatePendingCheckin(int checkinId, Checkin checkin);

}
