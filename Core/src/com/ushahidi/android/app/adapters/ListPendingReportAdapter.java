package com.ushahidi.android.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.java.sdk.api.Category;

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

	public List<Category> fetchCategoriesId(int reportId) {
		List<Category> categories = new ArrayList<Category>();
		Category c = new Category();
		for (CategoryEntity category : mListReportModel
				.getCategoriesByReportId(reportId)) {
			c.setId(category.getCategoryId());
			categories.add(c);
			

		}

		// delete the last ,
		
		return categories;
	}
}
