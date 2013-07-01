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

package com.ushahidi.android.app.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.util.ImageManager;

/**
 * This is the adapter for photos to be uploaded to the server.
 * 
 * @author eyedol
 */
public class UploadPhotoAdapter extends BaseListAdapter<PhotoEntity> {

    class Widgets {
        ImageView photo;

        public Widgets(View view) {

            this.photo = (ImageView) view.findViewById(R.id.upload_photo);

        }

    }

    private ListPhotoModel mListPhotoModel;

    private List<PhotoEntity> items;

    /**
     * @param context
     */
    public UploadPhotoAdapter(Context context) {
        super(context);
    }

    /**
     * @see android.widget.Adapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.upload_photo, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        widgets.photo.setImageDrawable(getPhoto(getItem(position).getPhoto()));

        return view;

    }

    /**
     * @see com.ushahidi.android.app.adapters.BaseListAdapter#refresh()
     */
    @Override
    public void refresh() {
        mListPhotoModel = new ListPhotoModel();
        items = mListPhotoModel.getPendingPhotos(context);
        this.setItems(items);

    }

    public void refresh(int reportId) {
        mListPhotoModel = new ListPhotoModel();
        items = mListPhotoModel.getPendingPhotosByReportId(reportId);
        this.setItems(items);

    }

    public List<File> pendingPhotos(int reportId) {
        mListPhotoModel = new ListPhotoModel();
        items = mListPhotoModel.getPendingPhotosByReportId(reportId);
        return getPhotos(items);
    }

    private List<File> getPhotos(List<PhotoEntity> photoEntities) {
        List<File> photos = new ArrayList<File>();
        for (PhotoEntity photo : photoEntities) {
            photos.add(new File(ImageManager.getPhotoPath(context,
                    photo.getPhoto())));

        }

        return photos;
    }

    private Drawable getPhoto(String fileName) {

        return ImageManager.getPendingDrawables(context, fileName);
    }

}
