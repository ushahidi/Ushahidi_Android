
package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListMapModel;

public class ListMapTabletAdapter extends BaseListAdapter<ListMapModel> {

    private int[] colors;

    private ListMapModel listMapModel;

    public ListMapTabletAdapter(Context context) {
        super(context);
        listMapModel = new ListMapModel();
        colors = new int[] {
                R.color.table_odd_row_color, R.color.table_even_row_color
        };
    }

    @Override
    // Use Context instead of FragmentActivity
    public void refresh(Context context) {
        final boolean loaded = listMapModel.load(context);
        if (loaded) {
            this.setItems(listMapModel.getMaps(context));
            log("Total: " + this.getCount());
        }
    }

    public void refresh(Context context, String query) {
        final boolean loaded = listMapModel.filter(context, query);
        if (loaded) {
            this.setItems(listMapModel.getMaps(context));
        }
    }

    public View getView(int position, View view, ViewGroup viewGroup) {

        final String mapId = String.valueOf(Preferences.activeDeployment);
        int colorPosition = position % colors.length;
        Widgets widgets = null;

        if (widgets == null) {
            view = inflater.inflate(R.layout.list_map_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets)view.getTag();
        }
        view.setBackgroundResource(colors[colorPosition]);

        // initialize view with content
        widgets.mapName.setText(getItem(position).getName());
        widgets.mapDesc.setText(getItem(position).getDesc());
        widgets.mapUrl.setText(getItem(position).getUrl());
        widgets.mapId.setText(getItem(position).getId());

        return view;
    }

    public class Widgets extends com.ushahidi.android.app.views.View {

        TextView mapName;

        TextView mapDesc;

        TextView mapUrl;

        TextView mapId;

        ImageView arrow;

        public Widgets(View convertView) {
            super(convertView);
            mapName = (TextView)convertView.findViewById(R.id.map_list_name);
            mapDesc = (TextView)convertView.findViewById(R.id.map_list_desc);
            mapUrl = (TextView)convertView.findViewById(R.id.map_list_url);
            mapId = (TextView)convertView.findViewById(R.id.map_list_id);
            arrow = (ImageView)convertView.findViewById(R.id.map_arrow);
        }
    }
}
