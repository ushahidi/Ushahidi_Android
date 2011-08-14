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

package com.ushahidi.android.app;

import java.io.File;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

public class CaptureImage {

    private Bitmap scaled;

    private static final String CLASS_TAG = CaptureImage.class.getCanonicalName();

    public CaptureImage() {
        scaled = null;
    }

    public int getScreenOrientation(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        if (display.getWidth() == display.getHeight()) {
            return Configuration.ORIENTATION_SQUARE;
        } else {
            if (display.getWidth() < display.getHeight()) {
                return Configuration.ORIENTATION_PORTRAIT;
            } else {
                return Configuration.ORIENTATION_LANDSCAPE;
            }
        }
    }

    public Uri getPhotoUri(String filename, Activity activity) {
        File path = new File(Environment.getExternalStorageDirectory(), activity.getPackageName());
        if (!path.exists())
            return null;

        return Uri.fromFile(new File(path, filename));
    }

    public String getPhotoPath(Activity activity) {
        Log.d(CLASS_TAG, "getPhotoPath ");
        File path = new File(Environment.getExternalStorageDirectory(), activity.getPackageName());
        if (!path.exists())
            return null;

        return path.getAbsolutePath();
    }

    public boolean imageExist(String filename, Activity activity) {
        Log.d(CLASS_TAG, "imageExist(): " + filename);
        File path = new File(filename);
        if (!path.exists()) {
            Log.d(CLASS_TAG, "image does not exist");
            return false;
        }

        Log.d(CLASS_TAG, "image does exist");
        return true;
    }

    public Bitmap getBitmap(Uri uri, Activity activity) {
        if (uri != null) {

            // Decode image size to handle proper image consumption
            Log.i(CLASS_TAG, "Decoding bitmap image to handle proper memory consumption ");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            Bitmap original = BitmapFactory.decodeFile(uri.getPath());
            if (original != null) {
                if (getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT
                        && original.getWidth() > original.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(),
                            original.getHeight(), matrix, true);
                    original.recycle();
                    return rotated;
                }
                return original;
            }
        }
        return null;
    }

    public Bitmap scaleBitmap(Bitmap original) {
        Log.i(CLASS_TAG, "scaleBitmap is called ");
        int width = 0;
        if (original != null) {
            float ratio = (float)original.getWidth() / (float)original.getHeight();
            Log.i(CLASS_TAG, "Scalling image to " + UshahidiPref.photoWidth + " x " + ratio);
            if(UshahidiPref.photoWidth == 0 ){
                width = 200;
            } else{
                width = UshahidiPref.photoWidth;
            }
            scaled = Bitmap.createScaledBitmap(original, (int)(width * ratio),
                    width, true);
            original.recycle();

            return scaled;
        }

        return null;
    }

}
