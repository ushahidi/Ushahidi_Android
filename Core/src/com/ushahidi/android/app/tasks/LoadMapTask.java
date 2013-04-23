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
package com.ushahidi.android.app.tasks;

import android.app.Activity;
import android.location.Location;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListMapAdapter;
import com.ushahidi.android.app.api.MapSearchApi;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 * 
 */
public class LoadMapTask extends ProgressTask {

	protected Boolean status;

	private MapSearchApi maps;

	public String distance;

	public Location location;

	public ListMapAdapter adapter;

	public LoadMapTask(Activity activity) {
		super(activity, R.string.loading_);
		maps = new MapSearchApi();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog.cancel();

	}

	@Override
	protected Boolean doInBackground(String... strings) {
		try {
			status = maps.fetchMaps(distance, location);
			Thread.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return status;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (!result) {

			Util.showToast(activity, R.string.could_not_fetch_data);
		} else {

			Util.showToast(activity, R.string.maps_fetched_successful);
		}

		if (adapter != null)
			adapter.refresh();

	}

}
