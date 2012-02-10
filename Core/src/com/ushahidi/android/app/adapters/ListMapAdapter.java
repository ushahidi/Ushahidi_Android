
package com.ushahidi.android.app.adapters;

import android.content.Context;
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
    
    public ListMapAdapter(Context context) {
        super(context);
        
        colors = new int[] {R.color.table_odd_row_color, R.color.table_even_row_color};
    }

    @Override
    public void refresh(Context context) {
        new ListMapModel().load(context);
        
    }
    
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.i("ListMapModel", "Total List size Yes ");
        final String mapId = String.valueOf(Preferences.activeDeployment);
        int colorPosition = position % colors.length;
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_map_item, viewGroup, false);
            
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            view.setBackgroundResource(colors[colorPosition]);
            widgets = (Widgets)view.getTag();
            
        }
        
        // initialize view with content
        widgets.mapName.setText(getItem(position) .getName());
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
    
    public static class Widgets extends com.ushahidi.android.app.views.View{
       
        
        TextView mapName;

        TextView mapDesc;

        TextView mapUrl;

        TextView mapId;

        ImageView arrow;

        public Widgets(View convertView) {
            super(convertView);
            mapName = (TextView)convertView.findViewById(R.id.deployment_list_name);
            mapDesc = (TextView)convertView.findViewById(R.id.deployment_list_desc);
            mapUrl = (TextView)convertView.findViewById(R.id.deployment_list_url);
            mapId = (TextView)convertView.findViewById(R.id.deployment_list_id);
            arrow = (ImageView)convertView.findViewById(R.id.deployment_arrow);
        }
    }
}
