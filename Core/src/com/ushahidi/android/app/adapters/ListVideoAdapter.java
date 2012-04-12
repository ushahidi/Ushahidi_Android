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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListReportVideoModel;

/**
 * @author eyedol
 */
public class ListVideoAdapter extends BaseListAdapter<ListReportVideoModel> {

	private ListReportVideoModel mListReportVideoModel;

	private List<ListReportVideoModel> items;

	private int totalVideos;

	/**
	 * @param context
	 */
	public ListVideoAdapter(Context context) {
		super(context);
	}

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
			this.video = (TextView) view.findViewById(R.id.report_video_webview);
			this.total = (TextView) view.findViewById(R.id.video_total);
		}

		TextView video;
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
		View row = inflater.inflate(R.layout.list_video_item, viewGroup, false);
		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		//widgets.video.loadUrl(getItem(position).getVideo());
		widgets.video.setText(getItem(position).getVideo());
		widgets.total.setText(context.getResources().getQuantityString(
				R.plurals.no_of_videos, totalVideos, totalVideos));
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
		mListReportVideoModel = new ListReportVideoModel();
		final boolean loaded = mListReportVideoModel.load(reportId);
		totalVideos = mListReportVideoModel.totalReportVideos();
		if (loaded) {
			items = mListReportVideoModel.getVideos();
			this.setItems(items);
		}
	}
}
