
package com.ushahidi.android.app.ui.phone;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.views.AboutView;

public class AboutActivity extends BaseActivity<AboutView> {

    public AboutActivity() {
        super(AboutView.class, R.layout.about_view, 0, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // lock about activity to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
