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

import com.ushahidi.android.app.util.Util;

import android.util.Log;

public class SaveIncidentsImage extends Thread {

    private byte[] mData;

    public String filename;

    public SaveIncidentsImage(byte[] data) {
        this.mData = data;
    }

    @Override
    public void run() {

        filename = "pictureupload" + Util.randomString() + ".jpg";

        Log.i("Capture Me", "What: " + filename);

        ImageManager.writeImage(mData, filename,UshahidiPref.savePath);
        UshahidiPref.fileName = filename;

        File f = new File(UshahidiPref.savePath, filename);
        if (f.exists()) {
            f.delete();
        }
    }
}
