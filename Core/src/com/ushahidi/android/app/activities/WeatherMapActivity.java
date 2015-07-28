package com.ushahidi.android.app.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.OpacqueWheaterTileProvider;
import com.ushahidi.android.app.adapters.WeatherTileProvider;

public class WeatherMapActivity extends SherlockFragmentActivity {

	private OpacqueWheaterTileProvider provider;
	private TileOverlay overlay;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_weather);

		getSupportActionBar().setTitle("Weather Conditions");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		provider = new OpacqueWheaterTileProvider();
		TileOverlayOptions opts = new TileOverlayOptions();

		opts.tileProvider(provider);

		overlay = map.addTileOverlay(opts);

		((TextView) findViewById(R.id.weather_title)).setText("PRECIPITATION\nMap data © OpenWeatherMap");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.weather_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return false;
		}

		if (provider == null)
			return false;

		if (item.getItemId() == R.id.menu_precipitation) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_PRECIPITATION);
			((TextView) findViewById(R.id.weather_title)).setText("PRECIPITATION\nMap data © OpenWeatherMap");
		} else if (item.getItemId() == R.id.menu_rain) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_RAIN);
			((TextView) findViewById(R.id.weather_title)).setText("RAIN\nMap data © OpenWeatherMap");
		} else if (item.getItemId() == R.id.menu_snow) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_SNOW);
			((TextView) findViewById(R.id.weather_title)).setText("SNOW\nMap data © OpenWeatherMap");
		} else if (item.getItemId() == R.id.menu_temp) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_TEMP);
			((TextView) findViewById(R.id.weather_title)).setText("TEMPERATURE\nMap data © OpenWeatherMap");
		} else if (item.getItemId() == R.id.menu_clouds) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_CLOUD);
			((TextView) findViewById(R.id.weather_title)).setText("CLOUDS\nMap data © OpenWeatherMap");
		} else if (item.getItemId() == R.id.menu_wind) {
			provider.changeTile(OpacqueWheaterTileProvider.TYPE_WIND);
			((TextView) findViewById(R.id.weather_title)).setText("WIND\nMap data © OpenWeatherMap");
		}

		overlay.clearTileCache();

		return super.onOptionsItemSelected(item);
	}

}
