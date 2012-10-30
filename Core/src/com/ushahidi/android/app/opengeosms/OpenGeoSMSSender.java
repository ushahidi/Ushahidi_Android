package com.ushahidi.android.app.opengeosms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.util.Util;

/**
 * @inoran Open GeoSMS
 */
public class OpenGeoSMSSender {

	private Context mContext;

	public OpenGeoSMSSender(Context c) {
		mContext = c;
	}

	public static String createReport(String url, ListReportModel report) {
		String date = Util.formatDate("MMMM dd, yyyy 'at' hh:mm:ss aaa",
				report.getDate(), "MM/dd/yyyy hh:mm a", null, Locale.US)
				.toLowerCase();

		String payload = String.format("%s#%s@%s\n%s\n%s", report.getTitle(),
				report.getCategories(), date, report.getLocation(),
				report.getDesc());

		return composeOpenGeoSMS(url, report.getLatitude(),
				report.getLongitude(), payload);
	}

	private class Receiver extends BroadcastReceiver {
		private Semaphore mSem = new Semaphore(0);
		private boolean mRecvRetVal = false;
		private List<PendingIntent> mPendingIntents;

		public boolean waitForResult(List<PendingIntent> pIntents) {
			mPendingIntents = pIntents;
			mSem.acquireUninterruptibly();
			return mRecvRetVal;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (getResultCode() != Activity.RESULT_OK) {
				mRecvRetVal = false;
				context.unregisterReceiver(this);
				mSem.release();
				return;
			}
			mPendingIntents.remove(mPendingIntents.size() - 1);
			if (mPendingIntents.isEmpty()) {
				mRecvRetVal = true;
				context.unregisterReceiver(this);
				mSem.release();
			}
		}

	}

	public boolean sendReport(String address, String url, ListReportModel report) {

		String smsMsg = createReport(url, report);
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> msgs = smsManager.divideMessage(smsMsg);
		ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
		String action = OpenGeoSMSSender.class.getCanonicalName()
				+ UUID.randomUUID().toString();
		Intent intent = new Intent(action);
		for (int i = 0; i < msgs.size(); i++) {
			pendingIntents.add(PendingIntent.getBroadcast(mContext, 0, intent,
					0));
		}
		Receiver r = new Receiver();
		mContext.registerReceiver(r, new IntentFilter(action));
		smsManager.sendMultipartTextMessage(address, null, msgs,
				pendingIntents, null);
		return r.waitForResult(pendingIntents);

	}

	/*
	 * Open GeoSMS template
	 * Schema://domain/?FirstParameter&RestParameters&GeoSMS=OP Payload
	 */
	private static String composeOpenGeoSMS(String url, String lat, String lng,
			String payload) {
		String firstParam = "q=";
		final String postfix = "GeoSMS";
		return url + "?" + firstParam + lat + "," + lng + "&" + postfix + "\n"
				+ payload;

	}

}
