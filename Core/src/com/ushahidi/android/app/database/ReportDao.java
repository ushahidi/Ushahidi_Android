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

import com.ushahidi.android.app.entities.Report;

public class ReportDao extends DbContentProvider implements IReportDao,
		IReportSchema {

	private Cursor cursor;

	private List<Report> listReport;

	private ContentValues initialValues;

	public ReportDao(SQLiteDatabase db) {
		super(db);
	}

	@Override
	public List<Report> fetchAllPendingReports() {

		final String sortOrder = INCIDENT_DATE + " DESC";
		final String selection = INCIDENT_PENDING + " =?";
		final String selectionArgs[] = { String.valueOf(1) };
		listReport = new ArrayList<Report>();
		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public List<Report> fetchAllReports() {

		final String sortOrder = INCIDENT_DATE + " DESC";
		final String selection = INCIDENT_PENDING + " = ?";
		final String selectionArgs[] = { String.valueOf(0) };
		listReport = new ArrayList<Report>();
		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public List<Report> fetchPendingReportByCategory(String category) {
		final String sortOrder = INCIDENT_TITLE + " DESC";
		final String selectionArgs[] = { category, String.valueOf(1) };

		final String selection = INCIDENT_CATEGORIES + " LIKE ? AND "
				+ INCIDENT_PENDING + " =? ";

		listReport = new ArrayList<Report>();

		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public int fetchPendingReportIdByDate(String date) {
		final String sortOrder = ID + " DESC";
		final String selectionArgs[] = { date, String.valueOf(1) };

		final String selection = INCIDENT_DATE + " =? AND " + INCIDENT_PENDING
				+ " =? ";
		int id = 0;
		listReport = new ArrayList<Report>();

		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				id = report.getDbId();
				cursor.moveToNext();
			}
			cursor.close();
		}

		return id;
	}

	@Override
	public List<Report> fetchReportByCategory(String category) {
		final String sortOrder = INCIDENT_TITLE + " DESC";
		final String selectionArgs[] = { category };

		final String selection = INCIDENT_CATEGORIES + " LIKE ?";

		listReport = new ArrayList<Report>();

		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public List<Report> fetchPendingReportByCategoryId(int categoryId) {
		final String sortOrder = INCIDENT_TITLE + " DESC";
		final String sql = "SELECT * FROM " + INCIDENTS_TABLE
				+ " reports INNER JOIN " + IReportCategorySchema.TABLE
				+ " cats ON reports." + ID + " = cats."
				+ IReportCategorySchema.ID + " WHERE cats."
				+ IReportCategorySchema.CATEGORY_ID + " =? AND "
				+ INCIDENT_PENDING + "=? " + "ORDER BY  " + sortOrder;
		final String selectionArgs[] = { String.valueOf(categoryId),
				String.valueOf(1) };
		listReport = new ArrayList<Report>();

		cursor = super.rawQuery(sql, selectionArgs);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public List<Report> fetchReportByCategoryId(int categoryId) {
		final String sortOrder = INCIDENT_TITLE + " DESC";
		final String sql = "SELECT * FROM " + INCIDENTS_TABLE
				+ " reports INNER JOIN " + IReportCategorySchema.TABLE
				+ " cats ON reports." + INCIDENT_ID + " = cats."
				+ IReportCategorySchema.REPORT_ID + " WHERE cats."
				+ IReportCategorySchema.CATEGORY_ID + " =? AND "
				+ INCIDENT_PENDING + "=? " + "ORDER BY  " + sortOrder;
		final String selectionArgs[] = { String.valueOf(categoryId),
				String.valueOf(0) };

		listReport = new ArrayList<Report>();

		cursor = super.rawQuery(sql, selectionArgs);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public List<Report> fetchReportById(long id) {
		final String sortOrder = INCIDENT_TITLE;

		final String selectionArgs[] = { String.valueOf(id) };

		final String selection = INCIDENT_ID + " = ?";

		listReport = new ArrayList<Report>();

		cursor = super.query(INCIDENTS_TABLE, INCIDENTS_COLUMNS, selection,
				selectionArgs, sortOrder);

		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Report report = cursorToEntity(cursor);
				listReport.add(report);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return listReport;
	}

	@Override
	public Report fetchPendingReportIdById(int reportId) {
		final String sortOrder = INCIDENT_DATE + " DESC";
		final String selectionArgs[] = { String.valueOf(reportId),
				String.valueOf(1) };

		final String selection = ID + " =? AND " + INCIDENT_PENDING + " =? ";
		Report report = new Report();

		cursor = super.query(INCIDENTS_TABLE, null, selection, selectionArgs,
				sortOrder);
		if (cursor != null) {

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				report = cursorToEntity(cursor);

				cursor.moveToNext();
			}
			cursor.close();
		}

		return report;
	}

	@Override
	public boolean deleteAllReport() {
		return super.delete(INCIDENTS_TABLE, null, null) > 0;
	}

	@Override
	public boolean deleteReportById(long id) {
		final String selectionArgs[] = { String.valueOf(id) };
		final String selection = INCIDENT_ID + " = ?";

		return super.delete(INCIDENTS_TABLE, selection, selectionArgs) > 0;
	}

	@Override
	public boolean addReport(Report report) {
		// set values
		setContentValue(report);
		return super.insert(INCIDENTS_TABLE, getContentValue()) > 0;
	}

	@Override
	public boolean addReport(List<Report> reports) {
		try {
			mDb.beginTransaction();

			for (Report report : reports) {

				addReport(report);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Report cursorToEntity(Cursor cursor) {
		Report report = new Report();
		int idIndex;
		int reportIdIndex;
		int titleIndex;
		int dateIndex;
		int verifiedIndex;
		int locationIndex;
		int descIndex;
		int mediaIndex;
		int imageIndex;
		int longitudeIndex;
		int latitudeIndex;

		if (cursor != null) {
			if (cursor.getColumnIndex(ID) != -1) {
				idIndex = cursor.getColumnIndexOrThrow(ID);
				report.setDbId(cursor.getInt(idIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_ID) != -1) {
				reportIdIndex = cursor.getColumnIndexOrThrow(INCIDENT_ID);
				report.setReportId(cursor.getInt(reportIdIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_TITLE) != -1) {
				titleIndex = cursor.getColumnIndexOrThrow(INCIDENT_TITLE);
				report.setTitle(cursor.getString(titleIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_DATE) != -1) {
				dateIndex = cursor.getColumnIndexOrThrow(INCIDENT_DATE);
				report.setReportDate(cursor.getString(dateIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_VERIFIED) != -1) {
				verifiedIndex = cursor.getColumnIndexOrThrow(INCIDENT_VERIFIED);
				report.setVerified(cursor.getString(verifiedIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_LOC_NAME) != -1) {
				locationIndex = cursor.getColumnIndexOrThrow(INCIDENT_LOC_NAME);
				report.setLocationName(cursor.getString(locationIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_DESC) != -1) {
				descIndex = cursor.getColumnIndexOrThrow(INCIDENT_DESC);
				report.setDescription(cursor.getString(descIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_MEDIA) != -1) {
				mediaIndex = cursor.getColumnIndexOrThrow(INCIDENT_MEDIA);
				report.setMedia(cursor.getString(mediaIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_IMAGE) != -1) {
				imageIndex = cursor.getColumnIndexOrThrow(INCIDENT_IMAGE);
				report.setImage(cursor.getString(imageIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_LOC_LATITUDE) != -1) {
				latitudeIndex = cursor
						.getColumnIndexOrThrow(INCIDENT_LOC_LATITUDE);
				report.setLatitude(cursor.getString(latitudeIndex));
			}

			if (cursor.getColumnIndex(INCIDENT_LOC_LONGITUDE) != -1) {
				longitudeIndex = cursor
						.getColumnIndexOrThrow(INCIDENT_LOC_LONGITUDE);
				report.setLongitude(cursor.getString(longitudeIndex));
			}
		}

		return report;
	}

	private void setContentValue(Report report) {
		initialValues = new ContentValues();
		initialValues.put(INCIDENT_ID, report.getReportId());
		initialValues.put(INCIDENT_TITLE, report.getTitle());
		initialValues.put(INCIDENT_DESC, report.getDescription());
		initialValues.put(INCIDENT_DATE, report.getReportDate());
		initialValues.put(INCIDENT_MODE, report.getMode());
		initialValues.put(INCIDENT_VERIFIED, report.getVerified());
		initialValues.put(INCIDENT_LOC_NAME, report.getLocationName());
		initialValues.put(INCIDENT_LOC_LATITUDE, report.getLatitude());
		initialValues.put(INCIDENT_LOC_LONGITUDE, report.getLongitude());
		initialValues.put(INCIDENT_PENDING, report.getPending());
	}

	private ContentValues getContentValue() {
		return initialValues;
	}

	@Override
	public boolean deletePendingReportById(int id) {
		final String selectionArgs[] = { String.valueOf(id), String.valueOf(1) };
		final String selection = ID + " = ? AND " + INCIDENT_PENDING + " = ?";

		return super.delete(INCIDENTS_TABLE, selection, selectionArgs) > 0;
	}

	@Override
	public boolean updatePendingReport(int reportId, Report report) {
		boolean status = false;
		try {
			mDb.beginTransaction();
			final String selectionArgs[] = { String.valueOf(reportId),
					String.valueOf(1) };
			final String selection = ID + " = ? AND " + INCIDENT_PENDING
					+ " = ?";
			setContentValue(report);
			super.update(INCIDENTS_TABLE, getContentValue(), selection,
					selectionArgs);

			mDb.setTransactionSuccessful();
			status = true;
		} finally {
			mDb.endTransaction();
		}
		return status;
	}
}
