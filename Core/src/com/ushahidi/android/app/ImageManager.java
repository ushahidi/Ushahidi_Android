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

package com.ushahidi.android.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.ushahidi.android.app.util.PhotoUtils;

public class ImageManager {

	// folder to save fetched photos.
	private static final String PHOTO = "/fetched";

	private static final String PENDING = "/pending";

	public static Drawable getDrawables(Context context, String fileName) {

		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleBitmap(original);
			return new FastBitmapDrawable(scaled);

		}
		return null;

	}

	/**
	 * Rename this to something meaningful
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Drawable getDrawables2(Context context, String pathfileName) {

		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context, "/"
				+ pathfileName));
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleBitmap(original);
			return new FastBitmapDrawable(scaled);

		}
		return null;

	}

	public static Drawable getDrawables(Context context, String fileName,
			int width) {
		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleBitmapByWidth(original, width);
			return new FastBitmapDrawable(scaled);

		}
		return null;
	}

	public static Drawable getPendingDrawables(Context context, String fileName) {

		Bitmap original = BitmapFactory.decodeFile(getPendingPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleBitmap(original);
			return new FastBitmapDrawable(scaled);

		}
		return null;

	}

	public static Drawable getPendingDrawables(Context context,
			String fileName, int width) {
		Bitmap original = BitmapFactory.decodeFile(getPendingPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleBitmapByWidth(original, width);
			return new FastBitmapDrawable(scaled);

		}
		return null;
	}

	public static Drawable getThumbnails(Context context, String fileName) {
		// get image

		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleThumbnail(original);
			return new BitmapDrawable(scaled);

		}
		return null;
	}

	protected static byte[] retrieveImageData(String imageUrl)
			throws IOException {
		URL url = new URL(imageUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// determine the image size and allocate a buffer
		int fileSize = connection.getContentLength();
		if (fileSize < 0) {
			return null;
		}
		byte[] imageData = new byte[fileSize];

		// download the file

		BufferedInputStream istream = new BufferedInputStream(
				connection.getInputStream());
		int bytesRead = 0;
		int offset = 0;
		while (bytesRead != -1 && offset < fileSize) {
			bytesRead = istream.read(imageData, offset, fileSize - offset);
			offset += bytesRead;
		}

		// clean up
		istream.close();
		connection.disconnect();

		return imageData;
	}

	public static void downloadImage(String imageUrl, String filename,
			Context context) {
		try {
			byte[] imageData = retrieveImageData(imageUrl);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArray);
			bitmap.recycle();

			// create photos directory
			writeImage(byteArray.toByteArray(), filename, getPhotoPath(context));
			byteArray.flush();

		} catch (Throwable e) {

			e.printStackTrace();

		}
	}

	public static void writeImage(byte[] data, String filename, String path) {

		deleteImage(filename, path);
		if (data != null) {
			FileOutputStream fOut;
			try {
				fOut = new FileOutputStream(new File(path, filename), false);
				fOut.write(data);
				fOut.flush();
				fOut.close();
			} catch (final FileNotFoundException e) {

				e.printStackTrace();
			} catch (final IOException e) {

				e.printStackTrace();
			}
		}

	}

	public static String getSavedPhotoPath(Context context, String folder) {
		// create photo directory if it doesn't exist
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName() + folder);
		if (!path.exists()) {
			// create path if it doesn't exist
			if (createDirectory(context)) {
				return path.getAbsolutePath() + "/";
			}
		}

		return path.getAbsolutePath() + "/";
	}

	public static String getPhotoPath(Context context) {
		return getSavedPhotoPath(context, PHOTO);
	}

	public static String getPhotoPath(Context context, String pathfileName) {
		return getSavedPhotoPath(context, pathfileName);
	}

	public static String getPendingPhotoPath(Context context) {
		return getSavedPhotoPath(context, PENDING);
	}

	public static boolean deletePendingPhoto(Context context, String fileName) {
		return deleteImage2(getSavedPhotoPath(context, fileName));
	}

	public static boolean deleteImage2(String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
			return true;
		}
		return false;
	}

	public static void deleteImage(String filename, String path) {
		File f = new File(path, filename);
		if (f.exists()) {
			f.delete();
		}
	}

	// make sure external storage is available
	private static boolean isExternalStoragePresent() {

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		return (mExternalStorageAvailable) && (mExternalStorageWriteable);
	}

	private static boolean createDirectory(Context context) {
		if (isExternalStoragePresent()) {
			File file = new File(Environment.getExternalStorageDirectory(),
					context.getPackageName() + PHOTO);
			if (!file.exists()) {
				if (!file.mkdirs()) {

					return false;
				}
			}
		}
		return true;
	}

	public static void deleteImages(Context context) {
		if (isExternalStoragePresent()) {
			File path = new File(Environment.getExternalStorageDirectory(),
					context.getPackageName() + PHOTO);
			deleteFiles(path);
		}
	}

	private static boolean deleteFiles(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}

			// go through the folder and delete its content
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
		return true;
	}

	public static void movePendingPhotos(Context context) {
		File[] pendingPhotos = PhotoUtils.getPendingPhotos(context);

		if (pendingPhotos != null && pendingPhotos.length > 0) {
			for (File file : pendingPhotos) {
				if (file.exists()) {
					// move file
					if (file.renameTo(new File(getPhotoPath(context)
							+ file.getName()))) {

						// delete after a successful move
						file.delete();
					}

				}
			}

		}
	}

}
