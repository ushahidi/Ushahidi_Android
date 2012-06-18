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
import android.app.ProgressDialog;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author eyedol
 * 
 */
public class AddCommentView extends View {

	public TextView fullNameLbl;
	public TextView emailAddressLbl;
	public TextView commentLbl;
	public EditText fullName;
	public EditText emailAddress;
	public EditText comment;
	public ProgressDialog dialog;

	/**
	 * @param activity
	 */
	public AddCommentView(Activity activity) {
		super(activity);
		fullNameLbl = (TextView) activity
				.findViewById(R.id.comment_full_name_lbl);
		fullName = (EditText) activity.findViewById(R.id.comment_full_name);
		emailAddressLbl = (TextView) activity
				.findViewById(R.id.comment_email_lbl);
		emailAddress = (EditText) activity.findViewById(R.id.comment_email);
		commentLbl = (TextView) activity.findViewById(R.id.comment_message);
		comment = (EditText) activity.findViewById(R.id.comment_message);
		this.dialog = new ProgressDialog(activity);
		this.dialog.setCancelable(true);
		this.dialog.setIndeterminate(true);
		this.dialog.setMessage(activity.getResources().getString(
				R.string.uploading));

	}

}
