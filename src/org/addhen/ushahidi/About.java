package org.addhen.ushahidi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;


public class About extends Activity {
	
	private Button urlBtn;
	private WebView webView;
	private String url = "http://www.ushahidi.com";
	
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.about);
        
        urlBtn = (Button) findViewById(R.id.view_website);
        
	    webView = new WebView(About.this);
	    
        urlBtn.setOnClickListener(new View.OnClickListener() {
        	public void onClick( View v ){
        		webView.loadUrl(url);
				
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
	
	private boolean applyMenuChoice(MenuItem item) {
		Intent intent;
	    switch (item.getItemId()) {
	    	case HOME:
			intent = new Intent( About.this, Ushahidi.class);
				startActivityForResult( intent, GOTOHOME );
				return true;
	    
	      case LIST_INCIDENT:
	    	 intent = new Intent( About.this, ListIncidents.class);
	  		startActivityForResult( intent,LIST_INCIDENTS );
	        return(true);
	    
	      case INCIDENT_ADD:
	    	intent = new Intent( About.this, AddIncident.class);
	  		startActivityForResult(intent, ADD_INCIDENTS  );
	        return(true);
	      
	      case ABOUT:
				intent = new Intent( About.this, About.class);
	    		startActivityForResult( intent, REQUEST_CODE_ABOUT );
	    		setResult(RESULT_OK);
				return true;  
	        
	      case SETTINGS:
	    	  intent = new Intent( About.this,  Settings.class);
				
	    	  // Make it a subactivity so we know when it returns
	    	  startActivityForResult( intent, REQUEST_CODE_SETTINGS );
	    	  return( true );
	    }
	    return false;
	}

}
