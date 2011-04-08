
package com.ushahidi.android.app.checkin;

import com.ushahidi.android.app.UshahidiApplication;
import com.ushahidi.android.app.data.UshahidiDatabase;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/21/11 Time: 7:08 PM To change
 * this template use File | Settings | File Templates.
 */
public class CheckinUtil {
    public static String IMEI(Context appContext) {
        TelephonyManager TelephonyMgr = (TelephonyManager)appContext
                .getSystemService(appContext.TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
    }
    
    public static String getCheckinUser(String userId) {
        Cursor cursor = UshahidiApplication.mDb.fetchUsersById(userId);
        if (cursor.moveToFirst()) {
            int userName = cursor.getColumnIndexOrThrow(UshahidiDatabase.USER_NAME);
            return cursor.getString(userName);
        }
        cursor.close();
        return null;
    }
    
    public static String getCheckinMedia(String checkinId) {
        Cursor cursor = UshahidiApplication.mDb.fetchCheckinsMediaByCheckinId(checkinId);
        if (cursor.moveToFirst()) {
            int mediaMediumLink = cursor.getColumnIndexOrThrow(UshahidiDatabase.MEDIA_MEDIUM_LINK);
            return cursor.getString(mediaMediumLink);
        }
        cursor.close();
        return null;
    }
}
