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

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.Checkin;
import com.ushahidi.android.app.models.ListCheckinModel;
import com.ushahidi.android.app.util.ImageViewWorker;

public class ListFetchedCheckinAdapter extends
		BaseListAdapter<ListCheckinModel> implements Filterable {

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
			this.thumbnail = (ImageView) view
					.findViewById(R.id.checkin_thumbnail);
			this.checkinMessage = (TextView) view
					.findViewById(R.id.checkin_message);
			this.title = (TextView) view.findViewById(R.id.checkin_title);
			this.date = (TextView) view.findViewById(R.id.checkin_date);
			this.arrow = (ImageView) view.findViewById(R.id.checkin_arrow);
		}

		TextView title;

		TextView checkinMessage;

		TextView date;

		ImageView thumbnail;

		ImageView arrow;

	}

	private int[] colors;

	private ListCheckinModel mListCheckinModel;

	private List<ListCheckinModel> items;

	public ListFetchedCheckinAdapter(Context context) {
		super(context);

		colors = new int[] { R.drawable.odd_row_rounded_corners,
				R.drawable.even_row_rounded_corners };
	}

	@Override
	public void refresh() {
		mListCheckinModel = new ListCheckinModel();
		final boolean loaded = mListCheckinModel.load();
		if (loaded) {
			items = mListCheckinModel.getCheckins(context);
			this.setItems(items);
		}

	}

	public void refresh(int userId) {

		mListCheckinModel = new ListCheckinModel();
		final boolean loaded = mListCheckinModel.loadCheckinByUser(userId);
		if (loaded) {
			items = mListCheckinModel.getCheckins(context);
			this.setItems(items);
		}

	}

	public List<ListCheckinModel> fetchedCheckins() {
		mListCheckinModel = new ListCheckinModel();
		final boolean loaded = mListCheckinModel.load();
		if (loaded) {
			return mListCheckinModel.getCheckins(context);
		}
		return null;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {

		int colorPosition = position % colors.length;
		View row = inflater.inflate(R.layout.list_checkin_item, viewGroup,
				false);
		row.setBackgroundResource(colors[colorPosition]);

		Widgets widgets = (Widgets) row.getTag();

		if (widgets == null) {
			widgets = new Widgets(row);
			row.setTag(widgets);
		}

		if (getItem(position).getThumbnail() == null) {
			widgets.thumbnail.setImageResource(R.drawable.report_icon);

		} else {
			getPhoto(getItem(position).getThumbnail(), widgets.thumbnail);

		}

		widgets.title.setText(items.get(position).getUsername());
		widgets.date.setText(items.get(position).getDate());
		widgets.checkinMessage.setText(items.get(position).getMessage());
		widgets.arrow.setImageDrawable(context.getResources().getDrawable(
				R.drawable.arrow));
		return row;
	}

	public void getPhoto(String fileName, ImageView imageView) {
		ImageViewWorker imageWorker = new ImageViewWorker(context);
		imageWorker.setImageFadeIn(true);
		imageWorker.setLoadingImage(ImageManager.drawableToBitmap(context
				.getResources().getDrawable(R.drawable.report_icon)));
		imageWorker.loadImage(fileName, imageView, true, 0);
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
				ArrayList<Checkin> filteredItems = new ArrayList<Checkin>();
				ArrayList<Checkin> itemsHolder = new ArrayList<Checkin>();
				itemsHolder.addAll(items);
				for (Checkin checkin : itemsHolder) {
					if (checkin.getMessage().toLowerCase().contains(constraint)) {
						filteredItems.add(checkin);
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
			List<ListCheckinModel> checkins = (ArrayList<ListCheckinModel>) results.values;
			setItems(checkins);

		}

	}
}
