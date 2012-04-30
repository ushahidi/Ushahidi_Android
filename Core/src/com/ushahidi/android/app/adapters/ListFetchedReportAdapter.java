package com.ushahidi.android.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.Category;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.util.Util;

public class ListFetchedReportAdapter extends BaseListAdapter<ListReportModel>
		implements Filterable {

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
			this.thumbnail = (ImageView) view
					.findViewById(R.id.report_thumbnail);
			this.title = (TextView) view.findViewById(R.id.report_title);
			this.description = (TextView) view
					.findViewById(R.id.report_description);
			this.date = (TextView) view.findViewById(R.id.report_date);
			this.iLocation = (TextView) view.findViewById(R.id.report_location);
			this.categories = (TextView) view
					.findViewById(R.id.report_categories);	
			this.status = (TextView) view.findViewById(R.id.report_status);
			this.arrow = (ImageView) view.findViewById(R.id.report_arrow);
		}

		TextView title;

		TextView iLocation;

		TextView date;

		TextView status;

		TextView categories;

		TextView description;

		ImageView thumbnail;

		ImageView arrow;

	}

	private int[] colors;

	private ListReportModel mListReportModel;

	private List<ListReportModel> items;

	public ListFetchedReportAdapter(Context context) {
		super(context);
	
		colors = new int[] { R.drawable.odd_row_rounded_corners,
				R.drawable.even_row_rounded_corners };
	}

	@Override
	public void refresh() {
		mListReportModel = new ListReportModel();
		final boolean loaded = mListReportModel.load();
		if (loaded) {
			items = mListReportModel.getReports(context);
			this.setItems(items);
		}

	}
	
	/**
	 * Get all fetched reports.
	 * 
	 * @return List<ListReportModel>
	 */
	public List<ListReportModel> fetchedReports() {
		mListReportModel = new ListReportModel();
		final boolean loaded = mListReportModel.load();
		if (loaded) {
			return mListReportModel.getReports(context);
		}
		
		return null;
	}

	public void refresh(int categoryId) {
		mListReportModel = new ListReportModel();
		final boolean loaded = mListReportModel
				.loadReportByCategory(categoryId);
		if (loaded) {
			items = mListReportModel.getReports(context);
			this.setItems(items);
		}
	}

	public String fetchCategories(int reportId) {
		mListReportModel = new ListReportModel();
		StringBuilder categories = new StringBuilder();
		for (Category category : mListReportModel
				.getCategoriesByReportId(reportId)) {
			if (category.getCategoryTitle().length() > 0) {
				categories.append(category.getCategoryTitle() + " |");
			}

		}

		// delete the last |
		if (categories.length() > 0) {
			categories.deleteCharAt(categories.length() - 1);
		}
		return categories.toString();
	}

	public View getView(int position, View view, ViewGroup viewGroup) {

		int colorPosition = position % colors.length;
		View row = inflater
				.inflate(R.layout.list_report_item, viewGroup, false);
		row.setBackgroundResource(colors[colorPosition]);

		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		widgets.thumbnail.setImageDrawable(getItem(position).getThumbnail());
		widgets.title.setText(getItem(position)
				.getTitle());
		widgets.date.setText(getItem(position).getDate());
		widgets.description.setText(Util.capitalizeString(getItem(position)
				.getDesc()));

		// FIXME: do this properly.
		widgets.categories.setText(Util
				.capitalizeString(Util.limitString(fetchCategories((int) getItem(position)
						.getReportId()), 100)));
		widgets.iLocation.setText(Util.capitalizeString(getItem(position)
				.getLocation()));
		// change the status color

		if (getItem(position).getStatus().equalsIgnoreCase(
				context.getString(R.string.report_verified))) {
			widgets.status.setTextColor(context.getResources().getColor(
					R.color.verified_text_color)); // green
		} else {
			widgets.status.setTextColor(context.getResources().getColor(
					R.color.unverified_text_color)); // red
		}

		widgets.status.setText(Util.capitalizeString(getItem(position)
				.getStatus()));

		widgets.arrow.setImageDrawable(getItem(position).getArrow());

		return row;
	}

	// Implements fitering pattern for the list items.
	@Override
	public Filter getFilter() {
		return new ReportFilter();
	}

	public class ReportFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			results.values = items;
			results.count = items.size();

			if (constraint != null && constraint.toString().length() > 0) {
				constraint = constraint.toString().toLowerCase();
				ArrayList<ListReportModel> filteredItems = new ArrayList<ListReportModel>();
				ArrayList<ListReportModel> itemsHolder = new ArrayList<ListReportModel>();
				itemsHolder.addAll(items);
				for (ListReportModel report : itemsHolder) {
					if (report.getTitle().toLowerCase().contains(constraint)
							|| report.getLocation().toLowerCase()
									.contains(constraint)) {
						filteredItems.add(report);
					}
				}
				results.count = filteredItems.size();
				results.values = filteredItems;
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			List<ListReportModel> reports = (ArrayList<ListReportModel>) results.values;
			setItems(reports);

		}

	}
}
