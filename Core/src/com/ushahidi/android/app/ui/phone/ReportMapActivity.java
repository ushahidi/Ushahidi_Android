
package com.ushahidi.android.app.ui.phone;

import android.os.Bundle;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseActivity;
import com.ushahidi.android.app.views.View;

public class ReportMapActivity extends BaseActivity<View> {

    public ReportMapActivity() {
        super(View.class, R.layout.report_map, 0, R.id.drawer_layout,
                R.id.left_drawer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNavDrawer();
    }

}
