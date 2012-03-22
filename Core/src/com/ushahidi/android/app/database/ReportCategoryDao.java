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

import com.ushahidi.android.app.entities.ReportCategory;

/**
 * @author eyedol
 */
public class ReportCategoryDao extends DbContentProvider implements IReportCategoryDao,
        IReportCategorySchema {

    private Cursor cursor;

    private List<ReportCategory> listReportCategories;

    private ContentValues initialValues;

    /**
     * @param db
     */
    public ReportCategoryDao(SQLiteDatabase db) {
        super(db);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IReportCategoryDao#fetchReportCategory
     * (long)
     */
    @Override
    public List<ReportCategory> fetchReportCategory(long reportId) {
        listReportCategories = new ArrayList<ReportCategory>();
        final String selectionArgs[] = {
            String.valueOf(reportId)
        };

        final String selection = ID + " =?";
        cursor = super.query(TABLE, COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ReportCategory reportCategory = cursorToEntity(cursor);
                listReportCategories.add(reportCategory);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listReportCategories;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IReportCategoryDao#addReportCategory
     * (com.ushahidi.android.app.entities.ReportCategory)
     */
    @Override
    public boolean addReportCategory(ReportCategory reportCategory) {
        // set values
        setContentValue(reportCategory);
        return super.insert(TABLE, getContentValue()) > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.IReportCategoryDao#addReportCategories
     * (java.util.List)
     */
    @Override
    public boolean addReportCategories(List<ReportCategory> reportCategories) {
        try {
            mDb.beginTransaction();

            for (ReportCategory reportCategory : reportCategories) {

                addReportCategory(reportCategory);
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
     * com.ushahidi.android.app.database.IReportCategoryDao#deleteAllReportCategory
     * ()
     */
    @Override
    public boolean deleteAllReportCategory() {
        return super.delete(TABLE, null, null) > 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.database.DbContentProvider#cursorToEntity(android
     * .database.Cursor)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected ReportCategory cursorToEntity(Cursor cursor) {
        ReportCategory reportCategory = new ReportCategory();
        int idIndex;
        int reportIdIndex;
        int categoryIdIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(ID);
                reportCategory.setDbId(cursor.getInt(idIndex));
            }

            if (cursor.getColumnIndex(REPORT_ID) != -1) {
                reportIdIndex = cursor.getColumnIndexOrThrow(REPORT_ID);
                reportCategory.setReportId(cursor.getInt(reportIdIndex));
            }

            if (cursor.getColumnIndex(CATEGORY_ID) != -1) {
                categoryIdIndex = cursor.getColumnIndexOrThrow(CATEGORY_ID);
                reportCategory.setCategoryId(cursor.getInt(categoryIdIndex));
            }
        }
        return reportCategory;
    }

    private void setContentValue(ReportCategory reportCategory) {
        initialValues = new ContentValues();
        initialValues.put(REPORT_ID, reportCategory.getReportId());
        initialValues.put(CATEGORY_ID, reportCategory.getCategoryId());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
