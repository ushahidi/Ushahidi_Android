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

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ListMapAdapter;

import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * ExampleListView
 *
 * All the widgets for /res/layout/example_list.xml
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
        Log.i("ListMapModel", "Total adapter size "+new ListMapAdapter(activity).getCount());
        mapName = (TextView) activity.findViewById(R.id.deployment_list_name);
        mapDesc = (TextView)activity.findViewById(R.id.deployment_list_desc);
        mapUrl = (TextView)activity.findViewById(R.id.deployment_list_url);
        mapId = (TextView)activity.findViewById(R.id.deployment_list_id);
        arrow = (ImageView)activity.findViewById(R.id.deployment_arrow);
        mTextView = (TextView)activity.findViewById(R.id.search_deployment);
        
        mTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //showResults(s.toString());
                 //toastShort(s.toString());       
            }

        });
    }
    
    
    
}
