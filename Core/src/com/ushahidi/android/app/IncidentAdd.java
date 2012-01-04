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

package com.ushahidi.android.app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.checkin.NetworkServices;
import com.ushahidi.android.app.data.AddIncidentData;
import com.ushahidi.android.app.data.Database;
import com.ushahidi.android.app.net.MainHttpClient;
import com.ushahidi.android.app.opengeosms.OpenGeoSMSSender;
import com.ushahidi.android.app.util.PhotoUtils;
import com.ushahidi.android.app.util.Util;

public class IncidentAdd extends MapUserLocation {

    /**
     * category that exists on the phone before any connection to a server, at
     * present it is trusted reporter, id number 4 but will change to specific
     * 'uncategorized' category when it is ready on the server
     */
    private static final String UNCATEGORIZED_CATEGORY_ID = "4";

    private static final String UNCATEGORIZED_CATEGORY_TITLE = "uncategorized";

    private static final int INCIDENT_CLEAR = Menu.FIRST + 1;

    private static final int SETTINGS = Menu.FIRST + 2;

    private static final int ABOUT = Menu.FIRST + 3;

    private static final int LIST_INCIDENTS = 2;

    private static final int REQUEST_CODE_SETTINGS = 2;

    private static final int REQUEST_CODE_ABOUT = 3;

    private static final int REQUEST_CODE_IMAGE = 4;

    private static final int REQUEST_CODE_CAMERA = 5;

    private static final int VIEW_SEARCH = 2;

    private ReverseGeocoderTask reverseGeocoderTask;

    // date and time
    private Calendar mCalendar;

    private int mCounter = 0;

    private String mErrorMessage = "";

    private String mDateToSubmit = "";

    private boolean mError = false;

    private EditText mIncidentTitle;

    private EditText mIncidentLocation;

    private EditText mIncidentDesc;

    private ImageView mSelectedPhoto;

    private EditText mLatitude;

    private EditText mLongitude;

    private TextView activityTitle;

    private Button mBtnSend;

    private Button mBtnAddCategory;

    private Button mPickTime;

    private Button mPickDate;

    private Button mBtnPicture;

    private static final int DIALOG_ERROR_NETWORK = 0;

    private static final int DIALOG_ERROR_SAVING = 1;

    private static final int DIALOG_CHOOSE_IMAGE_METHOD = 2;

    private static final int DIALOG_MULTIPLE_CATEGORY = 3;

    private static final int TIME_DIALOG_ID = 4;

    private static final int DATE_DIALOG_ID = 5;

    private Vector<String> mVectorCategories = new Vector<String>();

    private Vector<String> mCategoriesId = new Vector<String>();

    private HashMap<String, String> mCategoriesTitle = new HashMap<String, String>();

    private HashMap<String, String> mParams = new HashMap<String, String>();

    private static final String CLASS_TAG = IncidentAdd.class.getSimpleName();

    private boolean draft = true;
	
    
    //@inoran
	private String mTransmission;

	private LinearLayout mTransmissionLinearLayout;
	
	private int mCategoryLength;

	private EditText mPhoneNumber;

	protected SMSSendingReceiver SMSSentReceiver;

	private static final int DIALOG_REPORT_PIC_VIA_INTERNET = 7;

	private static final int DIALOG_REPORT_WAY_CHOOSE = 6;

	private static final CharSequence mPhoneNumberText = "";

	private static final int DIALOG_REPORT_OPENGEOSMS_PROGRASS = 8;
	
	private static final int DEVICE_ERROR = 133404;
	
	private int smsNum;

	public int mCurSMSReceivedNumber = 0;
	
