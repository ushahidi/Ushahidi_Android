
package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListCheckinModel;

public class ListCheckinAdapter extends BaseListAdapter<ListCheckinModel> {
    
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
    public void refresh(Context context) {
        // TODO implement loading of Example model classes
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_checkin_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets)view.getTag();
        }
        // ExampleModel exampleModel = getItem(position);
        widgets.textView.setText(String.format("Example %d", position));
        return view;
    }
}
