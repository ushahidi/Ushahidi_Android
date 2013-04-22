/** 
 ** Copyright (c) 2011 Ushahidi Inc
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

import com.ushahidi.android.app.Preferences;

public class PhotoUtils {

	private static final String CLASS_TAG = PhotoUtils.class.getCanonicalName();

	@SuppressWarnings("deprecation")
	public static int getScreenOrientation(Activity context) {
		Display display = context.getWindowManager().getDefaultDisplay();

		// The new API call requires API level 13 and above. suppressing this
		// for now
		if (display.getWidth() == display.getHeight()) {
			return Configuration.ORIENTATION_UNDEFINED;
		} else {
			if (display.getWidth() < display.getHeight()) {
				return Configuration.ORIENTATION_PORTRAIT;
			} else {
				return Configuration.ORIENTATION_LANDSCAPE;
			}
		}
	}

	public static File[] getPendingPhotos(Context context) {
		File path = pendingPhotosPath(context);

		if (path != null && path.exists()) {
			return path.listFiles();
		}
		return null;
	}

	public static File pendingPhotosPath(Context context) {
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName() + ImageManager.PENDING);

		return path;
	}

	public static Uri getPhotoUri(String filename, Activity activity) {
		File path = new File(Environment.getExternalStorageDirectory(),
				activity.getPackageName() + ImageManager.PENDING);
		if (!path.exists() && path.mkdir()) {
			return Uri.fromFile(new File(path, filename));
		}
		return Uri.fromFile(new File(path, filename));
	}

	public static String getPhotoPath(Activity activity) {
		new Util().log("getPhotoPath");
		File path = new File(Environment.getExternalStorageDirectory(),
				activity.getPackageName() + ImageManager.PENDING);
		return path.exists() ? path.getAbsolutePath() : null;

	}

	public static boolean imageExist(String filename, Activity activity) {
		new Util().log("%s %s ", "imageExist(): ", filename);
		File path = new File(filename);
		if (!path.exists()) {
			Log.d(CLASS_TAG, "image does not exist");
			return false;
		}
		Log.d(CLASS_TAG, "image does exist");
		return true;
	}

	public static Bitmap getGalleryPhoto(Activity activity, Uri uri) {
		if (uri != null) {
			String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.ORIENTATION };
			Cursor cursor = activity.getContentResolver().query(uri, columns,
					null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				String filePath = cursor.getString(cursor
						.getColumnIndex(columns[0]));
				int orientation = cursor.getInt(cursor
						.getColumnIndex(columns[1]));

				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, options);

				if (options != null) {
					Bitmap scaled = scaleBitmap(options, filePath);
					if (orientation == 0
							&& scaled.getWidth() < scaled.getHeight()) {
						new Util().log(String.format(
								"FILE:%s ORIENTATION: LANDSCAPE", filePath));
						Bitmap rotated = rotatePhoto(scaled, -90);
						scaled.recycle();
						return rotated;
					} else if (orientation == 90
							&& scaled.getWidth() > scaled.getHeight()) {
						new Util().log(String.format(
								"FILE:%s ORIENTATION: PORTRAIT", filePath));
						Bitmap rotated = rotatePhoto(scaled, 90);
						scaled.recycle();
						return rotated;
					} else {
						new Util().log(String.format("FILE:%s ORIENTATION: %d",
								filePath, orientation));
					}
					return scaled;
				}
			}
		}
		return null;
	}

	public static Bitmap getCameraPhoto(Activity activity, Uri uri) {
		if (uri != null) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(uri.getPath(), options);

			if (options != null) {
				new Util().log(String.format("ORIGINAL %dx%d",
						options.outWidth, options.outHeight));
				Bitmap scaled = scaleBitmap(options, uri.getPath());
				if (scaled != null) {
					new Util().log(String.format("SCALED %dx%d",
							scaled.getWidth(), scaled.getHeight()));
					if (getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT
							&& scaled.getWidth() > scaled.getHeight()) {
						Bitmap rotated = rotatePhoto(scaled, 90);
						scaled.recycle();
						return rotated;
					}
					return scaled;
				}
			}
		}
		return null;
	}

	public static boolean savePhoto(Activity activity, Bitmap bitmap,
			String fileName) {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArray);
			bitmap.recycle();
			ImageManager.writeImage(byteArray.toByteArray(), fileName,
					getPhotoPath(activity));
			byteArray.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap rotatePhoto(Bitmap bitmap, int rotate) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	public static synchronized Bitmap scaleBitmap(
			BitmapFactory.Options options, String filePath) {

		BitmapFactory.decodeFile(filePath, options);
		
		if (options != null) {
			float ratio = (float) options.outHeight / (float) options.outWidth;
			int width = Preferences.photoWidth > 0 ? Preferences.photoWidth
					: 500;
			new Util().log("Scaling image to " + width + " x " + ratio);
			int inSample = calculateInSampleSize(options, width,
					(int) (width * ratio));
			new Util().log("InSampleSize " + inSample);
			options.inSampleSize = inSample;
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(filePath, options);

		}
		return null;
	}

	public static synchronized Bitmap scaleBitmapByWidth(
			BitmapFactory.Options options, int width, String filePath) {
		// check dimensions

		if (options != null) {
			float ratio = (float) options.outHeight / (float) options.outWidth;

			new Util().log("Scaling image to " + width + " x " + ratio);
			int w = width > 0 ? width : 500;
			// calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, w,
					(int) (w * ratio));
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(filePath, options);

		}
		return null;
	}

	public static Bitmap scaleThumbnail(Bitmap original) {
		int height = 50;
		int width = 50;
		if (original != null) {

			Bitmap scaled = Bitmap.createScaledBitmap(original, width, height,
					true);
			original.recycle();
			return scaled;
		}
		return null;
	}

	/**
	 * Create rounded corner bitmap from original bitmap.
	 * <p>
	 * Reference
	 * http://stackoverflow.com/questions/2459916/how-to-make-an-imageview
	 * -to-have-rounded-corners
	 * 
	 * @param input
	 *            Original bitmap.
	 * @param cornerRadius
	 *            Corner radius in pixel.
	 * @param w
	 * @param h
	 * @param squareTL
	 * @param squareTR
	 * @param squareBL
	 * @param squareBR
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap input,
			float cornerRadius, int w, int h, boolean squareTL,
			boolean squareTR, boolean squareBL, boolean squareBR) {

		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		// make sure that our rounded corner is scaled appropriately
		final float roundPx = cornerRadius;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// draw rectangles over the corners we want to be squared
		if (squareTL) {
			canvas.drawRect(0, 0, w / 2, h / 2, paint);
		}
		if (squareTR) {
			canvas.drawRect(w / 2, 0, w, h / 2, paint);
		}
		if (squareBL) {
			canvas.drawRect(0, h / 2, w / 2, h, paint);
		}
		if (squareBR) {
			canvas.drawRect(w / 2, h / 2, w, h, paint);
		}

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(input, 0, 0, paint);

		return output;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		// raw height and weight of an image
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;
		new Util().log(String.format("ORIGINAL %dx%d", reqWidth, reqHeight));
		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

}