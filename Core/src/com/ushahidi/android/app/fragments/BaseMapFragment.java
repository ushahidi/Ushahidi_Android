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

package com.ushahidi.android.app.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.Util;

/**
 * @author eyedol
 */
public abstract class BaseMapFragment extends SherlockMapFragment {

    protected GoogleMap map;
    protected UpdatableMarker updatableMarker;
    protected static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";

    /**
     * Menu resource id
     */
    protected final int menu;

    /**
     * BaseActivity
     * 
     * @param menu menu resource id
     */
    public BaseMapFragment(int menu) {
        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        // GoogleMapOptions op = new GoogleMapOptions();

        // op.zOrderOnTop(true);

        // newInstance(op);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.menu != 0) {
            inflater.inflate(this.menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        return super.onContextItemSelected(item);
    }

    protected void updateMarker(double latitude, double longitude,
            boolean center) {
        updateMarker(getPoint(latitude, longitude), center);
    }

    /**
     * Convert latitude and longitude to a GeoPoint
     * 
     * @param latitude Latitude
     * @param longitude Lingitude
     * @return GeoPoint
     */
    protected LatLng getPoint(double latitude, double longitude) {
        return (new LatLng(latitude, longitude));
    }

    protected void updateMarker(LatLng point, boolean center) {
        if (map != null) {
            if (updatableMarker == null) {
                CameraUpdate p = CameraUpdateFactory.newLatLng(point);
                map.moveCamera(p);

                updatableMarker = createUpdatableMarker(point);

            } else {
                updatableMarker.update(point);
            }
            if (center) {
                CameraUpdate c = CameraUpdateFactory.newLatLng(point);
                map.moveCamera(c);
            }
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.animateCamera(zoom);
            map.getUiSettings().setZoomControlsEnabled(false);
        }

    }

    /* Override this to set a custom marker */
    protected UpdatableMarker createUpdatableMarker(LatLng point) {
        return new MapMarker(point);
    }

    /* Override this to set a custom marker */
    protected UpdatableMarker createUpdatableMarker() {
        return new MapMarker();
    }

    protected void createMarker(double lat, double lng, String title,
            String snippet) {
        if (map != null) {
            MapMarker marker = new MapMarker();
            marker.addMarker(map, lat, lng, title, snippet);
        }
    }

    protected void createMarker(double lat, double lng, String title,
            String snippet, String fileName) {
        if (map != null) {
            MapMarker marker = new MapMarker();
            marker.addMarkerWithIcon(map, lat, lng, title, snippet, fileName);
        }
    }

    /**
     * Check if Google Maps exist on the device
     * 
     * @return
     */
    protected boolean checkForGMap() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());

        if (status == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            ErrorDialogFragment.newInstance(status).show(
                    getActivity().getSupportFragmentManager(),
                    TAG_ERROR_DIALOG_FRAGMENT);
           
            getActivity().finish();
            return false;
        } else {
            Util.showToast(getActivity(), R.string.no_maps);
            getActivity().finish();
            return false;
        }
    }

    private class MapMarker implements UpdatableMarker {

        public MapMarker(LatLng point) {
            update(point);
        }

        public MapMarker() {
        }

        public LatLng getCenter(double lat, double lng) {
            LatLngBounds bounds = new LatLngBounds.Builder().include(
                    new LatLng(lat, lng)).build();

            return Util.getCenter(bounds);
        }

        public void update(LatLng point) {
            if (point != null) {

                map.addMarker(new MarkerOptions().position(point));
            }
        }

        public void addMarker(GoogleMap map, final double lat, final double lng,
                String title, String snippet) {

            final LatLng latLng = new LatLng(lat, lng);
            markersHolder.add(title);
            map.addMarker(new MarkerOptions().position(latLng).title(title)
                    .snippet(snippet));
        }

        public void addMarkerWithIcon(GoogleMap map, final double lat, final double lng,
                String title, String snippet, String filename) {
            final LatLng latLng = new LatLng(lat, lng);
            // FIX ME: Using the title to find which latlng have been tapped.
            // This ugly hack has to do with the limitation in Google maps api
            // for android
            // SEE:
            // https://code.google.com/p/gmaps-api-issues/issues/detail?id=4650
            markersHolder.add(title);

            if ((filename != null) && (!TextUtils.isEmpty(filename))) {

                map.addMarker(new MarkerOptions().position(latLng)
                        .title(title).snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromPath(filename)));
            } else {
                map.addMarker(new MarkerOptions().position(latLng)
                        .title(title).snippet(snippet));
            }

        }

        public void clearMapMarkers() {
            if (map != null && markersHolder != null) {
                markersHolder.clear();
                map.clear();
            }
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_STATUS = "status";

        static ErrorDialogFragment newInstance(int status) {
            Bundle args = new Bundle();

            args.putInt(ARG_STATUS, status);

            ErrorDialogFragment result = new ErrorDialogFragment();

            result.setArguments(args);

            return (result);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();

            return GooglePlayServicesUtil.getErrorDialog(
                    args.getInt(ARG_STATUS), getActivity(), 0);
        }

        @Override
        public void onDismiss(DialogInterface dlg) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    public abstract interface UpdatableMarker {

        // This is for a workaround regarding this issue
        // https://code.google.com/p/gmaps-api-issues/issues/detail?id=4650
        public List<String> markersHolder = new ArrayList<String>();

        public abstract void update(LatLng point);

        public abstract void addMarker(GoogleMap map, double lat, double lng,
                String title, String snippet);

        public abstract void addMarkerWithIcon(GoogleMap map, double lat,
                double lng, String title, String snippet, String filename);

        public abstract LatLng getCenter(double lat, double lng);
        
        public abstract void clearMapMarkers();
            
        
    }

    protected void log(String message) {
        new Util().log(message);
    }

    protected void log(String format, Object... args) {
        new Util().log(String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        new Util().log(message, ex);
    }

    protected void toastLong(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(getActivity(), getText(message), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastLong(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args),
                Toast.LENGTH_LONG).show();
    }

    protected void toastLong(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_LONG)
                .show();
    }

    protected void toastShort(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(String format, Object... args) {
        Toast.makeText(getActivity(), String.format(format, args),
                Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(getActivity(), getActivity().getString(message),
                Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(CharSequence message) {
        Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT)
                .show();
    }
}
