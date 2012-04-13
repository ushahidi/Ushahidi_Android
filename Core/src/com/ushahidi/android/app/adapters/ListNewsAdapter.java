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
import com.ushahidi.android.app.models.ListReportNewsModel;

/**
 * @author eyedol
 */
public class ListNewsAdapter extends BaseListAdapter<ListReportNewsModel> {

	private ListReportNewsModel mListNewsModel;

	private List<ListReportNewsModel> items;
	
	private int totalNews;

	/**
	 * @param context
	 */
	public ListNewsAdapter(Context context) {
		super(context);
	}

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
			this.newsTitle = (TextView) view.findViewById(R.id.news_title);
			this.total = (TextView) view.findViewById(R.id.news_total);
		}

		TextView newsTitle;
		
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
		View row = inflater.inflate(R.layout.list_news_item, viewGroup, false);
		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		widgets.newsTitle.setText(getItem(position).getTitle());
		widgets.total.setText(context.getResources().getQuantityString(
				R.plurals.no_of_news, totalNews, totalNews));
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
		mListNewsModel = new ListReportNewsModel();
		final boolean loaded = mListNewsModel.load(reportId);
		totalNews = mListNewsModel.totalReportNews();
		if (loaded) {
			items = mListNewsModel.getNews();
			this.setItems(items);
		}
	}
}
