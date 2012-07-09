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

package com.ushahidi.android.app.ui.phone;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseViewActivity;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.util.ImageSwitchWorker;
import com.ushahidi.android.app.views.ReportPhotoView;

/**
 * @author eyedol
 */
public class ViewReportPhotoActivity extends
		BaseViewActivity<ReportPhotoView, ListPhotoModel> implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory,
		View.OnTouchListener {

	private ListPhotoModel photo;

	private List<Photo> photos;

	private int position;

	private int reportId;

	private String fileName;

	private GestureDetector gestureDetector;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public ViewReportPhotoActivity() {
		super(ReportPhotoView.class, R.layout.photo, R.menu.view_media);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		photo = new ListPhotoModel();
		view = new ReportPhotoView(this);

		this.reportId = getIntent().getExtras().getInt("reportid", 0);
		this.position = getIntent().getExtras().getInt("position", 0);
		initReport(this.position);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_forward) {

			goNext();
			return true;

		} else if (item.getItemId() == R.id.menu_backward) {

			goPrevious();
			return true;

		} else if (item.getItemId() == R.id.menu_share) {
			sharePhoto(ImageManager.getPhotoPath(this) + fileName);
		}

		return super.onOptionsItemSelected(item);
	}

	private void goNext() {
		if (photos != null) {
			position++;
			if (!(position > (photos.size() - 1))) {
				setImage(view.imageSwitcher);
				view.goNext();
				int page = position;
				this.setTitle(page + 1);

			} else {
				position = photos.size() - 1;
			}
		}
	}

	private void goPrevious() {
		if (photos != null) {
			position--;
			if ((position < (photos.size() - 1)) && (position != -1)) {
				setImage(view.imageSwitcher);
				view.goPrevious();

				int page = position;
				this.setTitle(page + 1);
			} else {
				position = 0;
			}
		}
	}

	private void initReport(int position) {

		photos = photo.getPhotosByReportId(reportId);
		// Hack:: get by report ID, if it returns nothing get by checkin ID
		// FIXME:: make this independent of ID
		if (photos.size() == 0) {
			photos = photo.getPhotosByCheckinId(reportId);
		}
		if (view.imageSwitcher != null) {
			view.imageSwitcher.setFactory(this);
			view.imageSwitcher.setOnTouchListener(this);
		}

		gestureDetector = new GestureDetector(new GestureDetectorListener());
		if (photos != null && photos.size() > 0) {
			fileName = photos.get(position).getPhoto();
			setImage(view.imageSwitcher);
			int page = position;
			this.setTitle(page + 1);
		}

	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (photos != null)
			title.append(photos.size());
		setActionBarTitle(title.toString());
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public View makeView() {
		return view.imageView();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

	class GestureDetectorListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent eventA, MotionEvent eventB,
				float velocityX, float velocityY) {
			try {
				log("Swipe Max Off Path");
				if (Math.abs(eventA.getY() - eventB.getY()) > SWIPE_MAX_OFF_PATH) {

					return false;
				}

				// right to left
				if (eventA.getX() - eventB.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

					goPrevious();

				}
			} catch (Exception e) {
				log("GestureDetectorListener", e);
			}
			return false;
		}
	}
	
	private void setImage(ImageSwitcher imageSwitcher) {
		ImageSwitchWorker imageWorker = new ImageSwitchWorker(this);
		imageWorker.setImageFadeIn(true);
		imageWorker.loadImage(fileName, imageSwitcher, true, 0);
	}

}