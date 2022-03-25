package com.outsystems.nativeviewadder;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeViewAdder extends CordovaPlugin {

    final String ACTION_ADD_BUTTON = "addButton";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case ACTION_ADD_BUTTON:
                addButtonToView(args.getString(0),args.getString(1),callbackContext);
                return true;    
            default:
                break;
        }
        return false;
    }
}
