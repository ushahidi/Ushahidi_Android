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

package com.ushahidi.android.app.data;

public class CheckinsData {
    private int checkinId;

    private int checkinUserId;

    private String checkinMesg = "";

    private String checkinDate = "";

    private String checkinLat = "";

    private String checkinLon = "";

    private String checkinImage = "";

    public CheckinsData() {
        checkinId = 0;
        checkinUserId = 0;
        checkinMesg = "";
        checkinDate = "";
        checkinLat = "";
        checkinLon = "";
        checkinImage = "";
    }

    public int getCheckinId() {
        return this.checkinId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public int getCheckinUserId() {
        return this.checkinUserId;
    }

    public void setCheckinUserId(int checkinUserId) {
        this.checkinUserId = checkinUserId;
    }

    public String getCheckinMesg() {
        return this.checkinMesg;
    }

    public void setCheckinMesg(String checkinMesg) {
        this.checkinMesg = checkinMesg;
    }

    public String getCheckinDate() {
        return this.checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }

    public String getcheckinLat() {
        return this.checkinLat;
    }

    public void setCheckinLat(String checkinLat) {
        this.checkinLat = checkinLat;
    }
    
    public String getcheckinLon() {
        return this.checkinLon;
    }

    public void setCheckinLon(String checkinLon) {
        this.checkinLon = checkinLon;
    }
    
    public String getcheckinImage() {
        return this.checkinImage;
    }

    public void setCheckinImage(String checkinImage) {
        this.checkinImage = checkinImage;
    }

}
