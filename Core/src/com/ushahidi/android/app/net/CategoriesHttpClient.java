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

package com.ushahidi.android.app.net;

import android.content.Context;

import com.ushahidi.android.app.util.ApiUtils;
import com.ushahidi.android.app.util.CategoriesApiUtils;

/**
 * @author eyedol
 */
public class CategoriesHttpClient extends MainHttpClient {

	private ApiUtils apiUtils;

	/**
	 * @param context
	 */
	public CategoriesHttpClient(Context context) {
		super(context);
		apiUtils = new ApiUtils(context);
	}

	public int getCategoriesFromWeb() {

		// get the right domain to work with
		apiUtils.updateDomain();

		CategoriesApiUtils categoriesApiUtils = new CategoriesApiUtils();
		if (categoriesApiUtils.getCategoriesList()) {
			// Preferences.categoriesResponse = categoriesResponse;
			return 0;
		}

		// bad json string
		return 99;

	}
}
