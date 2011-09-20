/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app.checkin;

import android.content.res.TypedArray;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.android.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckinAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<CheckinItem> iItems = new ArrayList<CheckinItem>();

    private int[] colors;

    public CheckinAdapter(Context context) {
        colors = new int[] {R.color.table_odd_row_color, R.color.table_even_row_color};
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(CheckinItem it) {
        iItems.add(it);
    }

    public void removeItems() {
        iItems.clear();
    }

    public void setListItems(List<CheckinItem> lit) {
        iItems = lit;
    }

    public int getCount() {
        return iItems.size();
    }

    public Object getItem(int position) {
        return iItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        return iItems.get(position).isSelectable();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.checkin_list_item, parent, false);

        // alternate row colors
        int colorPosition = position % colors.length;
        row.setBackgroundResource(colors[colorPosition]);

        ViewHolder holder = (ViewHolder)row.getTag();

        if (holder == null) {
            holder = new ViewHolder(row);
            row.setTag(holder);

        }

        holder.thumbnail.setImageDrawable(iItems.get(position).getThumbnail());

        holder.title.setText(iItems.get(position).getTitle());
        holder.date.setText(iItems.get(position).getDate());
        holder.checkinMessage.setText(iItems.get(position).getDesc());
        holder.arrow.setImageDrawable(iItems.get(position).getArrow());

        return row;
    }

    class ViewHolder {
        TextView title;

        TextView checkinMessage;

        TextView date;

        ImageView thumbnail;

        ImageView arrow;

        ViewHolder(View convertView) {
            this.thumbnail = (ImageView)convertView.findViewById(R.id.checkin_thumbnail);
            this.checkinMessage = (TextView)convertView.findViewById(R.id.checkin_message);
            this.title = (TextView)convertView.findViewById(R.id.checkin_title);
            this.date = (TextView)convertView.findViewById(R.id.checkin_date);
            this.arrow = (ImageView)convertView.findViewById(R.id.checkin_arrow);
        }
    }

}
