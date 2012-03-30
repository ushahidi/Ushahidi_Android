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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
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

	public ReportPhotoView(FragmentActivity activity) {
		super(activity);
		this.context = activity;
		imageSwitcher = (ImageSwitcher) activity
				.findViewById(R.id.imageSwitcher);
	}

	public void goNext(Drawable drawable) {
		Animation out = AnimationUtils.loadAnimation(context,
				android.R.anim.slide_out_right);
		Animation in = AnimationUtils.loadAnimation(context,
				android.R.anim.slide_in_left);
		imageSwitcher.setInAnimation(in);
		imageSwitcher.setOutAnimation(out);
		imageSwitcher.setImageDrawable(drawable);
	}

	public void goPrevious(Drawable drawable) {
		Animation out = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		Animation in = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		imageSwitcher.setInAnimation(in);
		imageSwitcher.setOutAnimation(out);
		imageSwitcher.setImageDrawable(drawable);
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
