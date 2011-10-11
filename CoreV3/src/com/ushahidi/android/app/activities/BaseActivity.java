/**
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 **/

package com.ushahidi.android.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ushahidi.android.app.views.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * BaseActivity
 *
 * Add shared functionality that exists between all Activities
 */
public abstract class BaseActivity<V extends View> extends Activity {

    /**
     * Layout resource id
     */
    protected final int layout;

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     *  View class
     */
    protected final Class<V> viewClass;

    /**
     * View
     */
    protected V view;

    /**
     *  BaseActivity
     * @param view View class
     * @param layout layout resource id
     * @param menu menu resource id
     */
    protected BaseActivity(Class<V> view, int layout, int menu) {
        this.viewClass = view;
        this.layout = layout;
        this.menu = menu;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        if (layout != 0) {
            setContentView(layout);
        }
        view = createInstance(viewClass, Activity.class, this);
    }

    @Override
	protected void onStart() {
		super.onStart();
		log("onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
        log("onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		log("onResume");
	}

    @Override
	protected void onPause() {
		super.onPause();
		log("onPause");
    }

	@Override
	protected void onStop() {
		super.onStop();
		log("onStop");
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		log("onDestroy");
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            log("onKeyDown KEYCODE_BACK");
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log("onActivityResult");
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if (this.menu != 0) {
			getMenuInflater().inflate(this.menu, menu);
			return true;
        }
		return false;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		log("onOptionsItemSelected");
    	return super.onOptionsItemSelected(item);
    }

    protected EditText findEditTextById(int id) {
		return (EditText) findViewById(id);
	}

	protected ListView findListViewById(int id) {
		return (ListView) findViewById(id);
	}

	protected TextView findTextViewById(int id) {
		return (TextView) findViewById(id);
	}

	protected Spinner findSpinnerById(int id) {
		return (Spinner) findViewById(id);
	}

    protected TimePicker findTimePickerById(int id) {
		return (TimePicker) findViewById(id);
	}

	protected Button findButtonById(int id) {
		return (Button) findViewById(id);
	}

    protected void log(String message) {
        Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object...args) {
        Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        Log.e(getClass().getName(), message, ex);
    }

    protected void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(String format, Object...args) {
        Toast.makeText(this, String.format(format, args), Toast.LENGTH_LONG).show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
    }

    protected void toastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(String format, Object...args) {
        Toast.makeText(this, String.format(format, args), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(this, getText(message), Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unchecked")
    protected <T> T createInstance(Class type, Class constructor, Object...params) {
        try {
            return (T)type.getConstructor(constructor).newInstance(params);
        }
        catch (InstantiationException e) {
            log("InstantiationException", e);
        }
        catch (IllegalAccessException e) {
            log("IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            log("InvocationTargetException", e);
        }
        catch (NoSuchMethodException e) {
            log("NoSuchMethodException", e);
        }
        return null;
    }
}
