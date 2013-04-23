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
package com.ushahidi.android.app.helpers;

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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ushahidi.android.app.GMap;
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

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy
 * title indicating the page number, along with some dummy text.
 * 
 * <p>
 * This class is used by the {@link CardFlipActivity} and
 * {@link ScreenSlideActivity} samples.
 * </p>
 */
public class ScreenSlidePageFragment extends SherlockFragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	private ViewReportView mView;

	private ListReportModel mReports;

	private List<ReportEntity> mReport;

	private ListFetchedReportAdapter mReportAdapter;

	private int mPosition;

	private int mCategoryId;

	private int mReportId;

	private String mReportTitle;

	private Intent mFetchReportComments;

	private GMap mGMap;
	
	private SupportMapFragment fragment;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static ScreenSlidePageFragment create(int pageNumber) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlidePageFragment() {

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    FragmentManager fm = getFragmentManager();
	    fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_fragment_layout);
	    if (fragment == null) {
	        fragment = SupportMapFragment.newInstance();
	        fm.beginTransaction().replace(R.id.map_fragment_layout, fragment).commit();
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);

	}

	@Override
	public void onResume() {
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
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.view_report, container, false);
		mView = new ViewReportView(rootView, getActivity());

		mReports = new ListReportModel();
		if (new GMap(getActivity()).checkForGMaps()) {
			
			if(fragment !=null) {
				
				mView.mapView = fragment.getMap();
				mGMap = new GMap(mView.mapView);
			}
		}

		mCategoryId = getArguments().getInt("category", 0);
		mPosition = getArguments().getInt("id", 0);

		if (mCategoryId > 0) {
			mReports.loadReportByCategory(mCategoryId);
		} else {
			mReports.load();
		}

		initReport(mPosition);
		fetchComments();

		// Set the title view to show the page number.
		/*((TextView) rootView.findViewById(android.R.id.text1))
				.setText(getString(R.string.title_template_step,
						mPageNumber + 1));*/

		return rootView;
	}

	private String fetchCategories(int reportId) {
		mReportAdapter = new ListFetchedReportAdapter(getActivity());
		return mReportAdapter.fetchCategories(reportId);
	}

	private void initReport(int position) {
		mReport = mReports.getReports();

		if (mReport != null) {
			mReportId = (int) mReport.get(position).getIncident().getId();

			mReportTitle = mReport.get(position).getIncident().getTitle();

			mView.setBody(mReport.get(position).getIncident().getDescription());
			mView.setCategory(fetchCategories(mReportId));
			mView.setLocation(mReport.get(position).getIncident()
					.getLocationName());
			mView.setDate(mReport.get(position).getIncident().getDate()
					.toString());
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

			centerLocationWithMarker(mReport.get(position).getIncident()
					.getLatitude(), mReport.get(position).getIncident()
					.getLongitude());
			int page = position;
			//this.setTitle(page + 1);
		}
	}

	/**
	 * Convert latitude and longitude to a GeoPoint
	 * 
	 * @param latitude
	 *            Latitude
	 * @param longitude
	 *            Lingitude
	 * @return GeoPoint
	 */
	private LatLng getPoint(double latitude, double longitude) {
		return (new LatLng(latitude, longitude));
	}

	private void centerLocationWithMarker(double latitude, double longitude) {
		if (mGMap != null)
			mGMap.centerLocationWithMarker(getPoint(latitude, longitude));

	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (mReport != null)
			title.append(mReport.size());

		if (mGMap != null)
			mGMap.setActionBarTitle(title.toString());
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
				mView.dialog.cancel();
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
