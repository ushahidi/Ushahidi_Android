package org.addhen.ushahidi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UshahidiReceiver extends BroadcastReceiver {

  public static final String ACTION_REFRESH_EARTHQUAKE_ALARM = "org.addhen.ushahidi.ushahidi.ACTION_UPDATE_USHAHIDI_ALARM"; 
	
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent startIntent = new Intent(context, UshahidiService.class);
    context.startService(startIntent);
	}
}
