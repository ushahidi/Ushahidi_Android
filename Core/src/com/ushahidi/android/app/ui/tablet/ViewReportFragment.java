
package com.ushahidi.android.app.ui.tablet;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItem;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.GeoPoint;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.fragments.BaseFragment;
import com.ushahidi.android.app.models.ListReportModel;
import com.ushahidi.android.app.ui.ImagePreviewer;
import com.ushahidi.android.app.views.ViewReportView;

public class ViewReportFragment extends BaseFragment implements AdapterView.OnItemSelectedListener,
        ViewSwitcher.ViewFactory {

    private ListReportModel reports;

    private List<ListReportModel> report;

    private int position;

    private Bundle photosBundle;

    private String category;

    private ViewReportView view;

    private ViewGroup mRootView;

    protected ViewReportFragment() {
        super(R.menu.view_report);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reports = new ListReportModel();

        photosBundle = new Bundle();
        // load all reports
        Bundle items = getArguments();
        if (items != null) {
            this.category = items.getString("category", "");
            this.position = items.getInt("id", 0);
        }

        if ((category != null) && (!TextUtils.isEmpty(category)))
            reports.loadReportByCategory(getActivity(), category);
        else
            reports.load(getActivity());

        initReport(this.position);
        
    }

    private void previewImage(int position) {
        // FIXME redo this

        photosBundle.putInt("position", position);
        photosBundle.putStringArray("images", view.getThumbnails());
        Intent intent = new Intent(getActivity(), ImagePreviewer.class);
        intent.putExtra("photos", photosBundle);
        startActivityForResult(intent, 0);
        getActivity().setResult(Activity.RESULT_OK, intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.menu_forward) {

            if (report != null) {
                position++;
                if (!(position > (report.size() - 1))) {
                    initReport(position);

                } else {
                    position = report.size() - 1;
                }
            }
            return true;

        } else if (item.getItemId() == R.id.menu_backward) {

            if (report != null) {
                position--;
                if ((position < (report.size() - 1)) && (position != -1)) {
                    initReport(position);
                } else {
                    position = 0;
                }
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void initReport(int position) {
        report = reports.getReports(getActivity());

        if (report != null) {
            if (mRootView != null) {
                view = new ViewReportView(mRootView, getActivity());
                view.setBody(report.get(position).getDesc());
                view.setCategory(report.get(position).getCategories());
                view.setDate(report.get(position).getDate());
                view.setTitle(report.get(position).getTitle());
                view.setStatus(report.get(position).getStatus());
                view.setMedia(report.get(position).getMedia());
                view.mapView.setClickable(true);
                view.mapView.getController().setCenter(
                        getPoint(Double.parseDouble(report.get(position).getLatitude()),
                                Double.parseDouble(report.get(position).getLongitude())));

                view.mapView.setBuiltInZoomControls(true);
                view.getGallery().setOnItemSelectedListener(this);
                view.getGallery().setOnItemClickListener(new OnItemClickListener() {

                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        previewImage(position);
                    }

                });
                int page = position;
                this.setTitle(page + 1);
            }
        }

    }

    public void setTitle(int page) {
        final StringBuilder title = new StringBuilder(String.valueOf(page));
        title.append(" / ");
        /*
         * if (report != null) title.append(report.size());
         * setActionBarTitle(title.toString());
         */
    }

    /*
     * protected void setActionBarTitle(String title) {
     * getSupportActionBar().setTitle(title); }
     */

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public GeoPoint getPoint(double lat, double lon) {
        return (new GeoPoint((int)(lat * 1000000.0), (int)(lon * 1000000.0)));
    }

    @Override
    public View makeView() {

        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup)inflater.inflate(R.layout.view_report, container, false);
        
        return mRootView;
    }
}
