
package com.ushahidi.android.app.test;

import java.util.Vector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.ushahidi.android.app.AddIncident;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.data.UshahidiDatabase;

public class AddIncidentTest extends ActivityInstrumentationTestCase2<AddIncident> {

    private AddIncident mAddIncidentActivity;

    private UshahidiDatabase mUshahidiDatabase;

    private EditText mTitle;

    private EditText mLocation;

    private EditText mDescription;

    private Vector<String> mVectorCategories;

    private Button mSendButton;

    public AddIncidentTest() {
        super("com.ushahidi.android.app", AddIncident.class);
    }

    /**
     * Set up the test environment before each test
     */
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);
        setActivityIntent(new Intent(Intent.ACTION_VIEW));
        mAddIncidentActivity = getActivity();

        mVectorCategories = new Vector<String>();
        mVectorCategories.add("4");

        mUshahidiDatabase = new UshahidiDatabase(mAddIncidentActivity);
        mUshahidiDatabase.open();
        mUshahidiDatabase.deleteAddIncidents();

        mTitle = (EditText)mAddIncidentActivity.findViewById(R.id.incident_title);
        mLocation = (EditText)mAddIncidentActivity.findViewById(R.id.incident_location);
        mDescription = (EditText)mAddIncidentActivity.findViewById(R.id.incident_desc);

        // activate action to submit report
        mSendButton = (Button)mAddIncidentActivity.findViewById(R.id.incident_add_btn);

    }

    /**
     * Tear down the environment after each test
     */
    protected void tearDown() throws Exception {
        mUshahidiDatabase.close();
        super.tearDown();
    }

    @UiThreadTest
    public void testSendReport() {

        final SharedPreferences settings = getInstrumentation().getContext().getSharedPreferences(
                UshahidiPref.PREFS_NAME, 0);
        Editor editor = settings.edit();
        editor.putString("Domain", "http://demo.ushahidi.com");
        editor.commit();

        // set text in required fields
        mTitle.setText("James Blunt");
        mLocation.setText("UK");
        mDescription.setText("James Blunt playing a gig, everyone get out");
        mAddIncidentActivity.setVectorCategories(mVectorCategories);

        // activate action to submit report
        mSendButton.performClick();

        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        // check value exists in db
        assertEquals(0, mUshahidiDatabase.fetchAllOfflineIncidents().getCount());
    }

    @UiThreadTest
    public void testSendReportWithConnectionPostFailed() {

        // set text in required fields
        mTitle.setText("James Blunt");
        mLocation.setText("UK");
        mDescription.setText("James Blunt playing a gig, everyone get out");
        mAddIncidentActivity.setVectorCategories(mVectorCategories);

        mSendButton.performClick();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        // check value exists in db
        assertEquals(1, mUshahidiDatabase.fetchAllOfflineIncidents().getCount());
    }
}
