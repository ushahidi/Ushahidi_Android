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

package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.ui.navdrawer.AboutNavDrawerItem;
import com.ushahidi.android.app.ui.navdrawer.AdminNavDrawerItem;
import com.ushahidi.android.app.ui.navdrawer.MapNavDrawerItem;
import com.ushahidi.android.app.ui.tablet.ListMapFragment;
import com.ushahidi.android.app.views.ListMapView;

/**
 * @author eyedol
 */
public class ListMapActivity extends BaseActivity<ListMapView> {

    private ListMapFragment mPostFragment;

    public ListMapActivity() {
        super(ListMapView.class, R.layout.map_activity, R.menu.list_map, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if (savedInstanceState == null) {
        // load list map fragment
        mPostFragment = new ListMapFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.show_map_fragment, mPostFragment).commit();
        // }
        createNavDrawer();
        navDrawerAdapter.addItem(new MapNavDrawerItem(getString(R.string.maps),
                R.drawable.map, ListMapActivity.this));

        navDrawerAdapter.addItem(new AdminNavDrawerItem(getString(R.string.admin),
                R.drawable.web, ListMapActivity.this));

        navDrawerAdapter.addItem(new AboutNavDrawerItem(getString(R.string.about),
                R.drawable.about, this));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState.isEmpty()) {
            outState.putBoolean(ListMapFragment.BUG_1997_FIX, true);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.list_map, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(listView)) {
                drawerLayout.closeDrawer(listView);
            } else {
                drawerLayout.openDrawer(listView);
            }

            return true;
        } else if (item.getItemId() == R.id.clear_map) {
            mPostFragment.createDialog(ListMapFragment.DIALOG_CLEAR_DEPLOYMENT);
            return true;
        } else if (item.getItemId() == R.id.menu_find) {

            mPostFragment.createDialog(ListMapFragment.DIALOG_DISTANCE);
            return true;
        } else if (item.getItemId() == R.id.menu_add) {
            mPostFragment.edit = false;
            mPostFragment.createDialog(ListMapFragment.DIALOG_ADD_DEPLOYMENT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return super.onContextItemSelected(item);
    }

}
