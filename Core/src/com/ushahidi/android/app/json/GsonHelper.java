package com.ushahidi.android.app.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class GsonHelper {

	private static Gson mGson = new Gson();

	public static <T> T fromStream(InputStream s, Class<T> cls){
		return mGson
				.fromJson(
						new JsonReader(new InputStreamReader(s)), cls);

	}
	public static <T> T fromUrl(String url, Class<T> cls) throws MalformedURLException, IOException{
		return fromStream(new URL(url).openStream(), cls);
	}

}
