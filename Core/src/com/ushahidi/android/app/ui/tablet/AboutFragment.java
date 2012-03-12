
package com.ushahidi.android.app.ui.tablet;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ushahidi.android.app.R;

public class AboutFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        getDialog().setTitle(getString(R.string.about));
        android.view.View v = inflater.inflate(R.layout.about_view, container, false);

        return v;
    }

    /**
     * Create a new instance of MyDialogFragment, providing "num" as an
     * argument.
     */
    static AboutFragment newInstance() {
        AboutFragment f = new AboutFragment();
        return f;
    }
}
