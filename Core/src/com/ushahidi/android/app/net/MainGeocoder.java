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

package com.ushahidi.android.app.net;

import java.io.IOException;

import org.apache.http.HttpResponse;

import android.content.Context;

import com.ushahidi.android.app.R;

public class MainGeocoder extends MainHttpClient{

    /**
     * @param context
     */
    public MainGeocoder(Context context) {
        super(context);
    }

    private static final String GOOGLE_MAPS_GEO_URL = "http://maps.google.com/maps/geo?q=";

    /**
     * Reverse Geocode using google's geocode web service
     * 
     * @param latitude
     * @param longitude
     * @return String
     */
    public String reverseGeocode(double latitude, double longitude) throws IOException {

        HttpResponse response;

        StringBuilder uriBuilder = new StringBuilder(GOOGLE_MAPS_GEO_URL);
        uriBuilder.append(latitude + "," + longitude);
        uriBuilder.append("&output=json&oe=utf8&sensor=true&key=");
        uriBuilder.append(R.string.google_map_api_key);

        response = GetURL(uriBuilder.toString());

        if (response == null) {
            return null;
        }

        final int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200) {

            return GetText(response);
        } else {
            return null;
        }

    }
}
