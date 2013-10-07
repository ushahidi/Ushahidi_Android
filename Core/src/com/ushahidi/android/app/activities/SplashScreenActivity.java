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

package com.ushahidi.android.app.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.services.FetchReports;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.ui.phone.ListMapActivity;
import com.ushahidi.android.app.ui.phone.ReportTabActivity;
import com.ushahidi.android.app.ui.tablet.DashboardActivity;
import com.ushahidi.android.app.util.AnalyticsUtils;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;

public class SplashScreenActivity extends FragmentActivity {
    private boolean active = true;

    private int splashTime = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getSupportActionBar().hide();
        setContentView(R.layout.splash);
        AnalyticsUtils.setContext(this);
        // thread for displaying the SplashScreen
        checkGoogleServices();
       
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(
                SyncServices.SYNC_SERVICES_ACTION));
    }

    @Override
    public void onStart() {
        super.onStart();
        AnalyticsUtils.activityStop(this);
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        AnalyticsUtils.activityStop(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            active = false;
        }
        return true;
    }

    /**
     * Check if default deployment has been set.
     */
    private boolean checkDefaultDeployment() {
        try {
            // Check if default domain has been set.
            final String deployment = getString(R.string.deployment_url);
            if (!TextUtils.isEmpty(deployment)) {
                Log.i("Dashboard",
                        "Determing if default deployment has been set "
                                + deployment);

                // validate URL
                if (ApiUtils.validateUshahidiInstance(deployment)) {
                    Log.i("Dashboard", "Validate Domain " + deployment);
                    Preferences.domain = deployment;
                    Preferences.saveSettings(this);

                    // refresh for new reports
                    if (Preferences.appRunsFirstTime == 0) {
                        // refreshReports();
                        Preferences.appRunsFirstTime = 1;
                        Preferences.saveSettings(this);
                        startService(new Intent(this, FetchReports.class));
                        return true;
                    }
                }

                goToReports();
                return true;
            }
        } catch (Exception ex) {
            Log.e("Dashboard", "checkDefaultDeployment Exception", ex);
        }
        return false;
    }

    private void goToReports() {
        Intent launchIntent;
        launchIntent = new Intent(this, ReportTabActivity.class);
        startActivityForResult(launchIntent, 0);
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        setResult(RESULT_OK);
        finish();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException e) {

                }

                goToReports();

            }
        }
    };
    
    private void checkGoogleServices(){
    	// See if google play services are installed.
    	boolean services = false;
    	try
    	{
    		ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.gms", 0);
    		services = true;
    	}
    	catch(PackageManager.NameNotFoundException e)
    	{
    		services = false;
    	}

    	if (services)
    	{
    		// Ok, do whatever.
    		 Thread splashTread = new Thread() {
    	            @Override
    	            public void run() {
    	                try {
    	                    int waited = 0;
    	                    while (active && (waited < splashTime)) {
    	                        sleep(100);
    	                        if (active) {
    	                            waited += 100;
    	                        }
    	                    }
    	                } catch (InterruptedException e) {
    	                    // do nothing
    	                } finally {

    	                    // check if default deployment is set
    	                    if (!checkDefaultDeployment()) {
    	                        if (Util.isTablet(SplashScreenActivity.this)) {
    	                            startActivity(new Intent(SplashScreenActivity.this,
    	                                    DashboardActivity.class));
    	                            overridePendingTransition(R.anim.home_enter,
    	                                    R.anim.home_exit);
    	                            finish();
    	                        } else {
    	                            startActivity(new Intent(SplashScreenActivity.this,
    	                                    ListMapActivity.class));
    	                            overridePendingTransition(R.anim.home_enter,
    	                                    R.anim.home_exit);
    	                            finish();
    	                        }

    	                    }
    	                }
    	            }
    	        };
    	        splashTread.start();
    		return;
    	}
    	else
    	{
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreenActivity.this);

    		// set dialog message
    		alertDialogBuilder
    				.setTitle("GeoAvalanche")
    				.setMessage("GeoAvalanche requires Google Play Services to be installed.")
    				.setCancelable(true)
    				.setPositiveButton("Install", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						dialog.dismiss();
    						// Try the new HTTP method (I assume that is the official way now given that google uses it).
    						try
    						{
    							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
    							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    							intent.setPackage("com.android.vending");
    							startActivity(intent);
    						}
    						catch (ActivityNotFoundException e)
    						{
    							// Ok that didn't work, try the market method.
    							try
    							{
    								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
    								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    								intent.setPackage("com.android.vending");
    								startActivity(intent);
    							}
    							catch (ActivityNotFoundException f)
    							{
    								// Ok, weird. Maybe they don't have any market app. Just show the website.

    								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
    								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    								startActivity(intent);
    							}
    						}
    					}
    				})
    				.setNegativeButton("No",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						dialog.cancel();
    						SplashScreenActivity.this.finish();
    						Toast.makeText(getApplicationContext(), "GeoAvalanche cannot run without Google Play Services library installed. Please restart the app and install Google Play Services.", Toast.LENGTH_LONG).show();
    					}
    				})
    				.create()
    				.show();
    	}

    }

}
