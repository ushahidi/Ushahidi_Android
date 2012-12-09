package com.ushahidi.android.app.adapters;

import java.util.List;

import android.content.Context;

import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.ReportEntity;

public class ListPendingReportAdapter extends ReportAdapter {

	public ListPendingReportAdapter(Context context) {
		super(context);
	}

	@Override
	public void refresh() {

		final boolean loaded = mListReportModel.loadPendingReports();
		if (loaded) {
			items = mListReportModel.getReports();
			this.setItems(items);
		}

	}

	public List<ReportEntity> pendingReports() {
		final boolean loaded = mListReportModel.loadPendingReports();
		if (loaded) {
			return mListReportModel.getReports();
		}
		return null;
	}

	public void refresh(int categoryId) {
		final boolean loaded = mListReportModel
				.loadPendingReportsByCategory(categoryId);
		if (loaded) {
			items = mListReportModel.getReports();
			this.setItems(items);
		}
	}

	public String fetchCategoriesId(int reportId) {
		StringBuilder categories = new StringBuilder();
		for (CategoryEntity category : mListReportModel
				.getCategoriesByReportId(reportId)) {
			if (category.getCategoryTitle().length() > 0) {
				categories.append(category.getCategoryId() + ",");
			}

		}

		// delete the last ,
		if (categories.length() > 0) {
			categories.deleteCharAt(categories.length() - 1);
		}
		return categories.toString();
	}
}
