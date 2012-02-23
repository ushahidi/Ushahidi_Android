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

package com.ushahidi.android.app.views;

import java.util.Vector;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentMapActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.MapView;
import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.Preferences;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ViewReportView extends com.ushahidi.android.app.views.View {

    private TextView title;

    private TextView body;

    private TextView date;

    private TextView location;

    private TextView category;

    private TextView status;

    private TextView photos;

    private MapView mapView;

    private Gallery g;

    private FragmentMapActivity activity;

    private ImageSwitcher mSwitcher;

    private ImageAdapter imageAdapter;

    private ImageAdapter thumbnailAdapter;

    private String images;

    public ViewReportView(FragmentMapActivity activity) {
        super(activity);
        this.activity = activity;
        mapView = (MapView)activity.findViewById(R.id.loc_map);
        title = (TextView)activity.findViewById(R.id.title);
        category = (TextView)activity.findViewById(R.id.category);
        date = (TextView)activity.findViewById(R.id.date);
        location = (TextView)activity.findViewById(R.id.location);
        body = (TextView)activity.findViewById(R.id.webview);
        status = (TextView)activity.findViewById(R.id.status);
        photos = (TextView)activity.findViewById(R.id.report_photo);
        mSwitcher = (ImageSwitcher)activity.findViewById(R.id.switcher);
        imageAdapter = new ImageAdapter(activity);
        thumbnailAdapter = new ImageAdapter(activity);

    }

    public void setMedia(String media) {
        if (TextUtils.isEmpty(media)) {
            photos.setText("");
        } else {
            final String thumbnails[] = media.split(",");
            for (int i = 0; i < thumbnails.length; i++) {
                thumbnailAdapter.mImageIds.add(ImageManager.getImages(Preferences.savePath,
                        thumbnails[i]));
            }

            if (!TextUtils.isEmpty(getImage())) {
                final String images[] = getImage().split(",");

                for (int i = 0; i < images.length; i++) {

                    imageAdapter.mImageIds.add(ImageManager.getImages(Preferences.savePath,
                            images[i]));
                }
            }
        }
    }

    public void setImage(String image) {
        this.images = image;
    }

    public String getImage() {
        return this.images;
    }

    public void setTitle(String title) {
        this.title.setTypeface(Typeface.DEFAULT_BOLD);
        this.title.setText(title);
    }

    public String getTitle() {
        return this.getTitle().toString();
    }

    public void setCategory(String category) {
        this.category.setTextColor(Color.BLACK);
        this.category.setText(category);
    }

    public String getCategory() {
        return this.category.getText().toString();
    }

    public void setDate(String date) {
        this.date.setTextColor(Color.BLACK);
        this.date.setText(date);
    }

    public String getDate() {
        return this.date.getText().toString();
    }

    public void setLocation(String location) {
        this.location.setTextColor(Color.BLACK);
        this.location.setText(location);
    }

    public String getLocation() {
        return this.location.getText().toString();
    }

    public void setBody(String body) {
        this.body.setTextColor(Color.BLACK);
        this.body.setText(body);
    }

    public String getBody() {
        return this.body.getText().toString();
    }

    public void setStatus(String status) {
        final String iStatus = Util.toInt(status) == 0 ? activity.getString(R.string.status_no)
                : activity.getString(R.string.status_yes);
        this.status.setText(iStatus);
    }

    public String getStatus() {
        return this.status.getText().toString();
    }

    public MapView getMapView() {
        return this.mapView;
    }

    public Gallery getGallery() {
        return this.g;
    }

    public ImageSwitcher getImageSwitcher() {
        return this.mSwitcher;
    }

    public int imageBackgroundColor() {
        TypedArray a = activity.obtainStyledAttributes(R.styleable.PhotoGallery);
        int mGalleryItemBackground = a.getResourceId(
                R.styleable.PhotoGallery_android_galleryItemBackground, 0);
        a.recycle();

        return mGalleryItemBackground;
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

            i.setScaleType(ImageView.ScaleType.FIT_XY);

            i.setLayoutParams(new Gallery.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // The preferred Gallery item background
            i.setBackgroundResource(imageBackgroundColor());

            return i;
        }

    }

}
