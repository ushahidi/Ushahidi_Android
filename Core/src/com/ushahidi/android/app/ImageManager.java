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
import android.util.Log;

public class ImageManager {

    private static final String PHOTO = "/photo";

    public static Drawable getDrawables(Context context, String fileName) {

        Drawable d = null;
        
        BitmapDrawable bD = new BitmapDrawable(
                (new File(getPhotoPath(context), fileName)).toString());

        d = bD.mutate();

        return d;
    }

    protected static byte[] retrieveImageData(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        // determine the image size and allocate a buffer
        int fileSize = connection.getContentLength();
        if (fileSize < 0) {
            return null;
        }
        byte[] imageData = new byte[fileSize];

        // download the file

        BufferedInputStream istream = new BufferedInputStream(connection.getInputStream());
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

    // TODO: we could probably improve performance by re-using connections
    // instead of closing them
    // after each and every download
    public static void downloadImage(String imageUrl, String filename, Context context) {
        Log.i("Making directory ", "Dir " + getPhotoPath(context));
        try {
            byte[] imageData = retrieveImageData(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
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
        Log.d("Deleting Images: ", path + filename);
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

    public static String getPhotoPath(Context context) {
        // create photo directory if it doesn't exist
        File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName()
                + PHOTO);
        if (!path.exists()) {
            // create path if it doesn't exist
            if (createDirectory(context)) {
                return path.getAbsolutePath()+"/";
            }
        }

        return path.getAbsolutePath()+"/";
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

}
