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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.ushahidi.android.app.data.AddIncidentData;
import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.net.UshahidiHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddIncident extends Activity {

    /**
     * Name of category that exists on the phone before any connection to a server
     */
    private static final String GENERIC_CATEGORY_NAME = "GENERIC";
    
    private static final int HOME = Menu.FIRST + 1;

    private static final int LIST_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_MAP = Menu.FIRST + 3;

    private static final int INCIDENT_REFRESH = Menu.FIRST + 4;

    private static final int SETTINGS = Menu.FIRST + 5;

    private static final int ABOUT = Menu.FIRST + 6;

    private static final int GOTOHOME = 0;

    private static final int MAP_INCIDENTS = 1;

    private static final int LIST_INCIDENTS = 2;

    private static final int REQUEST_CODE_SETTINGS = 2;

    private static final int REQUEST_CODE_ABOUT = 3;

    private static final int REQUEST_CODE_IMAGE = 4;

    private static final int REQUEST_CODE_CAMERA = 5;

    private static final int VIEW_MAP = 1;

    private Geocoder mGc;

    private List<Address> mFoundAddresses;

    // date and time
    private int mYear;

    private int mMonth;

    private int mDay;

    private int mHour;

    private int mMinute;

    private int mCounter = 0;

    private static double sLongitude;

    private static double sLatitude;

    private String mErrorMessage = "";

    private String mDateToSubmit = "";

    private String mFilename = "";

    private boolean mError = false;

    private EditText mIncidentTitle;

    private EditText mIncidentLocation;

    private EditText mIncidentDesc;

    private TextView mIncidentDate;

    private ImageView mSelectedPhoto;

    private TextView mSelectedCategories;

    private Button mBtnSend;

    private Button mBtnCancel;

    private Button mBtnAddCategory;

    private Button mPickTime;

    private Button mPickDate;

    private Button mBtnPicture;

    private Button mAddLocation;

    private HashMap<Integer, Integer> mTimeDigits;

    private Bundle mBundle;

    private Bundle mExtras;

    private static final int DIALOG_ERROR_NETWORK = 0;

    private static final int DIALOG_ERROR_SAVING = 1;

    private static final int DIALOG_CHOOSE_IMAGE_METHOD = 2;

    private static final int DIALOG_MULTIPLE_CATEGORY = 3;

    private static final int TIME_DIALOG_ID = 4;

    private static final int DATE_DIALOG_ID = 5;

    private final static Handler mHandler = new Handler();

    private Vector<String> mVectorCategories = new Vector<String>();

    private Vector<String> mCategoriesId = new Vector<String>();

    private HashMap<String, String> mCategoriesTitle = new HashMap<String, String>();

    private HashMap<String, String> mParams = new HashMap<String, String>();

    public static final String PREFS_NAME = "UshahidiPref";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.add_incident);
        mFoundAddresses = new ArrayList<Address>();
       
        if(mVectorCategories.isEmpty()){
            mVectorCategories.add(GENERIC_CATEGORY_NAME);
        }

        mGc = new Geocoder(this);

        // load settings
        UshahidiPref.loadSettings(AddIncident.this);

        initComponents();

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

        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.ushahidi_home);

        i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list);
        i.setIcon(R.drawable.ushahidi_list);

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.ushahidi_map);

        i = menu.add(Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.incident_menu_refresh);
        i.setIcon(R.drawable.ushahidi_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.ushahidi_about);

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent launchPreferencesIntent;
        switch (item.getItemId()) {

            case LIST_INCIDENT:
                launchPreferencesIntent = new Intent(AddIncident.this, ListIncidents.class);
                startActivityForResult(launchPreferencesIntent, LIST_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case INCIDENT_MAP:
                launchPreferencesIntent = new Intent(AddIncident.this, ViewIncidents.class);
                startActivityForResult(launchPreferencesIntent, MAP_INCIDENTS);
                return true;

            case HOME:
                launchPreferencesIntent = new Intent(AddIncident.this, Ushahidi.class);
                startActivityForResult(launchPreferencesIntent, GOTOHOME);
                setResult(RESULT_OK);
                return true;

            case ABOUT:
                launchPreferencesIntent = new Intent(AddIncident.this, About.class);
                startActivityForResult(launchPreferencesIntent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                launchPreferencesIntent = new Intent().setClass(AddIncident.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
                return true;

        }
        return false;
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        mBtnPicture = (Button)findViewById(R.id.btnPicture);
        mBtnAddCategory = (Button)findViewById(R.id.add_category);
        mBtnSend = (Button)findViewById(R.id.incident_add_btn);
        mBtnCancel = (Button)findViewById(R.id.incident_add_cancel);
        mIncidentDate = (TextView)findViewById(R.id.lbl_date);
        mPickDate = (Button)findViewById(R.id.pick_date);
        mPickTime = (Button)findViewById(R.id.pick_time);
        mAddLocation = (Button)findViewById(R.id.location);

        mSelectedPhoto = (ImageView)findViewById(R.id.sel_photo_prev);
        mSelectedCategories = (TextView)findViewById(R.id.lbl_category);

        mIncidentTitle = (EditText)findViewById(R.id.incident_title);
        mIncidentLocation = (EditText)findViewById(R.id.incident_location);

        mIncidentTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(mIncidentTitle.getText())) {
                        mIncidentTitle.setError(getString(R.string.empty_report_title));
                    }
                }

            }

        });

        mIncidentLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(mIncidentLocation.getText())) {
                        mIncidentLocation.setError(getString(R.string.empty_report_location));
                    }
                }
            }
        });

        mIncidentDesc = (EditText)findViewById(R.id.incident_desc);
        mIncidentDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (TextUtils.isEmpty(mIncidentDesc.getText())) {
                        mIncidentDesc.setError(getString(R.string.empty_report_description));
                    }
                }
            }

        });

        // open location map window
        mAddLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(AddIncident.this, LocationMap.class);
                startActivityForResult(intent, VIEW_MAP);
                setResult(RESULT_OK, intent);

            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Dipo Fix
                mError = false;
                if (TextUtils.isEmpty(mIncidentTitle.getText())) {
                    mErrorMessage = getString(R.string.empty_report_title);
                    mError = true;
                }

                if (TextUtils.isEmpty(mIncidentDesc.getText())) {
                    mErrorMessage += getString(R.string.empty_report_description);
                    mError = true;
                }

                if (TextUtils.isEmpty(mIncidentLocation.getText())) {
                    mErrorMessage += getString(R.string.empty_report_location);
                    mError = true;
                }

                // Dipo Fix
                if (mVectorCategories.size() == 0) {
                    mErrorMessage += getString(R.string.empty_report_categories);
                    mError = true;
                }

                if (!mError) {

                    AddReportsTask addReportsTask = new AddReportsTask();
                    addReportsTask.appContext = AddIncident.this;
                    addReportsTask.execute();

                } else {
                    final Toast t = Toast.makeText(AddIncident.this, "Error!\n\n" + mErrorMessage,
                            Toast.LENGTH_LONG);
                    t.show();
                    mErrorMessage = "";
                }

            }
        });

        mBtnPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_CHOOSE_IMAGE_METHOD);
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
                Intent intent = new Intent(AddIncident.this, Ushahidi.class);
                startActivityForResult(intent, GOTOHOME);
                setResult(RESULT_OK);
                finish();
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

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        c.get(Calendar.AM_PM);
        updateDisplay();

    }

    // fetch categories
    public String[] showCategories() {
        Cursor cursor = UshahidiApplication.mDb.fetchAllCategories();

        String categories[] = new String[cursor.getCount()];

        int i = 0;
        if (cursor.moveToFirst()) {

            int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CATEGORY_TITLE);

            int idIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CATEGORY_ID);

            do {
                categories[i] = cursor.getString(titleIndex);
                mCategoriesTitle.put(String.valueOf(cursor.getInt(idIndex)),
                        cursor.getString(titleIndex));
                mCategoriesId.add(String.valueOf(cursor.getInt(idIndex)));
                i++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categories;

    }

    // reset records in the field
    private void clearFields() {
        mBtnPicture = (Button)findViewById(R.id.btnPicture);
        mBtnAddCategory = (Button)findViewById(R.id.add_category);
        mIncidentTitle.setText("");
        mIncidentLocation.setText("");
        mIncidentDesc.setText("");
        mVectorCategories.clear();
        mSelectedCategories.setText("");
        mCounter = 0;
        updateDisplay();

        // clear persistent data
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("title", "");
        editor.putString("desc", "");
        editor.putString("date", "");
        editor.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        switch (requestCode) {

            case REQUEST_CODE_CAMERA:
                if (resultCode != RESULT_OK) {
                    return;
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // pull
                                                                                      // it
                                                                                      // out
                                                                                      // of
                                                                                      // landscape
                                                                                      // mode
                mBundle = null;
                mExtras = data.getExtras();
                if (mExtras != null)
                    mBundle = mExtras.getBundle("filename");

                if (mBundle != null && !mBundle.isEmpty()) {
                    UshahidiPref.fileName = mBundle.getString("name");
                    mSelectedPhoto.setImageDrawable(ImageManager.getImages(UshahidiPref.fileName));
                }
                break;

            case REQUEST_CODE_IMAGE:

                if (resultCode != RESULT_OK) {
                    return;
                }

                Uri uri = data.getData();
                Bitmap b = null;

                try {
                    b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (FileNotFoundException e) {
                    break;
                } catch (IOException e) {
                    break;
                }

                ByteArrayOutputStream byteArrayos = new ByteArrayOutputStream();

                try {
                    b.compress(CompressFormat.JPEG, 75, byteArrayos);
                    byteArrayos.flush();
                } catch (OutOfMemoryError e) {
                    break;
                } catch (IOException e) {
                    break;
                }

                mFilename = "android_pic_upload" + randomString() + ".jpg";
                ImageManager.writeImage(byteArrayos.toByteArray(), mFilename);
                UshahidiPref.fileName = mFilename;
                mSelectedPhoto.setImageBitmap(ImageManager.getBitmap(UshahidiPref.fileName));
                break;

            case VIEW_MAP:
                if (resultCode != RESULT_OK) {
                    return;
                }

                mBundle = null;
                mExtras = data.getExtras();
                if (mExtras != null)
                    mBundle = mExtras.getBundle("locations");

                if (mBundle != null && !mBundle.isEmpty()) {
                    mIncidentLocation.setText(mBundle.getString("location"));

                    AddIncident.sLatitude = mBundle.getDouble("latitude");
                    AddIncident.sLongitude = mBundle.getDouble("longitude");
                }
                break;
        }
    }

    private static Random random = new Random();

    protected static String randomString() {
        return Long.toString(random.nextLong(), 10);
    }

    //
    final Runnable mSentIncidentOffline = new Runnable() {
        public void run() {
            if (addToDb() == -1) {
                mHandler.post(mSentIncidentFail);
            } else {
                mHandler.post(mSentIncidentOfflineSuccess);
                // clearFields();
            }
        }
    };

    final Runnable mSentIncidentFail = new Runnable() {
        public void run() {
            Util.showToast(AddIncident.this, R.string.failed_to_add_report_online);
        }
    };

    final Runnable mSentIncidentOfflineFail = new Runnable() {
        public void run() {
            Util.showToast(AddIncident.this, R.string.failed_to_add_report_offline);
        }
    };

    final Runnable mSentIncidentOfflineSuccess = new Runnable() {
        public void run() {
            Util.showToast(AddIncident.this, R.string.report_successfully_added_offline);

        }
    };

    //
    final Runnable mSendIncidentOnline = new Runnable() {
        public void run() {
            if (!postToOnline()) {
                mHandler.post(mSentIncidentFail);
            } else {
                mHandler.post(mSentIncidentSuccess);

            }
        }
    };

    //
    final Runnable mSentIncidentSuccess = new Runnable() {
        public void run() {
            Util.showToast(AddIncident.this, R.string.report_successfully_added_online);

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

                        Intent launchPreferencesIntent = new Intent().setClass(AddIncident.this,
                                ImageCapture.class);

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
                        .setMultiChoiceItems(showCategories(), null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                            boolean isChecked) {

                                        if (isChecked) {

                                            mVectorCategories.add(mCategoriesId.get(whichButton));
                                            if (!mVectorCategories.isEmpty()) {
                                                mSelectedCategories.setText(Util.limitString(
                                                        mCategoriesTitle.get(mVectorCategories
                                                                .get(0)), 15));
                                            }
                                            mError = false;
                                        } else {
                                            // fixed a crash here.
                                            mVectorCategories.remove(mCategoriesId.get(whichButton));

                                            if (mVectorCategories.isEmpty()) {
                                                mSelectedCategories.setText("");
                                            } else {
                                                mSelectedCategories.setText(Util.limitString(
                                                        mCategoriesTitle.get(mVectorCategories
                                                                .get(0)), 15));
                                            }
                                        }

                                        /*
                                         * User clicked on a check box do some
                                         * stuff
                                         */
                                    }
                                })
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked Yes so do some stuff */
                            }
                        }).create();
            }

            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);

            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog)dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog)dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }

    private void updateDisplay() {
        String amPm;
        mTimeDigits = new HashMap<Integer, Integer>();

        mTimeDigits.put(00, 12);
        mTimeDigits.put(13, 1);
        mTimeDigits.put(14, 2);
        mTimeDigits.put(15, 3);
        mTimeDigits.put(16, 4);
        mTimeDigits.put(17, 5);
        mTimeDigits.put(18, 6);
        mTimeDigits.put(19, 7);
        mTimeDigits.put(20, 8);
        mTimeDigits.put(21, 9);
        mTimeDigits.put(22, 10);
        mTimeDigits.put(23, 11);
        mTimeDigits.put(24, 12);
        mTimeDigits.put(12, 12);
        mTimeDigits.put(1, 1);
        mTimeDigits.put(2, 2);
        mTimeDigits.put(3, 3);
        mTimeDigits.put(4, 4);
        mTimeDigits.put(5, 5);
        mTimeDigits.put(6, 6);
        mTimeDigits.put(7, 7);
        mTimeDigits.put(8, 8);
        mTimeDigits.put(9, 9);
        mTimeDigits.put(10, 10);
        mTimeDigits.put(11, 11);
        mTimeDigits.put(12, 12);
        if (mHour >= 12)
            amPm = "PM";
        else
            amPm = "AM";

        String strDate = new StringBuilder()

                // Month is 0 based so add 1
                .append(mYear).append("-").append(pad(mMonth + 1)).append("-").append(pad(mDay))
                .toString();

        String dateTime = Util.formatDate("yyyy-MM-dd", strDate, "MMMM dd, yyyy");

        mIncidentDate.setText(dateTime + " at " + pad(mTimeDigits.get(mHour)) + ":" + pad(mMinute)
                + " " + amPm);

        mDateToSubmit = new StringBuilder()
                // Month is 0 based so add 1
                .append(pad(mMonth + 1)).append("/").append(pad(mDay)).append("/").append(mYear)
                .append(" ").append(pad(mTimeDigits.get(mHour))).append(":").append(pad(mMinute))
                .append(" ").append(amPm).toString();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

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
        addIncidentData.setIncidentAmPm(dates[2]);
        addIncidentData.setIncidentCategories(Util.implode(mVectorCategories));
        addIncidentData.setIncidentLocName(mIncidentLocation.getText().toString());
        addIncidentData.setIncidentLocLatitude(String.valueOf(sLatitude));
        addIncidentData.setIncidentLocLongitude(String.valueOf(sLongitude));
        addIncidentData.setIncidentPhoto(UshahidiPref.fileName);
        addIncidentData.setPersonFirst(UshahidiPref.firstname);
        addIncidentData.setPersonLast(UshahidiPref.lastname);
        addIncidentData.setPersonEmail(UshahidiPref.email);

        // add it to database.
        return UshahidiApplication.mDb.addIncidents(addIncidentsData);

    }

    /**
     * Post directly to online.
     * 
     * @author henryaddo
     */
    public boolean postToOnline() {

        String dates[] = mDateToSubmit.split(" ");
        String time[] = dates[1].split(":");
        String categories = Util.implode(mVectorCategories);

        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        StringBuilder urlBuilder = new StringBuilder(settings.getString("Domain", ""));
        urlBuilder.append("/api");

        mParams.put("task", "report");
        mParams.put("incident_title", mIncidentTitle.getText().toString());
        mParams.put("incident_description", mIncidentDesc.getText().toString());
        mParams.put("incident_date", dates[0]);
        mParams.put("incident_hour", time[0]);
        mParams.put("incident_minute", time[1]);
        mParams.put("incident_ampm", dates[2].toLowerCase());
        mParams.put("incident_category", categories);
        mParams.put("latitude", String.valueOf(sLatitude));
        mParams.put("longitude", String.valueOf(sLongitude));
        mParams.put("location_name", mIncidentLocation.getText().toString());
        mParams.put("person_first", UshahidiPref.firstname);
        mParams.put("person_last", UshahidiPref.lastname);
        mParams.put("person_email", UshahidiPref.email);
        mParams.put("filename", UshahidiPref.fileName);

        try {
            return UshahidiHttpClient.PostFileUpload(urlBuilder.toString(), mParams);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Upon being resumed we can retrieve the current state. This allows us to
     * update the state if it was changed at any time while paused.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(0);
        String title = prefs.getString("title", null);
        String desc = prefs.getString("desc", null);
        // String date = prefs.getString("date", null);

        if (title != null)
            mIncidentTitle.setText(title, TextView.BufferType.EDITABLE);

        if (desc != null)
            mIncidentDesc.setText(desc, TextView.BufferType.EDITABLE);

        // if( date != null )
        // mIncidentDate.setText(date, TextView.BufferType.EDITABLE);

    }

    /**
     * Any time we are paused we need to save away the current state, so it will
     * be restored correctly when we are resumed.
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("title", mIncidentTitle.getText().toString());
        editor.putString("desc", mIncidentDesc.getText().toString());
        // editor.putString("date", mIncidentDate.getText().toString());
        editor.commit();
    }

    /**
     * Geocode user entered location name.
     * 
     * @param String - the location to be geocoded
     * @return int - 0 on success, 1 network failure, 2 couldn't geocode wrong
     *         location name
     */
    public int geocodeLocationName(String locationName) {
        if (Util.isConnected(AddIncident.this)) {
            try {
                mFoundAddresses = mGc.getFromLocationName(locationName, 5);
                Address address = mFoundAddresses.get(0);
                AddIncident.sLatitude = address.getLatitude();
                AddIncident.sLongitude = address.getLongitude();

            } catch (IOException e) {
                return 2;
            }
        }
        return 1;
    }

    // thread class
    private class AddReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        protected Context appContext;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }

        @Override
        protected Integer doInBackground(Void... mParams) {
            if (Util.isConnected(AddIncident.this)) {

                if (!postToOnline()) {
                    status = 1; // fail
                } else {

                    status = 0; // success
                }
            } else {
                addToDb();
                status = 2; // no internet connection
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 2) {
                clearFields();
                Util.showToast(appContext, R.string.report_successfully_added_offline);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.failed_to_add_report_online);
            } else if (result == 0) {
                clearFields();
                // after a successful upload, delete the file
                File f = new File(UshahidiPref.savePath + UshahidiPref.fileName);
                if (f.exists()) {
                    f.delete();
                }
                Util.showToast(appContext, R.string.report_successfully_added_online);
            }
            setProgressBarIndeterminateVisibility(false);
        }
    }
}
