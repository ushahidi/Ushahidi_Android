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

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.NavDrawerItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author eyedol
 */
public class NavDrawerAdapter extends BaseListAdapter<NavDrawerItem> {

    /**
     * @param context
     */
    public NavDrawerAdapter(Context context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.adapters.BaseListAdapter#refresh()
     */
    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private class Widgets {
        TextView title;
        TextView counter;

        public Widgets(View convertView) {
            title = (TextView) convertView.findViewById(R.id.nav_drawer_title);
            counter = (TextView) convertView.findViewById(R.id.nav_drawer_counter);
        }
    }

}
