package com.ushahidi.android.app.ui.tablet;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.adapters.ListMapAdapter;
import com.ushahidi.android.app.fragments.BaseListFragment;
import com.ushahidi.android.app.helpers.ActionModeHelper;
import com.ushahidi.android.app.models.ListMapModel;
import com.ushahidi.android.app.net.MapsHttpClient;
import com.ushahidi.android.app.services.FetchReports;
import com.ushahidi.android.app.services.SyncServices;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.ui.phone.AboutActivity;
import com.ushahidi.android.app.util.Util;
import com.ushahidi.android.app.views.AddMapView;
import com.ushahidi.android.app.views.ListMapView;

public class ListMapFragment extends
		BaseListFragment<ListMapView, ListMapModel, ListMapAdapter> implements
		LocationListener {

	private final String[] items = { "50", "100", "250", "500", "750", "1000",
			"1500" };

	private static final int DIALOG_DISTANCE = 0;

	private static final int DIALOG_CLEAR_DEPLOYMENT = 1;

	private static final int DIALOG_ADD_DEPLOYMENT = 2;

	private static final int DIALOG_SHOW_MESSAGE = 3;

	private LocationManager mLocationMgr = null;

	private static Location location;

	private String distance = "";

	private Handler mHandler;

	private int mId = 0;

	private int mapId = 0;

	private int sId = 0;

	private ListMapModel mListMapModel;

	public boolean edit = true;

	private String filter;

	private ListMapFragmentListener listener = null;

	private String errorMessage = "";

	static private final String STATE_CHECKED = "com.ushahidi.android.app.activity.STATE_CHECKED";

	private Intent fetchReports;

	public ProgressDialog dialog;

	public ListMapFragment() {
		super(ListMapView.class, ListMapAdapter.class, R.layout.list_map, 0,
				android.R.id.list);
		mHandler = new Handler();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		mListMapModel = new ListMapModel();
		this.dialog = new ProgressDialog(getActivity());
		this.dialog.setCancelable(true);
		this.dialog.setIndeterminate(true);
		this.dialog.setMessage(getString(R.string.please_wait));

		if (Util.isHoneycomb()) {
			listView.setLongClickable(true);
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.setOnItemLongClickListener(new ActionModeHelper(this,
					listView));
		} else {

			registerForContextMenu(listView);
		}
		log("Adapter count " + adapter.getCount());
		mHandler.post(fetchMapList);

		if (savedInstanceState != null) {
			int position = savedInstanceState.getInt(STATE_CHECKED, -1);

			if (position > -1) {
				listView.setItemChecked(position, true);
			}
		}

	}

	private void fetchReports(int id) {
		if (id != 0) {
			mListMapModel.activateDeployment(getActivity(), id);
			dialog.show();
			fetchReports = new Intent(getActivity(), FetchReports.class);
			getActivity().startService(fetchReports);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putInt(STATE_CHECKED, listView.getCheckedItemPosition());
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver,
				new IntentFilter(SyncServices.SYNC_SERVICES_ACTION));
		mHandler.post(fetchMapList);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopLocating();
	}

	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(broadcastReceiver);
		} catch (IllegalArgumentException e) {
		}

	}

	public void setListMapListener(ListMapFragmentListener listener) {
		this.listener = listener;
	}

	public void enablePersistentSelection() {
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	/**
	 * Delete individual messages 0 - Successfully deleted. 1 - There is nothing
	 * to be deleted.
	 */
	final Runnable mDeleteMapById = new Runnable() {
		public void run() {
			boolean status = false;
			status = mListMapModel.deleteMapById(mId);

			try {
				if (status) {
					toastShort(R.string.map_deleted);
					refreshMapLists();
				} else {
					toastShort(R.string.map_deleted_failed);
				}
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Refresh the list view with new items
	 */
	final Runnable fetchMapList = new Runnable() {
		public void run() {
			try {
				adapter.refresh();
				// mListMapView.mListView.setAdapter(mListMapAdapter);
				// mListMapView.displayEmptyListText();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Filter the list view with new items
	 */
	final Runnable filterMapList = new Runnable() {
		public void run() {
			try {
				adapter.getFilter().filter(filter);
				// mListMapView.displayEmptyListText();
			} catch (Exception e) {
				return;
			}
		}
	};

	/**
	 * Delete all fetched maps
	 */
	final Runnable deleteAllMaps = new Runnable() {
		public void run() {
			boolean status = false;
			status = mListMapModel.deleteAllMap(getActivity());
			try {
				if (status) {
					toastShort(R.string.map_deleted);
					refreshMapLists();
				} else {
					toastShort(R.string.map_deleted_failed);
				}
			} catch (Exception e) {
				return;
			}
		}
	};

	final Runnable refreshMapList = new Runnable() {
		public void run() {
			try {
				refreshMapLists();
			} catch (Exception e) {
				return;
			}
		}
	};

	public void refreshMapLists() {
		adapter.refresh();
	}

	// Context Menu Stuff
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		new MenuInflater(getActivity()).inflate(R.menu.list_map_context, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		boolean result = performAction(item, info.position);

		if (!result) {
			result = super.onContextItemSelected(item);
		}

		return result;
	}

	public boolean performAction(android.view.MenuItem item, int position) {

		mId = adapter.getItem(position).getId();
		mapId = adapter.getItem(position).getMapId();
		if (item.getItemId() == R.id.map_delete) {
			// Delete by ID
			edit = false;
			mHandler.post(mDeleteMapById);
			return (true);
		} else if (item.getItemId() == R.id.map_edit) {
			// edit existing map
			edit = true;
			createDialog(DIALOG_ADD_DEPLOYMENT);
			return (true);
		}

		return (false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.clear_map) {
			createDialog(DIALOG_CLEAR_DEPLOYMENT);
			return true;
		} else if (item.getItemId() == R.id.menu_find) {

			createDialog(DIALOG_DISTANCE);
			return true;
		} else if (item.getItemId() == R.id.menu_add) {
			edit = false;
			createDialog(DIALOG_ADD_DEPLOYMENT);
			return true;
		} else if (item.getItemId() == R.id.app_settings) {
			startActivity(new Intent(getActivity(), Settings.class));

			return true;
		} else if (item.getItemId() == R.id.app_about) {
			startActivity(new Intent(getActivity(), AboutActivity.class));

			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		log("on map itemClicked");
		final int sId = adapter.getItem(position).getId();

		if (isMapActive(sId)) {
			if (listener != null) {
				listener.onMapSelected();
			}
		} else {
			fetchReports(sId);
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		l.setItemChecked(position, true);

		sId = adapter.getItem(position).getId();

		if (isMapActive(sId)) {
			if (listener != null) {
				listener.onMapSelected();
			}
		} else {

			fetchReports(sId);
		}

	}

	/**
	 * Check if a deployment is the active one
	 * 
	 * @param id
	 *            - map's id
	 * @return boolean
	 */

	public boolean isMapActive(int id) {
		Preferences.loadSettings(getActivity());
		if (Preferences.activeDeployment == id) {
			return true;
		}
		return false;

	}

	/**
	 * Create an alert dialog
	 */

	public void createDialog(int d) {
		switch (d) {

		case DIALOG_SHOW_MESSAGE:
			AlertDialog.Builder messageBuilder = new AlertDialog.Builder(
					getActivity());
			messageBuilder.setMessage(errorMessage).setPositiveButton(
					getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			AlertDialog showDialog = messageBuilder.create();
			showDialog.show();
			break;

		case DIALOG_DISTANCE:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.select_distance);
			builder.setSingleChoiceItems(items, Preferences.selectedDistance,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							distance = items[item];
							setDeviceLocation();
							Preferences.selectedDistance = item;
							// save prefrences
							Preferences.saveSettings(getActivity());
							dialog.cancel();
							toastLong(R.string.finding_map);
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
			break;

		case DIALOG_CLEAR_DEPLOYMENT:
			AlertDialog.Builder clearBuilder = new AlertDialog.Builder(
					getActivity());
			clearBuilder
					.setMessage(getString(R.string.confirm_clear))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mHandler.post(deleteAllMaps);
								}
							})
					.setNegativeButton(getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog clearDialog = clearBuilder.create();
			clearDialog.show();

			break;

		case DIALOG_ADD_DEPLOYMENT:
			LayoutInflater factory = LayoutInflater.from(getActivity());
			final View textEntryView = factory.inflate(R.layout.add_map, null);
			final AddMapView addMapView = new AddMapView(textEntryView);

			// if edit was selected at the context menu, populate fields
			// with existing map details
			if (edit) {
				final List<ListMapModel> listMap = mListMapModel.loadMapById(
						mId, mapId);
				if (listMap.size() > 0) {
					addMapView.setMapName(listMap.get(0).getName());
					addMapView.setMapDescription(listMap.get(0).getDesc());
					addMapView.setMapUrl(listMap.get(0).getUrl());
					addMapView.setMapId(listMap.get(0).getId());
				}
			}

			final AlertDialog.Builder addBuilder = new AlertDialog.Builder(
					getActivity());

			addBuilder
					.setTitle(R.string.enter_map_details)
					.setView(textEntryView)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// edit was selected
									if (edit) {

										if (!addMapView.updateMapDetails())
											toastLong(R.string.fix_error);
										else
											mHandler.post(refreshMapList);
									} else {

										if (!addMapView.addMapDetails())
											toastLong(R.string.fix_error);
										else
											mHandler.post(fetchMapList);
									}

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});

			AlertDialog deploymentDialog = addBuilder.create();
			deploymentDialog.show();
			break;
		}

	}

	/**
	 * Load Map details from the web
	 */
	class LoadMapTask extends ProgressTask {

		protected Boolean status;

		private MapsHttpClient maps;

		protected String distance;

		protected Location location;

		public LoadMapTask(Activity activity) {
			super(activity, R.string.loading_);
			// switch to a progress animation
			maps = new MapsHttpClient(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.cancel();

		}

		@Override
		protected Boolean doInBackground(String... strings) {
			try {
				status = maps.fetchMaps(distance, location);
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {

				toastShort(R.string.could_not_fetch_data);
			} else {

				toastShort(R.string.maps_fetched_successful);
			}
			adapter.refresh();

		}

	}

	@Override
	protected void onLoaded(boolean success) {
		try {

			if (success) {
				if (Preferences.isCheckinEnabled == 1) {
					toastLong(R.string.checkin_is_enabled);
				}
				if (listener != null) {
					listener.onMapSelected();
				}

			} else {
				toastLong(R.string.failed);
			}
		} catch (IllegalArgumentException e) {
			log(e.toString());
		}
	}

	/** Location stuff **/
	// Fetches the current location of the device.
	protected void setDeviceLocation() {
		mLocationMgr = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		// Get last known location from either GPS or Network provider
		Location loc = null;
		boolean netAvail = (mLocationMgr
				.getProvider(LocationManager.NETWORK_PROVIDER) != null);
		boolean gpsAvail = (mLocationMgr
				.getProvider(LocationManager.GPS_PROVIDER) != null);
		if (gpsAvail) {
			loc = mLocationMgr
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		} else if (netAvail) {
			loc = mLocationMgr
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		// Just use last location if it's less than 10 minutes old
		if (loc != null
				&& ((new Date()).getTime() - loc.getTime() < 10 * 60 * 1000)) {
			onLocationChanged(loc);
		} else {
			if (gpsAvail) {
				mLocationMgr.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, this);
			}
			if (netAvail) {
				mLocationMgr.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}
		}
	}

	public void stopLocating() {
		if (mLocationMgr != null) {
			try {
				mLocationMgr.removeUpdates(this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			mLocationMgr = null;
		}
	}

	public void onLocationChanged(Location loc) {
		if (loc != null) {
			location = loc;
			LoadMapTask deploymentTask = new LoadMapTask(getActivity());
			deploymentTask.location = location;
			deploymentTask.distance = distance;
			deploymentTask.execute();
			stopLocating();
		}

	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.android.app.fragments.BaseListFragment#headerView()
	 */
	@Override
	protected View headerView() {
		// TODO Auto-generated method stub
		return null;
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {

				int status = intent.getIntExtra("status", 113);
				getActivity().stopService(fetchReports);
				dialog.cancel();

				if (status == 0) {
					onLoaded(true);
				} else if (status == 100) {
					errorMessage = getString(R.string.internet_connection);
					createDialog(DIALOG_SHOW_MESSAGE);
				} else if (status == 99) {
					errorMessage = getString(R.string.failed);
					createDialog(DIALOG_SHOW_MESSAGE);
				} else if (status == 112) {
					errorMessage = getString(R.string.network_error);
					createDialog(DIALOG_SHOW_MESSAGE);
				} else {
					errorMessage = getString(R.string.error_occured);
					createDialog(DIALOG_SHOW_MESSAGE);

				}

			} else {
				toastLong(R.string.failed);
			}

		}

	};

}