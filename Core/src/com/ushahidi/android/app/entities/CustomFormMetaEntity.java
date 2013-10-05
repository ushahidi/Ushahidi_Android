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

import com.ushahidi.android.app.models.Model;
import com.ushahidi.java.sdk.api.CustomFormMeta;

public class CustomFormMetaEntity extends Model implements IDbEntity {

	private transient int id;

	private int formId = 0;
	private int fieldId = 0;
	private String name;
	private int type;
	private String defaultValues;
	private int required;
	private int maxLen;
	private int isDate;
	private int isPublicVisible;
	private int isPublicSubmit;

	public static CustomFormMetaEntity build(CustomFormMeta form, int formId) {
		CustomFormMetaEntity cf = new CustomFormMetaEntity();
		cf.setFormId(formId);
		cf.setFieldId(form.getFieldId());
		cf.setName(form.getName());
		cf.setType(form.getType());
		cf.setDefaultValues(form.getDefaultValues());
		cf.setRequired(form.getRequired());
		cf.setMaxLen(form.getMaxLen());
		cf.setIsDate(form.getIsDate());
		cf.setIsPublicVisible(form.getIsPublicVisible());
		cf.setIsPublicSubmit(form.getIsPublicSubmit());
		return cf;
	}

	@Override
	public void setDbId(int id) {
		this.id = id;
	}

	@Override
	public int getDbId() {
		return id;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public int getMaxLen() {
		return maxLen;
	}

	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}

	public int getIsDate() {
		return isDate;
	}

	public void setIsDate(int isDate) {
		this.isDate = isDate;
	}

	public int getIsPublicVisible() {
		return isPublicVisible;
	}

	public void setIsPublicVisible(int isPublicVisible) {
		this.isPublicVisible = isPublicVisible;
	}

	public int getIsPublicSubmit() {
		return isPublicSubmit;
	}

	public void setIsPublicSubmit(int isPublicSubmit) {
		this.isPublicSubmit = isPublicSubmit;
	}

	@Override
	public String toString() {
		return "CustomFormMetaEntity [formId=" + formId + ", fieldId="
				+ fieldId + ", name=" + name + ", type=" + type
				+ ", defaultValues=" + defaultValues + ", required=" + required
				+ ", maxLen=" + maxLen + ", isDate=" + isDate
				+ ", isPublicVisible=" + isPublicVisible + ", isPublicSubmit="
				+ isPublicSubmit + "]";
	}

}
