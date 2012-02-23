
package com.ushahidi.android.app.views;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.database.Database;
import com.ushahidi.android.app.util.ApiUtils;

public class AddMapView {

    private EditText mMapName;

    private EditText mMapDescription;

    private EditText mMapUrl;

    private String mMapId;

    /**
     * Handles views for the add dialog box
     * 
     * @param dialogViews
     */
    public AddMapView(android.view.View dialogViews) {
        mMapName = (EditText)dialogViews.findViewById(R.id.map_name);
        mMapDescription = (EditText)dialogViews.findViewById(R.id.map_description);
        mMapUrl = (EditText)dialogViews.findViewById(R.id.map_url);

        mMapUrl.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (TextUtils.isEmpty(getMapUrl())) {
                    setMapUrl("http://");
                }

                return false;
            }

        });
    }

    // validate the fields
    public void setMapName(String mapName) {
        if (!TextUtils.isEmpty(mapName))
            mMapName.setText(mapName);
    }

    public void setMapId(String mapId) {
        if (!TextUtils.isEmpty(mapId))
            mMapId = mapId;
    }

    public void setMapDescription(String mapDescription) {
        mMapDescription.setText(mapDescription);
    }

    public void setMapUrl(String mapUrl) {
        if (!TextUtils.isEmpty(mapUrl))
            mMapUrl.setText(mapUrl);
    }

    public String getMapId() {
        return mMapId;
    }

    public String getMapName() {
        return mMapName.getText().toString();
    }

    public String getMapDescription() {
        return mMapDescription.getText().toString();
    }

    public String getMapUrl() {
        return mMapUrl.getText().toString();
    }

    /**
     * Add map details to the database
     * 
     * @return boolean
     */
    public boolean addMapDetails() {
        if ((ApiUtils.validateUshahidiInstance(getMapUrl())) && !(TextUtils.isEmpty(getMapName()))) {

            if (!TextUtils.isEmpty(getMapDescription()))
                Database.map.addMap(getMapName(), getMapDescription(), getMapUrl());
            else
                // because map description wasn't set, use the map name as the
                // description
                Database.map.addMap(getMapName(), getMapName(), getMapUrl());
            return true;
        }
        return false;
    }

    /**
     * Update an existing map
     * 
     * @return boolean
     */
    public boolean updateMapDetails() {
        if (!(TextUtils.isEmpty(getMapName()))) {

            if (!TextUtils.isEmpty(getMapDescription()))
                return Database.map.updateMap(getMapId(), getMapName(), getMapDescription(),
                        getMapUrl());
            else
                // because map description wasn't set, use the map name as the
                // description
                return Database.map.updateMap(getMapId(), getMapName(), getMapName(), getMapUrl());
        }
        return false;
    }

}
