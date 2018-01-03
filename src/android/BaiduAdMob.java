package cn.liangyongxiong.cordova.plugin.admob.baidu;

import android.app.Activity;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BaiduAdMob extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();

        if (action.equals("showBannerAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");

        } else if (action.equals("hideBannerAd")) {

        } else if (action.equals("showInterstitialAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");

        } else if (action.equals("hideInterstitialAd")) {

        } else if (action.equals("showSplashAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");

        } else {
            return false;
        }

        return true;
    }
}

