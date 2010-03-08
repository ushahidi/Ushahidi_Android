package org.addhen.ushahidi;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.telephony.PhoneNumberUtils;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiverService extends Service {
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;
	private int mResultCode;

	private static final Object mStartingServiceSync = new Object();
	private static PowerManager.WakeLock mStartingService;

	@Override
	public void onCreate() {
	    
	    HandlerThread thread = new HandlerThread("Message sending starts", Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    getApplicationContext();
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public void onStart(Intent intent, int startId) {
	    
		mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;

	    Message msg = mServiceHandler.obtainMessage();
	    msg.arg1 = startId;
	    msg.obj = intent;
	    mServiceHandler.sendMessage(msg);
	}

	@Override
	public void onDestroy() {
	    
		mServiceLooper.quit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
	    }

		@Override
	    public void handleMessage(Message msg) {
			Log.i("Handle Message called ", "Yay!");
			int serviceId = msg.arg1;
			Intent intent = (Intent) msg.obj;
			String action = intent.getAction();
			String dataType = intent.getType();

			if (ACTION_SMS_RECEIVED.equals(action)) {
				handleSmsReceived(intent);
			} 

			// NOTE: We MUST not call stopSelf() directly, since we need to
			// make sure the wake lock acquired by AlertReceiver is released.
			finishStartingService(SmsReceiverService.this, serviceId);
	    }
	}

	/**
	 * Handle receiving a SMS message
	 */
	private void handleSmsReceived(Intent intent) {
		Log.i("Handle SMS Received called ", "Yep!");
	    String fromAddress = "";
	    String messageBody = "";
	    String fromEmailGateway = "";
	    
		//TODO send the message to ushahidi via the api
	    Bundle bundle = intent.getExtras();
	    if (bundle != null) {
	    	SmsMessage[] messages = getMessagesFromIntent(intent);
	    	SmsMessage sms = messages[0];
	    	if (messages != null) {
	    		//extract message details phone number and the message body
	    		fromAddress = sms.getDisplayOriginatingAddress();
	    		
	    		String body;
	    		if (messages.length == 1 || sms.isReplace()) {
	    			body = sms.getDisplayMessageBody();
	    		} else {
	    			StringBuilder bodyText = new StringBuilder();
	    			for (int i = 0; i < messages.length; i++) {
	    				bodyText.append(messages[i].getMessageBody());
	    			}
	    			body = bodyText.toString();
	    		}
	    		messageBody = body;
	    	}
	    }
	    Log.i("Msg ", "Message: "+messageBody + "No: "+fromAddress);
	}

	private void notifyMessageReceived(SmsMessage message) {
		//TODO decide whether to notify users or not.
	}

	  

	/**
	 * Handle receiving an arbitrary message (potentially coming from a 3rd party app)
	 */
	private void handleMessageReceived(Intent intent) {
	    
		Bundle bundle = intent.getExtras();

	    /*
	     * FROM: ContactURI -or- display name and display address -or- display address
	     * MESSAGE BODY: message body
	     * TIMESTAMP: optional (will use system timestamp)
	     * 
	     * QUICK REPLY INTENT:
	     * REPLY INTENT:
	     * DELETE INTENT:
	     */

	    if (bundle != null) {
	    	//notifySmsReceived(new SmsMmsMessage(context, messages, System.currentTimeMillis()));
	    }
	}

	  /*
	   * Handler to deal with showing Toast messages for message sent status
	   *
	  public Handler mToastHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {

	      if (msg != null) {
	        switch (msg.what) {
	          case TOAST_HANDLER_MESSAGE_SENT:
	            Toast.makeText(SmsReceiverService.this,
	                SmsReceiverService.this.getString(R.string.quickreply_sent_toast),
	                Toast.LENGTH_SHORT).show();
	            break;
	          case TOAST_HANDLER_MESSAGE_SEND_LATER:
	            Toast.makeText(SmsReceiverService.this,
	                SmsReceiverService.this.getString(R.string.quickreply_failed_send_later),
	                Toast.LENGTH_SHORT).show();
	            break;
	          case TOAST_HANDLER_MESSAGE_FAILED:
	            Toast.makeText(SmsReceiverService.this,
	                SmsReceiverService.this.getString(R.string.quickreply_failed),
	                Toast.LENGTH_SHORT).show();
	            break;
	        }
	      }
	    }
	  };*/
	
	public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
	    if (messages == null) {
	    	return null;
	    }
	    if (messages.length == 0) {
	    	return null;
	    }

	    byte[][] pduObjs = new byte[messages.length][];

	    for (int i = 0; i < messages.length; i++) {
	      pduObjs[i] = (byte[]) messages[i];
	    }
	    byte[][] pdus = new byte[pduObjs.length][];
	    int pduCount = pdus.length;
	    SmsMessage[] msgs = new SmsMessage[pduCount];
	    for (int i = 0; i < pduCount; i++) {
	    	pdus[i] = pduObjs[i];
	    	msgs[i] = SmsMessage.createFromPdu(pdus[i]);
	    }
	    return msgs;
	}

	  
	/**
	 * Start the service to process the current event notifications, acquiring the
	 * wake lock before returning to ensure that the service will run.
	 */
	public static void beginStartingService(Context context, Intent intent) {
		synchronized (mStartingServiceSync) {
	      
			if (mStartingService == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	            	"Sms messages .SmsReceiverService");
				mStartingService.setReferenceCounted(false);
			}
	      
			mStartingService.acquire();
			context.startService(intent);
		}
	}

	/**
	 * Called back by the service when it has finished processing notifications,
	 * releasing the wake lock if the service is now stopping.
	 */
	public static void finishStartingService(Service service, int startId) {
		synchronized (mStartingServiceSync) {
	      
			if (mStartingService != null) {
	      		if (service.stopSelfResult(startId)) {
	      			mStartingService.release();
	      		}
	      	}
		}
	}
}
