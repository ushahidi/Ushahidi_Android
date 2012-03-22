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

package com.ushahidi.android.app.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.database.IMediaSchema;
import com.ushahidi.android.app.entities.Category;
import com.ushahidi.android.app.entities.Media;
import com.ushahidi.android.app.entities.Report;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public class ListReportModel extends Model {

    public List<Report> mReports;

    public List<ListReportModel> reportModel;

    private long id;

    private String title;

    private String date;

    private String status;

    private Drawable thumbnail;

    private Drawable arrow;

    private Uri thumbnailUri;

    private String description;

    private String location;

    private String media;

    private String categories;

    private String latitude;

    private String longitude;

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Drawable getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnailUri(Uri uri) {
        this.thumbnailUri = uri;
    }

    public Uri getThumbnailUri() {
        return this.thumbnailUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    public String getDesc() {
        return this.description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia() {
        return this.media;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setArrow(Drawable arrow) {
        this.arrow = arrow;
    }

    public Drawable getArrow() {
        return this.arrow;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    @Override
    public boolean load() {
        mReports = Database.mReportDao.fetchAllReports();

        if (mReports != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean save() {
        return false;
    }

    public boolean loadReportById(long id) {
        mReports = Database.mReportDao.fetchReportById(id);

        if (mReports != null) {
            return true;
        }
        return false;
    }

    public boolean loadReportByCategory(String category) {
        mReports = Database.mReportDao.fetchReportByCategory(category);

        if (mReports != null) {
            return true;
        }
        return false;
    }

    public List<ListReportModel> getReports(Context context) {
        reportModel = new ArrayList<ListReportModel>();

        if (mReports != null && mReports.size() > 0) {
            for (Report item : mReports) {
                ListReportModel listReportModel = new ListReportModel();
                listReportModel.setId(item.getDbId());
                listReportModel.setTitle(Util.capitalize(item.getTitle()));
                listReportModel.setDesc(item.getDescription());
                listReportModel.setDate(Util.formatDate("yyyy-MM-dd HH:mm:ss",
                        item.getReportDate(), "MMMM dd, yyyy 'at' hh:mm:ss aaa"));
                final String status = Util.toInt(item.getVerified()) == 0 ? context
                        .getString(R.string.report_unverified) : context
                        .getString(R.string.report_verified);
                listReportModel.setStatus(status);
                listReportModel.setLocation(item.getLocationName());
                listReportModel.setLatitude(item.getLatitude());
                listReportModel.setLongitude(item.getLongitude());
                listReportModel.setArrow(context.getResources().getDrawable(R.drawable.menu_arrow));
                listReportModel.setCategories(Util.capitalize(item.getCategories()));
                listReportModel.setMedia(item.getMedia());

                final Drawable d = getImage(context, item.getDbId());

                if (d != null) {

                    listReportModel.setThumbnail(d);
                } else {
                    listReportModel.setThumbnail(context.getResources().getDrawable(
                            R.drawable.report_icon));
                }
                reportModel.add(listReportModel);
            }

        }
        return reportModel;
    }

    public Vector<String> getCategories(Context context) {
        final List<Category> categories = Database.mCategoryDao.fetchAllCategoryTitles();
        final Vector<String> vectorCategories = new Vector<String>();

        if ((categories != null) && (categories.size() > 0)) {
            vectorCategories.clear();
            vectorCategories.add(context.getString(R.string.all_categories));

            for (Category category : categories) {
                if (category != null) {
                    vectorCategories.add(category.getCategoryTitle());
                }
            }
        }
        return vectorCategories;
    }

    private Drawable getImage(Context context, int reportId) {
        List<Media> sMedia = Database.mMediaDao.fetchMedia(IMediaSchema.REPORT_ID, reportId,
                IMediaSchema.IMAGE, 1);

        if (sMedia != null && sMedia.size() > 0) {
            Log.i("Media ", "Dag " + sMedia.get(0).getLink());
            return ImageManager.getDrawables(context, sMedia.get(0).getLink());

            // return Drawable.createFromPath(ImageManager.getPhotoPath(context)
            // + "/" + sMedia.get(0).getLink());
        }
        return context.getResources().getDrawable(R.drawable.report_icon);
    }
}
