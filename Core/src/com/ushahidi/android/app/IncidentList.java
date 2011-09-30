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
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ushahidi.android.app.data.Database;
import com.ushahidi.android.app.data.IncidentsData;
import com.ushahidi.android.app.ui.PullToRefreshListView;
import com.ushahidi.android.app.ui.PullToRefreshListView.OnRefreshListener;
import com.ushahidi.android.app.util.Util;

public class IncidentList extends Activity {

    /** Called when the activity is first created. */
    private PullToRefreshListView listIncidents = null;

    private IncidentAdapter ila;

    private static final int HOME = Menu.FIRST + 1;

    private static final int ADD_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_MAP = Menu.FIRST + 3;

    private static final int INCIDENT_REFRESH = Menu.FIRST + 4;

    private static final int SETTINGS = Menu.FIRST + 5;

    private static final int ABOUT = Menu.FIRST + 6;

    private static final int GOTOHOME = 0;

    private static final int POST_INCIDENT = 1;

    private static final int INCIDENTS_MAP = 2;

    private static final int VIEW_INCIDENT = 3;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int REQUEST_CODE_ABOUT = 2;

    private Spinner spinner = null;

    private ArrayAdapter<String> spinnerArrayAdapter;

    private Bundle incidentsBundle = new Bundle();

    private final Handler mHandler = new Handler();

    public static Database mDb;

    private List<IncidentsData> mOldIncidents;

