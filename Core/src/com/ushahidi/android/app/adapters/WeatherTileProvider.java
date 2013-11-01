package com.ushahidi.android.app.adapters;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.UrlTileProvider;

public class WeatherTileProvider extends UrlTileProvider {

	public static final int TYPE_PRECIPITATION = 0;
	public static final int TYPE_RAIN = 1;
	public static final int TYPE_SNOW = 2;

	private static final String PRECIPITATION = "http://tile.openweathermap.org/map/precipitation/%d/%d/%d.png";
	private static final String RAIN = "http://tile.openweathermap.org/map/rain/%d/%d/%d.png";
	private static final String SNOW = "http://tile.openweathermap.org/map/snow/%d/%d/%d.png";
	private String url;

	public WeatherTileProvider() {
		super(256, 256);
		url = RAIN;
	}

	public void changeTile(int type){
			switch(type){
			case TYPE_PRECIPITATION:
				url = PRECIPITATION;
				break;
			case TYPE_RAIN:
				url = RAIN;
				break;
			case TYPE_SNOW:
				url = SNOW;
				break;
			}
		}

	@Override
	public URL getTileUrl(int x, int y, int z) {
		System.out.println("get url ");
		try {
			return new URL(String.format(url, z, x, y));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
}