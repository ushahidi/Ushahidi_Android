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
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentMapActivity;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewStub;
import android.view.View;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListNewsAdapter;
import com.ushahidi.android.app.adapters.ListPhotoAdapter;
import com.ushahidi.android.app.adapters.ListVideoAdapter;

/**
 * @author eyedol
 */
public class ViewReportView extends com.ushahidi.android.app.views.View {

	private TextView title;

	private TextView body;

	private TextView date;

	private TextView location;

	private TextView category;

	private TextView status;

	private TextView listNewsEmptyView;

	private TextView listPhotosEmptyView;

	private TextView listVideoEmptyView;

	public MapView mapView;

	private ListView listNews;

	private ListView listPhotos;

	private ListView listVideos;

	private Context context;

	private LinearLayout viewReportRoot;


	public ViewReportView(FragmentMapActivity activity) {
		super(activity);
		this.context = activity;

		viewReportRoot = (LinearLayout) activity
				.findViewById(R.id.view_report_root);

		mapView = (MapView) activity.findViewById(R.id.loc_map);
		title = (TextView) activity.findViewById(R.id.title);
		category = (TextView) activity.findViewById(R.id.category);
		date = (TextView) activity.findViewById(R.id.date);
		location = (TextView) activity.findViewById(R.id.location);
		body = (TextView) activity.findViewById(R.id.desc);
		status = (TextView) activity.findViewById(R.id.status);
		listNews = (ListView) activity.findViewById(R.id.list_news);
		listNewsEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_news);
		if (listNewsEmptyView != null) {
			listNews.setEmptyView(listNewsEmptyView);
		}

		listPhotos = (ListView) activity.findViewById(R.id.list_photos);
		listPhotosEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_photos);

		if (listPhotosEmptyView != null) {
			listPhotos.setEmptyView(listPhotosEmptyView);
		}

		listVideos = (ListView) activity.findViewById(R.id.list_video);
		listVideoEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_video);
		if (listVideoEmptyView != null) {
			listVideos.setEmptyView(listVideoEmptyView);
		}

	}

	public ViewReportView(ViewGroup activity, Context context) {
		super(activity);
		this.context = context;

		/*
		 * viewReportRoot = (LinearLayout) activity
		 * .findViewById(R.id.view_report_root);
		 */
		/*
		 * titleStub = ((ViewStub) activity.findViewById(R.id.stub_title))
		 * .inflate(); descriptionStub = ((ViewStub) activity
		 * .findViewById(R.id.stub_description)).inflate(); statusStub =
		 * ((ViewStub) activity.findViewById(R.id.stub_status)) .inflate();
		 * dateStub = ((ViewStub)
		 * activity.findViewById(R.id.stub_date)).inflate(); locationStub =
		 * ((ViewStub) activity.findViewById(R.id.stub_location)) .inflate();
		 * categoriesStub = ((ViewStub)
		 * activity.findViewById(R.id.stub_category)) .inflate(); // mapStub =
		 * ((ViewStub) // activity.findViewById(R.id.stub_map)).inflate(); /*
		 * photoStub = ((ViewStub) activity.findViewById(R.id.stub_photo))
		 * .inflate(); newsStub = ((ViewStub)
		 * activity.findViewById(R.id.stub_news)).inflate(); videoStub =
		 * ((ViewStub) activity.findViewById(R.id.stub_video)) .inflate();
		 */
		mapView = (MapView) activity.findViewById(R.id.loc_map);
		title = (TextView) activity.findViewById(R.id.title);
		category = (TextView)activity.findViewById(R.id.category);
		date = (TextView) activity.findViewById(R.id.date);
		location = (TextView) activity.findViewById(R.id.location);
		body = (TextView) activity.findViewById(R.id.desc);
		status = (TextView) activity.findViewById(R.id.status);
		listNews = (ListView) activity.findViewById(R.id.list_news);
		listNewsEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_news);
		if (listNewsEmptyView != null) {
			listNews.setEmptyView(listNewsEmptyView);
		}

		listPhotos = (ListView) activity.findViewById(R.id.list_photos);
		listPhotosEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_photos);

		if (listPhotosEmptyView != null) {
			listPhotos.setEmptyView(listPhotosEmptyView);
		}

		listVideos = (ListView) activity.findViewById(R.id.list_video);
		listVideoEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_video);
		if (listVideoEmptyView != null) {
			listVideos.setEmptyView(listVideoEmptyView);
		}
	}

	public void setTitle(String title) {
		this.title.setTypeface(Typeface.DEFAULT_BOLD);
		this.title.setText(title);
	}

	public String getTitle() {
		return this.getTitle().toString();
	}

	public void setCategory(String category) {
		this.category.setTextColor(Color.BLACK);
		this.category.setText(category);
	}

	public String getCategory() {
		return this.category.getText().toString();
	}

	public void setDate(String date) {
		this.date.setTextColor(Color.BLACK);
		this.date.setText(date);
	}

	public String getDate() {
		return this.date.getText().toString();
	}

	public void setLocation(String location) {
		this.location.setTextColor(Color.BLACK);
		this.location.setText(location);
	}

	public String getLocation() {
		return this.location.getText().toString();
	}

	public void setBody(String body) {
		this.body.setTextColor(Color.BLACK);
		this.body.setText(body);
	}

	public String getBody() {
		return this.body.getText().toString();
	}

	public void setStatus(String status) {

		this.status.setText(status);
	}

	public String getStatus() {
		return this.status.getText().toString();
	}

	public MapView getMapView() {
		return this.mapView;
	}

	public void setListNews(int reportId) {
		if (listNews != null) {
			ListNewsAdapter adapter = new ListNewsAdapter(context);
			adapter.refresh(reportId);
			listNews.setAdapter(adapter);
		}
	}

	public ListView getListNews() {
		return this.listNews;
	}

	public void setListPhotos(int reportId) {
		if (listPhotos != null) {
			ListPhotoAdapter adapter = new ListPhotoAdapter(context);
			adapter.refresh(reportId);
			listPhotos.setAdapter(adapter);
		}
	}

	public ListView getListPhotos() {
		return this.listPhotos;
	}

	public void setListVideos(int reportId) {
		if (listVideos != null) {
			ListVideoAdapter adapter = new ListVideoAdapter(context);
			adapter.refresh(reportId);
			listVideos.setAdapter(adapter);
		}
	}

	public ListView getListVideos() {
		return this.listVideos;
	}

	private void showPanel(android.view.View panel, boolean slideUp) {
		panel.startAnimation(AnimationUtils.loadAnimation(context,
				slideUp ? R.anim.slide_in : R.anim.slide_out_top));
		panel.setVisibility(android.view.View.VISIBLE);
	}

	private void hidePanel(android.view.View panel, boolean slideDown) {
		panel.startAnimation(AnimationUtils.loadAnimation(context,
				slideDown ? R.anim.slide_out : R.anim.slide_in_top));
		panel.setVisibility(android.view.View.GONE);
	}

	public void showViews() {
		showPanel(viewReportRoot, true);
	}

	public void hideViews() {
		hidePanel(viewReportRoot, true);
	}
}
