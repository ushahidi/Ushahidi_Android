package org.addhen.ushahidi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver{

	@Override
	 public void onReceive(Context context, Intent intent) {
		
		intent.setClass(context, SmsReceiverService.class);
		intent.putExtra("result", getResultCode());

		SmsReceiverService.beginStartingService(context, intent);
	}

}
