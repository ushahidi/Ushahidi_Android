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

import com.ushahidi.android.app.entities.ReportCategory;

/**
 * Define the methods for interacting with the report category table. These
 * methods needs to be implemented by {@link ReportCategoryDao}
 * 
 * @author eyedol
 */
public interface IReportCategoryDao {

    // get reports by report id
    public List<ReportCategory> fetchReportCategory(long reportId);

    public boolean addReportCategory(ReportCategory reportCategory);

    public boolean addReportCategories(List<ReportCategory> reportCategories);

    // delete all report categories
    public boolean deleteAllReportCategory();
    
    public boolean deleteReportCategoryByReportId(int reportId);
    
    public List<ReportCategory> fetchReportCategoryByReportId(int reportId);
    
    public boolean updateReportCategory(int reportId, ReportCategory reportCategory);
}
