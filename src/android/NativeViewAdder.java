package com.outsystems.nativeviewadder;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import $appid.R;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeViewAdder extends CordovaPlugin {

    final String ACTION_ADD_BUTTON = "addButton";
    final String ACTION_CLEAR_VIEWS = "clearViews";

    HashMap<String,View> viewsAdded;
    ConstraintLayout screen;
    CallbackContext callback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        viewsAdded = new HashMap<>();
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup webview = (ViewGroup) webView.getView();
                screen = new

                        ConstraintLayout(cordova.getContext());
                screen.setId(View.generateViewId());
                screen.setLayoutParams(new ConstraintLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                        , RelativeLayout.LayoutParams.MATCH_PARENT));
                webview.addView(screen);
            }
        });

        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;
        switch (action) {
            case ACTION_ADD_BUTTON:
                addButtonToView(new JSONObject(args.getString(0)));
                return true;
            case ACTION_CLEAR_VIEWS:
                clearViews();
                return true;
            default:
        }
        return false;
    }

    private void addButtonToView(JSONObject buttonConfig) throws JSONException {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                String id = generateUUID();



                Button newButton = new Button(cordova.getContext());
                try {
                    newButton.setText(buttonConfig.getString("title"));
                    newButton.setId(View.generateViewId());
                    newButton.setBackgroundColor(Color.parseColor(buttonConfig.optString("backgroundColor",String.format("#%06x", ContextCompat.getColor(cordova.getContext(), R.color.BackgroundColor) & 0xffffff))));

                    newButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            JSONObject response = new JSONObject();
                            PluginResult result;
                            try {
                                response.put("id",id);
                                response.put("action","Click");
                                result = new PluginResult(PluginResult.Status.OK,response);
                                result.setKeepCallback(true);
                                callback.sendPluginResult(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                result = new PluginResult(PluginResult.Status.ERROR,e.getLocalizedMessage());
                                result.setKeepCallback(true);
                                callback.sendPluginResult(result);
                            }
                        }
                    });

                    screen.addView(newButton);

                    ConstraintSet set = new ConstraintSet();
                    int width = buttonConfig.optInt("width",0);
                    if (width == 0 ){
                        set.constrainDefaultWidth(newButton.getId(),ConstraintSet.WRAP_CONTENT);
                    }else{
                        set.constrainDefaultWidth(newButton.getId(),width);
                    }
                    int height = buttonConfig.optInt("height",0);
                    if (height == 0 ){
                        set.constrainDefaultHeight(newButton.getId(),ConstraintSet.WRAP_CONTENT);
                    }else{
                        set.constrainDefaultHeight(newButton.getId(),height);
                    }
                    set.clone(screen);
                    JSONArray constraints = buttonConfig.getJSONArray("Constraints");
                    for (int i = 0; i < constraints.length(); i++) {
                        JSONObject constraint = constraints.getJSONObject(i);

                        double margin = constraint.getDouble("margin");

                        set = addConstraint(newButton.getId(),constraint.getString("direction"),(int)margin,set);
                    }
                    set.applyTo(screen);

                    viewsAdded.put(id,newButton);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private ConstraintSet addConstraint(int viewID, String directionString, int margin, ConstraintSet set){
        int direction = 0;
        switch (directionString){
            case "CenterX":
                set = addConstraint(viewID,"Top",0,set);
                set = addConstraint(viewID,"Bottom",0,set);
                break;
            case "CenterY":
                set = addConstraint(viewID,"Left",0,set);
                set = addConstraint(viewID,"Right",0,set);
                break;
            case "Top":
                direction = ConstraintSet.TOP;
                break;
            case "Bottom":
                direction = ConstraintSet.BOTTOM;
                break;
            case "Left":
                direction = ConstraintSet.START;
                break;
            default:
                direction = ConstraintSet.END;
                break;
        }
        if (direction != 0)
        set.connect(viewID, direction, screen.getId(), direction, margin);
        return set;
    }

    private void clearViews(){
        viewsAdded.clear();
        screen.removeAllViews();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

}

