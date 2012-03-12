
package com.ushahidi.android.app.ui.phone;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItem;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.views.AboutView;

public class AboutActivity extends BaseActivity<AboutView> {

    public AboutActivity() {
        super(AboutView.class, R.layout.about_view, 0);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //lock about activity to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new AboutView(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
