package org.addhen.ushahidi;
 

import java.io.File;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;
 
import org.addhen.ushahidi.net.Incidents;
import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;
import org.apache.http.impl.client.DefaultHttpClient;
 
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
 
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

 
public class UshahidiService extends Service {
	public static final String PREFS_NAME = "UshahidiService";
	public static boolean httpRunning = false;
	public static final DefaultHttpClient httpclient = new DefaultHttpClient();
	public static Vector<String> mNewIncidentsImages = new Vector<String>();
	public static String incidentsResponse = "";
	public static String categoriesResponse = "";
	public static String savePath = "";
	public static String domain = "";
	public static String firstname = "";
	public static String lastname = "";
	public static String email = "";
	public static int countries = 0;
	public static int AutoUpdateDelay = 0;
	public static String totalReports = "";
	public static String fileName = "";
	public static boolean AutoFetch = false;
	public static String total_reports = "";
	public static boolean smsUpdate = false;
	public static boolean vibrate = false;
	public static boolean ringtone = false;
	public static boolean flashLed = false;
	public static String username = "";
	public static String password = "";
	private Handler mHandler = new Handler();
	
	private static final String TAG = "Ushahidi - New Updates";
	 
    private ArrayList<IncidentsData> mNewIncidents;
    private ArrayList<CategoriesData> mNewCategories;
    
    public static final String NEW_USHAHIDI_REPORT_FOUND = "New_Ushahidi_Report_Found";
    public static final int NOTIFICATION_ID = 1;
    
    private Notification newUshahidiReportNotification;
    private NotificationManager mNotificationManager;
    private static QueueThread queue;
    private String title = "";
    private String text = "";
    
    private UshahidiDatabase getDb() {
        return UshahidiApplication.mDb;
    }
    
    /**
     * Local services Binder.
     * @author eyedol
     *
     */
    public class LocalBinder extends Binder {
        UshahidiService getService() {
            return UshahidiService.this;
        }
    }

    
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			
			UshahidiService.saveSettings(getApplicationContext());
			
			Util.fetchReports(UshahidiService.this);
				
