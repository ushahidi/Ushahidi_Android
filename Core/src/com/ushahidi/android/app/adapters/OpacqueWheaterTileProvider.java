package com.ushahidi.android.app.adapters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

public class OpacqueWheaterTileProvider implements TileProvider {

	public static final int TYPE_PRECIPITATION = 0;
	public static final int TYPE_RAIN = 1;
	public static final int TYPE_SNOW = 2;
	public static final int TYPE_TEMP = 3;
	public static final int TYPE_WIND = 4;
	public static final int TYPE_CLOUD = 5;

	private static final String PRECIPITATION = "http://tile.openweathermap.org/map/precipitation/%d/%d/%d.png";
	private static final String RAIN = "http://tile.openweathermap.org/map/rain/%d/%d/%d.png";
	private static final String SNOW = "http://tile.openweathermap.org/map/snow/%d/%d/%d.png";
	private static final String TEMP = "http://tile.openweathermap.org/map/temp/%d/%d/%d.png";
	private static final String WIND = "http://tile.openweathermap.org/map/wind/%d/%d/%d.png";
	private static final String CLOUD = "http://tile.openweathermap.org/map/clouds/%d/%d/%d.png";

	private String url;

	public OpacqueWheaterTileProvider() {
		url = RAIN;
	}

	public void changeTile(int type) {
		switch (type) {
		case TYPE_PRECIPITATION:
			url = PRECIPITATION;
			break;
		case TYPE_RAIN:
			url = RAIN;
			break;
		case TYPE_SNOW:
			url = SNOW;
			break;
		case TYPE_TEMP:
			url = TEMP;
			break;
		case TYPE_WIND:
			url = WIND;
			break;
		case TYPE_CLOUD:
			url = CLOUD;
			break;
		default:
			url = PRECIPITATION;
			break;
		}
	}

	public URL getTileUrl(int x, int y, int z) {
		try {
			return new URL(String.format(url, z, x, y));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tile getTile(int x, int y, int z) {

		URL localURL = getTileUrl(x, y, z);
		if (localURL == null)
			return NO_TILE;

		Tile localTile;
		try {
			localTile = getFinalTile(localURL.openStream());
		} catch (IOException localIOException) {
			localTile = null;
		}
		return localTile;
	}

	private static byte[] convertBitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
		bitmap.compress(CompressFormat.PNG, 100, buffer);
		return buffer.toByteArray();
	}

	public static Tile getFinalTile(InputStream urlInputStream) throws IOException {
		byte[] byteArray = getTileByteArray(urlInputStream);

		Options o = new BitmapFactory.Options();
		o.inMutable = true;
		o.inPreferredConfig = Bitmap.Config.ARGB_8888;

		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, o);

		Bitmap out = null;

		if (bitmap.hasAlpha()) {
			out = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			Canvas c = new Canvas(out);
			int colour = 0x5F << 24;
			c.drawColor(colour, PorterDuff.Mode.DST_OUT);
			c.drawBitmap(out, 0, 0, null);

		} else {
			out = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
			for (int px = 0; px < 256; px++) {
				for (int py = 0; py < 256; py++) {
					int color = bitmap.getPixel(px, py);
					out.setPixel(px, py, Color.argb(0x7F, Color.red(color), Color.green(color), Color.blue(color)));
				}
			}
		}
		byte[] data = convertBitmapToByteArray(out);
		Tile tile = new Tile(256, 256, data);
		return tile;

	}

	private static byte[] getTileByteArray(InputStream urlInputStream) throws IOException {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		getByteArray(urlInputStream, localByteArrayOutputStream);
		return localByteArrayOutputStream.toByteArray();
	}

	private static long getByteArray(InputStream inByteArray, OutputStream outByteArray) throws IOException {
		byte[] arrayOfByte = new byte[4096];
		long l = 0L;
		while (true) {
			int i = inByteArray.read(arrayOfByte);
			if (i == -1)
				break;
			outByteArray.write(arrayOfByte, 0, i);
			l += i;
		}
		return l;
	}

}
