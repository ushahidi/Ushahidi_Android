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

package com.ushahidi.android.app.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListReportPhotoModel;

/**
 * @author eyedol
 */
public class ListPhotoAdapter extends BaseListAdapter<ListReportPhotoModel> {

	private ListReportPhotoModel mListPhotoModel;

	private List<ListReportPhotoModel> items;

	/**
	 * @param context
	 */
	public ListPhotoAdapter(Context context) {
		super(context);
	}

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
			this.photo = (ImageView) view.findViewById(R.id.list_report_photo);

		}

		ImageView photo;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		View row = inflater.inflate(R.layout.list_photo_item, viewGroup, false);
		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		widgets.photo.setImageDrawable(getPhoto(getItem(position).getPhoto()));

		return row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.adapters.BaseListAdapter#refresh(android.content
	 * .Context)
	 */
	@Override
	public void refresh() {

	}

	public void refresh(int reportId) {
		mListPhotoModel = new ListReportPhotoModel();
		final boolean loaded = mListPhotoModel.load(reportId);
		if (loaded) {
			items = mListPhotoModel.getPhotos();
			this.setItems(items);
		}
	}

	public Drawable getPhoto(String fileName) {
		return ImageManager.getDrawables(context, fileName, getScreenWidth());
		//return Drawable.createFromPath(ImageManager.getPhotoPath(context)
			//	+ fileName);
	}

	public int getScreenWidth() {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth();
	}
}
