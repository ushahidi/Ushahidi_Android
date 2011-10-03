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

package com.ushahidi.android.app.checkin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.ushahidi.android.app.About;
import com.ushahidi.android.app.Dashboard;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.IncidentAdd;
import com.ushahidi.android.app.IncidentTab;
import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.data.Database;
import com.ushahidi.android.app.ui.PullToRefreshListView;
import com.ushahidi.android.app.ui.PullToRefreshListView.OnRefreshListener;
import com.ushahidi.android.app.util.Util;

public class CheckinList extends Activity {

    /** Called when the activity is first created. */
    private PullToRefreshListView listCheckins = null;

    private CheckinAdapter ila;

    private static final int HOME = Menu.FIRST + 1;

    private static final int ADD_INCIDENT = Menu.FIRST + 2;

    private static final int INCIDENT_MAP = Menu.FIRST + 3;

    private static final int INCIDENT_REFRESH = Menu.FIRST + 4;

    private static final int SETTINGS = Menu.FIRST + 5;

    private static final int ABOUT = Menu.FIRST + 6;

    private static final int GOTOHOME = 0;

    private static final int POST_INCIDENT = 1;

    private static final int INCIDENTS_MAP = 2;

    private static final int VIEW_CHECKINS = 3;

    private static final int REQUEST_CODE_SETTINGS = 4;

    private static final int REQUEST_CODE_ABOUT = 5;

    private Bundle checkinsBundle = new Bundle();

    private final Handler mHandler = new Handler();

    public static Database mDb;

    private List<Checkin> checkins;

