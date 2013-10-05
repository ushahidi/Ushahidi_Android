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
import com.ushahidi.java.sdk.api.CustomForm;

public class CustomFormValueEntity extends Model implements IDbEntity {

	private transient int id;
	private String customFormTitle = "";
	private String customFormDescription = "";
	private int customFormId = 0;
	


	
	public static CustomFormValueEntity build(CustomForm form){
		CustomFormValueEntity cf = new CustomFormValueEntity();
		cf.setCustomFormId(form.getId());
		cf.setCustomFormTitle(form.getTitle());
		cf.setCustomFormDescription(form.getDescription());
		return cf;
	}
	

	/**
	 * @return the customFormTitle
	 */
	public String getCustomFormTitle() {
		return customFormTitle;
	}





	/**
	 * @param customFormTitle the customFormTitle to set
	 */
	public void setCustomFormTitle(String customFormTitle) {
		this.customFormTitle = customFormTitle;
	}





	/**
	 * @return the customFormDescription
	 */
	public String getCustomFormDescription() {
		return customFormDescription;
	}





	/**
	 * @param customFormDescription the customFormDescription to set
	 */
	public void setCustomFormDescription(String customFormDescription) {
		this.customFormDescription = customFormDescription;
	}





	/**
	 * @return the customFormId
	 */
	public int getCustomFormId() {
		return customFormId;
	}





	/**
	 * @param customFormId the customFormId to set
	 */
	public void setCustomFormId(int customFormId) {
		this.customFormId = customFormId;
	}


	@Override
	public void setDbId(int id) {
		this.id = id;
	}

	@Override
	public int getDbId() {
		return id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomFormEntity [customFormTitle=" + customFormTitle
				+ ", customFormDescription=" + customFormDescription
				+ ", customFormId=" + customFormId + "]";
	}

	

	
	

}
