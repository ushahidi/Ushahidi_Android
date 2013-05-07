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
package com.ushahidi.android.app.ui.phone;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.WebViewClientActivity;
import com.ushahidi.android.app.views.View;

/**
 * @author eyedol
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class AdminActivity<V extends View> extends WebViewClientActivity<V> {

	public AdminActivity() {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createMenuDrawer(this.findViewById(R.id.webview_wrapper));

		this.setTitle(getResources().getText(R.string.admin));

		// configure webview
		mWebView.setWebChromeClient(new UshahidiWebChromeClient(this));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);

		loadAdmin();
	}

	public void loadAdmin() {
		// load dashboard
		final String dashboardUrl = Preferences.domain + "/admin";
		log("Admin: "+dashboardUrl);
		loadUrl(dashboardUrl);
	}

}
