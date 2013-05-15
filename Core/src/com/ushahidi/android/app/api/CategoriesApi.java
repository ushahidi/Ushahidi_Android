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
package com.ushahidi.android.app.api;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.java.sdk.UshahidiException;
import com.ushahidi.java.sdk.api.Category;
import com.ushahidi.java.sdk.api.tasks.CategoriesTask;

/**
 * Handles all the Ushahidi categories task API
 * 
 * @author eyedol
 * 
 */
public class CategoriesApi extends UshahidiApi {

	private CategoriesTask task;

	private boolean processingResult;

	private List<CategoryEntity> categories;

	public CategoriesApi() {
		processingResult = true;
		categories = new ArrayList<CategoryEntity>();
		task = factory.createCategoriesTask();
	}

	/**
	 * Fetch categories using the Ushahidi API
	 * 
	 * @return boolean Successful return true otherwise return false
	 */
	public boolean getCategoriesList() {
		new Util().log("Save categories list");
		if (processingResult) {
			try {
				List<Category> cats = task.all();
				if (cats != null) {
					for (com.ushahidi.java.sdk.api.Category cat : cats) {
						CategoryEntity category = new CategoryEntity();
						category.addCategory(cat);
						categories.add(category);
					}
					return saveCategories(categories);
				}

			} catch (UshahidiException e) {
				log("UshahidiException", e);
				processingResult = false;
			}catch(JsonSyntaxException e) {
				log("JSONSyntaxException", e);
			}
				
		}
		return false;
	}

	/**
	 * Save categories details to the database
	 * 
	 * @param categories
	 *            The list of categories
	 * 
	 * @return boolean Successful return true otherwise return false
	 */
	public boolean saveCategories(List<CategoryEntity> categories) {

		if (categories != null && categories.size() > 0) {

			return Database.mCategoryDao.addCategories(categories);
		}
		return false;
	}
}
