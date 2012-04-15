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

package com.ushahidi.android.app.views;

import android.support.v4.app.FragmentMapActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.R;

/**
 * AddReportView All the widgets for /res/layout/add_report.xml
 */
public class AddReportView extends View {

	public EditText mIncidentTitle;

	public EditText mIncidentLocation;

	public EditText mIncidentDesc;

	public ImageView mSelectedPhoto;

	public EditText mLatitude;

	public EditText mLongitude;

	public TextView mActivityTitle;

	public Button mBtnSend;

	public Button mBtnAddCategory;

	public Button mPickTime;

	public Button mPickDate;

	public Button mBtnPicture;

	public MapView mapView;

	public AddReportView(FragmentMapActivity activity) {
		super(activity);

		mBtnPicture = (Button) activity.findViewById(R.id.btnPicture);
		mBtnAddCategory = (Button) activity.findViewById(R.id.add_category);
		mPickDate = (Button) activity.findViewById(R.id.pick_date);
		mPickTime = (Button) activity.findViewById(R.id.pick_time);
		mLatitude = (EditText) activity.findViewById(R.id.incident_latitude);
		mLongitude = (EditText) activity.findViewById(R.id.incident_longitude);
		mSelectedPhoto = (ImageView) activity.findViewById(R.id.sel_photo_prev);
		mIncidentTitle = (EditText) activity.findViewById(R.id.incident_title);
		mIncidentLocation = (EditText) activity
				.findViewById(R.id.incident_location);
		mIncidentDesc = (EditText) activity.findViewById(R.id.incident_desc);
		this.mapView = (MapView) activity.findViewById(R.id.location_map);
	}

}
