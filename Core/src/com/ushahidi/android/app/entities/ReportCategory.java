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
public class ReportCategory implements IDbEntity {

	private int id;

	private int categoryId;

	private int reportId;

	private int status;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.DbEntity#getDbId()
	 */
	@Override
	public int getDbId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.database.DbEntity#setDbId(java.lang.Long)
	 */
	@Override
	public void setDbId(int id) {
		this.id = id;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 *  Set the status of a report. Pending or not.
	 * @param status
	 *            The status of the report - 1 for a pending report 0 is not a
	 *            pending report.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * Get the status of the report
	 * 
	 * @return int status - The status of the report
	 */
	public int getStatus() {
		return this.status;
	}
	
}
