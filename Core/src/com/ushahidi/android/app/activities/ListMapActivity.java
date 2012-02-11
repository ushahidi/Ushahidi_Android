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

package com.ushahidi.android.app.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.ListMapAdapter;
import com.ushahidi.android.app.models.ListMapModel;
import com.ushahidi.android.app.net.Deployments;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.views.ListMapView;

/**
 * @author eyedol
 */
public class ListMapActivity extends BaseListActivity<ListMapView, ListMapModel, ListMapAdapter>
        implements LocationListener {

    private final String[] items = {
            "50", "100", "250", "500", "750", "1000", "1500"
    };

    private static final int DIALOG_DISTANCE = 0;

    private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

    private static final int DIALOG_ADD_DEPLOYMENT = 2;

    private MenuItem refresh;

    // Context menu items
    private static final int DELETE = Menu.FIRST + 1;

    // private static final int EDIT = Menu.FIRST + 2;

    private LocationManager mLocationMgr = null;

    private static Location location;

    private String distance = "";

    private Handler mHandler;

    private ListMapView listMapView;

    private int mMapId = 0;

    private List<ListMapModel> mListMap;

    private ListMapAdapter listMapAdapter;

    public ListMapActivity() {
        super(ListMapView.class, ListMapAdapter.class, R.layout.list_map, R.menu.list_map,
                R.id.list_map_table);
        mHandler = new Handler();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listMapView = new ListMapView(this);
        mListMap = new ArrayList<ListMapModel>();
        listMapAdapter = new ListMapAdapter(this);
        new FetchMapTask(this).execute((String)null);
        listMapView.mTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // showResults(s.toString());
                toastShort(s.toString());
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
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

    /**
     * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
     * to be deleted.
     */
    final Runnable mDeleteMapById = new Runnable() {
        public void run() {
            boolean result = false;
            result = new ListMapModel().deleteMapById(mMapId);

            try {
                if (result) {
                    toastShort(R.string.deployment_deleted);

                } else {
                    toastShort(R.string.deployment_deleted_failed);
                }
            } catch (Exception e) {
                return;
            }
        }
    };

    // Context Menu Stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, DELETE, Menu.NONE, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        mMapId = Integer.parseInt(mListMap.get(info.position).getId());

        switch (item.getItemId()) {
            // context menu selected
            case DELETE:
                // Delete by ID
                mHandler.post(mDeleteMapById);
                return (true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_map) {
            createDialog(DIALOG_CLEAR_DEPLOYMENT);
            return true;
        } else if (item.getItemId() == R.id.menu_refresh) {
            refresh = item;
            createDialog(DIALOG_DISTANCE);
            return true;
        } else if (item.getItemId() == R.id.menu_add) {
            createDialog(DIALOG_ADD_DEPLOYMENT);
            return true;
        } else if (item.getItemId() == R.id.app_settings) {
            startActivity(new Intent(this, Settings.class));
            setResult(RESULT_OK);
            return true;
        } else if (item.getItemId() == R.id.app_about) {
            startActivity(new Intent(this, AboutActivity.class));
            setResult(RESULT_OK);
            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }

    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final String deploymentId = mListMap.get(position).getId();
        if (isMapActive(Integer.parseInt(deploymentId))) {
            // goToReports();
            // TODO :// do something here
        } else {
            FetchMapTask reportsTask = new FetchMapTask(this);

            reportsTask.execute();
        }

    }

    /**
     * Check if a deployment is the active one
     * 
     * @param id - map's id
     * @return boolean
     */

    public boolean isMapActive(long id) {
        Preferences.loadSettings(this);
        if (Preferences.activeDeployment == id) {
            return true;
        }
        return false;

    }

    /**
     * Checks if checkins is enabled on the configured Ushahidi deployment.
     */
    public void isCheckinsEnabled() {

        if (ApiUtils.isCheckinEnabled(this)) {
            Preferences.isCheckinEnabled = 1;
        } else {
            Preferences.isCheckinEnabled = 0;
        }
        Preferences.saveSettings(this);
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
                                        // clearAll();
                                        // showResults("");
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
                                if ((ApiUtils.validateUshahidiInstance(deploymentUrl.getText()
                                        .toString()))
                                        && !(TextUtils.isEmpty(deploymentName.getText().toString()))) {
                                    MainApplication.mDb.addDeployment(deploymentName.getText()
                                            .toString(), deploymentUrl.getText().toString());
                                    // showResults("");
                                } else {
                                    toastLong(R.string.fix_error);
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
     * Load Map details from the local database
     */
    class LoadMapTask extends ProgressTask {

        protected Boolean status;

        protected Context appContext;

        private Deployments deployments;

        protected String distance;

        protected Location location;

        public LoadMapTask(FragmentActivity activity) {
            super(activity, R.string.loading_);
            // switch to a progress animation
            refresh.setActionView(R.layout.indeterminate_progress_action);
            deployments = new Deployments(appContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                status = deployments.fetchDeployments(distance, location);

                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!status) {
                toastShort(R.string.could_not_fetch_data);
            } else {

                toastShort(R.string.deployment_fetched_successful);
            } // TODO refresh
            refresh.setActionView(null);
            listMapView.displayEmptyListText();
        }

    }

    /**
     * Load map details from the web
     */
    class FetchMapTask extends ProgressTask {

        public FetchMapTask(FragmentActivity activity) {
            super(activity, R.string.loading_);
            // pass custom loading message to super call
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listMapView.mProgressBar.setVisibility(View.VISIBLE);
            listMapView.mEmptyList.setVisibility(View.GONE);
            dialog.cancel();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                listMapAdapter.refresh(ListMapActivity.this);

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            onLoaded(success);
        }
    }

    @Override
    protected void onLoaded(boolean success) {
        Log.i("ListMapModel", "Total List size map activity: "+listMapAdapter.getCount());
        listMapView.mListView.setAdapter(listMapAdapter);
        listMapView.mProgressBar.setVisibility(View.GONE);
        listMapView.displayEmptyListText();
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
            LoadMapTask deploymentTask = new LoadMapTask(this);
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
