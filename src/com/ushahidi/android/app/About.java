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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class About extends DashboardActivity {

    private Button urlBtn;

    private static final String URL = "http://www.ushahidi.com";

    private static final int HOME = Menu.FIRST + 1;

    private static final int SETTINGS = Menu.FIRST + 2;

    private static final int GOTOHOME = 0;

    private static final int REQUEST_CODE_SETTINGS = 1;

    private static final int DIALOG_ERROR = 0;


    private String dialogErrorMsg = "An error occurred fetching the reports. "
            + "Make sure you have entered an Ushahidi instance.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        setTitleFromActivityLabel(R.id.title_text);
        urlBtn = (Button)findViewById(R.id.view_website);
        // Dipo Fix
        final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(URL));

        urlBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Dip Fix
                startActivity(i);
            }
        });
    }

    private void populateMenu(Menu menu) {

        MenuItem i;
        i = menu.add(Menu.NONE, HOME, Menu.NONE, R.string.menu_home);
        i.setIcon(R.drawable.ushahidi_home);

        i = menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.menu_settings);
        i.setIcon(R.drawable.ushahidi_about);

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
                intent = new Intent(About.this, Ushahidi.class);
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ERROR: {
                AlertDialog dialog = (new AlertDialog.Builder(this)).create();
                dialog.setTitle(R.string.alert_dialog_error_title);
                dialog.setMessage(dialogErrorMsg);
                dialog.setButton2(getString(R.string.btn_ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent launchPreferencesIntent = new Intent(About.this, Settings.class);

                        // Make it a sub activity so we know when it returns
                        startActivityForResult(launchPreferencesIntent, REQUEST_CODE_SETTINGS);
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                return dialog;
            }
        }
        return null;
    }

    final Runnable mDisplayErrorPrompt = new Runnable() {
        public void run() {
            showDialog(DIALOG_ERROR);
        }
    };

}
