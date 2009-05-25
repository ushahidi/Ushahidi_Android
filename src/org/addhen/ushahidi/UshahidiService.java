package org.addhen.ushahidi;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.addhen.ushahidi.net.Incidents;
import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.data.UshahidiDatabase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.photostream.UserTask;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;

public class UshahidiService extends Service {
	public static final String PREFS_NAME = "UshahidiService";
	public static boolean httpRunning = false;
	public static final DefaultHttpClient httpclient = new DefaultHttpClient();
	public static Vector<String> mNewIncidentsImages = new Vector<String>();
	public static String incidentsResponse = "";
	public static String categoriesResponse = "";
	public static String savePath = "";
	public static String domain = "";
	public static int countries = 0;
	public static int AutoUpdateDelay = 0;
	
	public static boolean AutoFetch = false;
	
	private Handler mHandler = new Handler();
	private NotificationManager mNotificationManager;
    private static final int YOURAPP_NOTIFICATION_ID = 500; 
    private static QueueThread queue;
    
    private static final String TAG = "UshahidiService";

    private SharedPreferences mPreferences;


    private ArrayList<IncidentsData> mNewIncidents;
    private ArrayList<CategoriesData> mNewCategories;
    
    private static int INCIDENTS_NOTIFICATION_ID = 0;
    private static int CATEGORIES_NOTIFICATION_ID = 1;
    
    
    private UshahidiDatabase getDb() {
        return UshahidiApplication.mDb;
    }

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if(AutoUpdateDelay <= 0){
				return;
			}
			// TODO get new incidents from the web
			   
