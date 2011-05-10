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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ushahidi.android.app.About;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.IncidentsTab;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.Ushahidi;
import com.ushahidi.android.app.UshahidiApplication;
import com.ushahidi.android.app.data.UshahidiDatabase;
import com.ushahidi.android.app.ui.PullToRefreshListView;
import com.ushahidi.android.app.ui.PullToRefreshListView.OnRefreshListener;
import com.ushahidi.android.app.util.Util;

public class ListCheckin extends Activity {

    /** Called when the activity is first created. */
    private PullToRefreshListView listCheckins = null;

    private ListCheckinAdapter ila;

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

    public static UshahidiDatabase mDb;

    private List<Checkin> checkins;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.list_checkins);

        listCheckins = (PullToRefreshListView)findViewById(R.id.list_checkins);
        
        listCheckins.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                refreshForNewCheckins();
            }
        });
        checkins = new ArrayList<Checkin>();
        ila = new ListCheckinAdapter(this);

        listCheckins.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                if (checkins != null) {
                    checkinsBundle.putString("name", checkins.get(position).getName());
                    checkinsBundle.putString("message", checkins.get(position).getMsg());
                    checkinsBundle.putString("longitude", checkins.get(position).getLon());
                    checkinsBundle.putString("latitude", checkins.get(position).getLat());
                    checkinsBundle.putString("date", checkins.get(position).getDate());
                    checkinsBundle.putString("photo", checkins.get(position).getImage());
                }

                Intent intent = new Intent(ListCheckin.this, ViewCheckins.class);
                intent.putExtra("checkins", checkinsBundle);
                startActivityForResult(intent, VIEW_CHECKINS);
                setResult(RESULT_OK, intent);

            }

        });
        refreshForNewCheckins();

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

    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.ushahidi_home);

        i = menu.add(Menu.NONE, ADD_INCIDENT, Menu.NONE, R.string.checkin_btn);
        i.setIcon(R.drawable.ushahidi_add);

        i = menu.add(Menu.NONE, INCIDENT_MAP, Menu.NONE, R.string.incident_menu_map);
        i.setIcon(R.drawable.ushahidi_map);

        i = menu.add(Menu.NONE, INCIDENT_REFRESH, Menu.NONE, R.string.menu_sync);
        i.setIcon(R.drawable.ushahidi_refresh);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_settings);

        i = menu.add(Menu.NONE, ABOUT, Menu.NONE, R.string.menu_about);
        i.setIcon(R.drawable.ushahidi_about);

    }

    private void refreshForNewCheckins() {
        CheckinsTask checkinsTask = new CheckinsTask();
        checkinsTask.appContext = this;
        checkinsTask.execute();
    }

    /**
     * Handle the click on the refresh button.
     * 
     * @param v View
     * @return void
     */
    public void onRefreshReports() {
        refreshForNewCheckins();
    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(ListCheckin.this, Ushahidi.class);
                startActivityForResult(intent, GOTOHOME);
                return true;
            case INCIDENT_REFRESH:
                refreshForNewCheckins();
                return (true);

            case INCIDENT_MAP:
                checkinsBundle.putInt("tab_index", 1);
                intent = new Intent(ListCheckin.this, IncidentsTab.class);
                intent.putExtra("tab", checkinsBundle);
                startActivityForResult(intent, INCIDENTS_MAP);
                return (true);

            case ADD_INCIDENT:
                intent = new Intent(ListCheckin.this, CheckinActivity.class);
                startActivityForResult(intent, POST_INCIDENT);
                return (true);

            case ABOUT:
                intent = new Intent(ListCheckin.this, About.class);
                startActivityForResult(intent, REQUEST_CODE_ABOUT);
                setResult(RESULT_OK);
                return true;

            case SETTINGS:
                intent = new Intent(ListCheckin.this, Settings.class);

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

        cursor = UshahidiApplication.mDb.fetchAllCheckins();
        String name;
        String date;
        String mesg;
        String location;
        Drawable d = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_ID);
            int userIdIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_USER_ID);
            int dateIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_DATE);
            int locationIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_LOC_NAME);

            int mesgIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_MESG);

            int latitudeIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_LOC_LATITUDE);

            int longitudeIndex = cursor
                    .getColumnIndexOrThrow(UshahidiDatabase.CHECKIN_LOC_LONGITUDE);

            ila.removeItems();
            ila.notifyDataSetChanged();

            checkins.clear();

            do {

                Checkin checkinsData = new Checkin();
                checkins.add(checkinsData);
                ListCheckinText listText = new ListCheckinText();

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
                    d = ImageManager.getImages(checkinsData.getThumbnail());
                } else {
                    d = null;
                }

                // set thumbnail
                listText.setThumbnail(d == null ? getResources().getDrawable(
                        R.drawable.ushahidi_report_icon) : d);

                listText.setId(id);
                listText.setArrow(getResources().getDrawable(R.drawable.ushahidi_arrow));
                ila.addItem(listText);

            } while (cursor.moveToNext());
        }

        cursor.close();
        ila.notifyDataSetChanged();
        listCheckins.setAdapter(ila);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
