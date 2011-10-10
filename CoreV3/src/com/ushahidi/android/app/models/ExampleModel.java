package com.ushahidi.android.app.models;

import android.content.Context;

/**
 *
 */
public class ExampleModel extends BaseModel {

    public ExampleModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    @Override
    public boolean load(Context context) {
        return false;
    }

    @Override
    public boolean save(Context context) {
        return false;
    }
}
