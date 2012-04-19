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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.Photo;
import com.ushahidi.android.app.models.ListReportPhotoModel;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListPhotoAdapter extends BaseListAdapter<Photo> {

	private ListReportPhotoModel mListPhotoModel;

	private List<Photo> items;
	
	private int totalPhotos;

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
			this.total = (TextView) view.findViewById(R.id.photo_total);

		}

		ImageView photo;
		TextView total;
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

		// FIXME: only show the first item for now. In the future only get one
		// item
		widgets.photo.setImageDrawable(getPhoto(getItem(position).getPhoto()));
		widgets.total.setText(context.getResources().getQuantityString(
				R.plurals.no_of_images, totalPhotos, totalPhotos));
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
		totalPhotos = mListPhotoModel.totalReportPhoto();
		if (loaded) {
			items = mListPhotoModel.getPhotos();
			this.setItems(items);
		}
	}

	public Drawable getPhoto(String fileName) {
		return ImageManager.getDrawables(context, fileName,
				Util.getScreenWidth(context));

	}

}
