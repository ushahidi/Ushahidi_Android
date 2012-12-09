package com.ushahidi.android.app.adapters;

import java.util.List;

import android.content.Context;

import com.ushahidi.android.app.entities.ReportEntity;

public class ListFetchedReportAdapter extends ReportAdapter {

	public ListFetchedReportAdapter(Context context) {
		super(context);
	}

	@Override
	public void refresh() {
		final boolean loaded = mListReportModel.load();
		if (loaded) {
			items = mListReportModel.getReports();
			this.setItems(items);
		}
	}

	/**
	 * Get all fetched reports.
	 * 
	 * @return List<ListReportModel>
	 */
	public List<ReportEntity> fetchedReports() {
		final boolean loaded = mListReportModel.load();
		if (loaded) {
			return mListReportModel.getReports();
		}

		return null;
	}

	public void refresh(int categoryId) {
		final boolean loaded = mListReportModel
				.loadReportByCategory(categoryId);
		if (loaded) {
			items = mListReportModel.getReports();
			this.setItems(items);
		}
	}

}
