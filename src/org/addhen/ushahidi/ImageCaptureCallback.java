package org.addhen.ushahidi;

import java.util.Random;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class ImageCaptureCallback implements PictureCallback {

	private static Random random = new Random();

	protected static String randomString() {
		return Long.toString(random.nextLong(), 10);
	}
	
	public ImageCaptureCallback() {
	
	}
	
	public void onPictureTaken(byte[] data, Camera camera) {
		try {	
			String filename = "ushandroid" + randomString() + ".jpg";
			ImageManager.writeImage(data, filename);
			UshahidiService.fileName = filename;
			
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

}
