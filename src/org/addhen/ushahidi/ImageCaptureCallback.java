package org.addhen.ushahidi;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class ImageCaptureCallback implements PictureCallback {


	public ImageCaptureCallback() {
	}
	

	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			SaveIncidentsImage tr = new SaveIncidentsImage(data);
			UshahidiService.AddThreadToQueue(tr);
			
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
}
