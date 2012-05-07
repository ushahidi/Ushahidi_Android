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
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.User;
import com.ushahidi.android.app.models.UserModel;

/**
 * @author eyedol
 * 
 */
public class UserSpinnerAdater extends BaseArrayAdapter<User> {

	private static final String DEFAULT_COLOR = "#000000";

	public UserSpinnerAdater(Context context) {
		super(context);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		Widgets widget;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.users_spinner_row_item,
					null);
			widget = new Widgets();
			widget.title = (TextView) convertView.findViewById(R.id.username);
			widget.color = (TextView) convertView.findViewById(R.id.user_color);
			convertView.setTag(widget);
		} else {
			widget = (Widgets) convertView.getTag();
		}

		if (getTag(position).getUsername() != null) {
			widget.title.setText(getTag(position).getUsername());
		}

		// check if color is set
		if (getTag(position).getUserColor() != null) {
			if (TextUtils.isEmpty(getTag(position).getUserColor().trim())) {
				try {
					widget.color.setBackgroundColor(Color
							.parseColor(DEFAULT_COLOR));
				} catch (IllegalArgumentException exception) {
					log("Error parsing color hex", exception);
				}
			} else {
				try {
					widget.color.setBackgroundColor(Color.parseColor(getTag(
							position).getUserColor().trim()));
				} catch (IllegalArgumentException exception) {
					log("Error parsing color", exception);
				}
			}
		}

		return convertView;
	}

	class Widgets {
		TextView title;
		TextView color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.adapters.BaseArrayAdapter#refresh()
	 */
	@Override
	public void refresh() {
		UserModel mUserModel = new UserModel();
		List<User> listUsers;
		if (mUserModel.load()) {
			listUsers = mUserModel.users;
			if (listUsers != null && listUsers.size() > 0) {
				
				// This is to make room for all categories label
				User sUser = new User();
				sUser.setUserId(0);
				sUser.setDbId(0);
				sUser.setUsername(context.getString(R.string.all_users));
				sUser.setUserColor(DEFAULT_COLOR);
				add(sUser.getUsername(), sUser);

				for (User user : listUsers) {
					add(user.getUsername(), user);
				}
			}
		}

	}

}
