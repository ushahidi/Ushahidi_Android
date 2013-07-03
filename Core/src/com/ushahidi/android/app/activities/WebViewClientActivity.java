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

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.views.View;

/**
 * Base WebView activity
 */
public class WebViewClientActivity extends BaseActivity<View> {

    /** Primary webview used to display content. */
    protected WebView mWebView;

    private static final String USER_AGENT = "ushahidi-android";

    private int mMenu;

    public WebViewClientActivity() {

    }

    public WebViewClientActivity(int menu) {
        super(View.class, R.layout.webview, menu, R.id.drawer_layout,
                R.id.left_drawer);
        mMenu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        ab.setDisplayShowTitleEnabled(true);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new UshahidiWebClient());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setUserAgentString(USER_AGENT);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);

        // load URL if one was provided in the intent
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            loadUrl(url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mMenu != 0) {
            getSupportMenuInflater().inflate(mMenu, menu);
            return true;
        }
        return false;
    }

    /**
     * Load the specified URL in the Webview.
     * 
     * @param url URL to load in the Webview.
     */
    protected void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        if (mWebView != null && mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    protected class UshahidiWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

    /**
     * WebChromeClient that displays "Loading..." title until the content of the
     * webview is fully loaded.
     */
    protected class UshahidiWebChromeClient extends WebChromeClient {
        private Context context;

        public UshahidiWebChromeClient(Context context) {
            this.context = context;
        }

        public void onProgressChanged(WebView webView, int progress) {
            setTitle(context.getResources().getText(R.string.loading_));
            setSupportProgress(progress * 100);

            if (progress == 100) {
                setTitle(webView.getTitle());
            }
        }
    }

}
