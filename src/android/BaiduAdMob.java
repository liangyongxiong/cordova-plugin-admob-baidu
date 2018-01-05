package cn.liangyongxiong.cordova.plugin.admob.baidu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.BaiduManager;
import com.baidu.mobads.production.BaiduXAdSDKContext;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BaiduAdMob extends CordovaPlugin {
    private static final int BOTTOM_VIEW_ID = 0x1;
    private RelativeLayout bottomView, contentView;
    private BaiduAdMobBannerFragment gdtBannerFragment;
    private BaiduAdMobInterstitialAdFragment gdtInterstitialAdFragment;
    private InterstitialAd interAd;

    @Override
    protected void pluginInitialize() {
        Activity activity = this.cordova.getActivity();
        Log.i("","pluginInitialize");
        // 百度广告展现前先调用sdk初始化方法，可以有效缩短广告第一次展现所需时间
        BaiduManager.init(activity);
    }

    @Override
    public void onDestroy() {
        Log.i("","onDestroy");
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
                    gdtBannerFragment = BaiduAdMobBannerFragment.newInstance(app, position);
                    gdtBannerFragment.setCallbackContext(callbackContext);
                    ft.replace(BOTTOM_VIEW_ID, gdtBannerFragment);
                    ft.commitAllowingStateLoss();
                }
            });

        } else if (action.equals("hideBannerAd")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (gdtBannerFragment != null) {
                        FragmentManager fm = activity.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(gdtBannerFragment);
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
            final int type = object.optInt("type");

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    gdtInterstitialAdFragment = BaiduAdMobInterstitialAdFragment.newInstance(app, position, type);
                    gdtInterstitialAdFragment.setCallbackContext(callbackContext);
                    ft.add(gdtInterstitialAdFragment, BaiduAdMobInterstitialAdFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });
        } else if (action.equals("hideInterstitialAd")) {

        } else if (action.equals("showSplashAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.optString("app");
            final String position = object.optString("position");
            final int delay = object.optInt("delay");
            JSONObject bottom = object.getJSONObject("bottom");
            final String image = bottom.getString("image");
            final int height = bottom.getInt("height");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    BaiduAdMobSplashAdFragment fragment = BaiduAdMobSplashAdFragment.newInstance(app, position, delay, image, height);
                    fragment.setCallbackContext(callbackContext);
                    ft.add(fragment, BaiduAdMobSplashAdFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });

        } else {
            return false;
        }

        return true;
    }
}

