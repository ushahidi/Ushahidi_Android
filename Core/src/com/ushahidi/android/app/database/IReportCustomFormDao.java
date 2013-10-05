package com.ushahidi.android.app.database;

import java.util.List;

import com.ushahidi.android.app.entities.ReportCustomFormEntity;

/**
 * Define the methods for interacting with the report custom forms table. 
 * 
 * @author markov00
 */
public interface IReportCustomFormDao {

		/**
		 * Add a list of ReportCustomForm to the database
		 * @param customForms the list of ReportCustomFormEntity
		 * @return true if successful, false otherwise
		 */
		public boolean addReportCustomForm(List<ReportCustomFormEntity> customForms);

		/**
		 * Add a single ReportCustomForm to the database
		 * @param customForm the list of ReportCustomFormEntity
		 * @return true if successful, false otherwise
		 */
		public boolean addReportCustomForm(ReportCustomFormEntity customForm);

		/**
		 * Fetch all the ReportCustomFormEntity by reportId
		 * @param checkinId
		 * @return
		 */
		public List<ReportCustomFormEntity> fetchReportCustomForms(int reportId);
		
		/**
		 * Fetch pending ReportCustomFormEntity
		 * @param reportId
		 * @return
		 */
		public List<ReportCustomFormEntity> fetchPendingReportCustomForms(int reportId);

		/**
		 * Delete all ReportCustomFormEntity on database
		 * @return
		 */
		public boolean deleteAllReportCustomForms();

		/**
		 * Delite ReportCustomFormEntity by reportId
		 * @param reportId
		 * @return
		 */
		public boolean deleteReportCustomFormsByReportId(int reportId);
}
