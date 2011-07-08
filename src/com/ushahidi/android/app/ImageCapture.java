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

package com.ushahidi.android.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class ImageCapture extends Activity implements SurfaceHolder.Callback {
    private Camera mCamera;

    private boolean mIsPreviewRunning = false;

    private SimpleDateFormat mTimeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private SurfaceView mSurfaceView;

    private SurfaceHolder mSurfaceHolder;

    private RelativeLayout mRelativeLayout;

    private ShutterButton mShutterButton;

    private Bundle mBundle;

    private Intent mIntent;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.take_picture);

        mSurfaceView = (SurfaceView)findViewById(R.id.sur_camera);
        mShutterButton = (ShutterButton)findViewById(R.id.shutter_button);
        mRelativeLayout = (RelativeLayout)findViewById(R.id.snap_photo);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mBundle = new Bundle();
        mIntent = new Intent();

        mShutterButton.setOnShutterButtonListener(new ShutterButton.OnShutterButtonListener() {

            public void onShutterButtonClick(ShutterButton b) {
                switch (b.getId()) {
                    case R.id.shutter_button:
                        onSnap();
                        break;
                }
            }

            public void onShutterButtonFocus(ShutterButton b, boolean pressed) {
                // TODO: Auto-generated method stub

            }
        });

        mRelativeLayout.setOnClickListener(new RelativeLayout.OnClickListener() {

            public void onClick(View v) {
                mShutterButton.setEnabled(false);
                // For auto-focus:
                // mCamera.autoFocus(mAutoFocusCallback);
            }

        });

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private static Random random = new Random();

    protected static String randomString() {
        return Long.toString(random.nextLong(), 10);
    }

    PictureCallback mPictureCallbackRaw = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            // TODO: do something with raw image
        }
    };

    PictureCallback mPictureCallbackJpeg = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera c) {

            mCamera.startPreview();
            String filename = "ushandroid_" + randomString() + ".jpg";
            ImageManager.writeImage(data, filename,UshahidiPref.savePath);
            mBundle.putString("name", filename);
            mIntent.putExtra("filename", mBundle);

            ImageCapture.this.setResult(RESULT_OK, mIntent);
            ImageCapture.this.finish();

        }
    };

    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO: Do something when button is pressed.
        }
    };

    // Implement auto focus
    /*
     * AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() { public
     * void onAutoFocus(boolean success, Camera camera) {
     * mShutterButton.setEnabled(true); } };
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_CAMERA) {
            try {
                String filename = mTimeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(MediaColumns.TITLE, filename);
                values.put(ImageColumns.DESCRIPTION, "Image capture by camera");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_CAMERA) {
            mCamera.takePicture(mShutterCallback, mPictureCallbackRaw, mPictureCallbackJpeg);
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mIsPreviewRunning) {
            mCamera.stopPreview();
        }
        
        Camera.Parameters p = mCamera.getParameters();
        
        //get supported screen sizes
        List<Camera.Size> previewSizes = p.getSupportedPreviewSizes();
        if (previewSizes != null){
            Camera.Size previewSize = previewSizes.get(0);
            p.setPreviewSize(previewSize.width, previewSize.height);
        }
        mCamera.setParameters(p);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.startPreview();
        mIsPreviewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mIsPreviewRunning = false;
        mCamera.release();
    }

    public void onSnap() {
        mCamera.takePicture(mShutterCallback, mPictureCallbackRaw, mPictureCallbackJpeg);
    }
}
