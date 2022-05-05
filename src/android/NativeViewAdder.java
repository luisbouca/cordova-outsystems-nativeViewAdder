package com.outsystems.nativeviewadder;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.luisbouca.test.R;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class echoes a string called from JavaScript.
 */
public class NativeViewAdder extends CordovaPlugin {

    final String ACTION_ADD_BUTTON = "addButton";

    HashMap<String,View> viewsAdded;
    ConstraintLayout screen;

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
        switch (action) {
            case ACTION_ADD_BUTTON:
                addButtonToView(new JSONObject(args.getString(0)),callbackContext);
                return true;    
            default:
                break;
        }
        return false;
    }

    private void addButtonToView(JSONObject buttonConfig, CallbackContext callbackContext) throws JSONException {
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
                                callbackContext.sendPluginResult(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                result = new PluginResult(PluginResult.Status.ERROR,e.getLocalizedMessage());
                                result.setKeepCallback(true);
                                callbackContext.sendPluginResult(result);
                            }
                        }
                    });

                    screen.addView(newButton);

                    ConstraintSet set = new ConstraintSet();
                    set.clone(screen);
                    set.connect(newButton.getId(), ConstraintSet.TOP, screen.getId(), ConstraintSet.TOP, 60);
                    set.applyTo(screen);

                    viewsAdded.put(id,newButton);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void clearViews(){
        viewsAdded.clear();
        screen.removeAllViews();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
