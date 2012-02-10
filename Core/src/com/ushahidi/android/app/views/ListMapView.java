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

package com.ushahidi.android.app.views;

import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.R;

/**
 * ExampleListView All the widgets for /res/layout/example_list.xml
 */
public class ListMapView extends View {
    public TextView mapName;

    public TextView mapDesc;

    public TextView mapUrl;

    public TextView mapId;

    public ImageView arrow;

    public TextView mTextView;

    public ListMapView(FragmentActivity activity) {
        super(activity);
        
        mapName = (TextView)activity.findViewById(R.id.deployment_list_name);
        mapDesc = (TextView)activity.findViewById(R.id.deployment_list_desc);
        mapUrl = (TextView)activity.findViewById(R.id.deployment_list_url);
        mapId = (TextView)activity.findViewById(R.id.deployment_list_id);
        arrow = (ImageView)activity.findViewById(R.id.deployment_arrow);
        mTextView = (TextView)activity.findViewById(R.id.search_deployment);

    }

}
