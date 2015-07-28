package com.ushahidi.android.app.tasks;

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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

/**
 * An abstract Reverse Geocoder Task
 */
public abstract class GeocoderTask extends AsyncTask<Double, Void, String> {

    protected final Context context;
    protected boolean executing;

    public GeocoderTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        executing = true;
    }

    @Override
    protected String doInBackground(Double...location) {
        Log.i(getClass().getSimpleName(), String.format("doInBackground %s", Arrays.toString(location)));
        Geocoder geoCoder = new Geocoder(context);
        try {
            List<Address> addresses = geoCoder.getFromLocation(location[0], location[1], 1);
            StringBuilder addressString = new StringBuilder();
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getFeatureName() != null) {
                    addressString.append(address.getFeatureName());
                }
                if (address.getThoroughfare() != null) {
                    if (addressString.length() > 0) {
                        addressString.append(", ");
                    }
                    addressString.append(address.getThoroughfare());
                }
                if (address.getSubAdminArea() != null) {
                    if (addressString.length() > 0) {
                        addressString.append(", ");
                    }
                    addressString.append(address.getSubAdminArea());
                }
                if (address.getLocality() != null &&
                    !address.getLocality().equalsIgnoreCase(address.getSubAdminArea())) {
                    if (addressString.length() > 0) {
                        addressString.append(", ");
                    }
                    addressString.append(address.getLocality());
                }
                if (address.getCountryName() != null) {
                    if (addressString.length() > 0) {
                        addressString.append(", ");
                    }
                    addressString.append(address.getCountryName());
                }
            }
            return addressString.toString();
        }
        catch (IOException ioe) {
            Log.e(getClass().getSimpleName(), "IOException", ioe);
        }catch (IllegalArgumentException ioe) {
            Log.e(getClass().getSimpleName(), "IllegalArgumentException", ioe);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(getClass().getSimpleName(), String.format("onPostExecute %s", result));
        executing = false;
    }

    public boolean isExecuting() {
        return executing;
    }

}