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

package com.ushahidi.android.app.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Category;

/**
 * @author eyedol
 */
public class CategoriesApiUtils {

	private JSONObject jsonObject;

	private boolean processingResult;

	public CategoriesApiUtils(String jsonString) {
		processingResult = true;

		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			new Util().log("JSONException", e);
			processingResult = false;
		}
	}

	private JSONObject getCategoryPayloadObj() {
		try {
			return jsonObject.getJSONObject("payload");
		} catch (JSONException e) {
			new Util().log("JSONException", e);
			return new JSONObject();
		}
	}

	private JSONArray getCategoriesArr() {
		try {
			return getCategoryPayloadObj().getJSONArray("categories");
		} catch (JSONException e) {
			new Util().log("JSONException", e);
			return new JSONArray();
		}
	}

	public boolean getCategoriesList() {
		new Util().log("Save report");
		if (processingResult) {
			List<Category> listCategory = new ArrayList<Category>();
			JSONArray categoriesArr = getCategoriesArr();
			int id = 0;
			if (categoriesArr != null) {
				for (int i = 0; i < categoriesArr.length(); i++) {
					Category category = new Category();
					try {
						id = categoriesArr.getJSONObject(i)
								.getJSONObject("category").getInt("id");
						category.setCategoryId(id);
						if (!categoriesArr.getJSONObject(i)
								.getJSONObject("category").isNull("color")) {
							category.setCategoryColor(categoriesArr
									.getJSONObject(i).getJSONObject("category")
									.getString("color"));
						}

						if (!categoriesArr.getJSONObject(i)
								.getJSONObject("category")
								.isNull("description")) {
							category.setCategoryDescription(categoriesArr
									.getJSONObject(i).getJSONObject("category")
									.getString("description"));
						}

						if (!categoriesArr.getJSONObject(i)
								.getJSONObject("category").isNull("title")) {

							category.setCategoryTitle(categoriesArr
									.getJSONObject(i).getJSONObject("category")
									.getString("title"));
						}

						if (!categoriesArr.getJSONObject(i)
								.getJSONObject("category").isNull("position")) {
							category.setCategoryPosition(categoriesArr
									.getJSONObject(i).getJSONObject("category")
									.getInt("position"));
						}

					} catch (JSONException e) {
						new Util().log("JSONException", e);
						processingResult = false;
						return false;
					}
					listCategory.add(category);

				}

				return saveCategories(listCategory);
			}
		}
		return false;
	}

	private boolean saveCategories(List<Category> categories) {
		
		if (categories != null && categories.size() > 0) {
			
			return Database.mCategoryDao.addCategories(categories);
		}
		return false;
	}

}
