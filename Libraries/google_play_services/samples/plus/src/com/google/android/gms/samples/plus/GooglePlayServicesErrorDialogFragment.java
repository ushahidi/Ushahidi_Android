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

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Wraps the {@link Dialog} returned by {@link GooglePlayServicesUtil#getErrorDialog}
 * so that it can be properly managed by the {@link android.app.Activity}.
 */
public class GooglePlayServicesErrorDialogFragment extends DialogFragment {

    /**
     * The error code returned by the
     * {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)} call.
     */
    public static final String ARG_ERROR_CODE = "errorCode";

    /**
     * The request code given when calling {@link android.app.Activity#startActivityForResult}.
     */
    public static final String ARG_REQUEST_CODE = "requestCode";

    /**
     * Returns a {@link Dialog} created by {@link GooglePlayServicesUtil#getErrorDialog} with the
     * provided errorCode, activity, and request code.
     *
     * @param savedInstanceState Not used.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        return GooglePlayServicesUtil.getErrorDialog(args.getInt(ARG_ERROR_CODE), getActivity(),
                args.getInt(ARG_REQUEST_CODE));
    }

    /**
     * Create a {@link DialogFragment} for displaying the
     * {@link GooglePlayServicesUtil#getErrorDialog}.
     *
     * @param errorCode The error code returned by
     *     {@link com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener}
     * @param requestCode The request code for resolving the resolution activity.
     * @return The {@link DialogFragment}.
     */
    public static Bundle createArguments(int errorCode, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(GooglePlayServicesErrorDialogFragment.ARG_ERROR_CODE, errorCode);
        args.putInt(GooglePlayServicesErrorDialogFragment.ARG_REQUEST_CODE, requestCode);
        return args;
    }
}
