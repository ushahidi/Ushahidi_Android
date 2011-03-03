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

import java.util.HashSet;

import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.net.UshahidiHttpClient;

import android.app.Application;
import android.database.Cursor;

public class UshahidiApplication extends Application {

    public static final String TAG = "UshahidiApplication";

    public static ImageManager mImageManager;

    public static UshahidiDatabase mDb;

    public static UshahidiHttpClient mApi;

    @Override
    public void onCreate() {
        super.onCreate();

        mImageManager = new ImageManager();
        mDb = new UshahidiDatabase(this);
        mDb.open();
        mApi = new UshahidiHttpClient();

    }

    @Override
    public void onTerminate() {
        cleanupImages();
        mDb.close();

        super.onTerminate();
    }

    private void cleanupImages() {
        HashSet<String> keepers = new HashSet<String>();

        Cursor cursor = mDb.fetchAllIncidents();

        if (cursor.moveToFirst()) {
            int imageIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_MEDIA);
            do {
                keepers.add(cursor.getString(imageIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = mDb.fetchAllCategories();

        if (cursor.moveToFirst()) {
            int imageIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_MEDIA);
            do {
                keepers.add(cursor.getString(imageIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();

        // mImageManager.cleanup(keepers);
    }
}
