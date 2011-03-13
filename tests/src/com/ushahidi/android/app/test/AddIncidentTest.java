
package com.ushahidi.android.app.test;

import com.ushahidi.android.app.R;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import com.ushahidi.android.app.AddIncident;
import com.ushahidi.android.app.data.UshahidiDatabase;

public class AddIncidentTest extends ActivityInstrumentationTestCase2<AddIncident> {

    private AddIncident mAddIncidentActivity;

    private UshahidiDatabase mUshahidiDatabase;

    public AddIncidentTest() {
        super("com.ushahidi.android.app", AddIncident.class);
    }

    /**
     * Set up the test environment before each test
     */
    protected void setUp() throws Exception {
        super.setUp();
        mAddIncidentActivity = getActivity();
        mUshahidiDatabase = new UshahidiDatabase(mAddIncidentActivity);
        mUshahidiDatabase.open();
        mUshahidiDatabase.deleteAddIncidents();
    }

    /**
     * Tear down the environment after each test
     */
    protected void tearDown() throws Exception {
        mUshahidiDatabase.close();
    }

    @UiThreadTest
    public void testSendReportWitoutConnection() {
        // set connectivity off

        // set text in required fields
        EditText title = (EditText)mAddIncidentActivity.findViewById(R.id.incident_title);
        title.setText("James Blunt");
        EditText location = (EditText)mAddIncidentActivity.findViewById(R.id.incident_location);
        location.setText("UK");
        EditText description = (EditText)mAddIncidentActivity.findViewById(R.id.incident_desc);
        description.setText("James Blunt playing a gig, everyone get out");
        // activate action to submit report
        Button sendButton = (Button)mAddIncidentActivity.findViewById(R.id.incident_add_btn);
        sendButton.performClick();
        // 
        while(!title.getText().toString().equals("")){
        }
        // check value exists in db
        assertEquals(1, mUshahidiDatabase.fetchAllOfflineIncidents().getCount());
        // turn connectivity on
    }
}
