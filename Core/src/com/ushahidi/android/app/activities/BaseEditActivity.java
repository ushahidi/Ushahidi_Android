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

package com.ushahidi.android.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.Model;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.views.View;

/**
 * BaseEditActivity
 * 
 * Add shared functionality that exists between all Edit Activities
 */
public abstract class BaseEditActivity<V extends View, M extends Model> extends
		BaseActivity<V> {

	private SaveTask mSaveTask;

	private DiscardTask mDiscardTask;

	public BaseEditActivity(Class<V> view, int layout, int menu) {
		super(view, layout, menu);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mSaveTask != null) {
			mSaveTask.cancel(true);
		}

		if (mDiscardTask != null) {
			mDiscardTask.cancel(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			log("onBackPressed");
			showDialogs();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void showDialogs() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getText(R.string.unsaved_changes))
				.setMessage(
						getText(R.string.would_you_like_to_save_your_changes_))
				.setCancelable(false)
				.setPositiveButton(getText(R.string.save),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (mSaveTask == null) {
									mSaveTask = new SaveTask(
											BaseEditActivity.this);
									mSaveTask.execute((String) null);
								}

							}
						})
				.setNeutralButton(getText(R.string.discard),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if(mDiscardTask == null) {
									mDiscardTask = new DiscardTask(BaseEditActivity.this);
									mDiscardTask.execute((String) null);
								}
								
								finish();
							}
						})
				.setNegativeButton(getText(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create().show();
	}

	/**
	 * Save Model class, return true if successful
	 * 
	 * @return returns true if successful
	 */
	protected abstract boolean onSaveChanges();

	protected abstract boolean onDiscardChanges();

	/**
	 * Background progress task for saving Model
	 */
	protected class SaveTask extends ProgressTask {
		public SaveTask(Activity activity) {
			super(activity, R.string.saving_);
		}

		@Override
		protected Boolean doInBackground(String... args) {
			return onSaveChanges();
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				toastLong(R.string.saved);
				activity.finish();
			} else {
				toastLong(R.string.not_saved);
			}
		}
	}

	/**
	 * Background progress task for saving Model
	 */
	protected class DiscardTask extends ProgressTask {
		public DiscardTask(Activity activity) {
			super(activity, R.string.discard);
		}

		@Override
		protected Boolean doInBackground(String... args) {
			return onDiscardChanges();
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if (success) {
				activity.finish();
			}
		}
	}
}