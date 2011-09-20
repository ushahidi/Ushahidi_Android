
package com.ushahidi.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ushahidi.android.app.checkin.CheckinActivity;
import com.ushahidi.android.app.util.Util;

public class Dashboard extends Activity {
    
    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Handle the click on the search button.
     * 
     * @param v View
     * @return void
     */

    public void onClickSearch(View v) {
        onSearchRequested();
    }
    
    public void onSearchDeployments( View v ) {
        Intent intent = new Intent(Dashboard.this, DeploymentSearch.class);
        startActivityForResult(intent, VIEW_SEARCH);
        setResult(RESULT_OK);
    }

    /**
     * Handle the click on the About button.
     * 
     * @param v View
     * @return void
     */
    public void onClickAdd(View v) {
        startActivity(new Intent(getApplicationContext(),Object.class));
    }
    

    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    public void goHome(Context context) 
    {
        final Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity (intent);
    }

    /**
     * Use the activity label to set the text in the activity's title text view.
     * The argument gives the name of the view.
     *
     * <p> This method is needed because we have a custom title bar rather than the default Android title bar.
     * See the theme definitons in styles.xml.
     * 
     * @param textViewId int
     * @return void
     */

    public void setTitleFromActivityLabel (int textViewId)
    {
        TextView tv = (TextView) findViewById (textViewId);
        if (tv != null) tv.setText (getTitle ());
    } // end setTitleText

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

    private static final int VIEW_SEARCH = 5;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int REQUEST_CODE_ABOUT = 2;

    private static final int DIALOG_PROMPT = 0;

    private static final int DIALOG_ERROR = 1;

    private Handler mHandler;

    private Button listBtn;

    private Button addBtn;

    private Button checkinListBtn;

    private Button checkinAddBtn;

    private Button settingsBtn;

    private Button mapBtn;

    private Button aboutBtn;

    private Button searchBtn;

    private LinearLayout middleGrid;

    private String dialogErrorMsg = "An error occurred fetching the reports. "
            + "Make sure you have entered an Ushahidi instance.";

    private Bundle bundle;

    private boolean refreshState = false;

