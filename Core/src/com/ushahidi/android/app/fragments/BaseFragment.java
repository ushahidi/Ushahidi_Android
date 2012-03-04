
package com.ushahidi.android.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Toast;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.helpers.MapFragment;

public class BaseFragment extends Fragment {

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     * BaseActivity
     * 
     * @param view View class
     * @param layout layout resource id
     * @param menu menu resource id
     */
    protected BaseFragment(int menu) {

        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.menu != 0) {
            inflater.inflate(this.menu, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    protected void log(String message) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        if (MainApplication.LOGGING_MODE)
            Log.e(getClass().getName(), message, ex);
    }

    protected void toastLong(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_LONG).show();
    }

    protected void toastShort(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(getActivity(), getActivity().getString(message), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT).show();
    }

}
