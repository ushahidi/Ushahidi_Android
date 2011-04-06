
package com.ushahidi.android.app.checkin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ushahidi.android.app.data.UsersData;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: Ahmed Date: 2/17/11 Time: 5:45 PM To change
 * this template use File | Settings | File Templates.
 */
public class RetrieveCheckinsJSONServices {
    private JSONObject jsonObject;

    private boolean processingResult;

    public RetrieveCheckinsJSONServices(String JSONString) {
        processingResult = true;

        try {
            jsonObject = new JSONObject(JSONString);
        } catch (JSONException e) {
            processingResult = false;
        }
    }

    private JSONObject getCheckinsObject() {
        try {
            return jsonObject.getJSONObject("payload");
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONArray getCheckinsArray() {
        try {
            return getCheckinsObject().getJSONArray("checkins");
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONArray getCheckinsUsersArray() {
        try {
            return getCheckinsObject().getJSONArray("users");
        } catch (JSONException e) {
            return null;
        }
    }

    public ArrayList<UsersData> getCheckinsUsersList() {
        if (processingResult) {
            ArrayList<UsersData> checkinsUsersList = new ArrayList<UsersData>();
            JSONArray checkinsUsersArray = getCheckinsUsersArray();

            for (int index = 0; index < checkinsUsersArray.length(); index++) {
                UsersData users = new UsersData();

                try {
                    users.setId(Integer.valueOf(checkinsUsersArray.getJSONObject(index).getString(
                            "id")));
                    users.setUserName(checkinsUsersArray.getJSONObject(index).getString(
                            "name"));
                    users.setColor(checkinsUsersArray.getJSONObject(index).getString(
                    "color"));
                } catch (JSONException e) {

                    processingResult = false;
                    return null;
                }
                
                checkinsUsersList.add(users);
            }
            
            return checkinsUsersList;
        }
        return null;
    }

    public ArrayList<Checkin> getCheckinsList() {
        if (processingResult) {
            ArrayList<Checkin> checkinsList = new ArrayList<Checkin>();
            JSONArray checkinsArray = getCheckinsArray();
            int arraySize = checkinsArray.length();

            for (int checkinsLoop = 0; checkinsLoop < arraySize; checkinsLoop++) {
                Checkin currentCheckin = new Checkin();

                try {
                    currentCheckin.setId(checkinsArray.getJSONObject(checkinsLoop).getString("id"));
                    currentCheckin.setLoc(checkinsArray.getJSONObject(checkinsLoop)
                            .getString("loc"));
                    currentCheckin.setLat(checkinsArray.getJSONObject(checkinsLoop)
                            .getString("lat"));
                    currentCheckin.setLon(checkinsArray.getJSONObject(checkinsLoop)
                            .getString("lon"));
                    currentCheckin.setDate(checkinsArray.getJSONObject(checkinsLoop).getString(
                            "date"));
                    currentCheckin.setMsg(checkinsArray.getJSONObject(checkinsLoop)
                            .getString("msg"));
                    currentCheckin.setUser(checkinsArray.getJSONObject(checkinsLoop).getString(
                            "user"));
                    // currentCheckin.setImage(checkinsArray.getJSONObject(checkinsLoop).getString(
                    // "media"));
                } catch (JSONException e) {

                    processingResult = false;
                    return null;
                }

                checkinsList.add(currentCheckin);
            }

            return checkinsList;
        }

        return null;
    }

    public boolean isProcessingResult() {
        return processingResult;
    }
}
