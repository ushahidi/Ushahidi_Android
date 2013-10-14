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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.CategoryEntity;
import com.ushahidi.android.app.entities.ReportEntity;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.util.ImageViewWorker;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public abstract class ReportAdapter extends BaseListAdapter<ReportEntity>
        implements Filterable {

    class Widgets {

        TextView title;

        TextView iLocation;

        TextView date;

        TextView status;

        TextView categories;

        TextView description;

        ImageView thumbnail;


        public Widgets(View view) {

            this.thumbnail = (ImageView) view
                    .findViewById(R.id.report_thumbnail);
            this.title = (TextView) view.findViewById(R.id.report_title);
            this.description = (TextView) view
                    .findViewById(R.id.report_description);
            this.date = (TextView) view.findViewById(R.id.report_date);
            this.iLocation = (TextView) view.findViewById(R.id.report_location);
            this.categories = (TextView) view
                    .findViewById(R.id.report_categories);
            this.status = (TextView) view.findViewById(R.id.report_status);
        }

    }

    protected int[] colors;

    protected ListReportModel mListReportModel;

    protected List<ReportEntity> items;

    public ReportAdapter(Context context) {
        super(context);

        colors = new int[] {
                R.drawable.odd_row_rounded_corners,
                R.drawable.even_row_rounded_corners
        };
        mListReportModel = new ListReportModel();
    }

    public String fetchCategories(int reportId) {
        StringBuilder categories = new StringBuilder();
        for (CategoryEntity category : mListReportModel
                .getCategoriesByReportId(reportId)) {
            if (category.getCategoryTitle().length() > 0) {
                categories.append(category.getCategoryTitle() + " |");
            }

        }

        // delete the last |
        if (categories.length() > 0) {
            categories.deleteCharAt(categories.length() - 1);
        }
        return categories.toString();
    }

    public View getView(int position, View view, ViewGroup viewGroup) {

        int colorPosition = position % colors.length;
        Widgets widgets;
        if (view == null) {
            view = inflater
                    .inflate(R.layout.list_report_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        view.setBackgroundResource(colors[colorPosition]);

        final String thumbnailPath = thumbnail(position);

        if (thumbnailPath == null) {

            widgets.thumbnail.setImageResource(R.drawable.report_icon);

        } else {
            getPhoto(thumbnailPath, widgets.thumbnail);

        }
        widgets.title.setText(getItem(position).getIncident().getTitle());

        widgets.date.setText(Util.datePattern(
                "MMMM dd, yyyy 'at' hh:mm:ss aaa", getItem(position)
                        .getIncident().getDate()));
        widgets.description.setText(Util.capitalizeString(getItem(position)
                .getIncident().getDescription()));

        widgets.categories.setText(Util.capitalizeString(Util.limitString(
                fetchCategories((int) getItem(position).getIncident().getId()),
                100)));
        widgets.iLocation.setText(Util.capitalizeString(getItem(position)
                .getIncident().getLocationName()));
        // change the status color

        if (getItem(position).getIncident().getVerified() == 1) {
            widgets.status.setTextColor(context.getResources().getColor(
                    R.color.verified_text_color)); // green
        } else {
            widgets.status.setTextColor(context.getResources().getColor(
                    R.color.unverified_text_color)); // red
        }

        widgets.status.setText(status((position)));

       

        return view;
    }

    public void getPhoto(String fileName, ImageView imageView) {
        ImageViewWorker imageWorker = new ImageViewWorker(context);
        imageWorker.setImageFadeIn(true);
        imageWorker.loadImage(fileName, imageView, true, 0);

    }

    private String thumbnail(int position) {

        // Get pending report's image
        if (getItem(position).getIncident().getId() == 0) {
            return mListReportModel.getImage(getItem(position).getDbId());

        }

        // Get fetched report's image
        return mListReportModel.getImage(getItem(position).getIncident()
                .getId());

    }

    private String status(int position) {
        return Util.setVerificationStatus(getItem(position).getIncident()
                .getVerified(), context);

    }

    // Implements fitering pattern for the list items.
    @Override
    public Filter getFilter() {
        return new ReportFilter();
    }

    public class ReportFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            results.values = items;
            results.count = items.size();

            if (constraint != null && constraint.toString().length() > 0) {
                constraint = Util.toLowerCase(constraint.toString(), context);
                ArrayList<ReportEntity> filteredItems = new ArrayList<ReportEntity>();
                ArrayList<ReportEntity> itemsHolder = new ArrayList<ReportEntity>();
                itemsHolder.addAll(items);
                for (ReportEntity report : itemsHolder) {
                    if (Util.toLowerCase(report.getIncident().getTitle(),
                            context).contains(constraint)
                            || Util.toLowerCase(
                                    report.getIncident().getLocationName(),
                                    context).contains(constraint)) {
                        filteredItems.add(report);
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            List<ReportEntity> reports = (ArrayList<ReportEntity>) results.values;
            setItems(reports);

        }

    }

}
