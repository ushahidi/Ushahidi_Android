package com.ushahidi.android.app.views;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import com.ushahidi.android.app.R;

public class ExampleView extends View {
    public ExampleView(Activity activity) {
        super(activity);
    }

    @Widget(R.id.example_label)
    public TextView label;

    @Widget(R.id.example_value)
    public EditText value;
}
