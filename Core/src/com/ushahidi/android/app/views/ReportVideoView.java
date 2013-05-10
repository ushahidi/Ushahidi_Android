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
package com.ushahidi.android.app.views;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ushahidi.android.app.R;

/**
 * Video view
 * 
 */
public class ReportVideoView extends View {

	public WebView mWebView;

	private ProgressBar mLoadingSpinner;

	private static boolean CLEAR_CACHE_ON_LOAD = false;

	/**
	 * @param activity
	 */
	public ReportVideoView(ViewGroup activity) {
		super(activity);
		
		mWebView = (WebView) activity.findViewById(R.id.videoWebView);
		mLoadingSpinner = (ProgressBar) activity
				.findViewById(R.id.loading_spinner);
	}

	@SuppressWarnings("deprecation")
	public void setWebView(final String url) {
		mWebView.setWebViewClient(webClient);
		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true);
		settings.setPluginsEnabled(true);
		settings.setLightTouchEnabled(true);
		
		mWebView.post(new Runnable() {
			public void run() {
				if (CLEAR_CACHE_ON_LOAD) {
					mWebView.clearCache(true);
				}
				mWebView.loadUrl(url);
			}
		});

	}

	private WebViewClient webClient = new WebViewClient() {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mLoadingSpinner.setVisibility(android.view.View.VISIBLE);
			mWebView.setVisibility(android.view.View.INVISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mLoadingSpinner.setVisibility(android.view.View.GONE);
			mWebView.setVisibility(android.view.View.VISIBLE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Toast.makeText(view.getContext(),
					"Error " + errorCode + ": " + description,
					Toast.LENGTH_LONG).show();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	};

}
