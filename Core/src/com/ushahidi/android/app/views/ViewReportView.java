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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.CustomFormAdapter;
import com.ushahidi.android.app.adapters.ListCommentAdapter;
import com.ushahidi.android.app.adapters.ListNewsAdapter;
import com.ushahidi.android.app.adapters.ListPhotoAdapter;
import com.ushahidi.android.app.adapters.ListVideoAdapter;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.entities.ReportCustomFormEntity;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.util.ImageViewWorker;

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

	private TextView mPageIndicator;

	private TextView listNewsEmptyView;

	private TextView listPhotosEmptyView;

	private TextView listVideoEmptyView;

	private TextView listCommentEmptyView;
	
	public MapView mMapView;
	
	public GoogleMap mapView;

	private ListView listNews;

	private ListView listVideos;

	private ListView listComments;

	private Context context;

	public ListPhotoAdapter photoAdapter;
	public ListNewsAdapter newsAdapter;
	public ListVideoAdapter videoAdapter;
	public ListCommentAdapter commentAdapter;
	private LayoutInflater inflater;

	public ImageView photo;
	public TextView total;
	
	private LinearLayout reportCustomFormLayout;

	public ViewReportView(ViewGroup activity, Context context) { 
		super(activity);
		
		this.context = context;
		title = (TextView) activity.findViewById(R.id.title);
		category = (TextView) activity.findViewById(R.id.category);
		date = (TextView) activity.findViewById(R.id.date);
		location = (TextView) activity.findViewById(R.id.location);
		body = (TextView) activity.findViewById(R.id.desc);
		status = (TextView) activity.findViewById(R.id.status);
		mPageIndicator = (TextView) activity.findViewById(R.id.page_indicator);
		mMapView = (MapView) activity.findViewById(R.id.loc_map);
		listNews = (ListView) activity.findViewById(R.id.list_news);
		this.photo = (ImageView) activity.findViewById(R.id.list_report_photo);
		this.total = (TextView) activity.findViewById(R.id.photo_total);
		listPhotosEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_photos);

		photoAdapter = new ListPhotoAdapter(activity.getContext());
		newsAdapter = new ListNewsAdapter(activity.getContext());
		videoAdapter = new ListVideoAdapter(activity.getContext());
		commentAdapter = new ListCommentAdapter(activity.getContext());

		listNewsEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_news);
		if (listNewsEmptyView != null) {
			listNews.setEmptyView(listNewsEmptyView);
		}

		listPhotosEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_photos);

		/*
		 * if (listPhotosEmptyView != null) {
		 * listPhotos.setEmptyView(listPhotosEmptyView); }
		 */

		listVideos = (ListView) activity.findViewById(R.id.list_video);
		listVideoEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_video);
		if (listVideoEmptyView != null) {
			listVideos.setEmptyView(listVideoEmptyView);
		}

		listComments = (ListView) activity.findViewById(R.id.list_comments);
		listCommentEmptyView = (TextView) activity
				.findViewById(R.id.empty_list_for_comment);
		if (listCommentEmptyView != null) {
			listComments.setEmptyView(listCommentEmptyView);
		}
		
		reportCustomFormLayout = (LinearLayout)activity.findViewById(R.id.report_custom_form);

	}

	public View filterReport() {
		View v = inflater.inflate(R.layout.list_report_header, null);
		return v;
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setPageIndicator(String title) {
		this.mPageIndicator.setText(title);
	}

	public String getPageIndicator() {
		return this.mPageIndicator.getText().toString();
	}

	public String getTitle() {
		return this.getTitle().toString();
	}

	public void setCategory(String category) {
		this.category.setText(category);
	}

	public String getCategory() {
		return this.category.getText().toString();
	}

	public void setDate(String date) {
		this.date.setText(date);
	}

	public String getDate() {
		return this.date.getText().toString();
	}

	public void setLocation(String location) {
		this.location.setText(location);
	}

	public String getLocation() {
		return this.location.getText().toString();
	}

	public void setBody(String body) {
		this.body.setText(body);
	}

	public String getBody() {
		return this.body.getText().toString();
	}

	public void setStatus(int status) {
		final String s = status == 1 ? context
				.getString(R.string.yes) : context.getString(R.string.no);
		this.status.setText(s);
	}

	public String getStatus() {
		return this.status.getText().toString();
	}

	public GoogleMap getMapView() {
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
		if (photo != null) {
			ListPhotoModel mListPhotoModel = new ListPhotoModel();
			final boolean loaded = mListPhotoModel.load(reportId);
			int totalPhotos = mListPhotoModel.totalReportPhoto();
			if (loaded) {
				final List<PhotoEntity> items = mListPhotoModel.getPhotos();
				if (items.size() > 0) {
					getPhoto(items.get(0).getPhoto(), photo);
					total.setText(context.getResources().getQuantityString(
							R.plurals.no_of_images, totalPhotos, totalPhotos));
				} else {
					photo.setVisibility(View.GONE);
					total.setVisibility(View.GONE);
					listPhotosEmptyView.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	public void setReportCustomFormsValues(int reportId) {
		if (reportCustomFormLayout != null) {
			reportCustomFormLayout.removeAllViews();
			List<ReportCustomFormEntity> values = Database.mReportCustomFormDao.fetchReportCustomForms(reportId);
			
			for(ReportCustomFormEntity entity: values){
				View v = CustomFormAdapter.createTextView(context, entity);
				reportCustomFormLayout.addView(v);
			}
		}
	}


	public ImageView getListPhotos() {
		return this.photo;
	}

	public void setListComments(int reportId) {
		if (listComments != null) {
			commentAdapter.refresh(reportId);
			listComments.setAdapter(commentAdapter);
		}
	}

	public ListView getListComments() {
		return this.listComments;
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

	public void getPhoto(String fileName, ImageView imageView) {
		Preferences.loadSettings(context);
		ImageViewWorker imageWorker = new ImageViewWorker(context);
		imageWorker.setImageFadeIn(true);
		imageWorker
				.loadImage(fileName, imageView, true, Preferences.photoWidth);
	}

}
