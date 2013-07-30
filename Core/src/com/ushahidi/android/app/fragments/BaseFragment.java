package com.ushahidi.android.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.util.AnalyticsUtils;
import com.ushahidi.android.app.util.Util;

public class BaseFragment extends SherlockFragment {

	/**
	 * Menu resource id
	 */
	protected int menu = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");

		setHasOptionsMenu(true);
		
		// start google analytics
		AnalyticsUtils.setContext(getActivity());

	}
	
	@Override
    public void onStart() {
        super.onStart();
        log("onStart");
       AnalyticsUtils.activityStart(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        log("onStop");
        AnalyticsUtils.activityStop(getActivity());
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.menu != 0) {
			inflater.inflate(this.menu, menu);
		}

	}

	@Override
	public android.view.View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		android.view.View root = null;

		return root;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		return super.onContextItemSelected(item);
	}

	protected void log(String message) {
		new Util().log(message);
	}

	protected void log(String format, Object... args) {
		new Util().log(String.format(format, args));
	}

	protected void log(String message, Exception ex) {
		new Util().log(message, ex);
	}

	protected void toastLong(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	protected void toastLong(int message) {
		Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG)
				.show();
	}

	protected void toastLong(String format, Object... args) {
		Toast.makeText(getActivity(), String.format(format, args),
				Toast.LENGTH_LONG).show();
	}

	protected void toastLong(CharSequence message) {
		Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_LONG)
				.show();
	}

	protected void toastShort(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	protected void toastShort(String format, Object... args) {
		Toast.makeText(getActivity(), String.format(format, args),
				Toast.LENGTH_SHORT).show();
	}

	protected void toastShort(int message) {
		Toast.makeText(getActivity(), getActivity().getString(message),
				Toast.LENGTH_SHORT).show();
	}

	protected void toastShort(CharSequence message) {
		Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT)
				.show();
	}

}
