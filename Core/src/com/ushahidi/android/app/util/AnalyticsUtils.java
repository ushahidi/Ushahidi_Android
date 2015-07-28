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

package com.ushahidi.android.app.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.ushahidi.android.app.R;

/**
 * @author eyedol
 */
public class AnalyticsUtils {

    public static void setContext(Context context) {
        if (!TextUtils.isEmpty(context.getString(R.string.ga_trackingId)))
            EasyTracker.getInstance().setContext(context);
    }

    public static void activityStart(Activity activity) {
        if (!TextUtils.isEmpty(activity.getString(R.string.ga_trackingId)))
            EasyTracker.getInstance().activityStart(activity);

    }

    public static void activityStop(Activity activity) {
        if (!TextUtils.isEmpty(activity.getString(R.string.ga_trackingId)))
            EasyTracker.getInstance().activityStop(activity);
    }
}
