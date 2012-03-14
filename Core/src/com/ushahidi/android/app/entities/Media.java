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

package com.ushahidi.android.app.entities;

/**
 * @author eyedol
 */
public class Media implements IDbEntity {

    private int id;

    private int checkinId;

    private int reportId;

    private int type;

    private String imageThumbnail;

    private String imageMediumSize;

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.database.DbEntity#getDbId()
     */
    @Override
    public int getDbId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.database.DbEntity#setDbId(java.lang.Long)
     */
    @Override
    public void setDbId(int id) {
        this.id = id;

    }

    public int getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public int getReportId() {
        return this.reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageThumbnail() {
        return this.imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public String getImageMediumSize() {
        return this.imageMediumSize;
    }

    public void setImageMediumSize(String imageMediumSize) {
        this.imageMediumSize = imageMediumSize;
    }
}
