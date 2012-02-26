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

package com.ushahidi.android.app.fragments;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.ViewReportActivity2;
import com.ushahidi.android.app.adapters.ListReportAdapter;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.views.ListReportView;

/**
 * @author eyedol
 */
public class ListReportListFragment extends
        BaseListFragment<ListReportView, ListReportModel, ListReportAdapter> {

    private int mPositionChecked = 0;

    private int mPositionShown = 1;

    private ListReportView mListReportView;

    private ListReportAdapter mListReportAdapter;

    private ListReportModel mListReportModel;

    private Handler mHandler;

    private MenuItem refresh;

    private ArrayAdapter<String> spinnerArrayAdapter;

    private Vector<String> categories;

    private String filterCategory;

    private ListReportListFragmentListener listener = null;

    public ListReportListFragment() {
        super(ListReportView.class, ListReportAdapter.class, R.layout.list_report,
                R.menu.list_report, android.R.id.list);
        categories = new Vector<String>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mListReportView = new ListReportView(getActivity());
        mListReportAdapter = new ListReportAdapter(getActivity());
        mListReportModel = new ListReportModel();
        mHandler = new Handler();
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mPositionChecked = savedInstanceState.getInt("curChoice", 0);
            mPositionShown = savedInstanceState.getInt("shownChoice", -1);
        }

        /*
         * View detailsFrame = getActivity().findViewById(R.id.frame_details);
         * mHasReportDetailFrame = (detailsFrame != null) &&
         * (detailsFrame.getVisibility() == View.VISIBLE); if
         * (mHasReportDetailFrame) { // In dual-pane mode, the list view
         * highlights the selected item.
         * getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); // Make
         * sure our UI is in the correct state.
         * showReportDetails(mPositionChecked); }
         */

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("curChoice", mPositionChecked);
        outState.putInt("shownChoice", mPositionShown);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(fetchReportList);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (listener != null) {
            listener.onReportSelected(mListReportModel.reportModel.get(position));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        l.setItemChecked(position, true);

        if (listener != null) {
            listener.onReportSelected(mListReportModel.reportModel.get(position));
        }

        launchViewReport(position);
    }

    public void setListReportListListener(ListReportListFragmentListener listener) {
        this.listener = listener;
    }

    public void enablePersistentSelection() {
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refresh = item;
            new RefreshReports(getActivity()).execute((String)null);
            return true;
        } else if (item.getItemId() == R.id.menu_add) {
            // TODO start activity to add a report
            return true;
        } else if (item.getItemId() == R.id.filter_by) {

            showDropDownNav();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refresh the list view with new items
     */
    final Runnable fetchReportList = new Runnable() {
        public void run() {
            try {
                mListReportAdapter.refresh(getActivity());
                mListReportView.getPullToRefreshListView().setAdapter(mListReportAdapter);
                mListReportView.displayEmptyListText();
                showCategories();
            } catch (Exception e) {
                return;
            }
        }
    };

    public void refreshMapLists() {
        mListReportAdapter.refresh(getActivity());
        mListReportView.displayEmptyListText();
    }

    private void showDropDownNav() {

        if (categories != null && categories.size() > 0) {

            new AlertDialog.Builder(getActivity())
                    .setTitle(getActivity().getString(R.string.prompt_mesg))
                    .setAdapter(spinnerArrayAdapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            filterCategory = categories.get(which);
                            // FIXME implement a proper way of filtering by
                            // category
                            if ((filterCategory != null)
                                    && (!TextUtils.isEmpty(filterCategory))
                                    && (filterCategory != getActivity().getString(
                                            R.string.all_categories))) {
                                mListReportAdapter.refresh(getActivity(), filterCategory);
                                mListReportView.getPullToRefreshListView().setAdapter(
                                        mListReportAdapter);
                                mListReportView.displayEmptyListText();
                                // filterCategory = null;

                            } else {
                                mListReportAdapter.refresh(getActivity());
                                mListReportView.getPullToRefreshListView().setAdapter(
                                        mListReportAdapter);
                                mListReportView.displayEmptyListText();
                            }
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }

    public void showCategories() {
        categories = mListReportModel.getCategories(getActivity());
        spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, categories);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Refresh for new reports
     */
    class RefreshReports extends ProgressTask {

        public RefreshReports(Activity activity) {
            super(activity, R.string.loading_);
            // pass custom loading message to super call
            refresh.setActionView(R.layout.indeterminate_progress_action);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.cancel();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            refresh.setActionView(null);
            mListReportAdapter.refresh(getActivity());
            mListReportView.getPullToRefreshListView().setAdapter(mListReportAdapter);
            mListReportView.displayEmptyListText();

        }
    }

    @Override
    protected void onLoaded(boolean success) {

    }

    public void launchViewReport(int id) {
        Intent i = new Intent(getActivity(), ViewReportActivity2.class);
        i.putExtra("id", id);
        if (filterCategory != null
                && !filterCategory.equalsIgnoreCase(getString(R.string.all_categories))) {
            i.putExtra("category", filterCategory);
        } else {
            i.putExtra("category", "");
        }
        startActivityForResult(i, 0);

    }

}
