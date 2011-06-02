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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.ushahidi.android.app.data.IncidentsData;
import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.util.Util;

public class IncidentMap extends MapActivity {
    private MapView mapView = null;

    private static final int HOME = Menu.FIRST + 1;

    private static final int LIST_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_ADD = Menu.FIRST + 3;

    private static final int INCIDENT_REFRESH = Menu.FIRST + 4;

    private static final int SETTINGS = Menu.FIRST + 5;

    private static final int ABOUT = Menu.FIRST + 6;

    private static final int GOTOHOME = 0;

    private static final int ADD_INCIDENTS = 1;

    private static final int LIST_INCIDENTS = 2;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int REQUEST_CODE_ABOUT = 2;

    private static final int DIALOG_MESSAGE = 0;

    private static double latitude;

    private static double longitude;

    public static Geocoder gc;

    private List<IncidentsData> mNewIncidents;

    private List<IncidentsData> mOldIncidents;

    private Handler mHandler;

    private Bundle extras;

    private int id;

    private String reportLatitude;

    private String reportLongitude;

    private String reportTitle;

    private String reportDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.incidents_map);
        mapView = (MapView)findViewById(R.id.map);
        UshahidiPref.loadSettings(this);
        mOldIncidents = new ArrayList<IncidentsData>();
        mNewIncidents = new ArrayList<IncidentsData>();

        mNewIncidents = showIncidents(getString(R.string.all_categories));
        mHandler = new Handler();

        Bundle incidents = getIntent().getExtras();

        if (incidents != null) {
            extras = incidents.getBundle("report");
            id = extras.getInt("id");
            reportTitle = extras.getString("title");
            reportDescription = extras.getString("desc");
            reportLatitude = extras.getString("latitude");
            reportLongitude = extras.getString("longitude");
        }

        if (mNewIncidents.size() > 0) {
            if (id > 0) {
                if (!UshahidiPref.deploymentLatitude.equals("0.0")
                        && !UshahidiPref.deploymentLatitude.equals("0.0")) {
                    IncidentMap.latitude = Double.parseDouble(UshahidiPref.deploymentLatitude);
                    IncidentMap.longitude = Double.parseDouble(UshahidiPref.deploymentLongitude);
                    
                } else {
                    IncidentMap.latitude = Double.parseDouble(reportLatitude);
                    IncidentMap.longitude = Double.parseDouble(reportLongitude);
                    
                }
            } else {
                if (!UshahidiPref.deploymentLatitude.equals("0.0")
                        && !UshahidiPref.deploymentLatitude.equals("0.0")) {
                    IncidentMap.latitude = Double.parseDouble(UshahidiPref.deploymentLatitude);
                    IncidentMap.longitude = Double.parseDouble(UshahidiPref.deploymentLongitude);
                    
                } else {
                    IncidentMap.latitude = Double.parseDouble(mNewIncidents.get(0)
                            .getIncidentLocLatitude());
                    IncidentMap.longitude = Double.parseDouble(mNewIncidents.get(0)
                            .getIncidentLocLongitude());
                }

            }

            mapView.getController()
                    .setCenter(getPoint(IncidentMap.latitude, IncidentMap.longitude));

            mapView.getController().setZoom(10);

            mapView.setBuiltInZoomControls(true);

            mHandler.post(mMarkersOnMap);

        } else {
            Util.showToast(IncidentMap.this, R.string.no_reports);
        }

    }

    /**
     * add marker to the map
     */
    private void populateMap() {
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mapView.getOverlays().add(new SitesOverlay(marker, mapView));
    }

    // put this stuff in a seperate thread
    final Runnable mMarkersOnMap = new Runnable() {
        public void run() {
            populateMap();
        }
    };

    @Override
    protected boolean isRouteDisplayed() {
        return (false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_I) {
            // Zoom not closer than possible
            mapView.getController().zoomIn();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_O) {
            // Zoom not farer than possible
            mapView.getController().zoomOut();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_T) {
            // Switch to satellite view
            mapView.setSatellite(true);

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_M) {
            // Switch to satellite view
            mapView.setSatellite(false);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // get incidents from the db
    public List<IncidentsData> showIncidents(String by) {

        Cursor cursor;
        String title;
        String description;
        String location;
        String categories;
        String thumbnail;
        String image;

        if (by.equals(getString(R.string.all_categories)))
            cursor = UshahidiApplication.mDb.fetchAllIncidents();
        else
            cursor = UshahidiApplication.mDb.fetchIncidentsByCategories(by);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_TITLE);
            int dateIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_DATE);
            int verifiedIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_VERIFIED);
            int locationIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_NAME);

            int descIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_DESC);

            int categoryIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_CATEGORIES);

            int mediaIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_MEDIA);

            int imageIndex = cursor.getColumnIndex(UshahidiDatabase.INCIDENT_IMAGE);

            int latitudeIndex = cursor
                    .getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LATITUDE);

            int longitudeIndex = cursor
                    .getColumnIndexOrThrow(UshahidiDatabase.INCIDENT_LOC_LONGITUDE);

            do {

                IncidentsData incidentData = new IncidentsData();
                mOldIncidents.add(incidentData);

                int id = Util.toInt(cursor.getString(idIndex));
                incidentData.setIncidentId(id);

                title = Util.capitalizeString(cursor.getString(titleIndex));
                incidentData.setIncidentTitle(title);

                description = cursor.getString(descIndex);
                incidentData.setIncidentDesc(description);

                categories = cursor.getString(categoryIndex);
                incidentData.setIncidentCategories(categories);

                location = cursor.getString(locationIndex);
                incidentData.setIncidentLocLongitude(location);

                Util.joinString(getString(R.string.report_date), cursor.getString(dateIndex));
                incidentData.setIncidentDate(cursor.getString(dateIndex));

                thumbnail = cursor.getString(mediaIndex);
                incidentData.setIncidentThumbnail(thumbnail);

                image = cursor.getString(imageIndex);
                incidentData.setIncidentImage(image);

                incidentData.setIncidentVerified(Util.toInt(cursor.getString(verifiedIndex)));

                incidentData.setIncidentLocLatitude(cursor.getString(latitudeIndex));
                incidentData.setIncidentLocLongitude(cursor.getString(longitudeIndex));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return mOldIncidents;

    }

    /**
     * Restart the receiving, when we are back on line.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mNewIncidents.size() == 0) {
            mHandler.post(mMarkersOnMap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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
        // applyMenuChoice(item);

        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }

    protected Dialog onCreateDialog(int id, String message, String title) {
        switch (id) {
            case DIALOG_MESSAGE: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }
        }
        return null;
    }

    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.ushahidi_home);

        i = menu.add(Menu.NONE, INCIDENT_ADD, Menu.NONE, R.string.incident_menu_add);
        i.setIcon(R.drawable.ushahidi_add);

        i = menu.add(Menu.NONE, LIST_INCIDENT, Menu.NONE, R.string.incident_list);
        i.setIcon(R.drawable.ushahidi_list);

        i = menu.add(Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.incident_menu_refresh);
        i.setIcon(R.drawable.ushahidi_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.ushahidi_about);

    }

    // thread class
    private class ReportsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        protected Context appContext;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);

        }

        @Override
        protected Integer doInBackground(Void... params) {
            status = Util.processReports(appContext);
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 4) {

                Util.showToast(appContext, R.string.internet_connection);
            } else if (result == 0) {
                mNewIncidents = showIncidents(getString(R.string.all_categories));
                populateMap();
                setProgressBarIndeterminateVisibility(false);
            }
        }

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(IncidentMap.this, Ushahidi.class);
                startActivityForResult(intent, GOTOHOME);
                return true;
            case INCIDENT_REFRESH:
                ReportsTask reportsTask = new ReportsTask();
                reportsTask.appContext = this;
                reportsTask.execute();
                return (true);

            case LIST_INCIDENT:
                extras = new Bundle();
                extras.putInt("tab_index", 0);
                intent = new Intent(IncidentMap.this, IncidentsTab.class);
                intent.putExtra("tab", extras);
                startActivityForResult(intent, LIST_INCIDENTS);
                return (true);

            case INCIDENT_ADD:
                intent = new Intent(IncidentMap.this, AddIncident.class);
                startActivityForResult(intent, ADD_INCIDENTS);
                return (true);

            case ABOUT:
                intent = new Intent(IncidentMap.this, About.class);
                startActivityForResult(intent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                intent = new Intent(IncidentMap.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return (true);
        }
        return false;
    }

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    private class SitesOverlay extends UshahidiItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

        public SitesOverlay(Drawable marker, MapView mapView) {
            super(boundCenterBottom(marker), mapView, IncidentMap.this, mNewIncidents, extras);
            mapView.getContext();

            if (id > 0) {
                IncidentMap.latitude = Double.parseDouble(reportLatitude);
                IncidentMap.longitude = Double.parseDouble(reportLongitude);

                items.add(new OverlayItem(getPoint(IncidentMap.latitude, IncidentMap.longitude),
                        reportTitle, Util.limitString(reportDescription, 30)));
            } else {

                for (IncidentsData incidentData : mNewIncidents) {
                    IncidentMap.latitude = Double
                            .parseDouble(incidentData.getIncidentLocLatitude());
                    IncidentMap.longitude = Double.parseDouble(incidentData
                            .getIncidentLocLongitude());

                    items.add(new OverlayItem(
                            getPoint(IncidentMap.latitude, IncidentMap.longitude), incidentData
                                    .getIncidentTitle(), Util.limitString(
                                    incidentData.getIncidentDesc(), 30)));

                }
            }

            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return items.get(i);
        }

        @Override
        protected boolean onBalloonTap(int i) {
            return true;
        }

        @Override
        public int size() {
            return (items.size());
        }
    }
}
