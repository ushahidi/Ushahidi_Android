package org.addhen.ushahidi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ImageCapture extends Activity implements SurfaceHolder.Callback {
	private Camera camera;
	private boolean isPreviewRunning = false;
	private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.take_picture);
		surfaceView = (SurfaceView)findViewById(R.id.surCamera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			camera.startPreview();
			ImageCapture.this.finish();
		}
	};

	Camera.PictureCallback mPictureCallbackJpeg= new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
		}
	};

	Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
		}
	};


	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		ImageCaptureCallback iccb = null;
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_CAMERA) {
			try {
				String filename = timeStampFormat.format(new Date());
				ContentValues values = new ContentValues();
				values.put(Media.TITLE, filename);
				values.put(Media.DESCRIPTION, "Image capture by camera");
				iccb = new ImageCaptureCallback();
			} catch(Exception ex ){
				ex.printStackTrace();
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_CAMERA) {
			camera.takePicture(mShutterCallback, mPictureCallbackRaw, iccb);
			return true;
		}

		return false;
	}

	protected void onResume(){
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}

	protected void onStop(){
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder){
		camera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
		if (isPreviewRunning) {
			camera.stopPreview();
		}
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(w, h);
		camera.setParameters(p);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.startPreview();
		isPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		camera.stopPreview();
		isPreviewRunning = false;
		camera.release();
	}
}

