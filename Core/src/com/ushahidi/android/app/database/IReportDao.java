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

import java.util.List;

import com.ushahidi.android.app.entities.ReportEntity;

/**
 * Define the methods for interacting with the report table. These methods needs
 * to be implemented by {@link ReportDao}
 * 
 * @author eyedol
 * 
 */
public interface IReportDao {

	// fetch all reports
	public List<ReportEntity> fetchAllReports();

	// fetch all reports
	public List<ReportEntity> fetchAllPendingReports();

	// fetch reports by categories title: This is deprecated
	public List<ReportEntity> fetchReportByCategory(String category);

	public List<ReportEntity> fetchPendingReportByCategory(String category);

	// fetch reports by categories by id
	public List<ReportEntity> fetchReportByCategoryId(int categoryId);

	public List<ReportEntity> fetchPendingReportByCategoryId(int categoryId);

	// fetch reports by ID
	public List<ReportEntity> fetchReportById(long id);

	// delete all report
	public boolean deleteAllReport();

	// delete report by id
	public boolean deleteReportById(long reportId);

	// delete report by id
	public boolean deletePendingReportById(int reportId);

	// add reports
	public boolean addReport(ReportEntity report);

	// add report
	public boolean addReport(List<ReportEntity> report);

	// fetch reports by categories title: This is deprecated
	public int fetchPendingReportIdByDate(String date);

	// fetch reports by categories title: This is deprecated
	public ReportEntity fetchPendingReportIdById(int reportId);

	// add reports
	public boolean updatePendingReport(int reportId, ReportEntity report);

}