			showNotification(total_reports);
			mHandler.postAtTime(mUpdateTimeTask, SystemClock.uptimeMillis() + (1000 * 60 * AutoUpdateDelay));	
				
		}
	};
	
	
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private final IBinder mBinder = new LocalBinder();
	
	@Override 
	public void onCreate() {
		super.onCreate();
		queue = new QueueThread("ushahidi");
		mHandler = new Handler();
		
		//if(AutoFetch){
			Log.i("Auto Update", "yes autoupdate "+ AutoFetch );
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
			mHandler.postDelayed(mUpdateTimeTask, (1000 * 60 * AutoUpdateDelay));
		
		//}
		
		final Thread tr = new Thread() {
			@Override
			public void run() {
				while(true){
					queue.GetQueueItem().start();
				}
			}
		};
		tr.start();
	}
	
	 
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mNotificationManager.cancel(NOTIFICATION_ID);
		
		// Tell the user we stopped.
		stopService(new Intent(UshahidiService.this, UshahidiService.class));
		
	}
	
	public static void AddThreadToQueue(Thread tr){
		queue.AddQueueItem(tr);
	}
	
	private void showNotification(String tickerText) {
        // This is who should be launched if the user selects our notification.
        Intent baseIntent = new Intent(this, ListIncidents.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, baseIntent, 0);

        // choose the ticker text
        newUshahidiReportNotification = new Notification(R.drawable.favicon, tickerText, System.currentTimeMillis());
        newUshahidiReportNotification.contentIntent = contentIntent;
        newUshahidiReportNotification.flags = Notification.FLAG_AUTO_CANCEL;
        newUshahidiReportNotification.defaults = Notification.DEFAULT_ALL;
        newUshahidiReportNotification.setLatestEventInfo(this, TAG, tickerText, contentIntent);
        if( ringtone ){ 
        	//set the ringer
        	Uri ringURI = Uri.fromFile(new File("/system/media/audio/ringtones/ringer.mp3"));
        	newUshahidiReportNotification.sound = ringURI; 
        }
        
        if( vibrate ){
        	double vibrateLength = 100*Math.exp(0.53*20);
        	long[] vibrate = new long[] {100, 100, (long)vibrateLength };
        	newUshahidiReportNotification.vibrate = vibrate;
        	
        	if( flashLed ){
        		int color = Color.BLUE;    
        		newUshahidiReportNotification.ledARGB = color;
        	}
        	
        	newUshahidiReportNotification.ledOffMS = (int)vibrateLength;
        	newUshahidiReportNotification.ledOnMS = (int)vibrateLength;
        	newUshahidiReportNotification.flags = newUshahidiReportNotification.flags |  Notification.FLAG_SHOW_LIGHTS;
        }
        
        mNotificationManager.notify(NOTIFICATION_ID, newUshahidiReportNotification);
	}
	
	private void processNewIncidents() {
	    if (mNewIncidents.size() <= 0) {
	    	return;
	    }
 
	    Log.i(TAG, mNewIncidents.size() + " new incidents.");
 
	    int count = getDb().addNewIncidentsAndCountUnread(mNewIncidents);
 	   
	    if (count <= 0) {
	      return;
	    }
 
	    IncidentsData latestIncident = mNewIncidents.get(0);
 
	    if (count == 1) {
	      title = latestIncident.getIncidentTitle();
	      text = latestIncident.getIncidentDate();
	    } else {
	      title = getString(R.string.new_incidents);
	      text = getString(R.string.new_categories);
	      text = MessageFormat.format(text, count);
	    }
	}
	
	/**
	 * Clear stored data
	 */
	public boolean clearCache() {
		
		return getDb().clearData();
	
	}
 
	private void processNewCategories() {
	    if (mNewCategories.size() <= 0) {
	      return;
	    }
 
	    Log.i(TAG, mNewCategories.size() + " new categories.");
 
	    int count = 0;
 
	    UshahidiDatabase db = getDb();
 
	    if (db.fetchCategoriesCount() > 0) {
	      count = db.addNewCategoryAndCountUnread(mNewCategories);
	    } else {
	      Log.i(TAG, "No existing categories. Don't notify.");
	      db.addCategories(mNewCategories, false);
	    }
 
	    if (count <= 0) {
	      return;
	    }
 
	    CategoriesData latest = mNewCategories.get(0);
 
	    if (count == 1) {
	      title = latest.getCategoryTitle();
	      text = latest.getCategoryDescription();
	    } else {
	      title = getString(R.string.new_categories);
	      text = getString(R.string.new_categories);
	      text = MessageFormat.format(text, count);
	    }
 
	}
	
	public static void loadSettings(Context context) {
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);
		savePath = settings.getString("savePath",
				"/data/data/org.addhen.ushahidi/files/");
		domain = settings.getString("Domain", "");
		firstname = settings.getString("Firstname", "");
		lastname = settings.getString("Lastname", "");
		email = settings.getString("Email", "");
		countries = settings.getInt("Countries", 0);
		AutoUpdateDelay = settings.getInt("AutoUpdateDelay", 5);
		AutoFetch = settings.getBoolean("AutoFetch", false);
		totalReports = settings.getString("TotalReports", "");
		smsUpdate = settings.getBoolean("SmsUpdate",false);
		username = settings.getString("Username", "");
		password = settings.getString("Password","");
		// make sure folder exists
		final File dir = new File(UshahidiService.savePath);
		dir.mkdirs();
 
	}
	
	public static void saveSettings(Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("Domain", domain);
		editor.putString("Firstname", firstname);
		editor.putString("Lastname", lastname);
		
		if( Util.validateEmail(settings.getString("Email", ""))) {
			editor.putString("Email", email);
		}
		
		editor.putString("savePath", savePath);
		editor.putInt("AutoUpdateDelay", AutoUpdateDelay);
		editor.putBoolean("AutoFetch", AutoFetch);
		editor.putString("TotalReports", totalReports);
		editor.putBoolean("SmsUpdate", smsUpdate);
		editor.putString("Username", username);
		editor.putString("Password", password);
		editor.commit();
	}
	
	public class QueueThread {
	    protected Vector<Thread>    queue;
	    protected int       itemcount;
	    protected String    queueName;
	    public QueueThread(String name) {
	        queue = new Vector<Thread>();
	        queueName = name;
	        itemcount = 0;
	    }
	    // Get an item from the vector.  Wait if no items available
	    public synchronized Thread GetQueueItem() {
	        Thread   item = null;
	        // If no items available, drop into wait() call
	        if (itemcount == 0) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	            	//Somebody woke me up!
	            }
	        }
	        // Get first item from vector, remove it and decrement item count.
	        item = (Thread)queue.firstElement();
	        queue.removeElement(item);
	        itemcount--;
	        // Send it back
	        return item;
	    }
	    // Place an item onto vector. Signal threads that an item is available.
	    public synchronized void AddQueueItem(Thread o) {
	        itemcount++;
	        queue.addElement(o);
	        notify();
	    }
	    // Handy place to put a separate notify call - used during shutdown.
	    public synchronized void BumpQueue() {
	        notify();
	    }
	}
 
}
