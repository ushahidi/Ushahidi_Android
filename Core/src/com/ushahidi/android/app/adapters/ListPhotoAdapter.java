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

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.PhotoEntity;
import com.ushahidi.android.app.models.ListPhotoModel;
import com.ushahidi.android.app.util.ImageViewWorker;

/**
 * @author eyedol
 */
public class ListPhotoAdapter extends BaseListAdapter<PhotoEntity> {

    private ListPhotoModel mListPhotoModel;

    private List<PhotoEntity> items;

    private int totalPhotos;

    /**
     * @param context
     */
    public ListPhotoAdapter(Context context) {
        super(context);
    }

    class Widgets {
        ImageView photo;
        TextView total;

        public Widgets(View view) {

            this.photo = (ImageView) view.findViewById(R.id.list_report_photo);
            this.total = (TextView) view.findViewById(R.id.photo_total);

        }

    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_photo_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        // FIXME: only show the first item for now. In the future only get one
        // item
        // widgets.photo.setImageDrawable(getPhoto(getItem(position).getPhoto()));

        getPhoto(getItem(position).getPhoto(), widgets.photo);
        widgets.total.setText(context.getResources().getQuantityString(
                R.plurals.no_of_images, totalPhotos, totalPhotos));
        return view;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.adapters.BaseListAdapter#refresh(android.content
     * .Context)
     */
    @Override
    public void refresh() {

    }

    public void refresh(int reportId) {
        mListPhotoModel = new ListPhotoModel();
        final boolean loaded = mListPhotoModel.load(reportId);
        totalPhotos = mListPhotoModel.totalReportPhoto();
        if (loaded) {
            items = mListPhotoModel.getPhotos();
            this.setItems(items);
        }
    }

    public void getPhoto(String fileName, ImageView imageView) {
        ImageViewWorker imageWorker = new ImageViewWorker(context);
        imageWorker.setImageFadeIn(true);
        imageWorker.loadImage(fileName, imageView, true, 0);
    }

}
