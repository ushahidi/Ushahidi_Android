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

import java.util.ArrayList;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.samples.plus.PlusClientFragment.OnSignedInListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Example of writing moments through the PlusClient.
 */
public class MomentActivity extends FragmentActivity implements
        AdapterView.OnItemClickListener, OnSignedInListener {

    public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

    private ListAdapter mListAdapter;
    private ListView mMomentListView;
    private ArrayList<Moment> mPendingMoments;
    private PlusClientFragment mPlusClientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_moment_activity);

        mPlusClientFragment =
                PlusClientFragment.getPlusClientFragment(this, MomentUtil.VISIBLE_ACTIVITIES);
        mListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, MomentUtil.MOMENT_LIST);
        mMomentListView = (ListView) findViewById(R.id.moment_list);
        mMomentListView.setOnItemClickListener(this);
        mMomentListView.setAdapter(mListAdapter);
        mPendingMoments = new ArrayList<Moment>();
    }

    @Override
    public void onSignedIn(PlusClient plusClient) {
        mMomentListView.setAdapter(mListAdapter);
        if (!mPendingMoments.isEmpty()) {
            // Write all moments that were written while the client was disconnected.
            for (Moment pendingMoment : mPendingMoments) {
                plusClient.writeMoment(pendingMoment);
                Toast.makeText(this, getString(R.string.plus_write_moment_status),
                        Toast.LENGTH_SHORT).show();
            }
        }
        mPendingMoments.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) view;
        String momentType = (String) textView.getText();
        String targetUrl = MomentUtil.MOMENT_TYPES.get(momentType);

        ItemScope target = new ItemScope.Builder()
            .setUrl(targetUrl)
            .build();

        Moment.Builder momentBuilder = new Moment.Builder();
        momentBuilder.setType("http://schemas.google.com/" + momentType);
        momentBuilder.setTarget(target);

        ItemScope result = MomentUtil.getResultFor(momentType);
        if (result != null) {
          momentBuilder.setResult(result);
        }

        // Resolve the connection status, and write the moment once PlusClient is connected.
        mPendingMoments.add(momentBuilder.build());
        mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPlusClientFragment.handleOnActivityResult(requestCode, resultCode, data);
    }
}
