package com.ushahidi.android.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.widgets.BaseWidgets;
import com.ushahidi.android.app.widgets.Widget;

/**
 *  Example activity that inherits from BaseActivity
 */
public class ExampleActivity extends BaseActivity<ExampleActivity.Widgets> {

    /**
     * UI Widgets from R.layout.example
     *
     * These widgets are automatically loaded in the BaseWidgets constructor
     */
    class Widgets extends BaseWidgets {
        public Widgets(Activity activity) {
            super(activity);
        }

        @Widget(R.id.example_label)
        TextView label;

        @Widget(R.id.example_value)
        EditText value;
    }

    public ExampleActivity() {
        super(R.layout.example, R.menu.example);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart label=%s", widgets.label.getText());
        log("onStart value=%s", widgets.value.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause label=%s", widgets.label.getText());
        log("onPause value=%s", widgets.value.getText());
    }
}
