
package com.ushahidi.android.app;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.net.Deployments;
import com.ushahidi.android.app.util.DeviceCurrentLocation;
import com.ushahidi.android.app.util.Util;

public class DeploymentSearch extends DashboardActivity implements LocationListener {

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

    private static final String CLASS_TAG = DeviceCurrentLocation.class.getCanonicalName();

    // Context menu items
    private static final int DELETE = Menu.FIRST + 1;

    private Handler mHandler;

    private DeploymentAdapter deploymentAdapter;
    
    private List<DeploymentsData> mDeployments;

    protected void onCreate(Bundle savedInstanceState) {
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
        showResults("");
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

        displayEmptyListText();

        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String deploymentId = mDeployments.get(position).getId();
                if (isDeploymentActive(Integer.parseInt(deploymentId)) ) {
                    goToReports();
                } else {
                    ReportsTask reportsTask = new ReportsTask();
                    reportsTask.appContext = DeploymentSearch.this;
                    reportsTask.id = String.valueOf(deploymentId);
                    reportsTask.execute();
                }

            }
        });

    }

    // menu stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE, Menu.NONE, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        deploymentId = (int)info.id;

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

            result = UshahidiApplication.mDb.deleteDeploymentById(String.valueOf(deploymentId));

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
        } else {
            mEmptyList.setVisibility(View.GONE);
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
            UshahidiApplication.mDb.deleteAllDeployment();
            UshahidiApplication.mDb.clearData();
            showResults("");

            // clear the stuff that has been initialized in the
            // sharedpreferences.
            UshahidiPref.activeDeployment = 0;
            UshahidiPref.domain = "";
            UshahidiPref.deploymentLatitude = "0.0";
            UshahidiPref.deploymentLongitude = "0.0";
            UshahidiPref.saveSettings(this);
            Util.showToast(this, R.string.items_cleared);
        }
    }

    /**
     * Searches the deployment database and displays results for the given
     * query.
     * 
     * @param query The search query
     */
    /*private Cursor showResults(String query) {

        Cursor cursor = managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
            query
        }, null);

        return cursor;
    }*/

    /**
     * Searches the dictionary and displays results for the given query.
     * 
     * @param query The search query
     */
    private void showResults(String query) {
        Cursor cursor = null;
        
        if( TextUtils.isEmpty(query)) {
            cursor = UshahidiApplication.mDb.fetchAllDeployments();
        }else {

            cursor = managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
                query
            }, null);
        }

        // clear everything in the list view

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                int deploymentIdIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
                int deploymentNameIndex = cursor
                        .getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_NAME);
                int deploymentDescIndex = cursor
                        .getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_DESC);
                int deploymentUrlIndex = cursor
                        .getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_URL);
                
                deploymentAdapter.removeItems();
                deploymentAdapter.notifyDataSetChanged();
                
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
                final View textEntryView = factory.inflate(R.layout.add_deployment, null);
                final EditText deploymentUrl = (EditText)textEntryView
                        .findViewById(R.id.deployment_description_edit);

                final EditText deploymentName = (EditText)textEntryView
                        .findViewById(R.id.deployment_url_edit);

                // Validate fields
                deploymentUrl.setOnTouchListener(new OnTouchListener() {

                    @Override
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
                                    UshahidiApplication.mDb.addDeployment(deploymentName.getText()
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
        launchIntent = new Intent(this, IncidentsTab.class);
        launchIntent.putExtra("tab", bundle);
        startActivityForResult(launchIntent, 0);
        setResult(RESULT_OK);
        finish();
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
        cursor = UshahidiApplication.mDb.fetchDeploymentById(id);
        String url = "";
        String latitude;
        String longitude;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int urlIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_URL);
                int latitudeIndex = cursor
                        .getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_LATITUDE);
                int longitudeIndex = cursor
                        .getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_LONGITUDE);

                do {
                    url = cursor.getString(urlIndex);
                    latitude = cursor.getString(latitudeIndex);
                    longitude = cursor.getString(longitudeIndex);
                    UshahidiPref.activeDeployment = Util.toInt(id);
                    UshahidiPref.domain = url;
                    UshahidiPref.deploymentLatitude = latitude;
                    UshahidiPref.deploymentLongitude = longitude;
                } while (cursor.moveToNext());

            }
            cursor.close();
            UshahidiPref.saveSettings(this);
            UshahidiPref.loadSettings(this);
        }

    }

    Runnable mIsCheckinsEnabled = new Runnable() {
        public void run() {

            if (checkin) {
                UshahidiPref.isCheckinEnabled = 1;
            } else {
                UshahidiPref.isCheckinEnabled = 0;
            }

            UshahidiPref.saveSettings(DeploymentSearch.this);

        }
    };

    /**
     * Checks if checkins is enabled on the configured Ushahidi deployment.
     */
    public void isCheckinsEnabled() {

        if (Util.isCheckinEnabled(this)) {
            UshahidiPref.isCheckinEnabled = 1;
        } else {
            UshahidiPref.isCheckinEnabled = 0;
        }
        UshahidiPref.saveSettings(this);
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
        UshahidiPref.loadSettings(this);
        if (UshahidiPref.activeDeployment == id) {
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
            UshahidiApplication.mDb.clearReports();
            status = Util.processReports(appContext);
            isCheckinsEnabled();
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
                goToReports();
            }
            this.dialog.cancel();

        }

    }

    /** Location stuff **/
    // Fetches the current location of the device.
    public void setDeviceLocation() {

        mLocationMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        long updateTimeMsec = 30 * 1000;
        try {

            // get low accuracy provider
            LocationProvider low = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                    Util.createCoarseCriteria(), true));

            // get high accuracy provider
            LocationProvider high = mLocationMgr.getProvider(mLocationMgr.getBestProvider(
                    Util.createFineCriteria(), true));

            mLocationMgr.requestLocationUpdates(low.getName(), updateTimeMsec, 0, this);

            mLocationMgr.requestLocationUpdates(high.getName(), updateTimeMsec, 0, this);

        } catch (Exception ex1) {
            try {

                if (mLocationMgr != null) {
                    mLocationMgr.removeUpdates(this);
                    mLocationMgr = null;
                }
            } catch (Exception ex2) {
                Log.d(CLASS_TAG, ex2.getMessage());
            }
        }

    }

    public void stopLocating() {

        try {

            try {
                mLocationMgr.removeUpdates(this);
            } catch (Exception ex) {
                Log.d(CLASS_TAG, ex.getMessage());
            }
            mLocationMgr = null;
        } catch (Exception ex) {
            Log.d(CLASS_TAG, ex.getMessage());
        }
    }

    @Override
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

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
