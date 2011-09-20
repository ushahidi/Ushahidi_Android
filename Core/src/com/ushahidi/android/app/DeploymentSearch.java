
package com.ushahidi.android.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ushahidi.android.app.data.DeploymentProvider;
import com.ushahidi.android.app.data.DeploymentsData;
import com.ushahidi.android.app.data.Database;
import com.ushahidi.android.app.net.Deployments;
import com.ushahidi.android.app.util.Util;

public class DeploymentSearch extends Dashboard implements LocationListener {

    private int deploymentId = 0;

    private TextView mTextView;

    private TextView mEmptyList;

    private ListView mListView;

    private final String[] items = {
            "50", "100", "250", "500", "750", "1000", "1500"
    };

    private static final int DIALOG_DISTANCE = 0;

    private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

    private static final int DIALOG_ADD_DEPLOYMENT = 2;

    private boolean refreshState = false;

    private boolean checkin = false;

    private LocationManager mLocationMgr = null;

    private static Location location;

    private String distance = "";

    // Context menu items
    private static final int DELETE = Menu.FIRST + 1;

    private Handler mHandler;

    private DeploymentAdapter deploymentAdapter;

    private List<DeploymentsData> mDeployments;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deployment_search);
        setTitleFromActivityLabel(R.id.title_text);

        mTextView = (TextView)findViewById(R.id.search_deployment);
        mListView = (ListView)findViewById(R.id.deployment_list);
        mEmptyList = (TextView)findViewById(R.id.empty_list_for_deployments);

        mDeployments = new ArrayList<DeploymentsData>();
        deploymentAdapter = new DeploymentAdapter(this);

        registerForContextMenu(mListView);
        mHandler = new Handler();
        mTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                showResults(mTextView.getText().toString());

            }

        });

        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String deploymentId = mDeployments.get(position).getId();
                if (isDeploymentActive(Integer.parseInt(deploymentId))) {
                    goToReports();
                } else {
                    ReportsTask reportsTask = new ReportsTask();
                    reportsTask.appContext = DeploymentSearch.this;
                    reportsTask.id = String.valueOf(deploymentId);
                    reportsTask.execute();
                }

            }
        });

        showResults("");
        displayEmptyListText();

    }

    // menu stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE, Menu.NONE, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        deploymentId = Integer.parseInt(mDeployments.get(info.position).getId());

        switch (item.getItemId()) {
            // context menu selected
            case DELETE:
                // Delete by ID
                mHandler.post(mDeleteDeploymentById);
                return (true);
        }
        return true;
    }

    /**
     * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
     * to be deleted.
     */
    final Runnable mDeleteDeploymentById = new Runnable() {
        public void run() {
            boolean result = false;

            result = MainApplication.mDb.deleteDeploymentById(String.valueOf(deploymentId));

            try {
                if (result) {
                    Util.showToast(DeploymentSearch.this, R.string.deployment_deleted);
                    showResults("");
                    displayEmptyListText();

                } else {
                    Util.showToast(DeploymentSearch.this, R.string.deployment_deleted_failed);
                }
            } catch (Exception e) {
                return;
            }
        }
    };

    private void updateRefreshStatus() {
        findViewById(R.id.refresh_report_btn)
                .setVisibility(refreshState ? View.GONE : View.VISIBLE);
        findViewById(R.id.title_refresh_progress).setVisibility(
                refreshState ? View.VISIBLE : View.GONE);
    }

    public void displayEmptyListText() {

        if (mListView.getCount() == 0) {
            mEmptyList.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        } else {
            mEmptyList.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deployments_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_deployments:
                createDialog(DIALOG_CLEAR_DEPLOYMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocating();
    }

    public void clearAll() {
        if (mListView.getCount() == 0) {
            Util.showToast(this, R.string.no_items_cleared);
        } else {
            MainApplication.mDb.deleteAllDeployment();
            MainApplication.mDb.clearData();
            deploymentAdapter.removeItems();
            deploymentAdapter.notifyDataSetChanged();
            showResults("");
            displayEmptyListText();
            // clear the stuff that has been initialized in the
            // sharedpreferences.
            Preferences.activeDeployment = 0;
            Preferences.domain = "";
            Preferences.deploymentLatitude = "0.0";
            Preferences.deploymentLongitude = "0.0";
            Preferences.saveSettings(this);
            Util.showToast(this, R.string.items_cleared);
        }
    }

    /**
     * Searches the deployment database and displays results for the given
     * query.
     * 
     * @param query The search query
     */
    /*
     * private Cursor showResults(String query) { Cursor cursor =
     * managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
     * query }, null); return cursor; }
     */

    /**
     * Searches the dictionary and displays results for the given query.
     * 
     * @param query The search query
     */
    private void showResults(String query) {
        Cursor cursor = null;

        if (TextUtils.isEmpty(query)) {
            cursor = MainApplication.mDb.fetchAllDeployments();
        } else {

            cursor = managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
                query
            }, null);
        }

        // clear everything in the list view
        if (deploymentAdapter != null) {
            deploymentAdapter.removeItems();
            deploymentAdapter.notifyDataSetChanged();
        }
        mDeployments.clear();
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                int deploymentIdIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int deploymentNameIndex = cursor
                        .getColumnIndexOrThrow(Database.DEPLOYMENT_NAME);
                int deploymentDescIndex = cursor
                        .getColumnIndexOrThrow(Database.DEPLOYMENT_DESC);
                int deploymentUrlIndex = cursor
                        .getColumnIndexOrThrow(Database.DEPLOYMENT_URL);
                if (deploymentAdapter != null) {
                    deploymentAdapter.removeItems();
                    deploymentAdapter.notifyDataSetChanged();
                }
                mDeployments.clear();
                do {

                    DeploymentsData deploymentsData = new DeploymentsData();
                    mDeployments.add(deploymentsData);

                    deploymentsData.setId(cursor.getString(deploymentIdIndex));
                    deploymentsData.setName(cursor.getString(deploymentNameIndex));
                    deploymentsData.setDesc(cursor.getString(deploymentDescIndex));
                    deploymentsData.setUrl(cursor.getString(deploymentUrlIndex));

                    deploymentAdapter.addItem(deploymentsData);

                } while (cursor.moveToNext());
            }
            cursor.close();
            deploymentAdapter.notifyDataSetChanged();
            mListView.setAdapter(deploymentAdapter);
            displayEmptyListText();
            // There are no results

        }

    }

    /**
     * Create an alert dialog
     */

    protected void createDialog(int d) {
        switch (d) {
            case DIALOG_DISTANCE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.select_distance);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        distance = items[item];
                        setDeviceLocation();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;

            case DIALOG_CLEAR_DEPLOYMENT:
                AlertDialog.Builder clearBuilder = new AlertDialog.Builder(this);
                clearBuilder
                        .setMessage(getString(R.string.confirm_clear))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.status_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clearAll();
                                        showResults("");
                                    }
                                })
                        .setNegativeButton(getString(R.string.status_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog clearDialog = clearBuilder.create();
                clearDialog.show();

                break;

            case DIALOG_ADD_DEPLOYMENT:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.deployment_add, null);
                final EditText deploymentUrl = (EditText)textEntryView
                        .findViewById(R.id.deployment_description_edit);

                final EditText deploymentName = (EditText)textEntryView
                        .findViewById(R.id.deployment_url_edit);

                // Validate fields
                deploymentUrl.setOnTouchListener(new OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {

                        if (TextUtils.isEmpty(deploymentUrl.getText().toString())) {
                            deploymentUrl.setText("http://");
                        }

                        return false;
                    }

                });

                final AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);

                addBuilder
                        .setTitle(R.string.add_deployment)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // validate URL
                                if ((Util.validateUshahidiInstance(deploymentUrl.getText()
                                        .toString()))
                                        && !(TextUtils.isEmpty(deploymentName.getText().toString()))) {
                                    MainApplication.mDb.addDeployment(deploymentName.getText()
                                            .toString(), deploymentUrl.getText().toString());
                                    showResults("");
                                } else {
                                    Util.showToast(DeploymentSearch.this, R.string.fix_error);
                                }

                            }
                        })
                        .setNegativeButton(R.string.btn_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        dialog.cancel();
                                    }
                                });
                AlertDialog deploymentDialog = addBuilder.create();
                deploymentDialog.show();
                break;
        }

    }

    /**
     * Do something when the refresh icon is pressed
     */
    @Override
    public void onRefreshReports(View v) {
        createDialog(DIALOG_DISTANCE);
    }

    public void onAddDeployment(View v) {
        createDialog(DIALOG_ADD_DEPLOYMENT);
    }

    public void goToReports() {
        Intent launchIntent;
        Bundle bundle = new Bundle();
        bundle.putInt("tab_index", 0);
        launchIntent = new Intent(this, IncidentTab.class);
        launchIntent.putExtra("tab", bundle);
        startActivityForResult(launchIntent, 0);
        setResult(RESULT_OK);
        finish();
    }
    
    /**
     * Clear saved reports
     */
    public void clearCachedReports(){
        
        // delete unset photo
        File f = new File(Preferences.fileName);
        if (f.exists()) {
            f.delete();
        }
        // clear persistent data
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("title", "");
        editor.putString("desc", "");
        editor.putString("date", "");
        editor.putString("selectedphoto", "");
        editor.putInt("requestedcode", 0);
        editor.commit();
    }

    /**
     * Fetch deployments
     * 
     * @author eyedol
     * @return 0 -- Successfully fetches details of a deployment
     * @return 1 -- Failed to fetch details of a deployment.
     * @return 2 -- No internet connection
     */
    public void activateDeployment(String id) {

        final Cursor cursor;
        cursor = MainApplication.mDb.fetchDeploymentById(id);
        String url = "";
        String latitude;
        String longitude;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int urlIndex = cursor.getColumnIndexOrThrow(Database.DEPLOYMENT_URL);
                int latitudeIndex = cursor
                        .getColumnIndexOrThrow(Database.DEPLOYMENT_LATITUDE);
                int longitudeIndex = cursor
                        .getColumnIndexOrThrow(Database.DEPLOYMENT_LONGITUDE);

                do {
                    url = cursor.getString(urlIndex);
                    latitude = cursor.getString(latitudeIndex);
                    longitude = cursor.getString(longitudeIndex);
                    Preferences.activeDeployment = Util.toInt(id);
                    Preferences.domain = url;
                    Preferences.deploymentLatitude = latitude;
                    Preferences.deploymentLongitude = longitude;
                } while (cursor.moveToNext());

            }
            cursor.close();
            Preferences.saveSettings(this);
            Preferences.loadSettings(this);
        }

    }

    Runnable mIsCheckinsEnabled = new Runnable() {
        public void run() {

            if (checkin) {
                Preferences.isCheckinEnabled = 1;
            } else {
                Preferences.isCheckinEnabled = 0;
            }

            Preferences.saveSettings(DeploymentSearch.this);

        }
    };

    /**
     * Checks if checkins is enabled on the configured Ushahidi deployment.
     */
    public void isCheckinsEnabled() {

        if (Util.isCheckinEnabled(this)) {
            Preferences.isCheckinEnabled = 1;
        } else {
            Preferences.isCheckinEnabled = 0;
        }
        Preferences.saveSettings(this);
    }

    // thread class
    private class RefreshDeploymentTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean status;

        protected Context appContext;

        private Deployments deployments;

        protected String distance;

        protected Location location;

        @Override
        protected void onPreExecute() {
            refreshState = true;
            updateRefreshStatus();
            deployments = new Deployments(appContext);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            status = deployments.fetchDeployments(distance, location);
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!status) {
                Util.showToast(appContext, R.string.could_not_fetch_data);
            } else {
                Util.showToast(appContext, R.string.deployment_fetched_successful);

            }
            showResults("");
            refreshState = false;
            updateRefreshStatus();
        }

    }

    /**
     * Check if a deployment is the active one
     * 
     * @author eyedol
     */

    public boolean isDeploymentActive(long id) {
        Preferences.loadSettings(this);
        if (Preferences.activeDeployment == id) {
            return true;
        }
        return false;

    }

    // thread class
    private class ReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        private ProgressDialog dialog;

        protected Context appContext;

        protected String id;

        @Override
        protected void onPreExecute() {

            this.dialog = ProgressDialog.show(appContext, getString(R.string.please_wait),
                    getString(R.string.loading), true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            activateDeployment(id);
            isCheckinsEnabled();
            if(Preferences.isCheckinEnabled == 0 ) {
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
                Util.showToast(appContext, R.string.ushahidi_sync);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.could_not_fetch_reports);
            } else if(result == 0 ){
                clearCachedReports();
                goToReports();
            }
            this.dialog.cancel();

        }

    }

    /** Location stuff **/
    // Fetches the current location of the device.
    protected void setDeviceLocation() {
        mLocationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Get last known location from either GPS or Network provider
        Location loc = null;
        boolean netAvail = (mLocationMgr.getProvider(LocationManager.NETWORK_PROVIDER) != null);
        boolean gpsAvail = (mLocationMgr.getProvider(LocationManager.GPS_PROVIDER) != null);
        if (gpsAvail) {
            loc = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if (netAvail) {
            loc = mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // Just use last location if it's less than 10 minutes old
        if (loc != null && ((new Date()).getTime() - loc.getTime() < 10 * 60 * 1000)) {
            onLocationChanged(loc);
        } else {
            if (gpsAvail) {
                mLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
            if (netAvail) {
                mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
    }

    public void stopLocating() {
        if (mLocationMgr != null) {
            try {
                mLocationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mLocationMgr = null;
        }
    }

    public void onLocationChanged(Location loc) {
        if (loc != null) {
            location = loc;
            RefreshDeploymentTask deploymentTask = new RefreshDeploymentTask();
            deploymentTask.appContext = DeploymentSearch.this;
            deploymentTask.location = location;
            deploymentTask.distance = distance;
            deploymentTask.execute();
            stopLocating();
        }

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
