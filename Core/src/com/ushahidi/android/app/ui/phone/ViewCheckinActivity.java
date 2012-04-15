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
package com.ushahidi.android.app.ui.phone;

import android.location.Location;
import android.os.Bundle;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseMapViewActivity;
import com.ushahidi.android.app.models.ViewCheckinModel;
import com.ushahidi.android.app.views.ViewCheckinView;

/**
 * @author eyedol
 *
 */
public class ViewCheckinActivity extends BaseMapViewActivity<ViewCheckinView,ViewCheckinModel>{

    public ViewCheckinActivity() {
        super(ViewCheckinView.class, R.layout.view_checkin, R.menu.view_checkin, R.id.loc_map );
        // TODO Auto-generated constructor stub
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

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

	/* (non-Javadoc)
	 * @see com.ushahidi.android.app.MapUserLocation#locationChanged(double, double)
	 */
	@Override
	protected void locationChanged(double latitude, double longitude) {
		// TODO Auto-generated method stub
		
	}

    

}
