package com.ushahidi.android.app.api;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.CustomFormEntity;
import com.ushahidi.android.app.entities.CustomFormMetaEntity;
import com.ushahidi.android.app.entities.ReportCustomFormEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.UshahidiException;
import com.ushahidi.java.sdk.api.CustomForm;
import com.ushahidi.java.sdk.api.CustomFormField;
import com.ushahidi.java.sdk.api.CustomFormMeta;
import com.ushahidi.java.sdk.api.tasks.CustomFormTask;

/**
 * Handles all the Ushahidi custom form task API
 * 
 * @author markov00
 * 
 */
public class CustomFormApi extends UshahidiApi {

	private CustomFormTask task;
	private static final Util log = new Util();
	
	
	public CustomFormApi() {
		task = factory.createCustomFormsTask();
	}

	/**
	 * Fetch custom form values for a list of reports
	 * 
	 * @return boolean Successful return true otherwise return false
	 */
	public boolean fetchReportCustomFormList(List<ReportEntity> reports) {
		
		ArrayList<ReportCustomFormEntity> reportCustomForms = new ArrayList<ReportCustomFormEntity>();
		
		log.log("Saving report custom forms list");
		try {
			
			for(ReportEntity report: reports){
				List<CustomFormField> customFormFields = task.getCustomFormsFieldsByIncidentId(report.getIncident().getId());
				for(CustomFormField f : customFormFields){
					ReportCustomFormEntity entity = new ReportCustomFormEntity();
					entity.setFieldName(f.getMeta().getName());
					entity.setFieldId(f.getMeta().getFieldId());
					entity.setFormId(-1);//we don't have information on FORM ID
					entity.setPending(0);
					entity.setReportId(report.getIncident().getId());
					entity.setValue(TextUtils.join(", ",f.getValues()));
					reportCustomForms.add(entity);
				}
			}
			Database.mReportCustomFormDao.addReportCustomForm(reportCustomForms);

		} catch (UshahidiException e) {
			log("UshahidiException", e);
		} catch (JsonSyntaxException e) {
			log("JSONSyntaxException", e);

		}
		return false;
	}

	/**
	 * Fetch custom form using the Ushahidi API
	 * 
	 * @return boolean Successful return true otherwise return false
	 */
	public boolean fetchCustomFormList() {
		List<CustomFormEntity> customForms = new ArrayList<CustomFormEntity>();
		List<CustomFormMetaEntity> customFormMetas = new ArrayList<CustomFormMetaEntity>();
		log.log("Saving custom forms list");
		try {
			List<CustomForm> cfs = task.getAvailableCustomForms();
			if (cfs != null) {
				for (CustomForm cf : cfs) {
					customForms.add(CustomFormEntity.build(cf));
					List<CustomFormMeta> metas = task.getCustomFormsMetaByFormId(cf.getId());
					if (metas != null) {
						for (CustomFormMeta meta : metas) {
							customFormMetas.add(CustomFormMetaEntity.build(meta, cf.getId()));
						}
					}
				}
				boolean cf = Database.mCustomFormDao.addCustomForms(customForms);
				boolean cfm = Database.mCustomFormMetaDao.addCustomFormMetas(customFormMetas);
				return cf && cfm;
			}

		} catch (UshahidiException e) {
			log("UshahidiException", e);
		} catch (JsonSyntaxException e) {
			log("JSONSyntaxException", e);
		}

		return false;
	}
}