	private ProgressDialog sendSMSProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_add);

        // load settings
        Preferences.loadSettings(IncidentAdd.this);
        initComponents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocating();
    }

    // menu stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        populateMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }

    private void populateMenu(Menu menu) {
        MenuItem i;

        i = menu.add(Menu.NONE, INCIDENT_CLEAR, Menu.NONE, R.string.clear_all);
        i.setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.menu_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.menu_about);
    }

    private boolean applyMenuChoice(MenuItem item) {
        switch (item.getItemId()) {

            case INCIDENT_CLEAR:
                discardReports();
                return true;

            case ABOUT:
                startActivityForResult(new Intent(IncidentAdd.this, About.class),
                        REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                startActivityForResult(new Intent(IncidentAdd.this, Settings.class),
                        REQUEST_CODE_SETTINGS);
                return true;
        }
        return false;
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        
    	//@inoran
    	mPhoneNumber = (EditText)findViewById(R.id.phoneNumber);
    	mPhoneNumber.setText(Preferences.phonenumber);
    	
    	mBtnPicture = (Button)findViewById(R.id.btnPicture);
        mBtnAddCategory = (Button)findViewById(R.id.add_category);
        mBtnSend = (Button)findViewById(R.id.incident_add_btn);
        mPickDate = (Button)findViewById(R.id.pick_date);
        mPickTime = (Button)findViewById(R.id.pick_time);
        mLatitude = (EditText)findViewById(R.id.incident_latitude);
        mLatitude.addTextChangedListener(latLonTextWatcher);
        mLongitude = (EditText)findViewById(R.id.incident_longitude);
        mLongitude.addTextChangedListener(latLonTextWatcher);
        mSelectedPhoto = (ImageView)findViewById(R.id.sel_photo_prev);
        activityTitle = (TextView)findViewById(R.id.title_text);
        if (activityTitle != null) {
            activityTitle.setText(getTitle());
        }
        mIncidentTitle = (EditText)findViewById(R.id.incident_title);
        mIncidentLocation = (EditText)findViewById(R.id.incident_location);
        mIncidentDesc = (EditText)findViewById(R.id.incident_desc);
        mapView = (MapView)findViewById(R.id.location_map);
        mapController = mapView.getController();

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	  
                // Dipo Fix
                mError = false;
                
                //@inoran
                if(mIncidentTitle.getText().length() < 3 || mIncidentTitle.getText().length() > 200 ){
          		  	mErrorMessage = getString(R.string.less_report_title) + "\n";
                    mError = true;            		  
          	  	}
                
                if (TextUtils.isEmpty(mIncidentDesc.getText())) {
                    mErrorMessage += getString(R.string.empty_report_description)  + "\n";
                    mError = true;
                }

                // Dipo Fix
                if (mVectorCategories.size() == 0) {
                    mErrorMessage += getString(R.string.empty_report_categories) + "\n";
                    mError = true;
                }

                // validate lat long
                if (TextUtils.isEmpty(mLatitude.getText().toString())) {
                    mErrorMessage += getString(R.string.empty_latitude);
                    mError = true;
                }

                // validate lat long
                if (TextUtils.isEmpty(mLongitude.getText().toString())) {
                    mErrorMessage += getString(R.string.empty_longitude);
                    mError = true;
                }

                try {
                    Double.parseDouble(mLatitude.getText().toString());
                } catch (NumberFormatException ex) {
                    mErrorMessage += getString(R.string.about_text);
                    mError = true;
                }

                try {
                    Double.parseDouble(mLongitude.getText().toString());
                } catch (NumberFormatException ex) {
                    mErrorMessage += getString(R.string.about_text);
                    mError = true;
                }

                if (!mError) {
                	showDialog(DIALOG_REPORT_WAY_CHOOSE);

                } else {
                    Toast.makeText(IncidentAdd.this, "Error!\n\n" + mErrorMessage,
                            Toast.LENGTH_LONG).show();
                    mErrorMessage = "";
                }

            }
        });

        mBtnPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Preferences.fileName)) {
                    ImageManager.deleteImage(Preferences.fileName, "");
                }
                showDialog(DIALOG_CHOOSE_IMAGE_METHOD);
            }
        });

        mBtnAddCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_MULTIPLE_CATEGORY);
                mCounter++;
            }
        });

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        

        mCalendar = Calendar.getInstance();
        updateDisplay();
    }
    

    
    private void addReports(){
		//if(!mError){
			AddReportsTask addReportsTask = new AddReportsTask();
            addReportsTask.appContext = IncidentAdd.this;
            addReportsTask.execute();                	  					
			//}
    }
    
    // fetch categories
    public String[] showCategories() {
        Cursor cursor = MainApplication.mDb.fetchAllCategories();

        // check if there are any existing categories in the database
        int categoryCount = cursor.getCount();
        int categoryAmount = 0;
        if (categoryCount > 0) {
            categoryAmount = categoryCount;
        } else {
            mCategoriesId.clear();
            mCategoriesTitle.clear();
            categoryAmount = 1;
        }

        String categories[] = new String[categoryAmount];
        mCategoryLength = categories.length;
        
        int i = 0;
   
        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndexOrThrow(Database.CATEGORY_TITLE);
            int idIndex = cursor.getColumnIndexOrThrow(Database.CATEGORY_ID);

            do {

                // Because the API returns trusted reports and we don't
                // want it, don't add it to the list of categories.

                categories[i] = cursor.getString(titleIndex);            	
                mCategoriesTitle.put(String.valueOf(cursor.getInt(idIndex)),
                        cursor.getString(titleIndex));
                mCategoriesId.add(String.valueOf(cursor.getInt(idIndex)));
                Log.d(CLASS_TAG,
                        "Title: "
                                + String.valueOf(cursor.getString(titleIndex) + " Index: "
                                        + String.valueOf(cursor.getInt(idIndex))));
                i++;
                

            } while (cursor.moveToNext());
        }

        // sets category to be on the phone from the beginning if there aren't
        // any already
        if (mCategoriesId.isEmpty()) {
            categories[0] = UNCATEGORIZED_CATEGORY_TITLE;
            mCategoriesId.add(UNCATEGORIZED_CATEGORY_ID);
            mCategoriesTitle.put(UNCATEGORIZED_CATEGORY_ID, UNCATEGORIZED_CATEGORY_TITLE);
        }

        cursor.close();
        return categories;
    }

    // reset records in the field
    private void clearFields() {
        Log.d(CLASS_TAG, "clearFields(): clearing fields");
        mBtnPicture = (Button)findViewById(R.id.btnPicture);
        mBtnAddCategory = (Button)findViewById(R.id.add_category);
        // delete unset photo
        if (Preferences.fileName != null) {
            File file = new File(Preferences.fileName);
            if (file.exists() && file.delete()) {
                Log.i("IncidentAdd", "File deleted " + file.getName());
            }
        }
        mBtnPicture.setText(getString(R.string.btn_add_photo));
        mPhoneNumber.setText("");
        mIncidentTitle.setText("");
        mIncidentLocation.setText("");
        mIncidentDesc.setText("");
//        mLatitude.setText("");
//        mLongitude.setText("");
        mVectorCategories.clear();
        mBtnAddCategory.setText(R.string.incident_add_category);
        mSelectedPhoto.setImageDrawable(null);
        mSelectedPhoto.setImageBitmap(null);
        mSelectedPhoto.setMinimumHeight(0);
        mCounter = 0;
        updateDisplay();

        // clear persistent data
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("title", "");
        editor.putString("description", "");
        editor.putString("date", "");
        editor.putString("latitude", mLatitude.getText().toString());
        editor.putString("longitude", mLongitude.getText().toString());
        editor.putString("categories", "");
        editor.putString("photo", "");
        editor.commit();
    }

    // discard reports
    private void discardReports() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_clear))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.btn_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getString(R.string.btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // delete all messages
                                clearFields();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Go to reports screen
     */
    public void goToReports() {
        Intent intent = new Intent(IncidentAdd.this, IncidentTab.class);
        startActivityForResult(intent, LIST_INCIDENTS);
        setResult(RESULT_OK);
    }

    /**
     * Get shared text from other Android applications
     */
    public void getSharedText() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Intent.ACTION_SEND) || action.equals(Intent.ACTION_CHOOSER)) {
                CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    mIncidentDesc.setText(text);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                Uri uri = PhotoUtils.getPhotoUri("photo.jpg", this);
                Bitmap bitmap = PhotoUtils.getCameraPhoto(this, uri);
                PhotoUtils.savePhoto(this, bitmap);
                Log.i(CLASS_TAG,
                        String.format("REQUEST_CODE_CAMERA %dx%d", bitmap.getWidth(),
                                bitmap.getHeight()));
            } else if (requestCode == REQUEST_CODE_IMAGE) {
                Bitmap bitmap = PhotoUtils.getGalleryPhoto(this, data.getData());
                PhotoUtils.savePhoto(this, bitmap);
                Log.i(CLASS_TAG,
                        String.format("REQUEST_CODE_IMAGE %dx%d", bitmap.getWidth(),
                                bitmap.getHeight()));
            }
            SharedPreferences.Editor editor = getPreferences(0).edit();
            editor.putString("photo", PhotoUtils.getPhotoUri("photo.jpg", this).getPath());
            editor.commit();
        }
    }

    /**
     * Create various dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ERROR_NETWORK: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(getString(R.string.network_error));
                dialog.setMessage(getString(R.string.network_error_msg));
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }
            case DIALOG_ERROR_SAVING: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(getString(R.string.network_error));
                dialog.setMessage(getString(R.string.file_system_error_msg));
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }

            case DIALOG_CHOOSE_IMAGE_METHOD: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(getString(R.string.choose_method));
                dialog.setMessage(getString(R.string.how_to_select_pic));
                dialog.setButton(getString(R.string.gallery_option), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_IMAGE);
                        dialog.dismiss();
                    }
                });
                dialog.setButton2(getString(R.string.btn_cancel), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setButton3(getString(R.string.camera_option), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                PhotoUtils.getPhotoUri("photo.jpg", IncidentAdd.this));
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(false);
                return dialog;
            }

            case DIALOG_MULTIPLE_CATEGORY: {
                return new AlertDialog.Builder(this)
                        .setTitle(R.string.add_categories)
                        .setMultiChoiceItems(showCategories(), null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                            boolean isChecked) {
                                        // see if categories have previously

                                        if (isChecked) {
                                            mVectorCategories.add(mCategoriesId.get(whichButton));
                                            mError = false;
                                        } else {
                                            mVectorCategories.remove(mCategoriesId.get(whichButton));
                                        }

                                        setSelectedCategories(mVectorCategories);
                                    }
                                })
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked Yes so do some stuff */
                            }
                        }).create();
            }

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mCalendar.get(Calendar.HOUR),
                        mCalendar.get(Calendar.MINUTE), false);

            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                
            //@inoran
            case DIALOG_REPORT_WAY_CHOOSE :{
            	final CharSequence[] items = { getString(R.string.report_way_internet), getString(R.string.report_way_opengeosms)};

            	return new AlertDialog.Builder(this)            	
            			.setTitle("Reporting via")
            	  		.setItems(items, new DialogInterface.OnClickListener() {    

							public void onClick(DialogInterface dialog, int item) {        
            	  				
            	  				//reporting via Internet, location can Not be empty
            	  				if(item == 0){
            	  	                if (mIncidentLocation.getText().length() < 3) {
            	  	                	Util.showToast(IncidentAdd.this, R.string.less_report_location);
            	  	                }else{
            	  	                	addReports();
            	  	                }

            	  				}
            	  				
            	  				//reporting via Open GeoSMS
            	  				if(item == 1){   
            	  					//send with picture
            	  					if (Preferences.fileName != null){
            	  	                    mErrorMessage = getString(R.string.not_support_by_opengeosms);
            	  	                    showDialog(DIALOG_REPORT_PIC_VIA_INTERNET);
            	  	                }else{
            	  	                	
            	  	                	if(!Util.isAirplaneModeOn(IncidentAdd.this))
            	  	                		sendOpenGeoSMS();
            	  	                	else            	  	                		
            	  	                		Util.showToast(IncidentAdd.this, R.string.airplane_mode_on);
            	  	                }
            	  				}
            	  				

            	  			}
            	  		}).create();
            	
            }
            
            //@inoran
            case DIALOG_REPORT_OPENGEOSMS_PROGRASS:{
	            sendSMSProgressDialog = new ProgressDialog(this);
	            sendSMSProgressDialog.setTitle(getString(R.string.checkin_progress_title));
	            sendSMSProgressDialog.setMessage(getString(R.string.sending_report_in_progress));
	            sendSMSProgressDialog.setCancelable(true);	       
	            
	            sendSMSProgressDialog.setButton(getString(R.string.btn_cancel),
	                    new DialogInterface.OnClickListener() {
	
	                        public void onClick(DialogInterface dialog, int which) {
	                            Util.showToast(IncidentAdd.this, R.string.report_opengeosms_cancelled);
	                            unregisterReceiver(SMSSentReceiver);
	                            removeDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);
	                        }
	
	                    });
	            return sendSMSProgressDialog;

            }

            //@inoran
            case DIALOG_REPORT_PIC_VIA_INTERNET  :{
                 return new AlertDialog.Builder(this)
                	.setMessage(mErrorMessage)
                	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							//report via Internet with photo
							addReports();
						}
					})
					
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Util.showToast(IncidentAdd.this, R.string.report_opengeosms_without_picture);
							
							if(!Util.isAirplaneModeOn(IncidentAdd.this))
	  	                		sendOpenGeoSMS();
	  	                	else            	  	                		
	  	                		Util.showToast(IncidentAdd.this, R.string.airplane_mode_on);
						}
					}).create();
            }
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog)dialog).updateTime(mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE));
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog)dialog).updateDate(mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                break;

            case DIALOG_MULTIPLE_CATEGORY:
                final AlertDialog alert = (AlertDialog)dialog;
                final ListView list = alert.getListView();
                // been
                // selected, then uncheck
                // selected categories
                if (mVectorCategories.size() > 0) {
                    for (String s : mVectorCategories) {
                        try {
                            //@inoran fix
                            list.setItemChecked(mCategoryLength - Integer.parseInt(s), true);
                        } catch (NumberFormatException e) {
                            Log.e(CLASS_TAG, "numberFormatException " + s + " " + e.toString());
                        }
                    }
                } else {
                    list.clearChoices();
                }

                break;
                
        }
    }

    private void updateDisplay() {
        Date date = mCalendar.getTime();
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            mPickDate.setText(dateFormat.format(date));

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
            mPickTime.setText(timeFormat.format(date));

            // Because the API doesn't support dates in diff Locale mode, force
            // it to show time in US
            SimpleDateFormat submitFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            mDateToSubmit = submitFormat.format(date);
        } else {
            mPickDate.setText(R.string.incident_date);
            mPickTime.setText(R.string.incident_time);
            mDateToSubmit = null;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            updateDisplay();
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            updateDisplay();
        }
    };


    /**
     * Insert incident data into db when app is offline.
     * 
     * @author henryaddo
     */
    public long addToDb() {

        String dates[] = mDateToSubmit.split(" ");
        String time[] = dates[1].split(":");

        List<AddIncidentData> addIncidentsData = new ArrayList<AddIncidentData>();
        AddIncidentData addIncidentData = new AddIncidentData();
        addIncidentsData.add(addIncidentData);

        addIncidentData.setIncidentTitle(mIncidentTitle.getText().toString());
        addIncidentData.setIncidentDesc(mIncidentDesc.getText().toString());
        addIncidentData.setIncidentDate(dates[0]);
        addIncidentData.setIncidentHour(Integer.parseInt(time[0]));
        addIncidentData.setIncidentMinute(Integer.parseInt(time[1]));
        addIncidentData.setIncidentAmPm(dates[2].toLowerCase());
        addIncidentData.setIncidentCategories(Util.implode(mVectorCategories));
        addIncidentData.setIncidentLocName(mIncidentLocation.getText().toString());
        addIncidentData.setIncidentLocLatitude(mLatitude.getText().toString());
        addIncidentData.setIncidentLocLongitude(mLongitude.getText().toString());
        addIncidentData.setIncidentPhoto(Preferences.fileName);
        addIncidentData.setPersonFirst(Preferences.firstname);
        addIncidentData.setPersonLast(Preferences.lastname);
        addIncidentData.setPersonEmail(Preferences.email);

        // add it to database.
        return MainApplication.mDb.addIncidents(addIncidentsData);

    }
    
    
    /**
     * @inoran
     * Receiving broadcast about Open GeoSMS sending status
     * 
     */
	public class SMSSendingReceiver extends BroadcastReceiver {
				
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(CLASS_TAG, "Received SMS SENT broadcast code: " + getResultCode());
			
			 switch (getResultCode())
             {
			 	 case DEVICE_ERROR:
			 		 Log.d(CLASS_TAG, "device temporary failure");
			 		 break;
                 case Activity.RESULT_OK:
                	 Log.d(CLASS_TAG, "number of sms received segment: " + mCurSMSReceivedNumber);
                	 if(++mCurSMSReceivedNumber >= smsNum){
                		 removeDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);
                		 unregisterReceiver(SMSSentReceiver);
	                     Util.showToast(getBaseContext(), R.string.report_opengeosms_successfully_sent);
	                     draft = false;
	                     clearFields();
	                     goToReports();
                	 }
                     break;                
                 case SmsManager.RESULT_ERROR_NO_SERVICE:
                	 removeDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);
                	 unregisterReceiver(SMSSentReceiver);
                     Util.showToast(getBaseContext(), R.string.report_opengeosms_failure_sent_no_service);
                     break;
                 case SmsManager.RESULT_ERROR_RADIO_OFF:
                	 removeDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);
                	 unregisterReceiver(SMSSentReceiver);
                     Util.showToast(getBaseContext(), R.string.report_opengeosms_failure_sent_radio_off);
                     break;
                 case SmsManager.RESULT_ERROR_GENERIC_FAILURE:    
                 case SmsManager.RESULT_ERROR_NULL_PDU:    
                 default:
                	 removeDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);
                	 unregisterReceiver(SMSSentReceiver);
                	 Util.showToast(getBaseContext(), R.string.report_opengeosms_failure_sent);
                	                      
             }
		}
	}
	
    
    /**
     * @inoran
     * Send report by Open GeoSMS.
     * 
     */
    public void sendOpenGeoSMS() {
    	
        Log.d(CLASS_TAG, "sendOpenGeoSMS(): sending report to server");
        if (TextUtils.isEmpty(Preferences.domain) || Preferences.domain.equalsIgnoreCase("http://")) {
        	 Util.showToast(this, R.string.deployment_domain_error);
        }
        
        String categories = Util.implode(mVectorCategories);
        Log.d(CLASS_TAG, "AM: PM " + categories);
        
        StringBuilder urlBuilder = new StringBuilder(Preferences.domain);

        String phoneNumber = mPhoneNumber.getText().toString();
        String latitude = mLatitude.getText().toString();
        String longitude = mLongitude.getText().toString();
        
        mParams.put("task", "report");
        mParams.put("incident_title", mIncidentTitle.getText().toString());
        mParams.put("incident_description", mIncidentDesc.getText().toString());        
        mParams.put("incident_datetime", mDateToSubmit);        
        mParams.put("incident_category", categories);
        mParams.put("location_name", mIncidentLocation.getText().toString());        
        mParams.put("person_first", Preferences.firstname);
        mParams.put("person_last", Preferences.lastname);
        mParams.put("person_email", Preferences.email);
        

        try {
        	smsNum = new OpenGeoSMSSender().SendOpenGeoSMS(phoneNumber, urlBuilder.toString(), latitude, longitude, mParams, this);
          
        	if(smsNum != 0){
	            SMSSentReceiver = new SMSSendingReceiver();
	         	registerReceiver(SMSSentReceiver, new IntentFilter("OPENGEOSMS_SENT"));
	            //reset
	         	mCurSMSReceivedNumber = 0;
	            
	            showDialog(DIALOG_REPORT_OPENGEOSMS_PROGRASS);            
	            Log.d(CLASS_TAG, "Waiting for receving Open GeoSMS sending statuses");
        	}else
        		Util.showToast(IncidentAdd.this, R.string.report_opengeosms_failure_sent);
        } catch (IOException e) {
            Log.d(CLASS_TAG, "sendOpenGeoSMS(): IO exception failed to submit report "
                    + Preferences.domain);
            e.printStackTrace();
        }

    }
    

    /**
     * Post directly to online.
     * 
     * @author henryaddo
     */
    public int postToOnline() {
        Log.d(CLASS_TAG, "postToOnline(): posting report to online");
        if (TextUtils.isEmpty(Preferences.domain) || Preferences.domain.equalsIgnoreCase("http://")) {
            return 12;
        }

        String dates[] = mDateToSubmit.split(" ");
        String time[] = dates[1].split(":");

        String categories = Util.implode(mVectorCategories);
        Log.d(CLASS_TAG, "AM: PM " + categories);
        StringBuilder urlBuilder = new StringBuilder(Preferences.domain);
        urlBuilder.append("/api");

        mParams.put("task", "report");
        mParams.put("incident_title", mIncidentTitle.getText().toString());
        mParams.put("incident_description", mIncidentDesc.getText().toString());
        mParams.put("incident_date", dates[0]);
        mParams.put("incident_hour", time[0]);
        mParams.put("incident_minute", time[1]);
        mParams.put("incident_ampm", dates[2].toLowerCase());
        mParams.put("incident_category", categories);
        mParams.put("latitude", mLatitude.getText().toString());
        mParams.put("longitude", mLongitude.getText().toString());
        mParams.put("location_name", mIncidentLocation.getText().toString());
        mParams.put("person_first", Preferences.firstname);
        mParams.put("person_last", Preferences.lastname);
        mParams.put("person_email", Preferences.email);
        mParams.put("filename", Preferences.fileName);

        try {
            final int status = MainHttpClient.PostFileUpload(urlBuilder.toString(), mParams);
            Log.i(CLASS_TAG, "Statuses: " + status);
            return status;
        } catch (IOException e) {
            Log.d(CLASS_TAG, "postToOnline(): IO exception failed to submit report "
                    + Preferences.domain);
            e.printStackTrace();
            return 13;
        }

    }

    /**
     * Upon being resumed we can retrieve the current state. This allows us to
     * update the state if it was changed at any time while paused.
     */
    @Override
    protected void onResume() {
        this.setSavedReport();
        getSharedText();
        super.onResume();
    }

    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    protected void goHome(Context context) {
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void onSearchDeployments(View v) {
        Intent intent = new Intent(IncidentAdd.this, DeploymentSearch.class);
        startActivityForResult(intent, VIEW_SEARCH);
        setResult(RESULT_OK);
    }

    /**
     * Any time we are paused we need to save away the current state, so it will
     * be restored correctly when we are resumed.
     */
    @Override
    protected void onPause() {
        if (reverseGeocoderTask != null) {
            reverseGeocoderTask.cancel(true);
        }

        final String selectedCategories = getSelectedCategories();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("phonenumber", mPhoneNumber.getText().toString());
        editor.putString("title", mIncidentTitle.getText().toString());
        editor.putString("description", mIncidentDesc.getText().toString());
        editor.putString("location", mIncidentLocation.getText().toString());
        editor.putString("latitude", mLatitude.getText().toString());
        editor.putString("longitude", mLongitude.getText().toString());

        if (selectedCategories != null) {
            editor.putString("categories", selectedCategories);
        }
        editor.putString("photo", Preferences.fileName);
        editor.commit();
        // Notify user that report has been saved as draft.
        if (draft) {
            Util.showToast(this, R.string.message_saved_as_draft);
        }
        super.onPause();
    }

    /**
     * This saves unsent reports as draft
     */
    private void setSavedReport() {
        // get the categories
        showCategories();
        SharedPreferences prefs = getPreferences(0);
        String phonenumber = prefs.getString("phonenumber", null);
        //@inoran
        //phonenubmer exist in SharedPreference
        if(phonenumber != null){
        	if(phonenumber.length() != 0)
        		mPhoneNumber.setText(phonenumber, TextView.BufferType.EDITABLE);
        	//if phonenumber is empty, retrieving phone number from preference of setting
        	else{
            	mPhoneNumber.setText(Preferences.phonenumber);
            }
        }
        String title = prefs.getString("title", null);
        if (title != null) {
            mIncidentTitle.setText(title, TextView.BufferType.EDITABLE);
        }
        String description = prefs.getString("description", null);
        if (description != null) {
            mIncidentDesc.setText(description, TextView.BufferType.EDITABLE);
        }

        String location = prefs.getString("location", null);
        if (location != null) {
            mIncidentLocation.setText(location, TextView.BufferType.EDITABLE);
        }

        String latitude = prefs.getString("latitude", null);
        if (latitude != null) {
            mLatitude.setText(latitude, TextView.BufferType.EDITABLE);
        }

        String longitude = prefs.getString("longitude", null);
        if (longitude != null) {
            mLongitude.setText(longitude, TextView.BufferType.EDITABLE);
        }

        String categories = prefs.getString("categories", null);
        //@inoran fix
        //if (categories != null) {        
        if(categories != "" && categories != null){
            String[] splitter = categories.split(",");

            // clear any existing categories
            mVectorCategories.clear();

            for (String s : splitter) {
                mVectorCategories.add(s.trim());

            }
            this.setSelectedCategories(mVectorCategories);
        }
       
        String photo = prefs.getString("photo", null);
        if (photo != null) {
            Preferences.fileName = photo;
            NetworkServices.fileName = photo;
            Bitmap bitmap = BitmapFactory.decodeFile(photo);
            if (bitmap != null) {
                Log.i(CLASS_TAG,
                        String.format("Photo %dx%d", bitmap.getWidth(), bitmap.getHeight()));
                mSelectedPhoto.setImageBitmap(bitmap);
                mSelectedPhoto.setMinimumHeight(mSelectedPhoto.getWidth() * bitmap.getHeight()
                        / bitmap.getWidth());
                mBtnPicture.setText(R.string.change_photo);
            } else {
                mSelectedPhoto.setImageBitmap(null);
                mSelectedPhoto.setMinimumHeight(0);
                mBtnPicture.setText(R.string.btn_add_photo);
                
                //@inoran add to fix
                Preferences.fileName = null;
                NetworkServices.fileName = null;
            }
        } else {
            Preferences.fileName = null;
            NetworkServices.fileName = null;
        }

    }

    /*
     * Implementation of MapUserLocation abstract methods
     */
    protected void locationChanged(double latitude, double longitude) {
        updateMarker(latitude, longitude, true);
        if (!mLatitude.hasFocus() && !mLongitude.hasFocus()) {
            mLatitude.setText(String.valueOf(latitude));
            mLongitude.setText(String.valueOf(longitude));
        }
        if (reverseGeocoderTask == null || !reverseGeocoderTask.isExecuting()) {
            reverseGeocoderTask = new ReverseGeocoderTask(this);
            reverseGeocoderTask.execute(latitude, longitude);
        }
    }

    /**
     * Asynchronous Reverse Geocoder Task
     */
    private class ReverseGeocoderTask extends GeocoderTask {

        public ReverseGeocoderTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(getClass().getSimpleName(), String.format("onPostExecute %s", result));
            if (TextUtils.isEmpty(mIncidentLocation.getText().toString()))
                mIncidentLocation.setText(result);
            executing = false;
        }
    }

    private TextWatcher latLonTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (mLatitude.hasFocus() || mLongitude.hasFocus()) {
                    locationChanged(Double.parseDouble(mLatitude.getText().toString()),
                            Double.parseDouble(mLongitude.getText().toString()));
                }
            } catch (Exception ex) {
                Log.w("IncidentAdd", "Exception TextWatcher", ex);
            }
        }
    };

    /**
     * Sets nVectorCategories
     * 
     * @param aVectorCategories categories
     */
    public void setVectorCategories(Vector<String> aVectorCategories) {
        mVectorCategories = aVectorCategories;
    }

    /**
     * Sets the selected categories for submission
     * 
     * @param aSelectedCategories
     */
    private void setSelectedCategories(Vector<String> aSelectedCategories) {
        // clear
        mBtnAddCategory.setText(R.string.incident_add_category);
        if (aSelectedCategories.size() > 0) {
            StringBuilder categories = new StringBuilder();
            for (String category : aSelectedCategories) {
                if (categories.length() > 0) {
                    categories.append(", ");
                }
                if (!TextUtils.isEmpty(category)) {
                    categories.append(mCategoriesTitle.get(category));
                }
            }

            if (!TextUtils.isEmpty(categories.toString())) {
                mBtnAddCategory.setText(categories.toString());
            } else {

                mBtnAddCategory.setText(R.string.incident_add_category);
            }
        }
    }

    /**
     * Get the selected categories as a csv
     * 
     * @param aSelectedCategories
     */
    private String getSelectedCategories() {
        if (mVectorCategories != null) {
            if (mVectorCategories.size() > 0) {
                StringBuilder categories = new StringBuilder();
                for (String catetory : mVectorCategories) {
                    if (categories.length() > 0) {
                        categories.append(", ");
                    }
                    categories.append(catetory);
                }
                return categories.toString();
            }
        }
        return null;
    }

    // thread class
    private class AddReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        protected Context appContext;

        protected ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(IncidentAdd.this);
            this.progressDialog.setTitle(getString(R.string.checkin_progress_title));
            this.progressDialog.setMessage(getString(R.string.sending_report_in_progress));
            this.progressDialog.setCancelable(true);
            this.progressDialog.setButton(getString(R.string.btn_cancel),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // add to db
                            addToDb();
                            clearFields();
                            Util.showToast(appContext, R.string.report_successfully_added_offline);
                            draft = false;
                            dialog.cancel();
                        }

                    });
            this.progressDialog.show();
        }
        
        @Override
        protected Integer doInBackground(Void... mParams) {
	        	if (Util.isConnected(IncidentAdd.this)) {
	                status = postToOnline();
	
	                if (status > 0) {
	                    addToDb();
	                    draft = false;
	                }
	            } else {
	                addToDb();
	                draft = false;
	                status = 14; // no internet connection
	            }
           
           return status;
        }

        /**
         * Upload files to server 0 - success, 1 - missing parameter, 2 - invalid
         * parameter, 3 - post failed, 5 - access denied, 6 - access limited, 7 - no
         * data, 8 - api disabled, 9 - no task found, 10 - json is wrong
         */
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.cancel();
            if (result == 14) {
                clearFields();
                Util.showToast(appContext, R.string.report_successfully_added_offline);
            } else if (result == 1 || result == 3) {
                clearFields();
                draft = false;
                Util.showToast(appContext, R.string.failed_to_add_report_offline);
            } else if (result == 0) {
                draft = false;
                clearFields();
                // after a successful upload, delete the file
                if (Preferences.fileName != null) {
                    File file = new File(Preferences.fileName);
                    if (file.exists() && file.delete()) {
                        Log.i(getClass().getSimpleName(), "File deleted " + file.getName());
                    }
                }

                Util.showToast(appContext, R.string.report_successfully_added_online);
                goToReports();
            } else {
                clearFields();
                draft = false;
                Util.showToast(appContext, R.string.report_successfully_added_offline);
            }

        }
    }
}
