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

package com.ushahidi.android.app;

import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class ListIncidentAdapter extends BaseAdapter {

    private Context iContext;

    private List<ListIncidentText> iItems = new ArrayList<ListIncidentText>();

    public ListIncidentAdapter(Context context) {
        iContext = context;
    }

    public void addItem(ListIncidentText it) {
        iItems.add(it);
    }

    public void removeItems() {
        iItems.clear();
    }

    public void setListItems(List<ListIncidentText> lit) {
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
        ListIncidentTextView iTv;
        
        if (convertView == null) {
            iTv = new ListIncidentTextView(iContext, iItems.get(position));

        } else {

            iTv = (ListIncidentTextView)convertView;

            iTv.setThumbnail(iItems.get(position).getThumbnail());

            iTv.setTitle(iItems.get(position).getTitle());

            iTv.setDate(iItems.get(position).getDate());
            
            //change the status color
            if(iItems.get(position).getStatus().equalsIgnoreCase("Verified")) {
                iTv.setStatusColor(Color.rgb(41, 142, 40)); // green
            } else if (iItems.get(position).getStatus().equalsIgnoreCase("Unverified")) {
                iTv.setStatusColor(Color.rgb(237, 0, 0)); // red
            }
            iTv.setStatus(iItems.get(position).getStatus());
            iTv.setLocation(iItems.get(position).getLocation());
            iTv.setDesc(iItems.get(position).getDesc());
            iTv.setMedia(iItems.get(position).getMedia());
            iTv.setCategories(iItems.get(position).getCategories());
            iTv.setId(iItems.get(position).getId());
            iTv.setArrow(iItems.get(position).getArrow());
        }
        return iTv;
    }

}
