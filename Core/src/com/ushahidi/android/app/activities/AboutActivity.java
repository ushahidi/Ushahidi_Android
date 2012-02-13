
package com.ushahidi.android.app.activities;

import android.content.Intent;
import android.support.v4.view.MenuItem;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.views.AboutView;

public class AboutActivity extends BaseActivity<AboutView> {

    public AboutActivity() {
        super(AboutView.class, R.layout.about_view, 0);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goHome();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void goHome() {
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, ListMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
