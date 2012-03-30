package com.ushahidi.android.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Class is responsible for receiving connectivity change events and sending any
 * off line incidents
 * 
 * @author wtb
 */
public class OfflineIncidentSendReceiver extends BroadcastReceiver {

	private static final String CLASS_TAG = OfflineIncidentSendReceiver.class
			.getCanonicalName();

	/**
	 * When connectivity returns, send off line messages and notify user
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(CLASS_TAG, "received connection state changed broadcast");

	}

}