    private Vector<String> vectorCategories = new Vector<String>();
    private TextView emptyListText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incident_list);

        listIncidents = (PullToRefreshListView)findViewById(R.id.view_incidents);
        emptyListText = (TextView) findViewById(R.id.empty_list_for_reports);
        
        mOldIncidents = new ArrayList<IncidentsData>();
        ila = new IncidentAdapter(this);
        listIncidents.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int positions, long id) {
                
                //It seems pull to refresh list is buggy; The list item position is by 1 higher
                //TODO Look into fixing this.
                int position = positions - 1;
                
                incidentsBundle.putInt("id", mOldIncidents.get(position).getIncidentId());
                incidentsBundle.putString("title", mOldIncidents.get(position).getIncidentTitle());
                incidentsBundle.putString("desc", mOldIncidents.get(position).getIncidentDesc());
                incidentsBundle.putString("longitude", mOldIncidents.get(position)
                        .getIncidentLocLongitude());
                incidentsBundle.putString("latitude", mOldIncidents.get(position)
                        .getIncidentLocLatitude());
                incidentsBundle.putString("category", mOldIncidents.get(position)
                        .getIncidentCategories());
                incidentsBundle.putString("location", mOldIncidents.get(position)
                        .getIncidentLocation());
                incidentsBundle.putString("date", mOldIncidents.get(position).getIncidentDate());
                incidentsBundle.putString("media", mOldIncidents.get(position)
                        .getIncidentThumbnail());
                incidentsBundle.putString("image", mOldIncidents.get(position).getIncidentImage());
                incidentsBundle.putString("status", ""
                        + mOldIncidents.get(position).getIncidentVerified());

                Intent intent = new Intent(IncidentList.this, IncidentView.class);
                intent.putExtra("incidents", incidentsBundle);
                startActivityForResult(intent, VIEW_INCIDENT);
                setResult(RESULT_OK, intent);

            }

        });
        
        listIncidents.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                refreshForReports();
            }
        });
        
        spinner = (Spinner)findViewById(R.id.incident_cat);

        mHandler.post(mDisplayIncidents);
        mHandler.post(mDisplayCategories);
        // mark all incidents as read
        MainApplication.mDb.markAllIncidentssRead();
        MainApplication.mDb.markAllCategoriesRead();
        displayEmptyListText();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ila.getCount() == 0) {
            mHandler.post(mDisplayIncidents);
            mHandler.post(mDisplayCategories);

            // mark all incidents as read
            MainApplication.mDb.markAllIncidentssRead();
            MainApplication.mDb.markAllCategoriesRead();
        }
    }
    
    public  void displayEmptyListText() {

        if (ila.getCount() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    final Runnable mDisplayIncidents = new Runnable() {
        public void run() {
            setProgressBarIndeterminateVisibility(true);
            showIncidents(getString(R.string.all_categories));
            showCategories();
            try {
                setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return; // means that the dialog is not showing, ignore please!
            }
        }
    };

    final Runnable mDisplayCategories = new Runnable() {
        public void run() {
            showCategories();
        }
    };
    
    public void refreshForReports() {
        ReportsTask reportsTask = new ReportsTask();
        reportsTask.appContext = this;
        reportsTask.execute();
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

    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.menu_home);

        i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.incident_menu_add);
        i.setIcon(R.drawable.menu_add);

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.menu_map);

        i = menu.add(Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.menu_sync);
        i.setIcon(R.drawable.menu_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.menu_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.menu_about);

    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(IncidentList.this, Dashboard.class);
                startActivityForResult(intent, GOTOHOME);
                return true;
            case INCIDENT_REFRESH:
                refreshForReports();
                return (true);

            case INCIDENT_MAP:
                incidentsBundle.putInt("tab_index", 1);
                intent = new Intent(IncidentList.this, IncidentTab.class);
                intent.putExtra("tab", incidentsBundle);
                startActivityForResult(intent, INCIDENTS_MAP);
                return (true);

            case ADD_INCIDENT:
                intent = new Intent(IncidentList.this, IncidentAdd.class);
                startActivityForResult(intent, POST_INCIDENT);
                return (true);

            case ABOUT:
                intent = new Intent(IncidentList.this, About.class);
                startActivityForResult(intent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                intent = new Intent(IncidentList.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return (true);

        }
        return (false);
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
            } else if (result == 3) {
                Util.showToast(appContext, R.string.invalid_ushahidi_instance);
            } else if (result == 2) {
                Util.showToast(appContext, R.string.could_not_fetch_reports);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.could_not_fetch_reports);
            } else if (result == 0) {
                showIncidents(getString(R.string.all_categories));
                showCategories();
                Util.showToast(appContext, R.string.reports_successfully_fetched);
            }
            listIncidents.onRefreshComplete();
        }

    }

    // get incidents from the db
    public void showIncidents(String by) {

        Cursor cursor;
        if (by.equals(getString(R.string.all_categories)))
            cursor = MainApplication.mDb.fetchAllIncidents();
        else
            cursor = MainApplication.mDb.fetchIncidentsByCategories(by);

        String title;
        String status;
        String date;
        String description;
        String location;
        String categories;
        String media;
        String image;
        
        String thumbnails[];
        Drawable d = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_TITLE);
            int dateIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_DATE);
            int verifiedIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_VERIFIED);
            int locationIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_LOC_NAME);

            int descIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_DESC);

            int categoryIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_CATEGORIES);

            int mediaIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_MEDIA);

            int imageIndex = cursor.getColumnIndexOrThrow(Database.INCIDENT_IMAGE);

            int latitudeIndex = cursor
                    .getColumnIndexOrThrow(Database.INCIDENT_LOC_LATITUDE);

            int longitudeIndex = cursor
                    .getColumnIndexOrThrow(Database.INCIDENT_LOC_LONGITUDE);

            ila.removeItems();
            ila.notifyDataSetChanged();

            mOldIncidents.clear();

            do {

                IncidentsData incidentData = new IncidentsData();
                mOldIncidents.add(incidentData);
                IncidentItem incidentItem = new IncidentItem();

                int id = Util.toInt(cursor.getString(idIndex));
                incidentData.setIncidentId(id);
                incidentData.setIncidentLocLatitude(cursor.getString(latitudeIndex));
                incidentData.setIncidentLocLongitude(cursor.getString(longitudeIndex));

                title = cursor.getString(titleIndex);
                incidentData.setIncidentTitle(title);
                incidentItem.setTitle(Util.capitalize(title));

                description = cursor.getString(descIndex);
                incidentData.setIncidentDesc(description);
                incidentItem.setDesc(description);

                categories = cursor.getString(categoryIndex);
                incidentData.setIncidentCategories(categories);
                incidentItem.setCategories(Util.capitalize(categories));

                location = cursor.getString(locationIndex);
                incidentData.setIncidentLocation(location);
                incidentItem.setLocation(Util.capitalize(location));

                date = Util.formatDate("yyyy-MM-dd HH:mm:ss", cursor.getString(dateIndex),
                        "MMMM dd, yyyy 'at' hh:mm:ss aaa");

                incidentData.setIncidentDate(date);
                incidentItem.setDate(date);

                media = cursor.getString(mediaIndex);
                incidentData.setIncidentThumbnail(media);
                incidentItem.setMedia(media);

                thumbnails = media.split(",");
                // TODO do a proper check for thumbnails
                if (!TextUtils.isEmpty(thumbnails[0])) {
                    d = ImageManager.getImages(Preferences.savePath,thumbnails[0]);
                }
                else {
                    d = null;
                }

                if (d != null) {
                    incidentItem.setThumbnail(d);
                }
                else {
                    incidentItem.setThumbnail(getResources().getDrawable(R.drawable.report_icon));
                }

                image = cursor.getString(imageIndex);
                incidentData.setIncidentImage(image);

                status = Util.toInt(cursor.getString(verifiedIndex)) == 0
                        ? getString(R.string.report_unverified)
                        : getString(R.string.report_verified);
                incidentData.setIncidentVerified(Util.toInt(cursor.getString(verifiedIndex)));
                incidentItem.setStatus(status);

                incidentItem.setId(id);
                incidentItem.setArrow(getResources().getDrawable(R.drawable.menu_arrow));
                ila.addItem(incidentItem);

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();
        listIncidents.setAdapter(ila);
        displayEmptyListText();
    }

    public void showCategories() {
        Cursor cursor = MainApplication.mDb.fetchAllCategories();
        MainApplication.mDb.fetchCategoriesCount();

        vectorCategories.clear();
        vectorCategories.add(getString(R.string.all_categories));
        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndexOrThrow(Database.CATEGORY_TITLE);
            do {
                vectorCategories.add(cursor.getString(titleIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                vectorCategories);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(spinnerListener);

    }

    // spinner listener
    Spinner.OnItemSelectedListener spinnerListener = new Spinner.OnItemSelectedListener() {

        
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            // clear data in the list
            ila.removeItems();
            ila.notifyDataSetChanged();
            mOldIncidents.clear();
            showIncidents(vectorCategories.get(position));
        }

       
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