    private TextView emptyListText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin_list);

        listCheckins = (PullToRefreshListView)findViewById(R.id.list_checkins);
        emptyListText = (TextView)findViewById(R.id.empty);
        listCheckins.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                refreshForNewCheckins();
            }
        });
        checkins = new ArrayList<Checkin>();
        ila = new CheckinAdapter(this);
        displayEmptyListText();
        listCheckins.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int positions, long id) {
                int position = positions - 1;
                if (checkins != null) {
                    checkinsBundle.putString("name", checkins.get(position).getName());
                    checkinsBundle.putString("message", checkins.get(position).getMsg());
                    checkinsBundle.putString("longitude", checkins.get(position).getLon());
                    checkinsBundle.putString("latitude", checkins.get(position).getLat());
                    checkinsBundle.putString("date", checkins.get(position).getDate());
                    checkinsBundle.putString("photo", checkins.get(position).getImage());
                }

                Intent intent = new Intent(CheckinList.this, CheckinView.class);
                intent.putExtra("checkins", checkinsBundle);
                startActivityForResult(intent, VIEW_CHECKINS);
                setResult(RESULT_OK, intent);

            }

        });
        refreshForNewCheckins();

    }

    public void displayEmptyListText() {

        if (ila.getCount() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ila.getCount() == 0) {
            mHandler.post(mDisplayCheckins);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    final Runnable mDisplayCheckins = new Runnable() {
        public void run() {
            // setProgressBarIndeterminateVisibility(true);
            showCheckins();
            try {
                // setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                return; // means that the dialog is not showing, ignore please!
            }
        }
    };

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

    public void onAddReport(View v) {

        Preferences.loadSettings(CheckinList.this);
        if (Preferences.isCheckinEnabled == 1) {
            Intent checkinActivityIntent = new Intent().setClass(CheckinList.this,
                    CheckinActivity.class);
            startActivity(checkinActivityIntent);
            setResult(RESULT_OK);

        } else {
            Intent intent = new Intent(CheckinList.this, IncidentAdd.class);
            startActivityForResult(intent, 0);
            setResult(RESULT_OK);
        }

    }

    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.menu_home);

        i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.checkin_btn);
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

    private void refreshForNewCheckins() {
        CheckinsTask checkinsTask = new CheckinsTask();
        checkinsTask.appContext = this;
        checkinsTask.execute();
    }

    /**
     * Handle the click on the refresh button.
     *
     * @return void
     */
    public void onRefreshReports() {
        refreshForNewCheckins();
    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(CheckinList.this, Dashboard.class);
                startActivityForResult(intent, GOTOHOME);
                return true;
            case INCIDENT_REFRESH:
                refreshForNewCheckins();
                return (true);

            case INCIDENT_MAP:
                checkinsBundle.putInt("tab_index", 1);
                intent = new Intent(CheckinList.this, IncidentTab.class);
                intent.putExtra("tab", checkinsBundle);
                startActivityForResult(intent, INCIDENTS_MAP);
                return (true);

            case ADD_INCIDENT:
                intent = new Intent(CheckinList.this, CheckinActivity.class);
                startActivityForResult(intent, POST_INCIDENT);
                return (true);

            case ABOUT:
                intent = new Intent(CheckinList.this, About.class);
                startActivityForResult(intent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                intent = new Intent(CheckinList.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return (true);

        }
        return (false);
    }

    // thread class
    private class CheckinsTask extends AsyncTask<Void, Void, Integer> {

        protected Integer status;

        protected Context appContext;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            status = Util.processCheckins(appContext);
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 4) {
                Util.showToast(appContext, R.string.internet_connection);
            } else if (result == 3) {
                Util.showToast(appContext, R.string.invalid_ushahidi_instance);
            } else if (result == 2) {
                Util.showToast(appContext, R.string.could_not_fetch_checkin);
            } else if (result == 1) {
                Util.showToast(appContext, R.string.could_not_fetch_checkin);
            } else if (result == 0) {
                showCheckins();
            }
            listCheckins.onRefreshComplete();
        }

    }

    // get checkins from the db
    public void showCheckins() {

        Cursor cursor;

        cursor = MainApplication.mDb.fetchAllCheckins();
        String name;
        String date;
        String mesg;
        String location;
        Drawable d = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_USER_ID);
            int dateIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_DATE);
            int locationIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_LOC_NAME);

            int mesgIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_MESG);

            int latitudeIndex = cursor.getColumnIndexOrThrow(Database.CHECKIN_LOC_LATITUDE);

            int longitudeIndex = cursor
                    .getColumnIndexOrThrow(Database.CHECKIN_LOC_LONGITUDE);

            ila.removeItems();
            ila.notifyDataSetChanged();

            checkins.clear();

            do {

                Checkin checkinsData = new Checkin();
                checkins.add(checkinsData);
                CheckinItem listText = new CheckinItem();

                int id = Util.toInt(cursor.getString(idIndex));
                checkinsData.setId(String.valueOf(id));
                checkinsData.setLat(cursor.getString(latitudeIndex));
                checkinsData.setLon(cursor.getString(longitudeIndex));

                name = cursor.getString(userIdIndex);
                checkinsData.setName(name);
                listText.setTitle(Util.capitalize(checkinsData.getName()));

                mesg = cursor.getString(mesgIndex);
                checkinsData.setMsg(mesg);
                listText.setDesc(Util.capitalizeString(checkinsData.getMsg()));

                location = cursor.getString(locationIndex);
                checkinsData.setLoc(location);
                listText.setLocation(Util.capitalize(location));

                date = Util.formatDate("yyyy-MM-dd hh:mm:ss", cursor.getString(dateIndex),
                        "MMMM dd, yyyy 'at' hh:mm:ss aaa");

                checkinsData.setDate(date);
                listText.setDate(date);

                checkinsData.setImage(String.valueOf(id));
                checkinsData.setThumbnail(String.valueOf(id));

                if (!TextUtils.isEmpty(checkinsData.getThumbnail())) {
                    Log.i("CLASS_TAG", " Image path: " + Preferences.savePath + " File path "
                            + checkinsData.getThumbnail());
                    d = ImageManager.getImages(Preferences.savePath, checkinsData.getThumbnail());
                }
                else {
                    d = null;
                }
                //THUMBNAIL
                if (d != null) {
                    listText.setThumbnail(d);
                }
                else {
                    listText.setThumbnail(getResources().getDrawable(R.drawable.report_icon));
                }
                listText.setId(id);
                listText.setArrow(getResources().getDrawable(R.drawable.menu_arrow));
                ila.addItem(listText);

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();
        listCheckins.setAdapter(ila);
        displayEmptyListText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
