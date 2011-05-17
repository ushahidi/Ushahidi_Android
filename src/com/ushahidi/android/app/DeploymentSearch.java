
package com.ushahidi.android.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ushahidi.android.app.data.DeploymentProvider;
import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.net.Deployments;
import com.ushahidi.android.app.util.DeviceCurrentLocation;
import com.ushahidi.android.app.util.Util;

public class DeploymentSearch extends DashboardActivity {
    private TextView mTextView;

    private TextView mEmptyList;

    private ListView mListView;

    private final String[] items = {
            "50", "100", "250", "500", "750", "1000", "1500"
    };

    private static final int DIALOG_DISTANCE = 0;

    private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

    private boolean refreshState = false;

    private boolean checkin = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deployment_search);
        setTitleFromActivityLabel(R.id.title_text);

        mTextView = (TextView)findViewById(R.id.search_deployment);
        mListView = (ListView)findViewById(R.id.deployment_list);
        mEmptyList = (TextView)findViewById(R.id.empty_list_for_deployments);

        showResults();
        mTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Cursor cursor = showResults(mTextView.getText().toString());

                // Specify the columns we want to display in the result
                String[] from = new String[] {
                        UshahidiDatabase.DEPLOYMENT_ID, UshahidiDatabase.DEPLOYMENT_NAME,
                        UshahidiDatabase.DEPLOYMENT_DESC, UshahidiDatabase.DEPLOYMENT_URL
                };

                // Specify the corresponding layout elements where we want the
                // columns to go
                int[] to = new int[] {
                        R.id.deploy_id, R.id.deploy_name, R.id.deploy_desc, R.id.deploy_url
                };

                // Create a simple cursor adapter for the details of the
                // deployment and apply
                // them to the ListView
                if (cursor != null) {
                    SimpleCursorAdapter deployments = new SimpleCursorAdapter(
                            DeploymentSearch.this, R.layout.deployment_search_result, cursor, from,
                            to);
                    mListView.setAdapter(deployments);

                } else {
                    showResults();
                }

            }

        });

        displayEmptyListText();

        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isDeploymentActive(id)) {
                    goToReports();
                } else {
                    ReportsTask reportsTask = new ReportsTask();
                    reportsTask.appContext = DeploymentSearch.this;
                    reportsTask.id = String.valueOf(id);
                    reportsTask.execute();
                }

            }
        });

    }

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

    public void clearAll() {
        if (mListView.getCount() == 0) {
            Util.showToast(this, R.string.no_items_cleared);
        } else {
            UshahidiApplication.mDb.deleteDeployment();
            showResults();
            Util.showToast(this, R.string.items_cleared);
        }
    }

    /**
     * Searches the deployment database and displays results for the given
     * query.
     * 
     * @param query The search query
     */
    private Cursor showResults(String query) {

        Cursor cursor = managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
            query
        }, null);

        return cursor;
    }

    /**
     * Searches the dictionary and displays results for the given query.
     * 
     * @param query The search query
     */
    private void showResults() {

        Cursor cursor = UshahidiApplication.mDb.fetchAllDeployments();

        if (cursor != null) {
            // There are no results

            // Specify the columns we want to display in the result
            String[] from = new String[] {
                    UshahidiDatabase.DEPLOYMENT_ID, UshahidiDatabase.DEPLOYMENT_NAME,
                    UshahidiDatabase.DEPLOYMENT_DESC, UshahidiDatabase.DEPLOYMENT_URL
            };

            // Specify the corresponding layout elements where we want the
            // columns to go
            int[] to = new int[] {
                    R.id.deployment_list_id, R.id.deployment_list_name, R.id.deployment_list_desc,
                    R.id.deployment_list_url
            };

            // Create a simple cursor adapter for the definitions and apply them
            // to the ListView
            SimpleCursorAdapter deployments = new SimpleCursorAdapter(this,
                    R.layout.deployment_list, cursor, from, to);
            mListView.setAdapter(deployments);
            displayEmptyListText();

        }

    }

    /**
     * Create an alert dialog
     */

    protected void createDialog(int d) {
        switch (d) {
            case DIALOG_DISTANCE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select distance in km");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        new DeviceCurrentLocation(DeploymentSearch.this);

                        RefreshDeploymentTask deploymentTask = new RefreshDeploymentTask();
                        deploymentTask.appContext = DeploymentSearch.this;
                        deploymentTask.location = DeviceCurrentLocation.getLocation();
                        deploymentTask.distance = items[item];
                        deploymentTask.execute();

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

        }

    }

    /**
     * Do something when the refresh icon is pressed
     */
    @Override
    public void onRefreshReports(View v) {
        createDialog(DIALOG_DISTANCE);
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
        
        if (cursor.moveToFirst()) {
            int urlIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_URL);
            int latitudeIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_LATITUDE);
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
                showResults();
            }
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

}
