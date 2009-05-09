package org.addhen.ushahidi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ListIncidents extends Activity{
	
	/** Called when the activity is first created. */
	private ListView listIncidents = null;
	private ListIncidentAdapter ila = new ListIncidentAdapter( this );
	private static final int LIST_INCIDENT = Menu.FIRST+1;
	private static final int MAP_INCIDENT = Menu.FIRST+2;
	private static final int ADD_INCIDENT = Menu.FIRST+3;
	private Spinner spinner = null;
	private ArrayAdapter spinnerArrayAdapter = null;
	private final String URL = "http://192.168.10.61/ushahidi2/media/uploads/";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView( R.layout.list_incidents );
        
        listIncidents = (ListView) findViewById( R.id.view_incidents );
        
        spinner = (Spinner) findViewById(R.id.incident_cat);
        this.showCategories();
        this.showIncidents();
    }

  //menu stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
		populateMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);

		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		applyMenuChoice(item);

		return(applyMenuChoice(item) ||
						super.onOptionsItemSelected(item));
	}

	public boolean onContextItemSelected(MenuItem item) {

		return(applyMenuChoice(item) ||
						super.onContextItemSelected(item));
	}
	
	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, "List Incident");
		menu.add(Menu.NONE, MAP_INCIDENT, Menu.NONE, "Map Incident");
		menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, "Add Incident");
	}
	
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
			case LIST_INCIDENT:
				//TODO
				return(true);
		
			case MAP_INCIDENT:
				//TODO
				return(true);
		
			case ADD_INCIDENT:
				//TODO
				return(true);
		
		}
		return(false);
	}
	
	public void showIncidents() {
		
		ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_icon),
				"Uhuru Fires still strong - Uhuru, Kenya",
				"Date:2009-04-17",
				"Verified",24)
		);
		
		ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_globe),
				"Uhuru Fires still strong - Uhuru, Kenya",
				"Date:2009-04-17",
				"Verified",25)
		);
		
		ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_fire),
				"Uhuru Fires still strong - Uhuru, Kenya",
				"Date:2009-04-17",
				"Verified",26)
		);
		
		ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_plane),
				"Uhuru Fires still strong - Uhuru, Kenya",
				"Date:2009-04-17",
				"Verified",27)
		);
		
		ila.addItem( new ListIncidentText(getResources().getDrawable( R.drawable.ushahidi_icon),
				"Uhuru Fires still strong - Uhuru, Kenya",
				"Date:2009-04-17",
				"Verified",24)
		);
		 
		listIncidents.setAdapter( ila );
	}
	
	@SuppressWarnings("unchecked")
	public void showCategories() {
		
		Vector<String> vector = new Vector<String>();
		
		vector.add("DEATHS");
		vector.add("PROPERTY LOSS");
		
		spinnerArrayAdapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, vector );
		
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(spinnerListener);

	}
	
	//spinner listener
	Spinner.OnItemSelectedListener spinnerListener =
	    new Spinner.OnItemSelectedListener() {
		
	      @SuppressWarnings("unchecked")
		public void onItemSelected(AdapterView parent, View v, int position, long id) {
	        Log.i("print", parent.getSelectedItem().toString());
	      }

	      @SuppressWarnings("unchecked")
		public void onNothingSelected(AdapterView parent) { }

	                  
	}; 
	
	// As drawable.  
	public static Drawable imageOperations(String url, String saveFilename) {
		try {
			InputStream is = (InputStream) fetch(url);
			Drawable d = Drawable.createFromStream(is, saveFilename);
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Fetch image from the given URL
	private static Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content; 
	}
}
