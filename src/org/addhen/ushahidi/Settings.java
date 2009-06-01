package org.addhen.ushahidi;

import java.io.IOException;
import java.util.List;

import org.addhen.ushahidi.data.CategoriesData;
import org.addhen.ushahidi.data.HandleXml;
import org.addhen.ushahidi.data.IncidentsData;
import org.addhen.ushahidi.net.Categories;
import org.addhen.ushahidi.net.Incidents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Settings extends Activity {
	//variables
	public final String PREFS_NAME = "Ushahidi";
	private final int DIALOG_NETWORK_ERROR = 1;
	private final int CLEAR_CACHE = 2;
	private EditText domain;
	private EditText etSdSavePath;
	private EditText firstname;
	private EditText lastname;
	private EditText email;
	private LinearLayout llOnSd;
	private RadioButton rbOnPhone;
	private RadioButton rbOnSD;
	private Button btnConfirm;
	private Button btnCancel;
	private Button btnClearCache;
	private String oldSavePath;
	private CheckBox chkAutoFetch;
	private Spinner spnCountries;
	private Spinner spnAutoUpdateDelay;
	private final Handler mHandler = new Handler();
	private static boolean busy = false;
	private List<IncidentsData> mNewIncidents;
	private List<CategoriesData> mNewCategories;
	
    //Load
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initComponents();
        loadSettings();
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_NETWORK_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Network error!");
                dialog.setMessage("Network error, please ensure you are connected to the internet");
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
        		});
                dialog.setCancelable(false);
                return dialog;
            }
            case CLEAR_CACHE: {
            	ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Clearing Cache");
                dialog.setMessage("Please wait while the cache is deleted...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            }
        }
        return null;
    } 
    @SuppressWarnings("unchecked")
	private void initComponents(){
    	btnConfirm = (Button) findViewById(R.id.btn_confirm);
    	btnCancel = (Button) findViewById(R.id.btn_cancel);
    	btnClearCache = (Button) findViewById(R.id.btn_clear_cache);
        domain = (EditText) findViewById(R.id.domain);
        firstname =(EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        spnCountries = (Spinner) findViewById(R.id.spn_countries);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
        		R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCountries.setAdapter(adapter);
        
        etSdSavePath = (EditText) findViewById(R.id.et_sd_save_path);
        llOnSd = (LinearLayout) findViewById(R.id.ll_on_sd);
        rbOnPhone = (RadioButton) findViewById(R.id.rb_on_phone);
        rbOnSD = (RadioButton) findViewById(R.id.rb_on_SD);
        chkAutoFetch = (CheckBox) findViewById(R.id.chk_auto_fetch);
        spnAutoUpdateDelay = (Spinner) findViewById(R.id.spn_auto_update_delay);
        
        if(busy){
        	setEnabled(false);
        } else {
        	setEnabled(true);
        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
        		android.R.layout.simple_spinner_item,
                new String[] { "Off", "5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "60 Minutes" });
        spnAutoUpdateDelay.setAdapter(spinnerArrayAdapter);
 
        rbOnSD.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					llOnSd.setVisibility(View.VISIBLE);				
				} else {
					llOnSd.setVisibility(View.GONE);
				}
			}
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				setEnabled(false);
				mHandler.post(mFinishCancel);
			}
        });
        
        btnClearCache.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				final Thread tr = new Thread() {
					@Override
					public void run() {
						mHandler.post(mDisplayClearCache);
						clearOldCache();
						mHandler.post(mDismissClearCache);
					}
				};
				tr.start();
			}
        });
    	
        btnConfirm.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				setEnabled(false);
				final Thread tr = new Thread() {
					@Override
					public void run() {
						busy = true;
						if(rbOnSD.isChecked() && etSdSavePath.getText().toString().length() == 0){
							mHandler.post(mInvalidSDCardFolder);
							busy = false;
				    		return;
						}
						if(domain.getText().toString().equals("")){
							mHandler.post(mInvalidDomain);
							busy = false;
							return;
						}
						
						//TODO test if domain can connect
						retrieveIncidentsAndCategories();
						mHandler.post(mFinishSave);
						busy = false;
					}
				};
				tr.start();
			}});
    }
    
    final Runnable mDisplayClearCache = new Runnable(){
    	public void run(){
    		showDialog(CLEAR_CACHE);
    	}
    };
    
    final Runnable mDismissClearCache = new Runnable(){
    	public void run(){
    		try{
				dismissDialog(CLEAR_CACHE);
				
			} catch(IllegalArgumentException e){
				return;	//means that the dialog is not showing, ignore please!
			}
    	}
    };
    
    final Runnable mDisplayNetworkError = new Runnable(){
		public void run(){
			showDialog(DIALOG_NETWORK_ERROR);
		}
	};
	
	final Runnable mInvalidSDCardFolder = new Runnable(){
		public void run(){
			final Toast t = Toast.makeText(Settings.this, "If you select to save on the SD card it must be in its own subfolder!", Toast.LENGTH_SHORT);
    		t.show();
    		setEnabled(true);
		}
	};
	
	final Runnable mInvalidDomain = new Runnable(){
		public void run(){
			final Toast t = Toast.makeText(Settings.this, "Enter a valid URL. It should start " +
							"with http://", Toast.LENGTH_SHORT);
    		t.show();
    		setEnabled(true);
		}
	};
	
	final Runnable mFinishSave = new Runnable(){
		public void run(){
			saveSettings();
			Settings.this.setResult(RESULT_OK);
			finish();
		}
	};
	
	final Runnable mFinishCancel = new Runnable(){
		public void run(){
			Settings.this.setResult(RESULT_CANCELED);
			finish();
		}
	};
	
	 final Runnable mRetrieveNewIncidents = new Runnable() {
		  public void run() {
		  try {
			  if( Util.isConnected()) {
				  setProgressBarIndeterminateVisibility(true);
				  
		   
				  if(Categories.getAllCategoriesFromWeb() ) {
					  mNewCategories = HandleXml.processCategoriesXml(UshahidiService.categoriesResponse);
				  }
				  
				  if(Incidents.getAllIncidentsFromWeb()){
					  mNewIncidents =  HandleXml.processIncidentsXml( UshahidiService.incidentsResponse ); 
				  }
				  
				  UshahidiApplication.mDb.addCategories(mNewCategories, false);
				  UshahidiApplication.mDb.addIncidents(mNewIncidents, false);
				  
		  			  setProgressBarIndeterminateVisibility(false);
			  
			  } else {
				  Toast.makeText(Settings.this, R.string.internet_connection, Toast.LENGTH_LONG).show();
			  }
		  	} catch (IOException e) {
				//means there was a problem getting it
		  	}
		  }
	  };
	
    
	private void retrieveIncidentsAndCategories() {
		  
		final Thread tr = new Thread() {
			public void run() {
				mHandler.post(mRetrieveNewIncidents);
			}
		};
	    tr.start();
	 }  
    //Helper functions
	void setEnabled(Boolean value){
		btnConfirm.setEnabled(value);
		btnCancel.setEnabled(value);
		btnClearCache.setEnabled(value);
		domain.setEnabled(value);
		firstname.setEnabled(value);
		lastname.setEnabled(value);
		email.setEnabled(value);
		spnCountries.setEnabled(value);
		etSdSavePath.setEnabled(value);
		rbOnPhone.setEnabled(value);
		rbOnSD.setEnabled(value);
		chkAutoFetch.setEnabled(value);
		spnAutoUpdateDelay.setEnabled(value);
	}
    
    public void clearOldCache(){
		//TODO clear the data from db
	}
    
    //Settings
    protected void loadSettings(){
		domain.setText(UshahidiService.domain);
		firstname.setText(UshahidiService.firstname);
		lastname.setText(UshahidiService.lastname);
		email.setText(UshahidiService.email);
		spnCountries.setSelection(UshahidiService.countries);
		chkAutoFetch.setChecked(UshahidiService.AutoFetch);
		oldSavePath = UshahidiService.savePath;
		int option = 0;
		switch(UshahidiService.AutoUpdateDelay){
			case 0:
				option = 0;
				break;
			case 5:
				option = 1;
				break;
			case 10:
				option = 2;
				break;
			case 15:
				option = 3;
				break;
			case 30:
				option = 4;
				break;
			case 60:
				option = 5;
				break;
		}
		spnAutoUpdateDelay.setSelection(option);
		if(oldSavePath.contains("sdcard")){
			rbOnSD.setChecked(true);
			etSdSavePath.setText(oldSavePath.substring(oldSavePath.indexOf("/", 2)));
			llOnSd.setVisibility(View.VISIBLE);
		} else { 
			rbOnPhone.setChecked(true);
			llOnSd.setVisibility(View.GONE);
		}
		
	}
	protected void saveSettings(){
		UshahidiService.domain = domain.getText().toString();
		UshahidiService.firstname = firstname.getText().toString();
		UshahidiService.lastname = lastname.getText().toString();
		UshahidiService.email = email.getText().toString();
		UshahidiService.countries = spnCountries.getSelectedItemPosition();
		UshahidiService.AutoFetch = chkAutoFetch.isChecked();
		String AutoUpdate = spnAutoUpdateDelay.getSelectedItem().toString();
		
		//"5 Minutes", "10 Minutes", "15 Minutes", "c", "60 Minutes" 
		if(AutoUpdate.matches("Off")){
			UshahidiService.AutoUpdateDelay = 0;
		} else if(AutoUpdate.matches("5 Minutes")){
			UshahidiService.AutoUpdateDelay = 5;
		} else if(AutoUpdate.matches("10 Minutes")){
			UshahidiService.AutoUpdateDelay = 10;
		} else if(AutoUpdate.matches("15 Minutes")){
			UshahidiService.AutoUpdateDelay = 15;
		} else if(AutoUpdate.matches("30 Minutes")){
			UshahidiService.AutoUpdateDelay = 30;
		} else if(AutoUpdate.matches("60 Minutes")){
			UshahidiService.AutoUpdateDelay = 60;
		}
		String newSavePath = "";
		if(rbOnPhone.isChecked()){
			newSavePath = "/data/data/org.addhen.ushahidi/files/";
			if(oldSavePath.compareTo(newSavePath) != 0){
				clearOldCache();
			}
		} else {	//means on sd is checked
			newSavePath = etSdSavePath.getText().toString();
			if(!newSavePath.startsWith("/")){
				newSavePath = "/" + newSavePath;
			}
			if(!newSavePath.endsWith("/")){
				newSavePath += "/";
			}
			newSavePath = "/sdcard" + newSavePath;
			if(oldSavePath.compareTo(newSavePath) != 0){
				clearOldCache();
			}
		}
		UshahidiService.savePath = newSavePath;
		UshahidiService.saveSettings(this);
	}
	
}