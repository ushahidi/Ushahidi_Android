
package com.ushahidi.android.app.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.entities.Map;
import com.ushahidi.android.app.models.ListMapModel;
import com.ushahidi.android.app.util.ApiUtils;

public class AddMapView {

    private EditText mMapName;

    private EditText mMapDescription;

    private EditText mMapUrl;

    private int mMapId;

    private ListMapModel mapModel;

    /**
     * Handles views for the add dialog box
     * 
     * @param dialogViews
     */
    public AddMapView(android.view.View dialogViews) {
        mapModel = new ListMapModel();
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

    public void setMapId(int mapId) {

        mMapId = mapId;
    }

    public void setMapDescription(String mapDescription) {
        mMapDescription.setText(mapDescription);
    }

    public void setMapUrl(String mapUrl) {
        if (!TextUtils.isEmpty(mapUrl))
            mMapUrl.setText(mapUrl);
    }

    public int getMapId() {
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
        	
            Map map = new Map();
            map.setMapId(0);
            map.setCatId(0);
            map.setActive("0");
            map.setLat("0.0");
            map.setLon("0.0");
            map.setName(getMapName());
            map.setDesc(getMapDescription());
            map.setUrl(getMapUrl());
            map.setDate((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));

            if (TextUtils.isEmpty(getMapDescription())) {
                map.setDesc(getMapName());
            }

            List<Map> maps = new ArrayList<Map>();
            maps.add(map);
            return mapModel.addMap(maps);

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
                return mapModel.updateMap(getMapId(), getMapName(), getMapDescription(),
                        getMapUrl());
            else
                // because map description wasn't set, use the map name as the
                // description
                return mapModel.updateMap(getMapId(), getMapName(), getMapName(), getMapUrl());
        }
        return false;
    }

}
