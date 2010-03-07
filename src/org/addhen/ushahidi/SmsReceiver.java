package org.addhen.ushahidi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver{

	@Override
	 public void onReceive(Context context, Intent intent) {
		Log.i("Sms hits receiver", "Sms came in");
		intent.setClass(context, SmsReceiverService.class);
		intent.putExtra("result", getResultCode());

		SmsReceiverService.beginStartingService(context, intent);
	}

}
