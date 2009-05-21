package org.addhen.ushahidi;

import java.util.HashSet;

import org.addhen.ushahidi.data.UshahidiDatabase;
import org.addhen.ushahidi.net.UshahidiHttpClient;

import android.app.Application;
import android.database.Cursor;

public class UshahidiApplication extends Application {
	
	public static final String TAG = "UshahidiApplication";
	  
	  public static ImageManager mImageManager;
	  public static UshahidiDatabase mDb; 
	  public static UshahidiHttpClient mApi;

	  @Override
	  public void onCreate() {
	    super.onCreate();

	    mImageManager = new ImageManager();
	    mDb = new UshahidiDatabase(this);
	    mDb.open();
	    mApi = new UshahidiHttpClient();
	    
	  }

	  @Override
	  public void onTerminate() {
	    cleanupImages();
	    mDb.close();
	    
	    super.onTerminate();
	  }
	  
	  private void cleanupImages() {
		  HashSet<String> keepers = new HashSet<String>();
	    
		  Cursor cursor = mDb.fetchAllIncidents();
	    
		  if (cursor.moveToFirst()) {
			  int imageIndex = cursor.getColumnIndexOrThrow(
					  UshahidiDatabase.INCIDENT_MEDIA);
			  do {
				  keepers.add(cursor.getString(imageIndex));
			  } while (cursor.moveToNext());
		  }
	    
		  cursor.close();
	    
		  cursor = mDb.fetchAllCategories();
	    
		  if (cursor.moveToFirst()) {
			  int imageIndex = cursor.getColumnIndexOrThrow(
					  UshahidiDatabase.INCIDENT_MEDIA);
			  do {
				  keepers.add(cursor.getString(imageIndex));
			  } while (cursor.moveToNext());
		  }
	    
		  cursor.close();
	    
		  //mImageManager.cleanup(keepers);
	  }
}
