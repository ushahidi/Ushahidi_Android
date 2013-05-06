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
package com.ushahidi.android.app.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.MenuDrawerItemModel;

/**
 * Adapter for Menu drawer implementation
 * 
 */
public class MenuAdapter extends BaseAdapter {
	private List<Object> mItems;
	private final LayoutInflater mInflater;
	public int activePosition;

	public MenuAdapter(Context context, List<Object> items) {
		mInflater = LayoutInflater.from(context);
		mItems = items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof MenuItem ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Object item = getItem(position);

		if (v == null) {
			v = mInflater.inflate(R.layout.menu_drawer_row, parent, false);
		}

		TextView titleTextView = (TextView) v.findViewById(R.id.menu_row_title);
		titleTextView.setText(((MenuDrawerItemModel) item).title);

		ImageView iconImageView = (ImageView) v
				.findViewById(R.id.menu_row_icon);
		iconImageView.setImageResource(((MenuDrawerItemModel) item).iconRes);
		
		v.setTag(position);
		
		if(position == activePosition) {
			int bottom = v.getPaddingBottom();
            int top = v.getPaddingTop();
            int right = v.getPaddingRight();
            int left = v.getPaddingLeft();
            v.setBackgroundResource(R.drawable.menu_drawer_selected);
            v.setPadding(left, top, right, bottom);
		} else {
			v.setBackgroundResource(R.drawable.md_list_selector);
		}
		
		return v;
	}
}
