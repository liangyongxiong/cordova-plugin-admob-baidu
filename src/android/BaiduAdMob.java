package cn.liangyongxiong.cordova.plugin.admob.baidu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.mobads.BaiduManager;
import com.baidu.mobads.CpuInfoManager;
import com.baidu.mobads.production.BaiduXAdSDKContext;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BaiduAdMob extends CordovaPlugin {
    private static final int BOTTOM_VIEW_ID = 0x1;
    private RelativeLayout bottomView, contentView;
    private BaiduAdMobBannerFragment bannerFragment;
    private BaiduAdMobInterstitialFragment interstitialFragment;

    @Override
    protected void pluginInitialize() {
        Activity activity = this.cordova.getActivity();
        // 百度广告展现前先调用sdk初始化方法，可以有效缩短广告第一次展现所需时间
        BaiduManager.init(activity);
    }

    @Override
    public void onDestroy() {
        // 通过BaiduXAdSDKContext.exit()来告知AdSDK，以便AdSDK能够释放资源.
        BaiduXAdSDKContext.exit();
    }

    @Override
    public boolean execute(final String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();
        if (action.equals("showBannerAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final String align = object.optString("align");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    bottomView = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (align.equalsIgnoreCase("top")) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bottomView.setLayoutParams(params);//底部容器
                    bottomView.setId(BOTTOM_VIEW_ID);

                    contentView = new RelativeLayout(activity);
                    contentView.addView(bottomView);
                    activity.addContentView(contentView, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bannerFragment = BaiduAdMobBannerFragment.newInstance(app, position);
                    bannerFragment.setCallbackContext(callbackContext);
                    ft.replace(BOTTOM_VIEW_ID, bannerFragment);
                    ft.commitAllowingStateLoss();
                }
            });

        } else if (action.equals("hideBannerAd")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerFragment != null) {
                        FragmentManager fm = activity.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(bannerFragment);
                        ft.commitAllowingStateLoss();
                    }
                    ViewGroup group = activity.findViewById(android.R.id.content);
                    if (group != null) {
                        group.removeView(contentView);
                    }
                }
            });

        } else if (action.equals("showInterstitialAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final int type = object.getInt("type");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    interstitialFragment = BaiduAdMobInterstitialFragment.newInstance(app, position, type);
                    interstitialFragment.setCallbackContext(callbackContext);
                    ft.add(interstitialFragment, BaiduAdMobInterstitialFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });
        } else if (action.equals("hideInterstitialAd")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (interstitialFragment != null) {
                        interstitialFragment.dismissAllowingStateLoss();
                    }
                }
            });
        } else if (action.equals("showSplashAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            JSONObject bottom = object.getJSONObject("bottom");
            final String image = bottom.getString("image");
            final int height = bottom.getInt("height");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    BaiduAdMobSplashFragment fragment = BaiduAdMobSplashFragment.newInstance(app, position, image, height);
                    fragment.setCallbackContext(callbackContext);
                    ft.add(fragment, BaiduAdMobSplashFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });
        } else if (action.equals("getCpuInfoUrl")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String channel = object.getString("channel");
            CpuInfoManager.getCpuInfoUrl(activity, app, Integer.parseInt(channel), new CpuInfoManager.UrlListener() {
                @Override
                public void onUrl(String url) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject();
                        obj.put("type", "onSuccess");
                        obj.put("url", url);
                    } catch (Exception e) {
                    }

                    PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                    result.setKeepCallback(false);
                    callbackContext.sendPluginResult(result);
                }
            });
        } else {
            return false;
        }

        return true;
    }
}

