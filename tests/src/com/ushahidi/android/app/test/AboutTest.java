package com.ushahidi.android.app.test;

import com.ushahidi.android.app.About;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class AboutTest extends ActivityInstrumentationTestCase2<About> {
	
	private About aboutActivity;
	private TextView aboutVersionView;
	private String aboutString;
	
	public AboutTest() {
		super("com.ushahidi.android.app", About.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		aboutActivity = this.getActivity();
		aboutVersionView = (TextView) aboutActivity.findViewById(com.ushahidi.android.app.R.id.version);
		aboutString = aboutActivity.getString(com.ushahidi.android.app.R.string.version);
	}
	
	public void testPreconditions() {
		assertNotNull(aboutVersionView);
	}

	public void testText() {
		assertEquals(aboutString,(String)aboutVersionView.getText());
	}
}
