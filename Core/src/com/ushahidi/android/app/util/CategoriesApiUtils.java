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

import java.util.List;

import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.entities.Category;
import com.ushahidi.android.app.json.GsonHelper;
import com.ushahidi.android.app.json.UshahidiApiCategories;

/**
 * @author eyedol
 */
public class CategoriesApiUtils {

	private UshahidiApiCategories mApiCategories;

	private boolean processingResult;

	public CategoriesApiUtils(String jsonString) {
		processingResult = true;
		mApiCategories = GsonHelper.fromString(jsonString,
				UshahidiApiCategories.class);

	}

	public boolean getCategoriesList() {
		new Util().log("Save report");
		if (processingResult) {
			return saveCategories(mApiCategories.getCategories());

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
