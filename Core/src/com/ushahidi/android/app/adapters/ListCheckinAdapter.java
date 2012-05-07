package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ushahidi.android.app.models.ListCheckinModel;

public class ListCheckinAdapter extends
		BaseSectionListAdapter<ListCheckinModel> {

	class Widgets extends com.ushahidi.android.app.views.View {

		public Widgets(View view) {
			super(view);
		}

		// @Widget(R.id.example_list_item_label)
		TextView textView;
	}

	public ListCheckinAdapter(Context context) {
		super(context);
	}

	@Override
	public void refresh() {

	}
}
