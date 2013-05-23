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
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.android.gms.samples.plus.PlusClientFragment.OnSignedInListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Example of listing people through the PlusClient.
 */
public class ListPeopleActivity extends FragmentActivity implements OnSignedInListener,
        PlusClient.OnPeopleLoadedListener {

    private static final String TAG = ListPeopleActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

    private ArrayAdapter mListAdapter;
    private ListView mPersonListView;
    private ArrayList<String> mListItems;
    private PlusClientFragment mPlusClientFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_list_activity);

        mListItems = new ArrayList<String>();
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                mListItems);
        mPersonListView = (ListView) findViewById(R.id.person_list);
        mPersonListView.setAdapter(mListAdapter);
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
        plusClient.loadPeople(this, Person.Collection.VISIBLE,
                Person.OrderBy.ALPHABETICAL, 10, null);
    }

    @Override
    public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer,
            String nextPageToken) {
        if (status.getErrorCode() == ConnectionResult.SUCCESS) {
            mListItems.clear();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    mListItems.add(personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }

            mListAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error when listing people: " + status);
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
}
