package com.ushahidi.android.app.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.util.Util;

/**
 * This Receiver class is designed to listen for changes in connectivity. When
 * we lose connectivity the relevant Service classes will automatically push
 * pending messages and check for tasks. This class will restart the service
 * that pushes pending messages to the configured URL and check for tasks from
 * the configured URL.
 */

public class ConnectivityChangedReceiver extends BroadcastReceiver {

	private boolean isConnected;

	private PackageManager pm;

	private ComponentName connectivityReceiver;

	@Override
    public void onReceive(Context context, Intent intent) {

        // load current settings
        Preferences.loadSettings(context);

        // check to see if we're connected to an active data network
        isConnected = Util.isConnected(context);

        if (isConnected) {
        	pm = context.getPackageManager();
        	connectivityReceiver = new ComponentName(context, ConnectivityChangedReceiver.class);
        	// The default state for this Receiver is disabled. it is only
        	// enabled when a Service disables updates pending connectivity.
        	pm.setComponentEnabledSetting(connectivityReceiver,
        			PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
        			PackageManager.DONT_KILL_APP);

        }
    }
}
