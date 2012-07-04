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
package com.ushahidi.android.app.util;

import java.lang.ref.WeakReference;

import com.ushahidi.android.app.ImageManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageSwitcher;

public class ImageSwitchWorker {

	private static final int FADE_IN_TIME = 200;

	private Context context;

	private Bitmap loadingBitmap;

	private boolean fadeIn = true;

	public ImageSwitchWorker(Context context) {
		this.context = context;
	}

	public void loadImage(Object data, ImageSwitcher imageSwitcher, boolean fullScale,
			int width) {
		final BitmapWorkerTask task = new BitmapWorkerTask(imageSwitcher);
		final AsyncDrawable asyncDrawable = new AsyncDrawable(
				context.getResources(), loadingBitmap, task);
		imageSwitcher.setImageDrawable(asyncDrawable);
		task.fullScale = fullScale;
		task.width = width;
		task.imageSwitcher = imageSwitcher;
		task.execute(data);
	}

	public void setLoadingImage(Bitmap bitmap) {
		this.loadingBitmap = bitmap;
	}

	public void setImageFadeIn(boolean fadeIn) {
		this.fadeIn = fadeIn;
	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	private class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
		private Object data;
		private final WeakReference<ImageSwitcher> imageSwitcherReference;
		protected ImageSwitcher imageSwitcher;
		protected int width;
		protected boolean fullScale = false;

		public BitmapWorkerTask(ImageSwitcher imageSwitcher) {
			imageSwitcherReference = new WeakReference<ImageSwitcher>(imageSwitcher);
		}

		/**
		 * Background processing.
		 */
		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			// If the bitmap was not found in the cache and this task has not
			// been cancelled by
			// another thread and the ImageSwitcher that was originally bound to
			// this task is still
			// bound back to this task and our "exit early" flag is not set,
			// then call the main
			// process method (as implemented by a subclass)
			if (bitmap == null && !isCancelled()) {
				if ((fullScale) && (width == 0)) {
					bitmap = ImageManager.getBitmaps(context, dataString);
				} else {
					bitmap = ImageManager
							.getBitmaps(context, dataString, width);
				}
			}

			return bitmap;
		}

		/**
		 * Once the image is processed, associates it to the ImageSwitcher
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// if cancel was called on this task or the "exit early" flag is set
			// then we're done
			if (isCancelled()) {
				bitmap = null;
			}

			if (bitmap != null && imageSwitcher != null) {
				Log.i("ImageSwitchWorker","Images set");
				setImageBitmap(imageSwitcher, bitmap);
			}else {
				Log.i("ImageSwitchWorker","Images set");
			}
		}

	}

	/**
	 * Called when the processing is complete and the final bitmap should be set
	 * on the ImageSwitcher.
	 * 
	 * @param ImageSwitcher
	 * @param bitmap
	 */
	private void setImageBitmap(ImageSwitcher imageSwitcher, Bitmap bitmap) {
		if (fadeIn) {
			// Transition drawable with a transparent drwabale and the final
			// bitmap
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(context.getResources(), bitmap) });
			// Set background to loading bitmap
			imageSwitcher.setBackgroundDrawable(new BitmapDrawable(context
					.getResources(), loadingBitmap));

			imageSwitcher.setImageDrawable(td);
			td.startTransition(FADE_IN_TIME);
		} else {
			imageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
		}
	}
}