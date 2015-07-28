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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * An Image Utility class
 * 
 */
public class ImageManager {

	// folder to save fetched photos.
	public static final String PHOTO = "/fetched";

	public static final String PENDING = "/pending";

	private static final int IO_BUFFER_SIZE = 512;

	private static BitmapFactory.Options getBitmapFactoryOptions(
			Context context, String fileName) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);
		return options;
	}

	public static Drawable getDrawables(Context context, String fileName) {
		String file = getPhotoPath(context) + fileName;
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);
		Bitmap scaled = PhotoUtils.scaleBitmap(options, file);
		return new BitmapDrawable(context.getResources(), scaled);

	}

	public static Bitmap getBitmaps(Context context, String fileName) {
		String file = getPhotoPath(context) + fileName;
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);
		// scale image
		return PhotoUtils
				.scaleBitmap(options, file);

	}

	public static Drawable getDrawables(Context context, String fileName,
			int width) {
		String file = getPhotoPath(context) + fileName;
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);
		// scale image
		Bitmap scaled = PhotoUtils.scaleBitmapByWidth(options, width,file);
		return new BitmapDrawable(context.getResources(), scaled);

	}

	public static Bitmap getBitmaps(Context context, String fileName, int width) {
		String file = getPhotoPath(context) + fileName;
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);

		return PhotoUtils.scaleBitmapByWidth(options, width,file);

	}

	public static Drawable getPendingDrawables(Context context, String fileName) {
		String file = getPhotoPath(context, fileName);
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);
		
		// scale image
		Bitmap scaled = PhotoUtils.scaleBitmap(options, file);
		return new BitmapDrawable(context.getResources(), scaled);

	}

	public static Drawable getPendingDrawables(Context context,
			String fileName, int width) {
		String file = getPhotoPath(context, fileName);
		final BitmapFactory.Options options = getBitmapFactoryOptions(context,
				file);

		// scale image
		Bitmap scaled = PhotoUtils.scaleBitmapByWidth(options, width, file);
		return new BitmapDrawable(context.getResources(), scaled);

	}

	public static Drawable getThumbnails(Context context, String fileName) {
		// get image
		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			Bitmap scaled = PhotoUtils.scaleThumbnail(original);
			return new BitmapDrawable(context.getResources(), scaled);

		}
		return null;
	}

	public static Bitmap getBitmapThumbnails(Context context, String fileName) {
		// get image
		Bitmap original = BitmapFactory.decodeFile(getPhotoPath(context)
				+ fileName);
		if (original != null) {
			// scale image
			return PhotoUtils.scaleThumbnail(original);
		}
		return null;
	}

	private static byte[] fetchImage(String address)
			throws MalformedURLException, IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(address).openStream(),
					IO_BUFFER_SIZE);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 4 * 1024);
			copy(in, out);
			out.flush();

			return dataStream.toByteArray();
		} catch (IOException e) {
			// android.util.Log.e("IO", "Could not load buddy icon: " + this,
			// e);

		} finally {
			closeStream(in);
			closeStream(out);

		}
		return null;

	}

	/**
	 * Copy the content of the input stream into the output stream, using a
	 * temporary byte array buffer whose size is defined by
	 * {@link #IO_BUFFER_SIZE}.
	 * 
	 * @param in
	 *            The input stream to copy from.
	 * @param out
	 *            The output stream to copy to.
	 * @throws IOException
	 *             If any error occurs during the copy.
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[4 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * Closes the specified stream.
	 * 
	 * @param stream
	 *            The stream to close.
	 */
	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e("IO", "Could not close stream", e);
			}
		}
	}

	public static void downloadImage(String imageUrl, String filename,
			Context context) {
		try {
			byte[] imageData = fetchImage(imageUrl);
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

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static String getSavedPhotoPath(Context context, String folder) {
		// create photo directory if it doesn't exist
		File path = new File(Environment.getExternalStorageDirectory(),
				String.format("%s%s%s", context.getPackageName(), "/", folder));
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
		File path = new File(Environment.getExternalStorageDirectory(),
				String.format("%s%s%s", context.getPackageName(), "/",
						pathfileName));

		if (!path.exists()) {
			return null;
		}

		return path.getAbsolutePath();
	}

	public static String getPendingPhotoPath(Context context) {
		return getSavedPhotoPath(context, PENDING);
	}

	public static boolean deletePendingPhoto(Context context, String fileName) {
		return deleteImage(getSavedPhotoPath(context, fileName));
	}

	public static boolean deleteImage(String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
			return true;
		}
		return false;
	}

	public static boolean deleteImage(String filename, String path) {
		File f = new File(path, filename);
		if (f.exists()) {
			f.delete();
			return true;
		}
		return false;
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

	public static void deletePendingImages(Context context) {
		if (isExternalStoragePresent()) {
			File path = new File(Environment.getExternalStorageDirectory(),
					context.getPackageName() + PENDING);
			deleteFiles(path);
		}
	}

	private static boolean deleteFiles(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return false;
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
