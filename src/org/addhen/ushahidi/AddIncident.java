package org.addhen.ushahidi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddIncident extends Activity {
	private static final int LIST_INCIDENT = Menu.FIRST+1;
	private static final int MAP_INCIDENT = Menu.FIRST+2;
	private static final int ADD_INCIDENT = Menu.FIRST+3;
	
	private static final int REQUEST_CODE_PREFERENCES = 1;
	private static final int REQUEST_CODE_ABOUT = 2;
	private static final int REQUEST_CODE_IMAGE = 3;
	private static final int REQUEST_CODE_CAMERA = 4;
	
	// date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mAmPm;
    private ImageButton btnPicture;
	private EditText incidentTitle;
	private EditText incidentLocation;
	private EditText incidentDesc;
	private TextView incidentDate;
	private Button btnSave;
	private Button btnAddCategory;
	private Button pickTime;
	private Button pickDate;
	private static boolean running = false;
	private static final int DIALOG_ERROR_NETWORK = 0;
	private static final int DIALOG_ERROR_SAVING = 1;
    private static final int DIALOG_LOADING_CATEGORIES= 2;
    private static final int DIALOG_LOADING_LOCATIONS = 3;
	private static final int DIALOG_CHOOSE_IMAGE_METHOD = 4;
	private static final int DIALOG_POST_INCIDENTS = 5;
	private static final int DIALOG_MULTIPLE_CATEGORY = 6;
	private static final int TIME_DIALOG_ID = 7;
    private static final int DATE_DIALOG_ID = 8;
    
	private static Geocoder gc;
	private List<Address> foundAddresses;
	private final static Handler mHandler = new Handler();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_incident);
        initComponents();
        
        foundAddresses = new ArrayList<Address>();
        gc = new Geocoder(this);
        
        MyLocationListener listener = new MyLocationListener(); 
        LocationManager manager = (LocationManager) 
    getSystemService(Context.LOCATION_SERVICE); 
        long updateTimeMsec = 1000L; 
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
    updateTimeMsec, 500.0f, 
            listener); 
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
    updateTimeMsec, 500.0f, 
            listener); 
        
    }
	
	//menu stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
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
				//TODO Intent that will list incidents
				return true;
		
			case MAP_INCIDENT:
				//TODO call the intent that list map view of incidents
				return true ;
		
			case ADD_INCIDENT:
				//TODO call intent that allows addition of incidents
				return true;
		
		}
		return false;
	}
	
	/**
	 * Initialize UI components
	 */
	private void initComponents(){
		btnPicture = (ImageButton) findViewById(R.id.btnPicture);
		btnAddCategory = (Button) findViewById(R.id.add_category);
		incidentTitle = (EditText) findViewById(R.id.incident_title);
		incidentLocation = (EditText) findViewById(R.id.incident_location);
		incidentDesc = (EditText) findViewById(R.id.incident_desc);
		btnSave = (Button) findViewById(R.id.incident_add_btn);
		incidentDate = (TextView) findViewById(R.id.lbl_date);
		pickDate = (Button) findViewById(R.id.pick_date);
		pickTime = (Button) findViewById(R.id.pick_time);
		
		final Map<String,String> params = new HashMap<String, String>();
		
		params.put("task","report");
		params.put("incident_title", incidentTitle.getText().toString());
		params.put("incident_description",incidentDesc.getText().toString()); 
		params.put("incident_date","03/18/2009"); 
		params.put("incident_hour","10"); 
		params.put("incident_minute","10");
		params.put("incident_ampm", "pm");
		params.put("incident_category","a:5:{i:0;i:1;i:1;i:2;i:2;i:3;i:3;i:4;i:4;i:5;}");
		params.put("latitude","-1.28730007");
		params.put("longitude","36.82145118200820"); 
		params.put("location_name","accra");
		params.put("person_first","Henry");
		params.put("person_last","Addo");
		params.put("person_email", "henry@ushahidi.com");
		
		final String FileName = "/sdcard/dcim/Camera/1238951556779.jpg";
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				//TODO send http post with data
				final Thread tr = new Thread() {
					@Override
					public void run() {
						running = true;
						//try {
							/*if (UshahidiHttp.PostFileUpload(URL, FileName, params)) {
								mHandler.post(mSentIncidentSuccess);
							} else {*/
								mHandler.post(mSentIncidentFail);
							//}
						//} catch (IOException e) {
							//e.printStackTrace();
							mHandler.post(mDisplayNetworkError);
						//} finally { 
							running = false;
						//}
					}
				};
				tr.start();
				}
		});
		
		btnPicture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_CHOOSE_IMAGE_METHOD);
			}
		});
		
		btnAddCategory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_MULTIPLE_CATEGORY);
			}
		});
		
		pickDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
		
        pickTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mAmPm = c.get(Calendar.AM_PM);
        updateDisplay();
        
	}
	
	//
	final Runnable mSentIncidentSuccess = new Runnable() {
		public void run() {
			final Toast t = Toast.makeText(AddIncident.this, "Incident Sent!",
					Toast.LENGTH_SHORT);
			t.show();
		}
	};
	
	final Runnable mSentIncidentFail = new Runnable() {
		public void run() {
			final Toast t = Toast.makeText(AddIncident.this,
					"Failed to send Incident! Hope there is internet.",
					Toast.LENGTH_LONG);
			t.show();
			////mHandler.post(mDismissLoading);
		}
	};
	
	final Runnable mDisplayNetworkError = new Runnable(){
		public void run(){
			showDialog(DIALOG_ERROR_NETWORK);
		}
	};
	
	/**
	 * Create various dialog
	 */
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ERROR_NETWORK: {
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
            case DIALOG_ERROR_SAVING:{
           	 	AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("File System error!");
                dialog.setMessage("File System error, please ensure your save path is correct!");
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();						
					}
        		});
                dialog.setCancelable(false);
                return dialog;
           }
            case DIALOG_LOADING_CATEGORIES: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Loading Categories");
                dialog.setMessage("Please wait while categories are loaded...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            }
            
            case DIALOG_LOADING_LOCATIONS: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Loading Categories");
                dialog.setMessage("Please wait while categories are loaded...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            }
            case DIALOG_CHOOSE_IMAGE_METHOD:{
            	AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Choose Method");
                dialog.setMessage("Please choose how you would like to get the picture.");
                dialog.setButton("Gallery", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent test = new Intent();
						test.setAction(Intent.ACTION_PICK);
						test.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(test, REQUEST_CODE_IMAGE);
						dialog.dismiss();
					}
                });
                dialog.setButton2("Cancel", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
                });
                dialog.setButton3("Camera", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
												
						Intent launchPreferencesIntent = new Intent().setClass(AddIncident.this, ImageCapture.class);
						// Make it a subactivity so we know when it returns
						startActivityForResult(launchPreferencesIntent, REQUEST_CODE_CAMERA);
						dialog.dismiss();
					}
        		});
                dialog.setCancelable(false);
                return dialog;
            	
            }
            
            case DIALOG_MULTIPLE_CATEGORY: {
            	return new AlertDialog.Builder(this)
                .setTitle(R.string.add_categories)
                .setMultiChoiceItems(R.array.cats,
                        new boolean[]{false, false, false, false, false, false, false,false,false},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton,
                                    boolean isChecked) {
                            	String items = "";
                            	if( isChecked ) {
                            		 
                            		items += "Items selected "+whichButton+",";
                            	}
                            	final Toast t = Toast.makeText(AddIncident.this, "Incident "+items+"Sent!",
                    					Toast.LENGTH_SHORT);
                            		t.show();
                                /* User clicked on a check box do some stuff */
                            }
                        })
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Yes so do some stuff */
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                    }
                })
               .create();
            }
            
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
                
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);
            
        }
        return null;
    }
	

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }    

    private void updateDisplay() {
    	incidentDate.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("/")
                    .append(mDay).append("/")
                    .append(mYear).append(" ")
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute)).append(":")
    				.append(mAmPm == 0 ? "Am":"Pm"));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    
                    updateDisplay();
                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	
	public class MyLocationListener implements LocationListener { 
	    public void onLocationChanged(Location location) { 
	    	String latLongString;
	    	double latitude = 0;
	    	double longitude = 0;
	    	if (location != null) { 
	    		latitude = location.getLatitude(); 
	  	        longitude = location.getLongitude(); 
	  	    } else { 
	  	      latLongString = "No Location Found"; 
	  	    } 
	    	
	    	try {
				
	    		foundAddresses = gc.getFromLocation( latitude, longitude, 5 );
	    		Address address = foundAddresses.get(0);
	    		
	    		incidentLocation.setText( "" + address.getSubAdminArea() );
			
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
	    } 
	    public void onProviderDisabled(String provider) { 
	      // TODO Auto-generated method stub 
	    } 
	    public void onProviderEnabled(String provider) { 
	      // TODO Auto-generated method stub 
	    } 
	    public void onStatusChanged(String provider, int status, Bundle extras) 
	    { 
	      // TODO Auto-generated method stub 
	    } 
	  } 
	
}
