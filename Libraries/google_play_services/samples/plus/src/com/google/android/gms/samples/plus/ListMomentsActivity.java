/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.plus;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnMomentsLoadedListener;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.moments.MomentBuffer;
import com.google.android.gms.samples.plus.PlusClientFragment.OnSignedInListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Example of listing the current user's moments through PlusClient.
 */
public class ListMomentsActivity extends FragmentActivity implements OnSignedInListener,
        OnMomentsLoadedListener, AdapterView.OnItemClickListener {
    private static final String TAG = MomentActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

    private ListView mMomentListView;
    private MomentListAdapter mMomentListAdapter;
    private ArrayList<Moment> mListItems;
    private ArrayList<Moment> mPendingDeletion;

    private PlusClientFragment mPlusClientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_moments_activity);

        mPendingDeletion = new ArrayList<Moment>();
        mListItems = new ArrayList<Moment>();
        mMomentListAdapter = new MomentListAdapter(this, android.R.layout.simple_list_item_1,
                mListItems);
        mMomentListView = (ListView) findViewById(R.id.moment_list);
        mMomentListView.setAdapter(mMomentListAdapter);
        mMomentListView.setOnItemClickListener(this);
        mPlusClientFragment = PlusClientFragment.getPlusClientFragment(this,
                MomentUtil.VISIBLE_ACTIVITIES);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
    }

    /**
     * Called when the {@link com.google.android.gms.plus.PlusClient} has been connected
     * successfully.
     *
     * @param plusClient The connected {@link PlusClient} for making API requests.
     */
    @Override
    public void onSignedIn(PlusClient plusClient) {
        int deleteCount = mPendingDeletion.size();
        for (int i = 0; i < deleteCount; i++) {
            plusClient.removeMoment(mPendingDeletion.get(i).getId());
        }

        mPendingDeletion.clear();
        plusClient.loadMoments(this);
    }

    @Override
    public void onMomentsLoaded(ConnectionResult status, MomentBuffer momentBuffer,
            String nextPageToken, String updated) {
        if (status.getErrorCode() == ConnectionResult.SUCCESS) {
            mListItems.clear();
            try {
                int count = momentBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    mListItems.add(momentBuffer.get(i).freeze());
                }
            } finally {
                momentBuffer.close();
            }

            mMomentListAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error when loading moments: " + status.getErrorCode());
        }
    }
    /**
     * Delete a moment when clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Moment moment = mMomentListAdapter.getItem(position);
        if (moment != null) {
            mPendingDeletion.add(moment);
            Toast.makeText(this, getString(R.string.plus_remove_moment_status),
                    Toast.LENGTH_SHORT).show();
            mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPlusClientFragment.handleOnActivityResult(requestCode, resultCode, data)) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    // User canceled sign in.
                    Toast.makeText(this, R.string.greeting_status_sign_in_required,
                            Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    }

    /**
     * Array adapter that maintains a Moment list.
     */
    private class MomentListAdapter extends ArrayAdapter<Moment> {

        private ArrayList<Moment> items;
        public MomentListAdapter(Context context, int textViewResourceId,
                ArrayList<Moment> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.moment_row, null);
            }
            Moment moment = items.get(position);
            if (moment != null) {
                TextView typeView = (TextView) v.findViewById(R.id.moment_type);
                TextView titleView = (TextView) v.findViewById(R.id.moment_title);

                String type = Uri.parse(moment.getType()).getPath().substring(1);
                typeView.setText(type);

                if (moment.getTarget() != null) {
                    titleView.setText(moment.getTarget().getName());
                }
            }

            return v;
        }
    }
}
