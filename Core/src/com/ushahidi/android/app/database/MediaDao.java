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

import com.ushahidi.android.app.entities.Media;

/**
 * @author eyedol
 */
public class MediaDao extends DbContentProvider implements IMediaDao, IMediaSchema {

    private Cursor cursor;

    private List<Media> listMedia;

    private ContentValues initialValues;

    /**
     * @param db
     */
    public MediaDao(SQLiteDatabase db) {
        super(db);

    }

    @Override
    public List<Media> fetchMediaByCheckinId(int checkinId) {
        listMedia = new ArrayList<Media>();
        final String selectionArgs[] = {
            String.valueOf(checkinId)
        };

        final String selection = MEDIA_CHECKIN_ID + " =?";
        cursor = super.query(MEDIA_TABLE, MEDIA_COLUMNS, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Media media = cursorToEntity(cursor);
                listMedia.add(media);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listMedia;
    }

    @Override
    public List<Media> fetchMediaByReportId(int reportId) {
        listMedia = new ArrayList<Media>();
        final String selectionArgs[] = {
            String.valueOf(reportId)
        };

        final String selection = MEDIA_REPORT_ID + " =?";
        cursor = super.query(MEDIA_TABLE, MEDIA_COLUMNS, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Media media = cursorToEntity(cursor);
                listMedia.add(media);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listMedia;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.database.IMediaDao#deleteAllMedia()
     */
    @Override
    public boolean deleteAllMedia() {
        return super.delete(MEDIA_TABLE, null, null) > 0;
    }

    /*
     * (non-Javadoc)
     * @see com.ushahidi.android.app.database.IMediaDao#addMedia(java.util.List)
     */
    @Override
    public boolean addMedia(List<Media> sMedia) {
        try {
            mDb.beginTransaction();

            for (Media media : sMedia) {

                addMedia(media);
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
     * com.ushahidi.android.app.database.IMediaDao#addMedia(com.ushahidi.android
     * .app.entities.Media)
     */
    @Override
    public boolean addMedia(Media media) {
        // set values
        setContentValue(media);
        return super.insert(MEDIA_TABLE, getContentValue()) > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.DbContentProvider#cursorToEntity(android
     * .database.Cursor)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Media cursorToEntity(Cursor cursor) {
        Media media = new Media();
        int idIndex;
        int reportIdIndex;
        int checkinIdIndex;
        int typeIndex;
        int mediumSizeIndex;
        int thumbnailIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(MEDIA_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(MEDIA_ID);
                media.setDbId(cursor.getInt(idIndex));
            }

            if (cursor.getColumnIndex(MEDIA_REPORT_ID) != -1) {
                reportIdIndex = cursor.getColumnIndexOrThrow(MEDIA_REPORT_ID);
                media.setReportId(cursor.getInt(reportIdIndex));
            }

            if (cursor.getColumnIndex(MEDIA_CHECKIN_ID) != -1) {
                checkinIdIndex = cursor.getColumnIndexOrThrow(MEDIA_CHECKIN_ID);
                media.setCheckinId(cursor.getInt(checkinIdIndex));
            }

            if (cursor.getColumnIndex(MEDIA_TYPE) != -1) {
                typeIndex = cursor.getColumnIndexOrThrow(MEDIA_TYPE);
                media.setType(cursor.getInt(typeIndex));
            }

            if (cursor.getColumnIndex(MEDIA_THUMBNAIL) != -1) {
                thumbnailIndex = cursor.getColumnIndexOrThrow(MEDIA_THUMBNAIL);
                media.setImageThumbnail(cursor.getString(thumbnailIndex));
            }

            if (cursor.getColumnIndex(MEDIA_MEDIUM_SIZE) != -1) {
                mediumSizeIndex = cursor.getColumnIndexOrThrow(MEDIA_MEDIUM_SIZE);
                media.setImageMediumSize(cursor.getString(mediumSizeIndex));
            }
        }
        return null;
    }

    private void setContentValue(Media media) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MEDIA_ID, media.getDbId());
        initialValues.put(MEDIA_REPORT_ID, media.getReportId());
        initialValues.put(MEDIA_CHECKIN_ID, media.getCheckinId());
        initialValues.put(MEDIA_TYPE, media.getType());
        initialValues.put(MEDIA_THUMBNAIL, media.getImageThumbnail());
        initialValues.put(MEDIA_MEDIUM_SIZE, media.getImageMediumSize());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
