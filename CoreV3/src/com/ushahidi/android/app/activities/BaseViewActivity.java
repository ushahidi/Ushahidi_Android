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

package com.ushahidi.android.app.activities;

import com.ushahidi.android.app.models.Model;
import com.ushahidi.android.app.views.View;

/**
 * BaseViewActivity
 *
 * Add shared functionality that exists between all View Activities
 */
public abstract class BaseViewActivity<V extends View, M extends Model> extends BaseActivity<V> {

    protected BaseViewActivity(Class<V> view, int layout, int menu) {
        super(view, layout, menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

	@Override
	protected void onResume(){
		super.onResume();
	}

    @Override
	protected void onPause() {
        super.onPause();
    }

}
