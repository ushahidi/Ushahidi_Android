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

import android.app.Application;
import android.database.Cursor;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.net.MainHttpClient;

public class MainApplication extends Application {

    public static final String TAG = MainApplication.class.getSimpleName();

    public static ImageManager mImageManager;

    public static Database mDb;

    public static MainHttpClient mApi;

    public static Application app = null;

    public static boolean LOGGING_MODE = true;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mImageManager = new ImageManager();
        mDb = new Database(this);
        mDb.open();
        mApi = new MainHttpClient(getApplicationContext());

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
            int imageIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_MEDIA);
            do {
                keepers.add(cursor.getString(imageIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = mDb.fetchAllCategories();

        if (cursor.moveToFirst()) {
            int imageIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_MEDIA);
            do {
                keepers.add(cursor.getString(imageIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();

        // mImageManager.cleanup(keepers);
    }

}
