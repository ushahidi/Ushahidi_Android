package com.ushahidi.android.app;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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

    private ListView mListView;

    private static final int MENU_SEARCH = 1;
    
    private final String[] items = {"50","100","250","500","750","1000","1500"};
    
    private static final int DIALOG_DISTANCE = 0;
    
    private boolean refreshState = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deployment_search);
        setTitleFromActivityLabel(R.id.title_text);

        mTextView = (TextView)findViewById(R.id.search_deploy);
        mListView = (ListView)findViewById(R.id.deployment_list);
        
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
                        UshahidiDatabase.DEPLOYMENT_NAME, UshahidiDatabase.DEPLOYMENT_DESC,
                        UshahidiDatabase.DEPLOYMENT_URL
                };

                // Specify the corresponding layout elements where we want the
                // columns to go
                int[] to = new int[] {
                        R.id.deploy_name, R.id.deploy_desc, R.id.deploy_url
                };

                // Create a simple cursor adapter for the details of the deployment and apply
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

    }
    
    private void updateRefreshStatus() {
        findViewById(R.id.refresh_report_btn).setVisibility(
                refreshState ? View.GONE : View.VISIBLE);
        findViewById(R.id.title_refresh_progress).setVisibility(
                refreshState ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SEARCH, 0, R.string.search_label)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEARCH:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        DeviceCurrentLocation deviceLocation = new DeviceCurrentLocation(DeploymentSearch.this);
        deviceLocation.stopLocating();
    }

    /**
     * Searches the dictionary and displays results for the given query.
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
                    UshahidiDatabase.DEPLOYMENT_NAME, UshahidiDatabase.DEPLOYMENT_DESC,
                    UshahidiDatabase.DEPLOYMENT_URL
            };

            // Specify the corresponding layout elements where we want the
            // columns to go
            int[] to = new int[] {
                    R.id.deployment_list_name, R.id.deployment_list_desc, R.id.deployment_list_url
            };

            // Create a simple cursor adapter for the definitions and apply them
            // to the ListView
            SimpleCursorAdapter deployments = new SimpleCursorAdapter(this,
                    R.layout.deployment_list, cursor, from, to);
            mListView.setAdapter(deployments);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: do something when the list item is clicked.
                }
            });
        }
    }
    
    /**
     * Create an alert dialog
     */
    
    protected void createDialog(int d) {
        switch(d) {
            case DIALOG_DISTANCE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select distance in km");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        DeviceCurrentLocation deviceLocation = new DeviceCurrentLocation(DeploymentSearch.this);
                        //deviceLocation.setDeviceLocation();
                        RefreshDeploymentTask deploymentTask = new RefreshDeploymentTask();
                        deploymentTask.appContext = DeploymentSearch.this;
                        deploymentTask.location = DeviceCurrentLocation.getLocation();
                        deploymentTask.distance = items[item];
                        deploymentTask.execute();
                        deviceLocation.stopLocating();
                    }
                });
                
                AlertDialog alert = builder.create();
                alert.show();
        }
        
        
    }
    
    /**
     * Do something when the refresh icon is pressed
     */
    @Override 
    public void onRefreshReports(View v) {
        createDialog(DIALOG_DISTANCE);
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
            status = deployments.fetchDeployments(distance,location); 
            Util.checkForCheckin(appContext);
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!status) {
                Util.showToast(appContext, R.string.internet_connection);
            } else {
                Util.showToast(appContext, R.string.reports_successfully_fetched);
                showResults();
            }
            refreshState = false;
            updateRefreshStatus();
        }

    }
}
