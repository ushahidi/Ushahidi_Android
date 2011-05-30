
package com.ushahidi.android.app.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ushahidi.android.app.DashboardActivity;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.UshahidiPref;

public class ImagePreviewer extends DashboardActivity implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    private Bundle extras;

    private Bundle photos;

    private String images[];

    private ImageSwitcher mSwitcher;

    private ImageAdapter imageAdapter;

    private ImageAdapter thumbnailAdapter;

    private TextView activityTitle;

    public static int photoPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_previewer);

        imageAdapter = new ImageAdapter(this);
        //thumbnailAdapter = new ImageAdapter(this);
        photos = new Bundle();
        extras = getIntent().getExtras();
        activityTitle = (TextView)findViewById(R.id.title_text);

        photos = extras.getBundle("photos");
        images = photos.getStringArray("images");
        
        ImagePreviewer.photoPosition = photos.getInt("position");
        mSwitcher = (ImageSwitcher)findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        if (activityTitle != null) {
            activityTitle.setTextColor(Color.WHITE);
            if (images.length > 1)
                activityTitle.setText(getString(R.string.preview_photo, "s"));
            else
                activityTitle.setText(getString(R.string.preview_photo, "ss"));
        }
        
        if (images.length > 0) {
            for (int i = 0; i < images.length; i++) {
                
                ///thumbnailAdapter.mImageIds.add(ImageManager.getImages(UshahidiPref.savePath,images[i]));
                imageAdapter.mImageIds.add(ImageManager.getImages(UshahidiPref.savePath,images[i]));
                
            }
        }
        mSwitcher.setImageDrawable(imageAdapter.mImageIds.get(ImagePreviewer.photoPosition));
        Gallery g = (Gallery)findViewById(R.id.gallery);

        g.setAdapter(imageAdapter);
        g.setOnItemSelectedListener(this);

    }

    public void onShareClick(View v) {
        // TODO: consider bringing in shortlink to session
        UshahidiPref.loadSettings(this);
        savePhoto(images[ImagePreviewer.photoPosition]);
        String state = Environment.getExternalStorageState();
        final String reportUrl = UshahidiPref.domain;
        final String shareString = getString(R.string.share_template, "", reportUrl);
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdCard = Environment.getExternalStorageDirectory();

            intent.putExtra(
                    Intent.EXTRA_STREAM,
                    Uri.parse("file://" + sdCard.getAbsolutePath() + "/"
                            + images[ImagePreviewer.photoPosition]));
            intent.putExtra(Intent.EXTRA_TEXT, shareString);
            startActivityForResult(Intent.createChooser(intent, getText(R.string.title_share)), 0);
            setResult(RESULT_OK);
        }
    }

    /**
     * Temporarily save photo for sharing sake
     * 
     * @author eyedol
     */
    public void savePhoto(String filename) {

        try {
            String state = Environment.getExternalStorageState();
            ByteArrayOutputStream byteArrayos = new ByteArrayOutputStream();
            Bitmap b = null;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File sdCard = Environment.getExternalStorageDirectory();
                b = ImageManager.getBitmap(filename, UshahidiPref.savePath);
                b.compress(CompressFormat.JPEG, 75, byteArrayos);
                byteArrayos.flush();

                ImageManager.writeImage(byteArrayos.toByteArray(), "/" + filename,
                        sdCard.getAbsolutePath());

            }

        } catch (OutOfMemoryError e) {
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
     
    }

    public class ImageAdapter extends BaseAdapter {

        public Vector<Drawable> mImageIds;

        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
            mImageIds = new Vector<Drawable>();

        }

        public int getCount() {
            return mImageIds.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            i.setImageDrawable(mImageIds.get(position));
            
            i.setAdjustViewBounds(true);

            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            // The preferred Gallery item background
            i.setBackgroundResource(imageBackgroundColor());

            return i;
        }

        public int imageBackgroundColor() {
            TypedArray a = obtainStyledAttributes(R.styleable.PhotoGallery);
            int mGalleryItemBackground = a.getResourceId(
                    R.styleable.PhotoGallery_android_galleryItemBackground, 0);
            a.recycle();

            return mGalleryItemBackground;
        }

    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT));
        return i;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        ImagePreviewer.photoPosition = position;
        mSwitcher.setImageDrawable(imageAdapter.mImageIds.get(position));
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // Ignore
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
     // delete image
        String path = getExternalStorage();
        if (path != null) {
            for (String image : images) {
                ImageManager.deleteImage("/" + image, path);
            }
        }
    }

    public String getExternalStorage() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdCard = Environment.getExternalStorageDirectory();
            return sdCard.getAbsolutePath();
        } else {
            return null;
        }
    }
}
