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

/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListFetchedReportAdapter;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.services.FetchReportsComments;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.ui.phone.ListReportCommentActivity;
import com.ushahidi.android.app.ui.phone.ViewReportNewsActivity;
import com.ushahidi.android.app.ui.phone.ViewReportPhotoActivity;
import com.ushahidi.android.app.ui.phone.ViewReportVideoActivity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.ViewReportView;

public class ViewReportFragment extends SherlockFragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	public static final String ARG_CAT_ID = "catid";

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	private ViewReportView mView;

	private ListReportModel mReports;

	private List<ReportEntity> mReport;

	private ListFetchedReportAdapter mReportAdapter;

	private int mCategoryId;

	private int mReportId;

	private String mReportTitle;

	private Intent mFetchReportComments;

	private GoogleMap mMap;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static ViewReportFragment newInstance(int pageNumber, int categoryId) {
		ViewReportFragment fragment = new ViewReportFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		args.putInt(ARG_CAT_ID, categoryId);
		fragment.setArguments(args);

		return fragment;
	}

	public ViewReportFragment() {

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		mCategoryId = getArguments().getInt(ARG_CAT_ID);
		mReports = new ListReportModel();

	}

	@Override
	public void onResume() {
		mView.mMapView.onResume();
		super.onResume();

		getActivity().registerReceiver(
				fetchBroadcastReceiver,
				new IntentFilter(
						SyncServices.FETCH_REPORT_COMMENTS_SERVICES_ACTION));
	}

	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(fetchBroadcastReceiver);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public void onDestroy() {
		mView.mMapView.onDestroy();
		super.onDestroy();

	}

	@Override
	public void onLowMemory() {
		mView.mMapView.onLowMemory();
		super.onLowMemory();

	}

	private void fetchComments() {
		getActivity().registerReceiver(
				fetchBroadcastReceiver,
				new IntentFilter(
						SyncServices.FETCH_REPORT_COMMENTS_SERVICES_ACTION));

		mFetchReportComments = new Intent(getActivity(),
				FetchReportsComments.class);
		mFetchReportComments.putExtra("reportid", mReportId);
		getActivity().startService(mFetchReportComments);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_report,
				container, false);
		mView = new ViewReportView(rootView, getActivity());

		mReports = new ListReportModel();
		mView.mMapView.onCreate(savedInstanceState);
		if (mCategoryId > 0) {
			mReports.loadReportByCategory(mCategoryId);
		} else {
			mReports.load();
		}

		// Get GoogleMap from MapView
		mMap = mView.mMapView.getMap();

		try {
			MapsInitializer.initialize(getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		// Initialize views with report data. This also handles map
		// initialization
		initReport(mPageNumber);
		fetchComments();

		// Set the title view to show the page number.

		mView.setPageIndicator(getString(R.string.title_template_step,
				mPageNumber + 1, mReports.getReports().size()));

		return rootView;
	}

	private String fetchCategories(int reportId) {
		mReportAdapter = new ListFetchedReportAdapter(getActivity());
		return mReportAdapter.fetchCategories(reportId);
	}

	private void initReport(int position) {
		mReport = mReports.getReports();

		if (mReport != null) {
			// Configure map
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					mReport.get(position).getIncident().getLatitude(), mReport
							.get(position).getIncident().getLongitude()));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			mMap.getUiSettings().setMyLocationButtonEnabled(false);
			mMap.moveCamera(center);
			mMap.moveCamera(zoom);

			// Add a marker to this location
			addMarker(mMap, mReport.get(position).getIncident().getLatitude(),
					mReport.get(position).getIncident().getLongitude());

			mReportId = (int) mReport.get(position).getIncident().getId();

			mReportTitle = mReport.get(position).getIncident().getTitle();

			mView.setBody(mReport.get(position).getIncident().getDescription());
			mView.setCategory(fetchCategories(mReportId));
			mView.setLocation(mReport.get(position).getIncident()
					.getLocationName());
			String date = Util.datePattern("MMMM dd, yyyy 'at' hh:mm:ss aaa",
					mReport.get(position).getIncident().getDate());
			mView.setDate(date);
			mView.setTitle(mReportTitle);
			mView.setStatus(Util.setVerificationStatus(mReport.get(position)
					.getIncident().getVerified(), getActivity()));
			mView.setListNews((int) mReportId);
			mView.setListPhotos((int) mReportId);
			mView.setListVideos((int) mReportId);
			mView.setListComments(mReportId);
			mView.getListPhotos().setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Intent i = new Intent(getActivity(),
							ViewReportPhotoActivity.class);
					i.putExtra("reportid", mReportId);
					i.putExtra("position", 0);
					startActivityForResult(i, 0);
					getActivity().overridePendingTransition(R.anim.home_enter,
							R.anim.home_exit);
				}
			});
			mView.getListNews().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(getActivity(),
									ViewReportNewsActivity.class);
							i.putExtra("reportid", mReportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							getActivity().overridePendingTransition(
									R.anim.home_enter, R.anim.home_exit);
						}
					});

			mView.getListVideos().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(getActivity(),
									ViewReportVideoActivity.class);
							i.putExtra("reportid", mReportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							getActivity().overridePendingTransition(
									R.anim.home_enter, R.anim.home_exit);
						}
					});

			mView.getListComments().setOnItemClickListener(
					new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent i = new Intent(getActivity(),
									ListReportCommentActivity.class);
							i.putExtra("reportid", mReportId);
							i.putExtra("position", position);
							startActivityForResult(i, 0);
							getActivity().overridePendingTransition(
									R.anim.home_enter, R.anim.home_exit);
						}
					});
		}
	}

	private void addMarker(GoogleMap map, double lat, double lon) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	private BroadcastReceiver fetchBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 4);
				getActivity().stopService(mFetchReportComments);
				if (status == 4) {
					Util.showToast(getActivity(), R.string.internet_connection);
				} else if (status == 110) {
					Util.showToast(getActivity(), R.string.connection_timeout);
				} else if (status == 100) {
					Util.showToast(getActivity(),
							R.string.could_not_fetch_comment);
				} else if (status == 0) {

					mView.setListComments(mReportId);
				}
			}

			try {
				getActivity().unregisterReceiver(fetchBroadcastReceiver);
			} catch (IllegalArgumentException e) {
			}
		}
	};
}
