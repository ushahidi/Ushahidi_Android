
package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListMapModel;

public class ListMapAdapter extends BaseListAdapter<ListMapModel> {

    private int[] colors;
    
    private ListMapModel listMapModel;
    public ListMapAdapter(Context context) {
        super(context);

        colors = new int[] {
                R.color.table_odd_row_color, R.color.table_even_row_color
        };
    }

    @Override
    //Use Context instead of FragmentActivity
    public void refresh(Context context) {
        listMapModel = new ListMapModel();
        listMapModel.load(context);
        this.setItems(listMapModel.mMaps);
    }
    
    public void refresh(Context context, String query) {
        listMapModel = new ListMapModel();
        listMapModel.filter(context, query);
        this.setItems(listMapModel.mMaps);
    }

    public View getView(int position, View view, ViewGroup viewGroup) {

        final String mapId = String.valueOf(Preferences.activeDeployment);
        int colorPosition = position % colors.length;
        Widgets widgets = null;
        
        if( widgets == null ) {
            view = inflater.inflate(R.layout.list_map_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }
        view.setBackgroundResource(colors[colorPosition]);
       
        // initialize view with content
        log("EbonyCounts : "+getItem(position).getName());
        widgets.mapName.setText(getItem(position).getName());
        widgets.mapDesc.setText(getItem(position).getDesc());
        widgets.mapUrl.setText(getItem(position).getUrl());
        widgets.mapId.setText(getItem(position).getId());

        if (getItem(position).getId().equals(mapId)) {
            widgets.arrow.setImageResource(R.drawable.deployment_selected);
        } else {
            widgets.arrow.setImageResource(R.drawable.menu_arrow);
        }

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
