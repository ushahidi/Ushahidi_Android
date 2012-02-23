
package com.ushahidi.android.app.activities;

import com.ushahidi.android.app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;

@SuppressWarnings("rawtypes")
public abstract class BaseSinglePaneActivity extends BaseActivity {
    
    @SuppressWarnings("unchecked")
    protected BaseSinglePaneActivity(@SuppressWarnings("rawtypes") Class view, int layout, int menu) {
        super(view, layout, menu);
        // TODO Auto-generated constructor stub
    }

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepane_empty);
        if (savedInstanceState == null) {
            mFragment = onCreatePane();
            mFragment.setArguments(intentToFragmentArguments(getIntent()));

            getSupportFragmentManager().beginTransaction().add(R.id.root_container, mFragment)
                    .commit();
        }
    }

    /**
     * Called in <code>onCreate</code> when the fragment constituting this
     * activity is needed. The returned fragment's arguments will be set to the
     * intent used to invoke this activity.
     */
    protected abstract Fragment onCreatePane();
}
