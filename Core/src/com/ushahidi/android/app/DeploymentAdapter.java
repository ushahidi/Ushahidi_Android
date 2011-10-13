
package com.ushahidi.android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.data.DeploymentsData;

public class DeploymentAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<DeploymentsData> iItems = new ArrayList<DeploymentsData>();

    private int[] colors;

    public DeploymentAdapter(Context context) {
        colors = new int[] {R.color.table_odd_row_color, R.color.table_even_row_color};
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return iItems.size();

    }

    public Object getItem(int position) {
        return iItems.get(position);

    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * Clear all items from the list
     */
    public void removeItems() {
        iItems.clear();
    }

    public void addItem(DeploymentsData deploymentsData) {
        iItems.add(deploymentsData);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final String deploymentId = String.valueOf(Preferences.activeDeployment);
        View row = mInflater.inflate(R.layout.deployment_list, parent, false);
        int colorPosition = position % colors.length;
        row.setBackgroundResource(colors[colorPosition]);

        ViewHolder holder = (ViewHolder)row.getTag();

        if (holder == null) {
            holder = new ViewHolder(row);
            row.setTag(holder);

        }

        // initialize view with content
        holder.deploymentName.setText(iItems.get(position).getName());
        holder.deploymentDesc.setText(iItems.get(position).getDesc());
        holder.deploymentUrl.setText(iItems.get(position).getUrl());
        holder.deploymentId.setText(iItems.get(position).getId());

        if (iItems.get(position).getId().equals(deploymentId)) {
            holder.arrow.setImageResource(R.drawable.deployment_selected);
        } else {
            holder.arrow.setImageResource(R.drawable.menu_arrow);
        }
        return row;
    }

    class ViewHolder {
        TextView deploymentName;

        TextView deploymentDesc;

        TextView deploymentUrl;

        TextView deploymentId;

        ImageView arrow;

        ViewHolder(View convertView) {
            deploymentName = (TextView)convertView.findViewById(R.id.deployment_list_name);
            deploymentDesc = (TextView)convertView.findViewById(R.id.deployment_list_desc);
            deploymentUrl = (TextView)convertView.findViewById(R.id.deployment_list_url);
            deploymentId = (TextView)convertView.findViewById(R.id.deployment_list_id);
            arrow = (ImageView)convertView.findViewById(R.id.deployment_arrow);
        }
    }
}
