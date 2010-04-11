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

package org.addhen.ushahidi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class About extends Activity {
	
	private Button urlBtn;
	private static final String URL = "http://www.ushahidi.com";
	
	private static final int HOME = Menu.FIRST+1;
	private static final int LIST_INCIDENT = Menu.FIRST+2;
	private static final int INCIDENT_ADD = Menu.FIRST+3;
	private static final int INCIDENT_REFRESH= Menu.FIRST+4;
	private static final int SETTINGS = Menu.FIRST+5;
	private static final int ABOUT = Menu.FIRST+6;
	private static final int INCIDENT_MAP = Menu.FIRST+7;
	private static final int GOTOHOME = 0;
	private static final int ADD_INCIDENTS = 1;
	private static final int LIST_INCIDENTS = 2;
	private static final int REQUEST_CODE_SETTINGS = 1;
	private static final int REQUEST_CODE_ABOUT = 2;
	private static final int DIALOG_ERROR = 0;
	
	private Bundle bundle;
	
	private String dialogErrorMsg = "An error occurred fetching the reports. " +
		"Make sure you have entered an Ushahidi instance.";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.about);
        
        bundle = new Bundle();
        urlBtn = (Button) findViewById(R.id.view_website);
	    //Dipo Fix
	    final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(URL));
	    
        urlBtn.setOnClickListener(new View.OnClickListener() {
        	public void onClick( View v ){
        		//Dip Fix
        		startActivity(i);
        	}
        });
	}
	
	private void populateMenu(Menu menu) {
		
		MenuItem i;i = menu.add( Menu.NONE, HOME, Menu.NONE, R.string.menu_home );
		i.setIcon(R.drawable.ushahidi_home);
		
		i = menu.add( Menu.NONE, INCIDENT_ADD, Menu.NONE, R.string.incident_menu_add);
		i.setIcon(R.drawable.ushahidi_add);
		  
		i = menu.add( Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list );
		i.setIcon(R.drawable.ushahidi_list);
		
		i = menu.add( Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
		i.setIcon(R.drawable.ushahidi_map);
		 
		i = menu.add( Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.incident_menu_refresh );  
		i.setIcon(R.drawable.ushahidi_refresh);
		
		i = menu.add( Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings );
		i.setIcon(R.drawable.ushahidi_about);
	  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
 
		return(super.onCreateOptionsMenu(menu));
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//applyMenuChoice(item);
 
		return(applyMenuChoice(item) ||
				super.onOptionsItemSelected(item));
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		Intent intent;
	    switch (item.getItemId()) {
	    	case HOME:
	    		intent = new Intent( About.this, Ushahidi.class);
				startActivityForResult( intent, GOTOHOME );
				return true;
	    
	    	case LIST_INCIDENT:
	    		bundle.putInt("tab_index", 0);
	    		intent = new Intent( About.this, IncidentsTab.class);
	    		intent.putExtra("tab", bundle);
	    	  	startActivityForResult( intent,LIST_INCIDENTS );
	    	  	return(true);
	    
	    	case INCIDENT_ADD:
	    		intent = new Intent( About.this, AddIncident.class);
	    	  	startActivityForResult(intent, ADD_INCIDENTS  );
	  		  	return(true);
	      
	    	case ABOUT:
				intent = new Intent( About.this, About.class);
	    		startActivityForResult( intent, REQUEST_CODE_ABOUT );
				return true;
	    	case INCIDENT_REFRESH:
	    	  	ReportsTask reportsTask = new ReportsTask();
	            reportsTask.appContext = this;
	            reportsTask.execute();	
	    	case SETTINGS:
	    		intent = new Intent( About.this,  Settings.class);
				
	    		// Make it a subactivity so we know when it returns
	    		startActivityForResult( intent, REQUEST_CODE_SETTINGS );
	    		return( true );
	    }
	    return false;
	}
	
	@Override
	 protected Dialog onCreateDialog(int id) {
		 switch (id) {
	     	case DIALOG_ERROR: {
	     		AlertDialog dialog = (new AlertDialog.Builder(this)).create();
	     		dialog.setTitle(R.string.alert_dialog_error_title);
	            dialog.setMessage(dialogErrorMsg);
	            dialog.setButton2("Ok", new Dialog.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int which) {
	            		
	            		Intent launchPreferencesIntent = new Intent(About.this, 
	            				Settings.class);
	            		
	    				// Make it a sub activity so we know when it returns
	    				startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
							dialog.dismiss();						
						}
	        		});
	                dialog.setCancelable(false);
	                return dialog;
	     	}
		 }
		 return null;
	  }
	
	final Runnable mDisplayErrorPrompt = new Runnable() {
		public void run() {
			showDialog(DIALOG_ERROR);
		}
	};
	
	//thread class
	private class ReportsTask extends AsyncTask <Void, Void, Integer> {
		
		protected Integer status;
		private ProgressDialog dialog;
		protected Context appContext;
		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(appContext, "Please wait...",
					"Fetching new reports", true);

		}
		
		@Override 
		protected Integer doInBackground(Void... params) {
			status = Util.processReports(appContext);
			return status;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if( result == 4 ){
				Util.showToast(appContext, R.string.internet_connection);
			}
			this.dialog.cancel();
		}
		
	}

}
