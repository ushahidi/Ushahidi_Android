
package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListReportModel;

public class ListReportAdapter extends BaseListAdapter<ListReportModel> {

    class Widgets extends com.ushahidi.android.app.views.View {

        public Widgets(View view) {
            super(view);
            this.thumbnail = (ImageView)view.findViewById(R.id.report_thumbnail);
            this.title = (TextView)view.findViewById(R.id.report_title);
            this.date = (TextView)view.findViewById(R.id.report_date);
            this.iLocation = (TextView)view.findViewById(R.id.report_location);
            this.status = (TextView)view.findViewById(R.id.report_status);
            this.arrow = (ImageView)view.findViewById(R.id.report_arrow);
        }

        TextView title;

        TextView iLocation;

        TextView date;

        TextView status;

        TextView mCategories;

        ImageView thumbnail;

        ImageView arrow;
        
    }

    private int[] colors;

    private ListReportModel mListReportModel;
    
    private Context mContext;

    public ListReportAdapter(Context context) {
        super(context);
        mContext = context;
        colors = new int[] {
                R.color.table_odd_row_color, R.color.table_even_row_color
        };
    }

    @Override
    public void refresh(Context context) {
        mListReportModel = new ListReportModel();
        final boolean loaded = mListReportModel.load(context);
        if( loaded) {
            this.setItems(mListReportModel.getReports(context));
        }
        
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        
        int colorPosition = position % colors.length;
        View row = inflater.inflate(R.layout.list_report_item, viewGroup, false);
        row.setBackgroundResource(colors[colorPosition]);
        
        Widgets widgets = (Widgets)row.getTag();
        
        if (widgets == null) {
            widgets = new Widgets(row);
            row.setTag(widgets);
        }
        
        widgets.thumbnail.setImageDrawable(getItem(position).getThumbnail());
        widgets.title.setText(getItem(position).getTitle());
        widgets.date.setText(getItem(position).getDate());
        widgets.iLocation.setText(getItem(position).getLocation());
        widgets.status.setText(getItem(position).getStatus());
        // change the status color
        
        if (getItem(position).getStatus().equalsIgnoreCase(mContext.getString(R.string.report_verified))) {
            widgets.status.setTextColor(R.color.verified_text_color); // green
        }
        else {
            widgets.status.setTextColor(R.color.unverified_text_color); // red
        }
        
        widgets.arrow.setImageDrawable(getItem(position).getArrow());
        
        return row;
    }
}
