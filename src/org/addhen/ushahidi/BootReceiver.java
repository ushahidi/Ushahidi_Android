package org.addhen.ushahidi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This broadcast receiver is awoken after boot and registers the service that
 * checks for new tweets.
 */
/*public class BootReceiver extends BroadcastReceiver {
  private static final String TAG = "BootReceiver";
  
  public void onReceive(Context context, Intent intent) {
	  Log.i(TAG, "Ushahidi BootReceiver is receiving.");
	  if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {      
		  UshahidiService.schedule(context);
	  }
  }
}*/
