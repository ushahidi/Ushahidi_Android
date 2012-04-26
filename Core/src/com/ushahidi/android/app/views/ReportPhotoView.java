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
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.ushahidi.android.app.R;

/**
 * @author eyedol
 * 
 */
public class ReportPhotoView extends View {

	public ImageSwitcher imageSwitcher;

	public Context context;

	public ReportPhotoView(Activity activity) {
		super(activity);
		this.context = activity;
		imageSwitcher = (ImageSwitcher) activity
				.findViewById(R.id.imageSwitcher);
	}

	public void goNext() {
		Animation in = AnimationUtils.loadAnimation(context,
				android.R.anim.fade_in);
		imageSwitcher.startAnimation(in);
	}

	public void goPrevious() {
		Animation out = AnimationUtils.loadAnimation(context,
				android.R.anim.fade_in);
		imageSwitcher.startAnimation(out);
	}

	public ImageView imageView() {
		ImageView i = new ImageView(context);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		return i;
	}

}
