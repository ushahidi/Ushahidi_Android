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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.BaseListAdapter;
import com.ushahidi.android.app.models.BaseModel;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.utils.Objects;

import java.lang.reflect.Type;

/**
 * BaseListActivity
 *
 * Add shared functionality that exists between all List Activities
 */
public abstract class BaseListActivity<M extends BaseModel, L extends BaseListAdapter<M>> extends BaseActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private final int listViewId;
    private L listAdapter;
    private ListView listView;

    protected BaseListActivity(int layoutId, int menuId, int listViewId) {
        super(layoutId, menuId);
        this.listViewId = listViewId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (listViewId != 0) {
            listView = findListViewById(listViewId);
            listView.setOnItemClickListener(this);
            View emptyView = findViewById(android.R.id.empty);
            if (emptyView != null) {
                listView.setEmptyView(emptyView);
            }
            //HACK to get the generic parameter type due to Java's reflection limitations
            Type genericType = Objects.getGenericType(this, 1);
            listAdapter = (L)Objects.createInstance(genericType, new Class []{Context.class}, new Object []{this});
            listView.setAdapter(listAdapter);
            listView.setFocusable(true);
            listView.setFocusableInTouchMode(true);
        }
    }

    protected abstract void onLoaded();

    @Override
	protected void onResume(){
		super.onResume();
		new LoadingTask(this).execute((String)null);
    }

    @SuppressWarnings("unchecked")
	protected M getItem(int position) {
		return (M)listView.getItemAtPosition(position);
	}

    @SuppressWarnings("unchecked")
    protected M getSelectedItem() {
		return (M)listView.getSelectedItem();
	}

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {}

    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * ProgressTask sub-class for showing Loading... dialog while the BaseListAdapter loads the data
     */
    protected class LoadingTask extends ProgressTask {
        public LoadingTask(Activity activity) {
            super(activity, R.string.loading_);
        }

        @Override
        protected Boolean doInBackground(String...args) {
            listAdapter.refresh(activity);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                //call the onLoaded method which all BaseListActivity sub-classes must implement
                onLoaded();
            }
        }
    }

}
