
package com.ushahidi.android.app.ui.tablet;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.ListMapAdapter;
import com.ushahidi.android.app.adapters.ListMapTabletAdapter;
import com.ushahidi.android.app.fragments.BaseListFragment;
import com.ushahidi.android.app.helpers.ActionModeHelper;
import com.ushahidi.android.app.models.ListMapModel;
import com.ushahidi.android.app.net.Maps;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.ui.phone.ReportTabActivity;
import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.AddMapView;
import com.ushahidi.android.app.views.ListMapView;

public class ListMapFragment extends BaseListFragment<ListMapView, ListMapModel, ListMapAdapter>
        implements LocationListener {

    private final String[] items = {
            "50", "100", "250", "500", "750", "1000", "1500"
    };

    private static final int DIALOG_DISTANCE = 0;

    private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

    private static final int DIALOG_ADD_DEPLOYMENT = 2;

    private LocationManager mLocationMgr = null;

    private static Location location;

    private String distance = "";

    private Handler mHandler;

    private long mMapId = 0;
    
    private String url = "";

    private ListMapModel mListMapModel;

    private ListMapView mListMapView;

    private ListMapTabletAdapter mListMapAdapter;

    private boolean edit = true;

    private boolean refreshState = false;

    private MenuItem refresh;

    private ImageButton addMap = null;

    private ImageButton refreshMap = null;

    private String filter;

    private ListMapFragmentListener listener = null;

    private String TAG = ListMapFragment.class.getSimpleName();

    private ViewGroup mRootView;

    static private final String STATE_CHECKED = "com.ushahidi.android.app.activity.STATE_CHECKED";

    public ListMapFragment() {
        super(ListMapView.class, ListMapAdapter.class, R.layout.list_map, 0,
                android.R.id.list);
        mHandler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        
        mListMapView = new ListMapView(getActivity());
        mListMapAdapter = new ListMapTabletAdapter(getActivity());
        mListMapModel = new ListMapModel();

        if (Util.isHoneycomb()) {
            mListMapView.mListView.setLongClickable(true);
            mListMapView.mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListMapView.mListView.setOnItemLongClickListener(new ActionModeHelper(this,
                    mListMapView.mListView));
        } else {

            registerForContextMenu(mListMapView.mListView);
        }

        mHandler.post(fetchMapList);

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(STATE_CHECKED, -1);

            if (position > -1) {
                mListMapView.mListView.setItemChecked(position, true);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(STATE_CHECKED, mListMapView.mListView.getCheckedItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(fetchMapList);
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

    public void setListMapListener(ListMapFragmentListener listener) {
        this.listener = listener;
    }

    public void enablePersistentSelection() {
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
     * to be deleted.
     */
    final Runnable mDeleteMapById = new Runnable() {
        public void run() {
            boolean status = false;
            status = mListMapModel.deleteMapById(mMapId);

            try {
                if (status) {
                    toastShort(R.string.map_deleted);
                    refreshMapLists();
                } else {
                    toastShort(R.string.map_deleted_failed);
                }
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Refresh the list view with new items
     */
    final Runnable fetchMapList = new Runnable() {
        public void run() {
            try {
                mListMapAdapter.refresh(getActivity());
                mListMapView.mListView.setAdapter(mListMapAdapter);
                mListMapView.displayEmptyListText();
            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Filter the list view with new items
     */
    final Runnable filterMapList = new Runnable() {
        public void run() {
            try {
                mListMapAdapter.refresh(getActivity(), filter);
                mListMapView.mListView.setAdapter(mListMapAdapter);
                mListMapView.displayEmptyListText();

            } catch (Exception e) {
                return;
            }
        }
    };

    /**
     * Delete all fetched maps
     */
    final Runnable deleteAllMaps = new Runnable() {
        public void run() {
            boolean status = false;
            status = mListMapModel.deleteAllMap(getActivity());
            try {
                if (status) {
                    toastShort(R.string.map_deleted);
                    refreshMapLists();
                } else {
                    toastShort(R.string.map_deleted_failed);
                }
            } catch (Exception e) {
                return;
            }
        }
    };

    final Runnable refreshMapList = new Runnable() {
        public void run() {
            try {
                refreshMapLists();
            } catch (Exception e) {
                return;
            }
        }
    };

    public void refreshMapLists() {
        mListMapAdapter.refresh(getActivity());
        mListMapView.mListView.setAdapter(mListMapAdapter);
        mListMapView.displayEmptyListText();
    }

    // Context Menu Stuff
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.list_map_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item
                .getMenuInfo();
        boolean result = performAction(item, info.position);

        if (!result) {
            result = super.onContextItemSelected(item);
        }

        return result;
    }

    public boolean performAction(android.view.MenuItem item, int position) {
        
        mMapId = Long.valueOf(mListMapAdapter.getItem(position).getId());
        url = mListMapAdapter.getItem(position).getUrl();
        if (item.getItemId() == R.id.map_delete) {
            // Delete by ID
            edit = false;
            mHandler.post(mDeleteMapById);
            return (true);
        } else if (item.getItemId() == R.id.map_edit) {
            // edit existing map
            edit = true;
            createDialog(DIALOG_ADD_DEPLOYMENT);
            return (true);
        }

        return (false);
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
            edit = false;
            createDialog(DIALOG_ADD_DEPLOYMENT);
            return true;
        } else if (item.getItemId() == R.id.app_settings) {
            startActivity(new Intent(getActivity(), Settings.class));

            return true;
        } else if (item.getItemId() == R.id.app_about) {
            startActivity(new Intent(getActivity(), AboutActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        log("on map itemClicked");
        final String deploymentId = mListMapAdapter.getItem(position).getId();

        if (isMapActive(Integer.parseInt(deploymentId))) {
            if (listener != null) {
                listener.onMapSelected(Integer.parseInt(deploymentId));
            }
        } else {
            FetchMapReportTask fetchMapReportTask = new FetchMapReportTask(this);
            fetchMapReportTask.id = deploymentId;
            fetchMapReportTask.execute((String)null);
            if (fetchMapReportTask.getStatus() == android.os.AsyncTask.Status.FINISHED) {
                if (listener != null) {
                    listener.onMapSelected(Integer.parseInt(deploymentId));
                }
            }
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        l.setItemChecked(position, true);

        final String deploymentId = mListMapAdapter.getItem(position).getId();

        if (isMapActive(Integer.parseInt(deploymentId))) {
            if (listener != null) {
                listener.onMapSelected(Integer.parseInt(deploymentId));
            }
        } else {
            FetchMapReportTask fetchMapReportTask = new FetchMapReportTask(this);
            fetchMapReportTask.id = deploymentId;
            fetchMapReportTask.execute((String)null);
            if (fetchMapReportTask.getStatus() == android.os.AsyncTask.Status.FINISHED) {
                if (listener != null) {
                    listener.onMapSelected(Integer.parseInt(deploymentId));
                }
            }
        }

    }

    /**
     * Check if a deployment is the active one
     * 
     * @param id - map's id
     * @return boolean
     */

    public boolean isMapActive(long id) {
        Preferences.loadSettings(getActivity());
        if (Preferences.activeDeployment == id) {
            return true;
        }
        return false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup)inflater.inflate(R.layout.list_map, null);
        addMap = (ImageButton)mRootView.findViewById(R.id.list_map_toolbar_add);
        refreshMap = (ImageButton)mRootView.findViewById(R.id.list_map_refresh_btn);

        if (addMap != null) {
            addMap.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    edit = false;
                    createDialog(DIALOG_ADD_DEPLOYMENT);
                }

            });
        }

        if (refreshMap != null) {
            refreshMap.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    createDialog(DIALOG_DISTANCE);
                }

            });
        }

        return mRootView;
    }

    private void updateRefreshStatus() {
        if (mRootView != null) {
            if (addMap != null) {
                mRootView.findViewById(R.id.list_map_refresh_btn).setVisibility(
                        refreshState ? View.GONE : View.VISIBLE);
                mRootView.findViewById(R.id.list_map_refresh_progress).setVisibility(
                        refreshState ? View.VISIBLE : View.GONE);
            }
        }

        if (refresh != null) {
            if (refreshState)
                refresh.setActionView(R.layout.indeterminate_progress_action);
            else
                refresh.setActionView(null);
        }
    }

    /**
     * Checks if checkins is enabled on the configured Ushahidi deployment.
     */
    public void isCheckinsEnabled() {

        if (ApiUtils.isCheckinEnabled(getActivity())) {
            Preferences.isCheckinEnabled = 1;
        } else {
            Preferences.isCheckinEnabled = 0;
        }
        Preferences.saveSettings(getActivity());
    }

    public void goToReports() {
        Intent launchIntent;
        Bundle bundle = new Bundle();
        bundle.putInt("tab_index", 0);
        launchIntent = new Intent(getActivity(), ReportTabActivity.class);
        launchIntent.putExtra("tab", bundle);
        startActivityForResult(launchIntent, 0);
        getActivity().setResult(FragmentActivity.RESULT_OK);
        getActivity().finish();
    }

    /**
     * Clear saved reports
     */
    public void clearCachedReports() {

        // delete unset photo
        if (Preferences.fileName != null) {
            File f = new File(Preferences.fileName);
            if (f != null) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }

        // clear persistent data
        SharedPreferences.Editor editor = getActivity().getPreferences(0).edit();
        editor.putString("title", "");
        editor.putString("desc", "");
        editor.putString("date", "");
        editor.putString("selectedphoto", "");
        editor.putInt("requestedcode", 0);
        editor.commit();
    }

    /**
     * Create an alert dialog
     */

    protected void createDialog(int d) {
        switch (d) {
            case DIALOG_DISTANCE:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder clearBuilder = new AlertDialog.Builder(getActivity());
                clearBuilder
                        .setMessage(getString(R.string.confirm_clear))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.status_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mHandler.post(deleteAllMaps);
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
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View textEntryView = factory.inflate(R.layout.add_map, null);
                final AddMapView addMapView = new AddMapView(textEntryView);

                // if edit was selected at the context menu, populate fields
                // with existing map details
                if (edit) {
                    final List<ListMapModel> listMap = mListMapModel.loadMapById(String
                            .valueOf(mMapId), url);
                    addMapView.setMapName(listMap.get(0).getName());
                    addMapView.setMapDescription(listMap.get(0).getDesc());
                    addMapView.setMapUrl(listMap.get(0).getUrl());
                    addMapView.setMapId(String.valueOf(listMap.get(0).getId()));
                }

                final AlertDialog.Builder addBuilder = new AlertDialog.Builder(getActivity());

                addBuilder
                        .setTitle(R.string.add_map)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // edit was selected
                                if (edit) {

                                    if (!addMapView.updateMapDetails())
                                        toastLong(R.string.fix_error);
                                    else
                                        mHandler.post(refreshMapList);
                                } else {

                                    if (!addMapView.addMapDetails())
                                        toastLong(R.string.fix_error);
                                    else
                                        mHandler.post(fetchMapList);
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
     * Load Map details from the web
     */
    class LoadMapTask extends ProgressTask {

        protected Boolean status;

        protected Context appContext;

        private Maps maps;

        protected String distance;

        protected Location location;

        public LoadMapTask(ListFragment activity) {
            super(activity, R.string.loading_);
            // switch to a progress animation
            refreshState = true;
            maps = new Maps(appContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
            refreshState = true;
            updateRefreshStatus();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                status = maps.fetchMaps(distance, location);

                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {

                toastShort(R.string.could_not_fetch_data);
            } else {

                toastShort(R.string.deployment_fetched_successful);
            }
            mListMapAdapter.refresh(getActivity());
            mListMapView.mListView.setAdapter(mListMapAdapter);
            mListMapView.displayEmptyListText();
            refreshState = false;
            updateRefreshStatus();
        }

    }

    /**
     * Load the map's report
     */

    class FetchMapReportTask extends ProgressTask {

        protected String id;

        protected Integer status;

        public FetchMapReportTask(ListFragment activity) {
            super(activity, R.string.please_wait);
            // pass custom loading message to super call
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (id != null)
                    mListMapModel.activateDeployment(getActivity(), id);
                isCheckinsEnabled();
                if (Preferences.isCheckinEnabled == 0) {
                    status = ApiUtils.processReports(getActivity());
                } else {
                    status = ApiUtils.processCheckins(getActivity());
                }

                Thread.sleep(1000);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            this.dialog.cancel();
            try {
                if (success) {
                    if (status == 4) {
                        toastLong(R.string.internet_connection);
                    } else if (status == 3) {
                        toastLong(R.string.invalid_ushahidi_instance);
                    } else if (status == 2) {
                        toastLong(R.string.ushahidi_sync);
                    } else if (status == 1) {
                        toastLong(R.string.could_not_fetch_reports);
                    } else if (status == 0) {

                    }
                }
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "IllegalArgumentException " + e.toString());
            }
        }
    }

    @Override
    protected void onLoaded(boolean success) {

    }

    /** Location stuff **/
    // Fetches the current location of the device.
    protected void setDeviceLocation() {
        mLocationMgr = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

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
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
