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

import android.location.Location;
import android.os.Bundle;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.AddCheckinModel;
import com.ushahidi.android.app.views.AddCheckinView;

/**
 * @author eyedol
 */
public class AddCheckinActivity extends BaseEditMapActivity<AddCheckinView, AddCheckinModel> {

    public AddCheckinActivity() {
        super(AddCheckinView.class, R.layout.add_checkin, R.menu.add_checkin,
                R.id.checkin_location_map);
        // TODO Auto-generated constructor stub
    }

    

    @Override
    protected boolean onSaveChanges() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }



    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        
    }



    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        
    }



    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        
    }



    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        
    }

}