    // Checkin specific variables and functions

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.dashboard);
        setTitleFromActivityLabel(R.id.title_text);
        mHandler = new Handler();
        bundle = new Bundle();

        // load settings
        Preferences.loadSettings(this);

        // check for default deployment
        checkDefaultDeployment();

        // check if domain has been set
        if (Preferences.domain.length() == 0 || Preferences.domain.equals("http://")) {
            mHandler.post(mDisplayPrompt);
        }

        listBtn = (Button)findViewById(R.id.incident_list);
        checkinListBtn = (Button)findViewById(R.id.checkin_list_btn);

        addBtn = (Button)findViewById(R.id.incident_add);
        checkinAddBtn = (Button)findViewById(R.id.checkin_add_btn);

        settingsBtn = (Button)findViewById(R.id.deployment_settings);
        mapBtn = (Button)findViewById(R.id.incident_map);
        searchBtn = (Button)findViewById(R.id.deployment_search);
        aboutBtn = (Button)findViewById(R.id.deployment_about);

        initializeUI();

    }

    /**
     * Check if default deployment has been set.
     */
    private void checkDefaultDeployment() {
        // Check if default domain has been set.
        final String deployment = getString(R.string.deployment_url);
        if (!TextUtils.isEmpty(deployment)) {
            Log.i("Ushahidi", "Determing if default deployment has been set " + deployment);

            // validate URL
            if (Util.validateUshahidiInstance(deployment)) {
                Log.i("Ushahidi", "Validate Domain " + deployment);
                middleGrid = (LinearLayout)findViewById(R.id.middle_grid);
                middleGrid.setVisibility(View.GONE);
                Preferences.domain = deployment;
                Preferences.saveSettings(this);

                // refresh for new reports
                if (Preferences.appRunsFirstTime == 0) {
                    refreshReports();
                    Preferences.appRunsFirstTime = 1;
                    Preferences.saveSettings(this);
                }
            }
        }
    }

    public void onRefreshReports(View v) {
        refreshReports();
    }

    private void refreshReports() {
        // make sure there is a deployment to fetch reports/checkins from
        if (Preferences.domain.length() == 0 || Preferences.domain.equals("http://")) {
            mHandler.post(mDisplayPrompt);
        } else {
            ReportsTask reportsTask = new ReportsTask();
            reportsTask.appContext = this;
            reportsTask.execute();
        }
    }

    @Override
    public void onResume() {
        initializeUI();
        super.onResume();
    }

    /**
     * Initializes some of the UI components and test
     */
    protected void initializeUI() {
        Preferences.loadSettings(this);
        // This is to temporarily disable reports stuff
        if (Preferences.isCheckinEnabled == 1) {

            listBtn.setVisibility(View.GONE);
            addBtn.setVisibility(View.GONE);
            checkinListBtn.setVisibility(View.VISIBLE);

            checkinAddBtn.setVisibility(View.VISIBLE);

        } else {

            listBtn.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.VISIBLE);
            checkinListBtn.setVisibility(View.GONE);
            checkinAddBtn.setVisibility(View.GONE);
        }

        listBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, IncidentTab.class);
                startActivityForResult(intent, INCIDENTS);
                setResult(RESULT_OK);

            }
        });

        checkinListBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, IncidentTab.class);
                startActivityForResult(intent, INCIDENTS);
                setResult(RESULT_OK);

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, Settings.class);
                startActivityForResult(intent, VIEW_SETTINGS);
                setResult(RESULT_OK);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, IncidentAdd.class);
                startActivityForResult(intent, ADD_INCIDENTS);
                setResult(RESULT_OK);

            }
        });

        checkinAddBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent checkinActivityIntent = new Intent().setClass(Dashboard.this,
                        CheckinActivity.class);
                startActivity(checkinActivityIntent);
                setResult(RESULT_OK);

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this, DeploymentSearch.class);
                startActivityForResult(intent, VIEW_SEARCH);
                setResult(RESULT_OK);
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent launchIntent = new Intent(Dashboard.this, About.class);
                startActivityForResult(launchIntent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);

            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                bundle.putInt("tab_index", 1);
                Intent launchIntent = new Intent(Dashboard.this, IncidentTab.class);
                launchIntent.putExtra("tab", bundle);
                startActivityForResult(launchIntent, MAP_INCIDENTS);
                setResult(RESULT_OK);
            }
        });
    }

    public void onAddReport(View v) {
        Preferences.loadSettings(Dashboard.this);
        if (Preferences.isCheckinEnabled == 1) {
            Intent checkinActivityIntent = new Intent().setClass(Dashboard.this,
                    CheckinActivity.class);
            startActivity(checkinActivityIntent);
            setResult(RESULT_OK);

        } else {
            Intent intent = new Intent(Dashboard.this, IncidentAdd.class);
            startActivityForResult(intent, ADD_INCIDENTS);
            setResult(RESULT_OK);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROMPT: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(R.string.ushahidi_setup_title);
                dialog.setMessage(getString(R.string.ushahidi_setup_blub));
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent launchPreferencesIntent = new Intent().setClass(Dashboard.this,
                                DeploymentSearch.class);

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

                        Intent launchPreferencesIntent = new Intent(Dashboard.this, Settings.class);

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

    private void updateRefreshStatus() {
        findViewById(R.id.refresh_report_btn)
                .setVisibility(refreshState ? View.GONE : View.VISIBLE);
        findViewById(R.id.title_refresh_progress).setVisibility(
                refreshState ? View.VISIBLE : View.GONE);
    }

    private void populateMenu(Menu menu) {
        MenuItem i;

        if (Preferences.isCheckinEnabled == 1) {
            i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.checkin_btn);
            i.setIcon(R.drawable.menu_add);
        } else {
            i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add);
            i.setIcon(R.drawable.menu_add);
        }

        if (Preferences.isCheckinEnabled == 1) {
            i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.checkin_list);
            i.setIcon(R.drawable.menu_list);
        } else {
            i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list);
            i.setIcon(R.drawable.menu_list);
        }

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.menu_map);

        i = menu.add(Menu.NONE, SYNC, Menu.NONE, R.string.menu_sync);
        i.setIcon(R.drawable.menu_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.menu_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.menu_about);

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent launchIntent;
        switch (item.getItemId()) {
            case LIST_INCIDENT:
                bundle.putInt("tab_index", 0);
                launchIntent = new Intent(Dashboard.this, IncidentTab.class);
                launchIntent.putExtra("tab", bundle);
                startActivityForResult(launchIntent, LIST_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case INCIDENT_MAP:
                bundle.putInt("tab_index", 1);
                launchIntent = new Intent(Dashboard.this, IncidentTab.class);
                launchIntent.putExtra("tab", bundle);
                startActivityForResult(launchIntent, MAP_INCIDENTS);
                setResult(RESULT_OK);
                return true;

            case ADD_INCIDENT:
                if (Preferences.isCheckinEnabled == 1) {
                    launchIntent = new Intent(Dashboard.this, CheckinActivity.class);
                    startActivityForResult(launchIntent, ADD_INCIDENTS);
                    setResult(RESULT_OK);
                } else {
                    launchIntent = new Intent(Dashboard.this, IncidentAdd.class);
                    startActivityForResult(launchIntent, ADD_INCIDENTS);
                    setResult(RESULT_OK);
                }
                return true;

            case ABOUT:
                launchIntent = new Intent(Dashboard.this, About.class);
                startActivityForResult(launchIntent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                launchIntent = new Intent().setClass(Dashboard.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(launchIntent, REQUEST_CODE_SETTINGS);
                setResult(RESULT_OK);
                return true;

            case SYNC:
                this.checkDefaultDeployment();
                this.refreshReports();
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
            refreshState = true;
            updateRefreshStatus();
            this.dialog = ProgressDialog.show(appContext, getString(R.string.please_wait),
                    getString(R.string.loading), true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Util.checkForCheckin(appContext);
            if (Preferences.isCheckinEnabled == 0) {
                status = Util.processReports(appContext);
            } else {
                status = Util.processCheckins(appContext);
            }
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
            refreshState = false;
            updateRefreshStatus();
        }

    }
}
