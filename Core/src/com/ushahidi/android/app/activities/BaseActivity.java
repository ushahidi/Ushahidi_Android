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

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.MenuAdapter;
import com.ushahidi.android.app.models.MenuDrawerItemModel;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.ui.phone.AdminActivity;
import com.ushahidi.android.app.ui.phone.ListMapActivity;
import com.ushahidi.android.app.ui.tablet.AboutFragment;
import com.ushahidi.android.app.util.Objects;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.View;

/**
 * BaseActivity Add shared functionality that exists between all Activities
 */
public abstract class BaseActivity<V extends View> extends
		SherlockFragmentActivity {

	/**
	 * Layout resource id
	 */
	protected int layout;

	/**
	 * Menu resource id
	 */
	protected int menu;

	/**
	 * View class
	 */
	protected Class<V> viewClass;

	/**
	 * View
	 */
	protected V view;

	protected ActionBar actionBar;

	private MenuDrawer mMenuDrawer;

	private ListView mListView;

	private MenuAdapter mAdapter;

	protected static final int MAP_ACTIVITY = 0;
	protected static final int ADMIN_ACTIVITY = 1;
	protected static final int SETTINGS_ACTIVITY = 2;
	protected static final int ABOUT_ACTIVITY = 3;

	public BaseActivity() {

		this.viewClass = null;
		this.layout = 0;
		this.menu = 0;
	}

	/**
	 * BaseActivity
	 * 
	 * @param view
	 *            View class
	 * @param layout
	 *            layout resource id
	 * @param menu
	 *            menu resource id
	 */
	protected BaseActivity(Class<V> view, int layout, int menu) {

		this.viewClass = view;
		this.layout = layout;
		this.menu = menu;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (layout != 0) {
			createMenuDrawer(layout);
		}

		if (viewClass != null) {
			view = Objects.createInstance(viewClass, Activity.class, this);
		}
	}

	private MenuDrawer appendMenuDrawer() {
		MenuDrawer menuDrawer = null;
		menuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_CONTENT);
		int shadowSizeInPixels = getResources().getDimensionPixelSize(
				R.dimen.menu_shadow_width);
		menuDrawer.setDropShadowSize(shadowSizeInPixels);
		menuDrawer.setDropShadowColor(getResources().getColor(
				R.color.md_shadowColor));
		return menuDrawer;
	}

	protected void createMenuDrawer(int contentViewID) {
		mMenuDrawer = appendMenuDrawer();
		mMenuDrawer.setContentView(contentViewID);
		initMenuDrawer();
	}

	/**
	 * Create a menu drawer and attach it to the activity.
	 * 
	 * @param contentView
	 *            {@link android.view.View} of the main content for the
	 *            activity.
	 */
	protected void createMenuDrawer(android.view.View contentView) {
		mMenuDrawer = appendMenuDrawer();
		mMenuDrawer.setContentView(contentView);

		initMenuDrawer();
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
		if (mMenuDrawer != null) {
			updateMenuDrawer();
		}
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

	protected void setActionBarTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			log("onKeyDown KEYCODE_BACK");
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Called when the activity has detected the user's press of the back key.
	 * If the activity has a menu drawer attached that is opened or in the
	 * process of opening, the back button press closes it. Otherwise, the
	 * normal back action is taken.
	 */
	@Override
	public void onBackPressed() {
		if (mMenuDrawer != null) {
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN
					|| drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
				finish();
				return;
			}
		}

		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log("onActivityResult");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (this.menu != 0) {
			getSupportMenuInflater().inflate(this.menu, menu);
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggleMenuDrawer();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		return super.onContextItemSelected(item);
	}

	protected void toggleMenuDrawer() {
		if (mMenuDrawer != null) {
			mMenuDrawer.toggleMenu();
		}
	}

	protected void shareText(String shareItem) {

		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, shareItem);

		startActivity(Intent.createChooser(intent,
				getText(R.string.title_share)));
	}

	protected void sharePhoto(String path) {

		// TODO: consider bringing in shortlink to session
		Preferences.loadSettings(this);
		final String reportUrl = Preferences.domain;
		final String shareString = getString(R.string.share_template, " ", " "
				+ reportUrl);
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/jpg");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
		intent.putExtra(Intent.EXTRA_TEXT, shareString);
		startActivityForResult(
				Intent.createChooser(intent, getText(R.string.title_share)), 0);
		setResult(RESULT_OK);

	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 */
	public static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable("_uri", data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}

	private void initMenuDrawer() {
		mListView = new ListView(this);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setDivider(null);
		mListView.setDividerHeight(0);
		mListView.setCacheColorHint(android.R.color.transparent);

		mListView.setOnItemClickListener(mItemClickListener);
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mMenuDrawer.invalidate();
			}
		});

		mMenuDrawer.setMenuView(mListView);

		updateMenuDrawer();
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, android.view.View view,
				int position, long id) {

			if (position == mAdapter.activePosition) {
				// Same row selected
				mMenuDrawer.closeMenu();
				return;
			}

			int activityTag = (Integer) view.getTag();

			mAdapter.activePosition = position;
			mAdapter.notifyDataSetChanged();
			Intent intent = null;

			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(BaseActivity.this);
			SharedPreferences.Editor editor = settings.edit();
			switch (activityTag) {
			case MAP_ACTIVITY:
				intent = new Intent(BaseActivity.this, ListMapActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				editor.putInt(Preferences.PREFS_NAME, MAP_ACTIVITY);
				break;

			case ADMIN_ACTIVITY:
				intent = new Intent(BaseActivity.this, AdminActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				editor.putInt(Preferences.PREFS_NAME, ADMIN_ACTIVITY);
				break;

			case SETTINGS_ACTIVITY:
				intent = new Intent(BaseActivity.this, Settings.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				editor.putInt(Preferences.PREFS_NAME, SETTINGS_ACTIVITY);
				break;
			case ABOUT_ACTIVITY:
				intent = new Intent(BaseActivity.this, AboutActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				editor.putInt(Preferences.PREFS_NAME, ABOUT_ACTIVITY);
				break;
			}
			editor.commit();
			if (intent != null) {
				mMenuDrawer.closeMenu();
				startActivityWithDelay(intent);
			}
		}

	};

	protected void startActivityZoomIn(final Intent i) {
		startActivity(i);
		overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
	}

	protected void startActivityWithDelay(final Intent i) {

		// Let the menu animation finish before starting a new activity
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(i);
			}
		}, 400);

	}

	/**
	 * Update all of the items in the menu drawer.
	 */
	protected void updateMenuDrawer() {

		List<Object> items = new ArrayList<Object>();
		Resources resources = getResources();
		int position = 0;
		
		items.add(new MenuDrawerItemModel(resources.getString(R.string.maps),
				R.drawable.map));
		
		items.add(new MenuDrawerItemModel(resources.getString(R.string.admin),
				R.drawable.web));
		
		items.add(new MenuDrawerItemModel(resources
				.getString(R.string.settings), R.drawable.settings));
		
		items.add(new MenuDrawerItemModel(resources.getString(R.string.about),
				R.drawable.about));

		if ((BaseActivity.this instanceof ListMapActivity))
			position = 0;

		if ((BaseActivity.this instanceof AdminActivity))
			position = 1;

		else if ((BaseActivity.this instanceof AboutActivity))
			position = 2;

		mAdapter = new MenuAdapter(this, items);
		mAdapter.activePosition = position;
		mListView.setAdapter(mAdapter);
	}
	
	public void showAboutDialog() {

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out,
				R.anim.slide_right_in, R.anim.slide_right_out);
		ft.addToBackStack(null);

		// Create and show the dialog.
		AboutFragment newFragment = AboutFragment.newInstance();
		newFragment.show(ft, "dialog");
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

	protected ImageView findImageViewById(int id) {
		return (ImageView) findViewById(id);
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
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	protected void toastLong(int message) {
		Toast.makeText(this, getText(message), Toast.LENGTH_LONG).show();
	}

	protected void toastLong(String format, Object... args) {
		Toast.makeText(this, String.format(format, args), Toast.LENGTH_LONG)
				.show();
	}

	protected void toastLong(CharSequence message) {
		Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();
	}

	protected void toastShort(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	protected void toastShort(String format, Object... args) {
		Toast.makeText(this, String.format(format, args), Toast.LENGTH_SHORT)
				.show();
	}

	protected void toastShort(int message) {
		Toast.makeText(this, getText(message), Toast.LENGTH_SHORT).show();
	}

	protected void toastShort(CharSequence message) {
		Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
	}
}