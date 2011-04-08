
package com.ushahidi.android.app;

import java.io.IOException;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.util.Log;

import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.net.UshahidiHttpClient;

/**
 * Class is responsible for receiving connectivity change events and sending any
 * off line incidents
 * 
 * @author wtb
 */
public class OfflineIncidentSendReceiver extends BroadcastReceiver {

    private static final String CLASS_TAG = OfflineIncidentSendReceiver.class.getCanonicalName();

    private NotificationManager mNotificationManager;

    private static final int OFFLINE_MESSAGES_SENT = 1;

    /**
     * When connectivity returns, send off line messages and notify user
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(CLASS_TAG, "received connection state changed broadcast");
        
        // Check if connectivity is being switch back on (could be a FAILOVER_CONNECTION event as well
        // but I am going to ignore as no harm will come attempting to check) 
        if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            Log.d(CLASS_TAG, "High possibility connection is on, so try sending any off line incidents");
            if (postToOnlineAllOfflineIncidents(context)) {
                mNotificationManager = (NotificationManager)context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                // creates notification to be displayed on the status bar
                Notification notification = createNotfication(context);

                // Notification expanding stuff
                CharSequence contentTitle = context.getString(R.string.notification_expanded_title);
                CharSequence contentText =  context.getString(R.string.notification_expanded_message);
                Intent notificationIntent = new Intent(context, OfflineIncidentSendReceiver.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, 0);

                notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

                // Send notification
                mNotificationManager.notify(OFFLINE_MESSAGES_SENT, notification);
            }
        }
    }

    /**
     * Try post all incidents stored in off line storage online
     */
    private boolean postToOnlineAllOfflineIncidents(Context context) {

        // Need to create own db handle instance as it can't be guaranteed the
        // app handle
        // will be about
        UshahidiDatabase db = new UshahidiDatabase(context);
        db.open();

        boolean someOfflineIncidentsSent = false;

        final SharedPreferences settings = context.getSharedPreferences(AddIncident.PREFS_NAME, 0);
        StringBuilder urlBuilder = new StringBuilder(settings.getString("Domain", ""));
        urlBuilder.append("/api");

        Cursor cursor = db.fetchAllOfflineIncidents();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Log.d(CLASS_TAG,
                    "Sending message with title : "
                            + cursor.getString(UshahidiDatabase.ADD_INCIDENT_TITLE_INDEX));
            try {
                boolean postSuccess = UshahidiHttpClient.PostFileUpload(urlBuilder.toString(),
                        preparePostParams(cursor));
                if (postSuccess) {
                    Log.d(CLASS_TAG, "Incident submitted successfully");
                    db.deleteAddIncident(cursor.getInt(UshahidiDatabase.ADD_INCIDENT_ID_INDEX));
                    someOfflineIncidentsSent = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
        }

        cursor.close();

        db.close();

        return someOfflineIncidentsSent;
    }

    /**
     * Create notification for status bar to inform the user at least one off
     * line message were sent
     * 
     * @return Notification
     */
    private Notification createNotfication(Context context) {
        int icon = R.drawable.ushahidi_report_icon;
        CharSequence tickerText = context.getString(R.string.notification_status_bar_message);
        long when = System.currentTimeMillis();
        return new Notification(icon, tickerText, when);
    }

    /**
     * Prepares a hash map of parameters representing an off line incident
     * report so it can be posted to the Ushahidi server
     * 
     * @param cursor - data to be added to the map
     * @return HashMap<String, String>
     */
    private HashMap<String, String> preparePostParams(Cursor cursor) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(UshahidiHttpClient.TASK, "report");
        params.put(UshahidiHttpClient.INCIDENT_TITLE, cursor.getString(UshahidiDatabase.ADD_INCIDENT_TITLE_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_DESCRIPTION,
                cursor.getString(UshahidiDatabase.ADD_INCIDENT_DESC_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_DATE, cursor.getString(UshahidiDatabase.ADD_INCIDENT_DATE_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_HOUR, cursor.getString(UshahidiDatabase.ADD_INCIDENT_HOUR_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_MINUTE, cursor.getString(UshahidiDatabase.ADD_INCIDENT_MINUTE_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_AMPM, cursor.getString(UshahidiDatabase.ADD_INCIDENT_AMPM_INDEX));
        params.put(UshahidiHttpClient.INCIDENT_CATEGORY,
                cursor.getString(UshahidiDatabase.ADD_INCIDENT_CATEGORIES_INDEX));
        params.put(UshahidiHttpClient.LATITUDE, cursor.getString(UshahidiDatabase.INCIDENT_LOC_LATITUDE_INDEX));
        params.put(UshahidiHttpClient.LONGITUDE, cursor.getString(UshahidiDatabase.INCIDENT_LOC_LONGITUDE_INDEX));
        params.put(UshahidiHttpClient.LOCATION_NAME, cursor.getString(UshahidiDatabase.INCIDENT_LOC_NAME_INDEX));
        params.put(UshahidiHttpClient.PERSON_FIRST, cursor.getString(UshahidiDatabase.ADD_PERSON_FIRST_INDEX));
        params.put(UshahidiHttpClient.PERSON_LAST, cursor.getString(UshahidiDatabase.ADD_PERSON_LAST_INDEX));
        params.put(UshahidiHttpClient.PERSON_EMAIL, cursor.getString(UshahidiDatabase.ADD_PERSON_EMAIL_INDEX));
        params.put("filename", cursor.getString(UshahidiDatabase.ADD_INCIDENT_PHOTO_INDEX));
        return params;
    }
}
