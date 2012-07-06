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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.ushahidi.android.app.R;

/**
 * @author eyedol
 * 
 */
public class ReportVideoView extends View {

	public ViewAnimator viewAnimator;

	public WebView webView;

	public Context context;

	private ProgressBar loadingSpinner;

	public String url;

	private static boolean CLEAR_CACHE_ON_LOAD = false;

	/**
	 * @param activity
	 */
	public ReportVideoView(Activity activity) {
		super(activity);
		this.context = activity;
		viewAnimator = (ViewAnimator) activity
				.findViewById(R.id.videoViewAnimator);
		webView = (WebView) activity.findViewById(R.id.videoWebView);
		loadingSpinner = (ProgressBar) activity
				.findViewById(R.id.loading_spinner);
	}

	public void goNext(String url) {
		Animation out = AnimationUtils.loadAnimation(context,
				android.R.anim.slide_out_right);
		Animation in = AnimationUtils.loadAnimation(context,
				android.R.anim.slide_in_left);
		viewAnimator.setInAnimation(in);
		viewAnimator.setOutAnimation(out);
		this.url = url;
		setWebView();
	}

	public void goPrevious(String url) {
		Animation out = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		Animation in = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		viewAnimator.setInAnimation(in);
		viewAnimator.setOutAnimation(out);
		this.url = url;
		setWebView();
	}

	public void setWebView() {
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setPluginsEnabled(true);
		settings.setLightTouchEnabled(true);
		webView.setWebViewClient(webClient);
		webView.post(new Runnable() {
			public void run() {
				if (CLEAR_CACHE_ON_LOAD) {
					webView.clearCache(true);
				}
				webView.loadUrl(url);
			}
		});

	}

	private WebViewClient webClient = new WebViewClient() {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			loadingSpinner.setVisibility(android.view.View.VISIBLE);
			webView.setVisibility(android.view.View.INVISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			loadingSpinner.setVisibility(android.view.View.GONE);
			webView.setVisibility(android.view.View.VISIBLE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			Toast.makeText(view.getContext(),
					"Error " + errorCode + ": " + description,
					Toast.LENGTH_LONG).show();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	};

}
