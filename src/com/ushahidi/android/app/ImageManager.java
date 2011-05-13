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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.ushahidi.android.app.net.UshahidiHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import android.graphics.drawable.BitmapDrawable;

public class ImageManager {

    // Images
    public static Drawable getImages(String fileName) {

        Drawable d = null;
        BitmapDrawable bD = new BitmapDrawable(UshahidiPref.savePath + fileName);
        d = bD.mutate();

        return d;
    }

    public static void saveImage(String path) {
        byte[] is;
        for (String image : UshahidiService.mNewIncidentsImages) {
            if (!TextUtils.isEmpty(image)) {
                File imageFilename = new File(image);
                File f = new File(path + imageFilename.getName());
                if (!f.exists()) {
                    try {
                        is = UshahidiHttpClient.fetchImage(UshahidiPref.domain + "/media/uploads/"
                                + image);
                        if (is != null) {
                            writeImage(is, imageFilename.getName(),path);
                        }
                    } catch (MalformedURLException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            }
        }

        // clear images
        UshahidiService.mNewIncidentsImages.clear();

    }

    public static void saveThumbnail(String path) {
        byte[] is;
        for (String image : UshahidiService.mNewIncidentsThumbnails) {

            if (!TextUtils.isEmpty(image)) {
                File thumbnailFilename = new File(image);
                // Log.i("Save Images", "Image :" + UshahidiPref.savePath +
                // thumbnailFilename.getName());
                File f = new File(path + thumbnailFilename.getName());
                if (!f.exists()) {
                    try {
                        is = UshahidiHttpClient.fetchImage(UshahidiPref.domain + "/media/uploads/"
                                + image);
                        if (is != null) {
                            writeImage(is, thumbnailFilename.getName(),path);
                        }
                    } catch (MalformedURLException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            }
        }

        // clear images
        UshahidiService.mNewIncidentsThumbnails.clear();

    }

    public static void writeImage(byte[] data, String filename, String path) {

        deleteImage(filename,path);
        Log.d("Deleting Images: ","FilePaht "+filename+path);
        if (data != null) {
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(path + filename);
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

    public static void deleteImage(String filename, String path) {

        File f = new File(path + filename);
        if (f.exists()) {
            f.delete();
        }
    }

    public static Bitmap getBitmap(String fileName,String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(UshahidiPref.savePath + fileName), null,
                    o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 400;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(
                    new FileInputStream(path + fileName), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static void saveImageFromURL(String url, String fileName, String path) {
        byte[] is;
        
        if (!TextUtils.isEmpty(url)) {
            File imageFilename = new File(fileName);
            File f = new File(path + imageFilename.getName());
            if (!f.exists()) {
                try {
                    is = UshahidiHttpClient.fetchImage(url);
                    if (is != null) {
                        writeImage(is, imageFilename.getName(),path);
                    }
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        }
    }

}
