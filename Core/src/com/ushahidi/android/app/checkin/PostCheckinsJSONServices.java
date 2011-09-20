package com.ushahidi.android.app.checkin;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: Ahmed
 * Date: 3/3/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostCheckinsJSONServices {
    private JSONObject jsonObject;

    private boolean processingResult;

    public PostCheckinsJSONServices(String JSONString) {
        processingResult = true;

        try {
            jsonObject = new JSONObject(JSONString);
        } catch (JSONException e) {
            processingResult = false;
        }
    }

    private JSONObject getErrorObject() {
        try {
            return jsonObject.getJSONObject("error");
        } catch (JSONException e) {
            
            return null;
        }
    }

    public String getErrorCode() {
        if(processingResult) {
            try {
                return getErrorObject().getString("code");
            } catch (JSONException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public String getErrorMessage() {
        if(processingResult) {
            try {
                return getErrorObject().getString("message");
            } catch (JSONException e) {
                
                return null;
            }
        }
        else {
            
            return null;
        }
    }

    public boolean isProcessingResult() {
        return processingResult;
    }
}
