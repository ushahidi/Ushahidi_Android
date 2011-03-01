package com.ushahidi.android.app.checkin;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 2/21/11
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static String IMEI(Context appContext) {
        TelephonyManager TelephonyMgr = (TelephonyManager)appContext.getSystemService(
                appContext.TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
    }
}
