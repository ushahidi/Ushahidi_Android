package com.ushahidi.android.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class is solely responsible for receiving connectivity change events and trigger
 * the the service which will send any offline incidents
 * 
 * @author wtb
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    
    private static final int OFFLINE_MESSAGES_SENT = 1;
   /**
    * When a connectivity change event is received, send off line messages and notify user
    */
    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Create notification for status bar to inform the user offline message were sent 
        int icon = R.drawable.ushahidi_report_icon;
        CharSequence tickerText = "Incidents sent";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
       
        // Notification expanding stuff
        CharSequence contentTitle = "Ushahidi Incidents";
        CharSequence contentText = "Incidents Sent Successfully";
        Intent notificationIntent = new Intent(context, ConnectivityChangeReceiver.class); 
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        
        // Send notification
        mNotificationManager.notify(OFFLINE_MESSAGES_SENT, notification);
        
    }
    
}
