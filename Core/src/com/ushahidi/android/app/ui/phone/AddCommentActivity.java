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
package com.ushahidi.android.app.ui.phone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.View;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseEditActivity;
import com.ushahidi.android.app.models.ListCommentModel;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.services.UploadComments;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.AddCommentView;

/**
 * @author eyedol
 * 
 */
public class AddCommentActivity extends
		BaseEditActivity<AddCommentView, ListCommentModel> {

	private boolean mError = false;

	private String mErrorMessage;

	private Intent uploadComment;

	private int reportId;

	private int checkinId;

	private static final int DIALOG_SHOW_REQUIRED = 0;

	private static final int DIALOG_SHOW_MESSAGE = 1;

	/**
	 * @param view
	 * @param layout
	 * @param menu
	 */
	public AddCommentActivity() {
		super(AddCommentView.class, R.layout.add_comment, R.menu.add_comment);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hidePersonalInfo();
		this.reportId = getIntent().getExtras().getInt("reportid", 0);
		this.checkinId = getIntent().getExtras().getInt("checkinid", 0);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(uploadBroadcastReceiver, new IntentFilter(
				SyncServices.UPLOAD_COMMENT_SERVICES_ACTION));

	}

	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(uploadBroadcastReceiver);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_cancel_comment) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_send_comment) {
			validateComment();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * Hide contact info if first and last names are set at the settings screen
	 */
	private void hidePersonalInfo() {

		if (!TextUtils.isEmpty(Preferences.lastname)
				|| !TextUtils.isEmpty(Preferences.firstname)) {
			view.fullNameLbl.setVisibility(View.GONE);
			view.fullName.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(Preferences.email)) {
			view.emailAddressLbl.setVisibility(View.GONE);
			view.emailAddress.setVisibility(View.GONE);
		}

		view.fullName.setText(String.format("%s %s ", Preferences.firstname,
				Preferences.lastname));
		view.emailAddress.setText(Preferences.email);

	}

	private void validateComment() {

		mError = false;
		boolean required = false;

		// validate email field
		if ((TextUtils.isEmpty(view.fullName.getText().toString()))
				&& (TextUtils.isEmpty(Preferences.firstname) || TextUtils
						.isEmpty(Preferences.lastname))) {
			mErrorMessage = getString(R.string.enter_full_name) + "\n";
			required = true;
		}

		if (TextUtils.isEmpty(view.emailAddress.getText().toString())
				|| !Util.validateEmail(view.emailAddress.getText().toString())) {
			mErrorMessage = getString(R.string.valid_email_address) + "\n";
			mError = true;
		}

		if (TextUtils.isEmpty(view.comment.getText().toString())
				|| view.comment.getText().toString().length() < 3) {
			mErrorMessage = getString(R.string.enter_comment) + "\n";
			mError = true;
			required = true;
		}

		if (required) {
			showDialog(DIALOG_SHOW_REQUIRED);
		} else if (mError) {
			showDialog(DIALOG_SHOW_MESSAGE);
		} else {
			addComment();
		}
	}

	private void addComment() {
		view.dialog.show();
		Bundle comments = new Bundle();
		comments.putString("comment_author", view.fullName.getText().toString());
		comments.putString("comment_description", view.comment.getText()
				.toString());
		comments.putString("comment_email", view.emailAddress.getText()
				.toString());
		comments.putInt("report_id", reportId);
		comments.putInt("checkin_id", checkinId);

		uploadComment = new Intent(this, UploadComments.class);
		uploadComment.putExtras(comments);
		startService(uploadComment);
	}

	/**
	 * Create various dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SHOW_MESSAGE:
			AlertDialog.Builder messageBuilder = new AlertDialog.Builder(this);
			messageBuilder.setMessage(mErrorMessage).setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog showDialog = messageBuilder.create();
			showDialog.show();
			break;

		case DIALOG_SHOW_REQUIRED:
			AlertDialog.Builder requiredBuilder = new AlertDialog.Builder(this);
			requiredBuilder.setTitle(R.string.required_fields);
			requiredBuilder.setMessage(mErrorMessage).setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog showRequiredDialog = requiredBuilder.create();
			showRequiredDialog.show();
			break;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.android.app.activities.BaseEditMapActivity#onSaveChanges()
	 */
	@Override
	protected boolean onSaveChanges() {

		return false;
	}

	private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int status = intent.getIntExtra("status", 3);
				stopService(uploadComment);
				try {
					unregisterReceiver(uploadBroadcastReceiver);
				} catch (IllegalArgumentException e) {
				}
				stopService(uploadComment);
				view.dialog.cancel();
				if (status == 0) {
					toastLong(getString(R.string.uploaded));

				} else if (status == 1 || status == 2) {
					toastLong(R.string.saved);
				}

			} else {
				toastLong(R.string.failed);
			}

			finish();
		}
	};

	/* (non-Javadoc)
	 * @see com.ushahidi.android.app.activities.BaseEditActivity#onDiscardChanges()
	 */
	@Override
	protected boolean onDiscardChanges() {
		// TODO Auto-generated method stub
		return true;
	}

}
