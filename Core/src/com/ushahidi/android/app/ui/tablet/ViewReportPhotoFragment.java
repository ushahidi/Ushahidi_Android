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
package com.ushahidi.android.app.ui.tablet;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.util.ImageManager;
import com.ushahidi.android.app.util.ImageSwitchWorker;
import com.ushahidi.android.app.util.ImageViewWorker;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ReportPhotoView;

/**
 * View report fragment
 */
public class ViewReportPhotoFragment extends SherlockFragment {

	private static final String ARG_PAGE = "position";

	private static final String ARG_REPORT_ID = "report_id";

	private ListPhotoModel mPhoto;

	private List<PhotoEntity> mPhotos;

	private int mPageNumber;

	private int mReportId;

	private ReportPhotoView mView;

	public static ViewReportPhotoFragment newInstance(int position, int reportId) {
		ViewReportPhotoFragment viewPhotoFrag = new ViewReportPhotoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, position);
		args.putInt(ARG_REPORT_ID, reportId);
		viewPhotoFrag.setArguments(args);
		return viewPhotoFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPhoto = new ListPhotoModel();
		mPageNumber = getArguments().getInt(ARG_PAGE);
		mReportId = getArguments().getInt(ARG_REPORT_ID);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.photo,
				container, false);
		mView = new ReportPhotoView(rootView);
		if (mReportId > 0) {
			mPhoto.getPendingPhotosByReportId(mReportId);

			initReport();
		}
		return rootView;
	}

	private void initReport() {
		mPhotos = mPhoto.getPhotosByReportId(mReportId);
		if (mView.imageSwitcher != null)
			setImage(mView.imageSwitcher, mPhotos.get(mPageNumber).getPhoto());

	}

	private void setImage(ImageView imageSwitcher, String fileName) {
		Preferences.loadSettings(getActivity());
		ImageViewWorker imageWorker = new ImageViewWorker(getActivity());
		imageWorker.setImageFadeIn(true);
		imageWorker.loadImage(fileName, imageSwitcher, true,
				Util.getScreenWidth(getActivity()));
	}

	public static String getTitle(Context context, int position, int total) {
		return String.format(context.getString(R.string.title_template_step,
				position + 1, total), position + 1);
	}
}
