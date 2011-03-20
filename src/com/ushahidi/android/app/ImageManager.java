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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.ushahidi.android.app.net.UshahidiHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import android.graphics.drawable.BitmapDrawable;

public class ImageManager {

    // Images
    public static Drawable getImages(String fileName) {

        Drawable d = null;
        BitmapDrawable bD = new BitmapDrawable(UshahidiPref.savePath + fileName);
        d = bD.mutate();
        /*
         * FileInputStream fIn; if( !TextUtils.isEmpty( fileName) ) { try { fIn
         * = new FileInputStream(UshahidiPref.savePath + fileName); d =
         * Drawable.createFromStream(fIn, "src"); } catch (FileNotFoundException
         * e) { e.printStackTrace(); } }
         */

        return d;
    }

    public static void saveImage() {
        byte[] is;
        for (String image : UshahidiService.mNewIncidentsImages) {
            if (!TextUtils.isEmpty(image)) {
                File imageFilename = new File(image);
                File f = new File(UshahidiPref.savePath + imageFilename.getName());
                if (!f.exists()) {
                    try {
                        is = UshahidiHttpClient.fetchImage(UshahidiPref.domain + "/media/uploads/"
                                + image);
                        if (is != null) {
                            writeImage(is,  imageFilename.getName());
                        }
                    } catch (MalformedURLException e) {

                        e.printStackTrace();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
            }
        }
        
        //clear images
        UshahidiService.mNewIncidentsImages.clear();

    }

    public static void saveThumbnail() {
        byte[] is;
        for (String image : UshahidiService.mNewIncidentsThumbnails) {
           
            if (!TextUtils.isEmpty(image)) {
                File thumbnailFilename = new File(image);
                //Log.i("Save Images", "Image :" + UshahidiPref.savePath + thumbnailFilename.getName());
                File f = new File(UshahidiPref.savePath + thumbnailFilename.getName());
                if (!f.exists()) {
                    try {
                        is = UshahidiHttpClient.fetchImage(UshahidiPref.domain + "/media/uploads/"
                                + image);
                        if (is != null) {
                            writeImage(is, thumbnailFilename.getName());
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

    public static void writeImage(byte[] data, String filename) {

        deleteImage(filename);

        if (data != null) {
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(UshahidiPref.savePath + filename);
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

    public static void deleteImage(String filename) {

        File f = new File(UshahidiPref.savePath + filename);
        if (f.exists()) {
            f.delete();
        }
    }

    public static Bitmap getBitmap(String fileName) {
        Bitmap bitMap = BitmapFactory.decodeFile(UshahidiPref.savePath + fileName);
        return bitMap;
    }

}
