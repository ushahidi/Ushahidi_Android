package com.ushahidi.android.app.activities;

import android.os.Bundle;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.views.ExampleView;

/**
 *  Example activity that inherits from BaseActivity
 */
public class ExampleActivity extends BaseActivity<ExampleView> {

    public ExampleActivity() {
        super(ExampleView.class,
              R.layout.example,
              R.menu.example);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart label=%s", view.label.getText());
        log("onStart value=%s", view.value.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause label=%s", view.label.getText());
        log("onPause value=%s", view.value.getText());
    }
}
