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

import com.ushahidi.android.app.checkin.CheckinActivity;
import com.ushahidi.android.app.checkin.NetworkServices;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Ushahidi extends Activity {
    /** Called when the activity is first created. */
    private static final int ADD_INCIDENT = Menu.FIRST + 1;

    private static final int LIST_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_MAP = Menu.FIRST + 3;

    private static final int SETTINGS = Menu.FIRST + 4;

    private static final int ABOUT = Menu.FIRST + 5;

    private static final int SYNC = Menu.FIRST + 6;

    private static final int LIST_INCIDENTS = 0;

    private static final int MAP_INCIDENTS = 1;

    private static final int ADD_INCIDENTS = 2;

    private static final int INCIDENTS = 3;

    private static final int VIEW_SETTINGS = 4;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int REQUEST_CODE_ABOUT = 2;

    private static final int DIALOG_PROMPT = 0;

    private static final int DIALOG_ERROR = 1;

    private Handler mHandler;

    private Button listBtn;

    private Button addBtn;

    private Button settingsBtn;

    private String dialogErrorMsg = "An error occurred fetching the reports. "
            + "Make sure you have entered an Ushahidi instance.";

    private Bundle bundle;

    // Checkin specific variables and functions

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        mHandler = new Handler();
        bundle = new Bundle();

        // load settings
        UshahidiPref.loadSettings(this);

        // check if domain has been set
        if (UshahidiPref.domain.length() == 0 || UshahidiPref.domain.equals("http://")) {

            // means this is a new install or the settings have been corrupted,
            // prompt them!
            mHandler.post(mDisplayPrompt);
            // This return statement had to be commented out bcos it will not
            // allow the initialisation of the buttons below.
            // return;

        }

        listBtn = (Button)findViewById(R.id.incident_list);
        addBtn = (Button)findViewById(R.id.incident_add);
        settingsBtn = (Button)findViewById(R.id.incident_map);
   
        // this for now
        initializeUI();

    }

    @Override
    public void onResume() {
        super.onResume();
        initializeUI();
    }

    /**
     * Initializes some of the UI components and test
     */
    public void initializeUI() {
        UshahidiPref.loadSettings(this);
        // This is to temporarily disable reports stuff
        if (UshahidiPref.isCheckinEnabled == 1) {
            addBtn.setText(getString(R.string.checkin_btn));
            listBtn.setText(getString(R.string.checkin_list));
        }

        listBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Ushahidi.this, IncidentsTab.class);
                startActivityForResult(intent, INCIDENTS);
                setResult(RESULT_OK);

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Ushahidi.this, Settings.class);
                startActivityForResult(intent, VIEW_SETTINGS);
                setResult(RESULT_OK);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (UshahidiPref.isCheckinEnabled == 1) {
                    NetworkServices.fileName = "";
                    Intent checkinActivityIntent = new Intent().setClass(Ushahidi.this,
                            CheckinActivity.class);
                    startActivity(checkinActivityIntent);
                    setResult(RESULT_OK);
                } else {
                    Intent intent = new Intent(Ushahidi.this, AddIncident.class);
                    startActivityForResult(intent, ADD_INCIDENTS);
                    setResult(RESULT_OK);
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROMPT: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle("Ushahidi Setup");
                dialog.setMessage("Setup an ushahidi instance.");
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent launchPreferencesIntent = new Intent().setClass(Ushahidi.this,
                                Settings.class);

                        // Make it a subactivity so we know when it returns
                        startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }

            case DIALOG_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(R.string.alert_dialog_error_title);
                dialog.setMessage(dialogErrorMsg);
                dialog.setButton2("Ok", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent launchPreferencesIntent = new Intent(Ushahidi.this, Settings.class);

                        // Make it a subactivity so we know when it returns
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

    final Runnable mDisplayPrompt = new Runnable() {
        public void run() {
            showDialog(DIALOG_PROMPT);
        }
    };

    final Runnable mDisplayErrorPrompt = new Runnable() {
        public void run() {
            showDialog(DIALOG_ERROR);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                if (resultCode != RESULT_OK) {
                    break;
                }
                break;
        }
    }

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
        // applyMenuChoice(item);

        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }

    private void populateMenu(Menu menu) {
        MenuItem i;

        i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add);
        i.setIcon(R.drawable.ushahidi_add);

        i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list);
        i.setIcon(R.drawable.ushahidi_list);

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.ushahidi_map);

        i = menu.add(Menu.NONE, SYNC, Menu.NONE, R.string.menu_sync);
        i.setIcon(R.drawable.ushahidi_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.ushahidi_about);

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent launchIntent;
        switch (item.getItemId()) {
            case LIST_INCIDENT:
                bundle.putInt("tab_index", 0);
                launchIntent = new Intent(Ushahidi.this, IncidentsTab.class);
                launchIntent.putExtra("tab", bundle);
                startActivityForResult(launchIntent, LIST_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case INCIDENT_MAP:
                bundle.putInt("tab_index", 1);
                launchIntent = new Intent(Ushahidi.this, IncidentsTab.class);
                launchIntent.putExtra("tab", bundle);
                startActivityForResult(launchIntent, MAP_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case ADD_INCIDENT:
                launchIntent = new Intent(Ushahidi.this, AddIncident.class);
                startActivityForResult(launchIntent, ADD_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case ABOUT:
                launchIntent = new Intent(Ushahidi.this, About.class);
                startActivityForResult(launchIntent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                launchIntent = new Intent().setClass(Ushahidi.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(launchIntent, REQUEST_CODE_SETTINGS);
                setResult(RESULT_OK);
                return true;

            case SYNC:
                ReportsTask reportsTask = new ReportsTask();
                reportsTask.appContext = this;
                reportsTask.execute();
                return true;

        }
        return false;
    }

    // thread class
    private class ReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        private ProgressDialog dialog;

        protected Context appContext;

        @Override
        protected void onPreExecute() {
            this.dialog = ProgressDialog.show(appContext, getString(R.string.please_wait),
                    getString(R.string.fetching_new_reports), true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            status = Util.processReports(appContext);
            Util.checkForCheckin(appContext);
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 4) {
                Util.showToast(appContext, R.string.internet_connection);
            } else if (result == 3) {
                Util.showToast(appContext, R.string.invalid_ushahidi_instance);
            } else if (result == 2) {
                Util.showToast(appContext, R.string.could_not_fetch_reports);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.could_not_fetch_reports);
            } else {
                Util.showToast(appContext, R.string.reports_successfully_fetched);
            }
            this.dialog.cancel();
        }

    }
}
