
package com.ushahidi.android.app.test;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ushahidi.android.app.DeploymentSearch;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.data.Database;
import com.ushahidi.android.app.net.Deployments;

public class DeploymentSearchTest extends ActivityInstrumentationTestCase2<DeploymentSearch> {

    private Database mUshahidiDatabase;

    private DeploymentSearch mDeploymentSearchActivity;

    private final String[] distances = {
            "50", "100", "250", "500", "750", "1000", "1500"
    };

    private ListView mListView;

    private Deployments mDeployments;

    private Location location;

    public DeploymentSearchTest() {
        super("com.ushahidi.android.app", DeploymentSearch.class);
        // TODO Auto-generated constructor stub
    }

    /**
     * Setup test environment
     */
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);
        setActivityIntent(new Intent(Intent.ACTION_VIEW));
        mDeploymentSearchActivity = getActivity();

        mUshahidiDatabase = new Database(mDeploymentSearchActivity);
        mUshahidiDatabase.open();

        mDeployments = new Deployments(mDeploymentSearchActivity);

        mListView = (ListView)mDeploymentSearchActivity.findViewById(R.id.deployment_list);
        

    }

    /**
     * Clean test data after testing
     */
    protected void tearDown() throws Exception {

        mUshahidiDatabase.close();
        super.tearDown();
    }

    public void testGetDeploymentsFromOnline() {
        assertNotNull("It couldn't fetch data from online because app was offline",
                mDeployments.getDeploymentsFromOnline());
    }

    /**
     * Test when user refreshes for a new deployments
     */
    @UiThreadTest
    public void testRefreshDeployment() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        // get current location of the device
       /* location = DeviceCurrentLocation.getLocation();
        assertNotNull(
                "Device location couldn't be retrieved because device data has been turned off",
                location);
        assertNotNull(mDeployments);
        assertTrue(mDeployments.fetchDeployments(distances[0], location));*/

    }

    @UiThreadTest
    public void testShowResults() {

        Cursor cursor = MainApplication.mDb.fetchAllDeployments();
        assertTrue("Couldn't fetch deployments from online because app is offline",
                cursor.getCount() > 0);
        String[] from = new String[] {
                Database.DEPLOYMENT_ID, Database.DEPLOYMENT_NAME,
                Database.DEPLOYMENT_DESC, Database.DEPLOYMENT_URL
        };

        // Specify the corresponding layout elements where we want the
        // columns to go
        int[] to = new int[] {
                R.id.deployment_list_id, R.id.deployment_list_name, R.id.deployment_list_desc,
                R.id.deployment_list_url
        };

        // Create a simple cursor adapter for the definitions and apply them
        // to the ListView
        SimpleCursorAdapter deployments = new SimpleCursorAdapter(mDeploymentSearchActivity,
                R.layout.deployment_list, cursor, from, to);
        mListView.setAdapter(deployments);

        assertTrue(mListView.getCount() > 0);
        mUshahidiDatabase.deleteAllDeployment();

    }
}
