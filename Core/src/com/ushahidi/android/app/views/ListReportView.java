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

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ushahidi.android.app.R;

public class ListReportView extends View {

	private ArrayAdapter<String> spinnerArrayAdapter;

	public TextView footerText;
	
	public TextView emptyText;

	public ListReportView(Activity activity) {
		super(activity);
		emptyText = (TextView) activity.findViewById(android.R.id.empty);
		footerText = (TextView) activity.findViewById(R.id.footer_text);
	}

	public ArrayAdapter<String> getArrayAdapter() {
		return spinnerArrayAdapter;
	}

}