			try {
				if(Incidents.getAllIncidentsFromWeb()){
					UshahidiService.saveSettings(getApplicationContext());
					processNewCategories();
					processNewIncidents();
					showNotification("New Updates!");
				}
			} catch (IOException e) {
					//means there was a problem getting it
			} finally {
				mHandler.postAtTime(mUpdateTimeTask, SystemClock.uptimeMillis() + (1000 * 60 * AutoUpdateDelay));
			}
		}
	};
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();
	    
	    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    
		queue = new QueueThread("ushahidi");

		// init the service here
		mHandler = new Handler();
		if(AutoUpdateDelay > 0){
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
			mHandler.postDelayed(mUpdateTimeTask, (1000 * 60 * AutoUpdateDelay));
		}
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
	
	private void processNewIncidents() {
	    if (mNewIncidents.size() <= 0) {
	    	return;
	    }

	    Log.i(TAG, mNewIncidents.size() + " new incidents.");

	    int count = getDb().addNewIncidentsAndCountUnread(mNewIncidents);

	    /*for (IncidentsData incident : mNewIncidents) {
	      if (!incident.getIncidentMedia().equals("") ) {
	        // Fetch image to cache.
	        try {
	        	//UshahidiApplication.mImageManager.put(incident.getIncidentMedia());
	        } catch (IOException e) {
	        	Log.e(TAG, e.getMessage(), e);
	        }
	      }
	    }*/

	    if (count <= 0) {
	      return;
	    }

	    IncidentsData latestIncident = mNewIncidents.get(0);

	    String title;
	    String text;

	    if (count == 1) {
	      title = latestIncident.getIncidentTitle();
	      text = latestIncident.getIncidentDate();
	    } else {
	      title = getString(R.string.new_incidents);
	      text = getString(R.string.new_categories);
	      text = MessageFormat.format(text, count);
	    }

	   // PendingIntent intent = PendingIntent.getActivity(this, 0, ListIncidents.createIntent(this), 0);

	    //notify(intent, INCIDENTS_NOTIFICATION_ID, R.drawable.favicon,
	    		//latestIncident.getIncidentDate(), title, text);
	}
	
	/*private void notify(PendingIntent intent, int notificationId,
		      int notifyIconId, String tickerText, String title, String text) {
		    Notification notification = new Notification(notifyIconId, tickerText,
		        System.currentTimeMillis());

		    notification.setLatestEventInfo(this, title, text, intent);

		    notification.flags = Notification.FLAG_AUTO_CANCEL
		        | Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS;

		    notification.ledARGB = 0xFF84E4FA;
		    notification.ledOnMS = 5000;
		    notification.ledOffMS = 5000;

		    String ringtoneUri = mPreferences.getString(RINGTONE_KEY, null);

		    if (ringtoneUri == null) {
		      notification.defaults |= Notification.DEFAULT_SOUND;
		    } else {
		      notification.sound = Uri.parse(ringtoneUri);
		    }

		    if (mPreferences.getBoolean(VIBRATE_KEY, false)) {
		      notification.defaults |= Notification.DEFAULT_VIBRATE;
		    }

		    mNotificationManager.notify(notificationId, notification);
	}*/
	
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

	    String title;
	    String text;

	    if (count == 1) {
	      title = latest.getCategoryTitle();
	      text = latest.getCategoryDescription();
	    } else {
	      title = getString(R.string.new_categories);
	      text = getString(R.string.new_categories);
	      text = MessageFormat.format(text, count);
	    }

	    //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ListIncidents.createIntent(this), 0);

	    //notify(pendingIntent, CATEGORIES_NOTIFICATION_ID, R.drawable.favicon,
	       // latest.getCategoryTitle(), title, text);
	  }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public static void AddThreadToQueue(Thread tr){
		queue.AddQueueItem(tr);
	}
	/*static void schedule(Context context) {
	    SharedPreferences preferences = PreferenceManager
	        .getDefaultSharedPreferences(context);

	    if (!preferences.getBoolean(CHECK_UPDATES_KEY, false)) {
	      Log.i(TAG, "Check update preference is false.");
	      return;
	    }

	    String intervalPref = preferences.getString(
	        CHECK_UPDATE_INTERVAL_KEY, context
	            .getString(UshahidiService.AutoUpdateDelay));
	    int interval = Integer.parseInt(intervalPref);

	    Intent intent = new Intent(context, UshahidiService.class);
	    PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
	    Calendar c = new GregorianCalendar();
	    c.add(Calendar.MINUTE, interval);

	    DateFormat df = new SimpleDateFormat("h:mm a");
	    Log.i(TAG, "Scheduling alarm at " + df.format(c.getTime()));

	    AlarmManager alarm = (AlarmManager) context
	        .getSystemService(Context.ALARM_SERVICE);
	    alarm.cancel(pending);
	    alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pending);
	  }

	  static void unschedule(Context context) {
	    Intent intent = new Intent(context, UshahidiService.class);
	    PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
	    AlarmManager alarm = (AlarmManager) context
	        .getSystemService(Context.ALARM_SERVICE);
	    Log.i(TAG, "Cancelling alarms.");
	    alarm.cancel(pending);
	  }*/
	
	private void showNotification(String tickerText) {
        // This is who should be launched if the user selects our notification.
        Intent baseIntent = new Intent(this, ListIncidents.class);
        baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, baseIntent, 0);

        // choose the ticker text
        Notification not = new Notification(R.drawable.icon, tickerText, System.currentTimeMillis());
        not.contentIntent = contentIntent;
        not.flags = Notification.FLAG_AUTO_CANCEL;
        not.defaults = Notification.DEFAULT_ALL;
        not.setLatestEventInfo(this, PREFS_NAME, tickerText, contentIntent);

        mNotificationManager.notify(
                   YOURAPP_NOTIFICATION_ID, not);
    }

	public static void loadSettings(Context context) {
		final SharedPreferences settings = context.getSharedPreferences(
				PREFS_NAME, 0);
		savePath = settings.getString("savePath",
				"/data/data/org.addhen.ushahidi/files/");
		domain = settings.getString("Domain", "");
		countries = settings.getInt("Countries", 0);
		
		AutoUpdateDelay = settings.getInt("AutoUpdateDelay", 0);
		
		AutoFetch = settings.getBoolean("AutoFetch", false);

		// make sure folder exists
		final File dir = new File(UshahidiService.savePath);
		dir.mkdirs();

	}
	public static void saveSettings(Context context) {
		final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("Domain", domain);
		editor.putInt("Countries", countries);
		editor.putString("savePath", savePath);
		editor.putInt("AutoUpdateDelay", AutoUpdateDelay);
		editor.putBoolean("AutoFetch", AutoFetch);

		// Don't forget to commit your edits!!!
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
