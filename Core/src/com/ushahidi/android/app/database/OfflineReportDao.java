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

package com.ushahidi.android.app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ushahidi.android.app.entities.OfflineReport;

/**
 * @author eyedol
 */
public class OfflineReportDao extends DbContentProvider implements IOfflineReportDao,
        IOfflineReportSchema {

    /**
     * @param db
     */
    public OfflineReportDao(SQLiteDatabase db) {
        super(db);
    }

    private Cursor cursor;

    private List<OfflineReport> listOfflineReport;

    private ContentValues initialValues;

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IOfflineReportDao#fetchAllOfflineIncidents
     * ()
     */
    @Override
    public List<OfflineReport> fetchAllOfflineIncidents() {
        final String sortOrder = DATE + " DESC";

        listOfflineReport = new ArrayList<OfflineReport>();
        cursor = super.query(OFFLINE_REPORT_TABLE, null, null, null, sortOrder);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                OfflineReport offlineReport = cursorToEntity(cursor);
                listOfflineReport.add(offlineReport);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return listOfflineReport;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IOfflineReportDao#deleteAllOfflineReport
     * ()
     */
    @Override
    public boolean deleteAllOfflineReport() {
        return super.delete(OFFLINE_REPORT_TABLE, null, null) > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IOfflineReportDao#addOfflineReport(
     * com.ushahidi.android.app.entities.OfflineReport)
     */
    @Override
    public boolean addOfflineReport(OfflineReport offlineReport) {
        // set values
        setContentValue(offlineReport);
        return super.insert(OFFLINE_REPORT_TABLE, getContentValue()) > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IOfflineReportDao#addOfflineReport(
     * java.util.List)
     */
    @Override
    public boolean addOfflineReport(List<OfflineReport> offlineReports) {
        try {
            mDb.beginTransaction();

            for (OfflineReport offlineReport : offlineReports) {
                addOfflineReport(offlineReport);
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.DbContentProvider#cursorToEntity(android
     * .database.Cursor)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected OfflineReport cursorToEntity(Cursor cursor) {
        OfflineReport offlineReport = new OfflineReport();
        int idIndex;
        int titleIndex;
        int dateIndex;
        int locationIndex;
        int descIndex;
        int categoryIndex;
        int photoIndex;
        int newsIndex;
        int videoIndex;
        int longitudeIndex;
        int latitudeIndex;
        int firstnameIndex;
        int lastnameIndex;
        int emailIndex;
        int hourIndex;
        int ampmIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(ID);
                offlineReport.setDbId(cursor.getInt(idIndex));
            }

            if (cursor.getColumnIndex(TITLE) != -1) {
                titleIndex = cursor.getColumnIndexOrThrow(TITLE);
                offlineReport.setTitle(cursor.getString(titleIndex));
            }

            if (cursor.getColumnIndex(DATE) != -1) {
                dateIndex = cursor.getColumnIndexOrThrow(DATE);
                offlineReport.setDate(cursor.getString(dateIndex));
            }

            if (cursor.getColumnIndex(HOUR) != -1) {
                hourIndex = cursor.getColumnIndexOrThrow(HOUR);
                offlineReport.setHour(cursor.getInt(hourIndex));
            }

            if (cursor.getColumnIndex(LOCATION_NAME) != -1) {
                locationIndex = cursor.getColumnIndexOrThrow(LOCATION_NAME);
                offlineReport.setLocationName(cursor.getString(locationIndex));
            }

            if (cursor.getColumnIndex(DESCRIPTION) != -1) {
                descIndex = cursor.getColumnIndexOrThrow(DESCRIPTION);
                offlineReport.setDescription(cursor.getString(descIndex));
            }

            if (cursor.getColumnIndex(CATEGORIES) != -1) {
                categoryIndex = cursor.getColumnIndexOrThrow(CATEGORIES);
                offlineReport.setCategories(cursor.getString(categoryIndex));
            }

            if (cursor.getColumnIndex(PHOTO) != -1) {
                photoIndex = cursor.getColumnIndexOrThrow(PHOTO);
                offlineReport.setPhoto(cursor.getString(photoIndex));
            }

            if (cursor.getColumnIndex(VIDEO) != -1) {
                videoIndex = cursor.getColumnIndexOrThrow(VIDEO);
                offlineReport.setVideo(cursor.getString(videoIndex));
            }

            if (cursor.getColumnIndex(LATITUDE) != -1) {
                latitudeIndex = cursor.getColumnIndexOrThrow(LATITUDE);
                offlineReport.setLatitude(cursor.getString(latitudeIndex));
            }

            if (cursor.getColumnIndex(LONGITUDE) != -1) {
                longitudeIndex = cursor.getColumnIndexOrThrow(LONGITUDE);
                offlineReport.setLongitude(cursor.getString(longitudeIndex));
            }

            if (cursor.getColumnIndex(NEWS) != -1) {
                newsIndex = cursor.getColumnIndexOrThrow(NEWS);
                offlineReport.setNews(cursor.getString(newsIndex));
            }

            if (cursor.getColumnIndex(AMPM) != -1) {
                ampmIndex = cursor.getColumnIndexOrThrow(AMPM);
                offlineReport.setAmPm(cursor.getString(ampmIndex));
            }

            if (cursor.getColumnIndex(FIRST_NAME) != -1) {
                firstnameIndex = cursor.getColumnIndexOrThrow(FIRST_NAME);
                offlineReport.setFirstName(cursor.getString(firstnameIndex));
            }

            if (cursor.getColumnIndex(LAST_NAME) != -1) {
                lastnameIndex = cursor.getColumnIndexOrThrow(LAST_NAME);
                offlineReport.setLastName(cursor.getString(lastnameIndex));
            }

            if (cursor.getColumnIndex(EMAIL) != -1) {
                emailIndex = cursor.getColumnIndexOrThrow(EMAIL);
                offlineReport.setEmail(cursor.getString(emailIndex));
            }
        }

        return offlineReport;
    }

    private void setContentValue(OfflineReport offlineReport) {
        initialValues = new ContentValues();
        initialValues.put(TITLE, offlineReport.getTitle());
        initialValues.put(DESCRIPTION, offlineReport.getDescription());
        initialValues.put(DATE, offlineReport.getDate());
        initialValues.put(HOUR, offlineReport.getHour());
        initialValues.put(MINUTE, offlineReport.getMinute());
        initialValues.put(AMPM, offlineReport.getAmPm());
        initialValues.put(CATEGORIES, offlineReport.getCategories());
        initialValues.put(LOCATION_NAME, offlineReport.getLocationName());
        initialValues.put(LATITUDE, offlineReport.getLatitude());
        initialValues.put(LONGITUDE, offlineReport.getLongitude());
        initialValues.put(PHOTO, offlineReport.getPhoto());
        initialValues.put(VIDEO, offlineReport.getVideo());
        initialValues.put(NEWS, offlineReport.getNews());
        initialValues.put(FIRST_NAME, offlineReport.getFirstName());
        initialValues.put(LAST_NAME, offlineReport.getLastName());
        initialValues.put(EMAIL, offlineReport.getEmail());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }

}
