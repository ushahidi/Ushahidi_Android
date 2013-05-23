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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.samples.plus.PlusClientFragment.OnSignedInListener;

/**
 * Example of sharing with Google+ through the ACTION_SEND intent.
 */
public class ShareActivity extends FragmentActivity implements View.OnClickListener,
        OnSignedInListener {

    protected static final String TAG = ShareActivity.class.getSimpleName();

    private static final String STATE_SHARING = "resolving_error";

    private static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 1;
    private static final int REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR = 2;
    private static final int REQUEST_CODE_INTERACTIVE_POST = 3;

    /** The button should say "View item" in English. */
    private static final String LABEL_VIEW_ITEM = "VIEW_ITEM";

    private EditText mEditSendText;
    private Button mSendButton;
    private PlusClientFragment mPlusClientFragment;
    private boolean mSharing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        mSendButton = (Button) findViewById(R.id.send_interactive_button);
        mSendButton.setOnClickListener(this);
        mSendButton.setEnabled(true);
        mEditSendText = (EditText) findViewById(R.id.share_prefill_edit);
        mPlusClientFragment =
                PlusClientFragment.getPlusClientFragment(this, MomentUtil.VISIBLE_ACTIVITIES);
        mSharing =
                savedInstanceState != null && savedInstanceState.getBoolean(STATE_SHARING, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHARING, mSharing);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_interactive_button:
                // Set sharing so that the share is started in onSignedIn.
                mSharing = true;
                mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
                break;
        }
    }

    @Override
    public void onSignedIn(PlusClient plusClient) {
        if (!mSharing) {
            // The share button hasn't been clicked yet.
            return;
        }

        // Reset sharing so future calls to onSignedIn don't start a share.
        mSharing = false;
        final int errorCode = GooglePlusUtil.checkGooglePlusApp(this);
        if (errorCode == GooglePlusUtil.SUCCESS) {
            startActivityForResult(getInteractivePostIntent(plusClient),
                    REQUEST_CODE_INTERACTIVE_POST);
        } else {
            // Prompt the user to install the Google+ app.
            GooglePlusErrorDialogFragment
                    .create(errorCode, REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR)
                    .show(getSupportFragmentManager(), TAG_ERROR_DIALOG_FRAGMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (mPlusClientFragment.handleOnActivityResult(requestCode, resultCode, intent)) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_INTERACTIVE_POST:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Failed to create interactive post");
                }
                break;
            case REQUEST_CODE_RESOLVE_GOOGLE_PLUS_ERROR:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Unable to recover from missing Google+ app.");
                } else {
                    mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
                }
                break;
        }
    }

    private Intent getInteractivePostIntent(PlusClient plusClient) {
        // Create an interactive post with the "VIEW_ITEM" label. This will
        // create an enhanced share dialog when the post is shared on Google+.
        // When the user clicks on the deep link, ParseDeepLinkActivity will
        // immediately parse the deep link, and route to the appropriate resource.
        String action = "/?view=true";
        Uri callToActionUrl = Uri.parse(getString(R.string.plus_example_deep_link_url) + action);
        String callToActionDeepLinkId = getString(R.string.plus_example_deep_link_id) + action;

        // Create an interactive post builder.
        PlusShare.Builder builder = new PlusShare.Builder(this, plusClient);

        // Set call-to-action metadata.
        builder.addCallToAction(LABEL_VIEW_ITEM, callToActionUrl, callToActionDeepLinkId);

        // Set the target url (for desktop use).
        builder.setContentUrl(Uri.parse(getString(R.string.plus_example_deep_link_url)));

        // Set the target deep-link ID (for mobile use).
        builder.setContentDeepLinkId(getString(R.string.plus_example_deep_link_id),
                null, null, null);

        // Set the pre-filled message.
        builder.setText(mEditSendText.getText().toString());

        return builder.getIntent();
    }
}
