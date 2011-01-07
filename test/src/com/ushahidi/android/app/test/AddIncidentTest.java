package com.ushahidi.android.app.test;

import com.ushahidi.android.app.AddIncident;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class AddIncidentTest extends ActivityInstrumentationTestCase2<AddIncident> {
	
	public static final int CATEGORY_ADAPTER_COUNT = 5;
	
	public static final int ITEM_TO_BE_SELECTED_POSITION = 2;
	
	public static final int INITIAL_POSITION = 0;
	
	public static final String INITIAL_SELECTION = "Trusted Reports";
	
    public static final int TEST_STATE_DESTROY_POSITION = 2;
    public static final String TEST_STATE_DESTROY_SELECTION = "Earth";

    
    public static final int TEST_STATE_PAUSE_POSITION = 4;
    public static final String TEST_STATE_PAUSE_SELECTION = "Jupiter";

    private AddIncident addIncidentActivity;

    private String categorySelection;

    private int mPos;

    private Spinner categorySpinner;

    private SpinnerAdapter categoryData;
	
	public AddIncidentTest() {
		super("com.ushahidi.android.app",AddIncident.class);
	}
	
	/**
	 * Set up the test environment before each test
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		// turn off touch mode.
		setActivityInitialTouchMode(false);
		
		addIncidentActivity = getActivity();
		
		//categorySpinner = addIncidentActivity.findViewById(com.ushahidi.android.app.R.id.category);
	}
	
	public void testPreconditions() {
		
	}
	
	public void testAddIncidentUI() {
		
	}
	
	public void testStateDestroy() {
		
	}
	
	public void testStatePause() {
		
	}
}
