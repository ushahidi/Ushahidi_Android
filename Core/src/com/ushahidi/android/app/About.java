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

package com.ushahidi.android.app;


import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class About extends Dashboard {

    private static final int HOME = Menu.FIRST + 1;

    private static final int SETTINGS = Menu.FIRST + 2;

    private static final int GOTOHOME = 0;

    private static final int REQUEST_CODE_SETTINGS = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        setTitleFromActivityLabel(R.id.title_text);
        //SEARCH
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_report_btn);
        if (!TextUtils.isEmpty(getString(R.string.deployment_url))) {
            searchButton.setVisibility(View.GONE);
        }
        else {
            searchButton.setVisibility(View.VISIBLE);
        }
        //VERSION
        TextView version = (TextView) findViewById(R.id.version);
        try {
            version.setText(getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        }
        catch (NameNotFoundException e) {
            Log.e("About", "NameNotFoundException", e);
            version.setText("");
        }
        //BUTTONS
        setButtonVisibility((Button)findViewById(R.id.media_link), getString(R.string.media_url));
        setButtonVisibility((Button)findViewById(R.id.team_link), getString(R.string.team_url));
        setButtonVisibility((Button)findViewById(R.id.twitter_link), getString(R.string.twitter_url));
        setButtonVisibility((Button)findViewById(R.id.facebook_link), getString(R.string.facebook_url));
        setButtonVisibility((Button)findViewById(R.id.contact_link), getString(R.string.contact_url));
    }
    
    // override the prompt display so it doesn't show a prompt
    @Override
    public void promptForDeployment() {}

    private void setButtonVisibility(final Button button, final String url) {
        if (!TextUtils.isEmpty(url)) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
        }
        else {
             button.setVisibility(View.GONE);
        }
    }

    private void populateMenu(Menu menu) {
        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.menu_home);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.menu_about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // applyMenuChoice(item);

        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case HOME:
                intent = new Intent(About.this, Dashboard.class);
                startActivityForResult(intent, GOTOHOME);
                return true;

            case SETTINGS:
                intent = new Intent(About.this, Settings.class);

                // Make it a subactivity so we know when it returns
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return (true);
        }
        return false;
    }

}
