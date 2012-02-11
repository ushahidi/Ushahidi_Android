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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ushahidi.android.app.R;

/**
 * ExampleListView All the widgets for /res/layout/example_list.xml
 */
public class ListMapView extends  com.ushahidi.android.app.views.View {
    
    public TextView mTextView;

    public TextView mEmptyList;
    
    public ListView mListView;
    
    public ProgressBar mProgressBar;

    public ListMapView(FragmentActivity activity) {
        super(activity);
        
        mTextView = (TextView)activity.findViewById(R.id.search_map);
        mEmptyList = (TextView)activity.findViewById(R.id.empty_list_for_maps);
        mListView = (ListView)activity.findViewById(R.id.list_map_table);
        mProgressBar = (ProgressBar)activity.findViewById(R.id.map_refresh_progress);
    }
    
    public void displayEmptyListText() {

        if (mListView.getCount() == 0) {
            mEmptyList.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mEmptyList.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            Log.i("ListMapModel", "Total " + mListView.getCount());
        }
        
    }
    
    public void displayProgressBar() {
        
    }

}
