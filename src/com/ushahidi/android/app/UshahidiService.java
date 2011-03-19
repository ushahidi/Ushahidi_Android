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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import com.ushahidi.android.app.data.UshahidiDatabase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class UshahidiService extends Service {

    private TimerTask mDoTask;

    private Timer mT = new Timer();

    private Handler handler = new Handler();

    private Context mContext;

    private static final String TAG = "Ushahidi - New Updates";

    public static boolean httpRunning = false;

    public static boolean AutoFetch = false;

    public static boolean vibrate = false;

    public static boolean ringtone = false;

    public static boolean flashLed = false;

    public static int countries = 0;

    public static int AutoUpdateDelay = 0;

    public static final int NOTIFICATION_ID = 1;

    public static final String PREFS_NAME = "UshahidiService";

    public static String incidentsResponse = "";

    public static String categoriesResponse = "";

    public static String savePath = "";

    public static String domain = "";

    public static String firstname = "";

    public static String lastname = "";

    public static String email = "";

    public static String totalReports = "";

    public static String fileName = "";

    public static String total_reports = "";

    public static final String NEW_USHAHIDI_REPORT_FOUND = "New_Ushahidi_Report_Found";

    public static Vector<String> mNewIncidentsImages = new Vector<String>();

    public static Vector<String> mNewIncidentsThumbnails = new Vector<String>();

    public static final DefaultHttpClient httpclient = new DefaultHttpClient();

    private Notification newUshahidiReportNotification;

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {

        mContext = getApplicationContext();

        UshahidiPref.loadSettings(mContext);

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        this.startService();

    }

    private UshahidiDatabase getDb() {
        return UshahidiApplication.mDb;
    }

    /**
     * Starts the background service
     * 
     * @return void
     */
    private void startService() {

        UshahidiPref.loadSettings(mContext);
        long period = (1 * 60000);
        long delay = 500;

        mDoTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {

                    public void run() {

                        // Perform a task
                        Util.fetchReports(UshahidiService.this);
                        showNotification(UshahidiPref.total_reports);
                    }

                });
            }

        };

        // Schedule the task.
        mT.scheduleAtFixedRate(mDoTask, delay, period);
    }

    /**
     * Stop background service.
     * 
     * @return void
     */
    private void stopService() {
        if (mDoTask != null) {
            mDoTask.cancel();
            mT.cancel();
            mT.purge();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopService();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification(String tickerText) {

        // This is what should be launched if the user selects our notification.
        Intent baseIntent = new Intent(this, IncidentsTab.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, baseIntent, 0);

        // choose the ticker text
        newUshahidiReportNotification = new Notification(R.drawable.favicon, tickerText,
                System.currentTimeMillis());
        newUshahidiReportNotification.contentIntent = contentIntent;
        newUshahidiReportNotification.flags = Notification.FLAG_AUTO_CANCEL;
        newUshahidiReportNotification.defaults = Notification.DEFAULT_ALL;
        newUshahidiReportNotification.setLatestEventInfo(this, TAG, tickerText, contentIntent);

        if (UshahidiPref.ringtone) {
            // set the ringer
            Uri ringURI = Uri.fromFile(new File("/system/media/audio/ringtones/ringer.mp3"));
            newUshahidiReportNotification.sound = ringURI;
        }

        if (UshahidiPref.vibrate) {
            double vibrateLength = 100 * Math.exp(0.53 * 20);
            long[] vibrate = new long[] {
                    100, 100, (long)vibrateLength
            };
            newUshahidiReportNotification.vibrate = vibrate;

            if (UshahidiPref.flashLed) {
                int color = Color.BLUE;
                newUshahidiReportNotification.ledARGB = color;
            }

            newUshahidiReportNotification.ledOffMS = (int)vibrateLength;
            newUshahidiReportNotification.ledOnMS = (int)vibrateLength;
            newUshahidiReportNotification.flags = newUshahidiReportNotification.flags
                    | Notification.FLAG_SHOW_LIGHTS;
        }

        mNotificationManager.notify(UshahidiPref.NOTIFICATION_ID, newUshahidiReportNotification);
    }

}
