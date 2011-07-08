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
import android.view.Display;

public class CaptureImage  {

    public Bitmap scaled;

  
    public CaptureImage() {
        scaled = null;
    }

    public int getScreenOrientation( Activity context ) {
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
        if (!path.exists() && path.mkdir()) {
            // /directory created
        }
        return Uri.fromFile(new File(path, filename));
    }
    

    public Bitmap getBitmap(Uri uri, Activity activity) {
        if (uri != null) {
           
            Bitmap original = BitmapFactory.decodeFile(uri.getPath());
            
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
        return null;
    }

}
