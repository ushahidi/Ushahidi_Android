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

import android.app.Activity;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.R;

/**
 * @author eyedol
 * 
 */
public class AddCheckinView extends View {

	public EditText mCheckinMessageText;

	public EditText mFirstName;

	public EditText mLastName;

	public EditText mEmailAddress;

	public TextView mCheckinLocation;

	public TextView mFirstNameLabel;

	public TextView mLastNameLabel;

	public TextView mEmaiLabel;

	public TextView mContactLabel;

	public Button mPickPhoto;

	public MapView mMapView;

	public Button mDeleteCheckin;

	public Gallery gallery;

	public ImageSwitcher mSwitcher;

	public AddCheckinView(Activity activity) {
		super(activity);
		mCheckinMessageText = (EditText) activity
				.findViewById(R.id.checkin_message_text);
		mPickPhoto = (Button) activity.findViewById(R.id.checkin_photo_button);
		mDeleteCheckin = (Button) activity.findViewById(R.id.delete_checkin);
		mFirstName = (EditText) activity.findViewById(R.id.checkin_firstname);
		mLastName = (EditText) activity.findViewById(R.id.checkin_lastname);
		mEmailAddress = (EditText) activity.findViewById(R.id.checkin_email);
		mFirstNameLabel = (TextView) activity
				.findViewById(R.id.checkin_lbl_firstname);
		mLastNameLabel = (TextView) activity
				.findViewById(R.id.checkin_lbl_lastname);
		mEmaiLabel = (TextView) activity.findViewById(R.id.txt_lbl_email);
		mContactLabel = (TextView) activity
				.findViewById(R.id.personal_information);

		mCheckinLocation = (TextView) activity.findViewById(R.id.latlon);
		mMapView = (MapView) activity.findViewById(R.id.checkin_location_map);
		mSwitcher = (ImageSwitcher) activity
				.findViewById(R.id.checkin_image_switcher);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(activity,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(activity,
				android.R.anim.fade_out));
		gallery = (Gallery) activity.findViewById(R.id.checkin_gallery);
	}

}
