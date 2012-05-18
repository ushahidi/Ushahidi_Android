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

import android.app.Activity;
import android.widget.TextView;

/**
 * ExampleListView
 * 
 * All the widgets for /res/layout/example_list.xml
 */
public class ListCheckinView extends View {
	public TextView footerText;

	public TextView emptyText;

	public ListCheckinView(Activity activity) {
		super(activity);
		emptyText = (TextView) activity.findViewById(android.R.id.empty);
		footerText = (TextView) activity.findViewById(R.id.checkin_filter_by);
	}
}
