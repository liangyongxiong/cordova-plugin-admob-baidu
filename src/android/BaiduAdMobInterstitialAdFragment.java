package cn.liangyongxiong.cordova.plugin.admob.baidu;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdSize;
import com.baidu.mobads.AdView;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

/**
 * Created by shion on 2017/12/15.
 */

public class BaiduAdMobInterstitialAdFragment extends DialogFragment {
    public static final String APPID = "APPID";//应用id
    public static final String InterteristalPosID = "InterteristalPosID";
    public static final String TYPE = "type";
    private String appId = "";//应用id
    private String interteristalPosID = "";
    private int type;
    private Context mContext;
    private InterstitialAd interAd;
    private int adWidth, adHeight;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams reLayoutParams;

    public static BaiduAdMobInterstitialAdFragment newInstance(String appid, String bannerPosID, int popup) {
        BaiduAdMobInterstitialAdFragment fragment = new BaiduAdMobInterstitialAdFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(InterteristalPosID, bannerPosID);
        bundle.putInt(TYPE, popup);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appId = getArguments().getString(APPID);
        interteristalPosID = getArguments().getString(InterteristalPosID);
        type = getArguments().getInt(TYPE);
        AdView.setAppSid(mContext, appId);
        AdSettings.setSupportHttps(true);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        adWidth = (int) (outMetrics.widthPixels * 3.0f / 4);
        adHeight = (int) (outMetrics.heightPixels * 3.0f / 4);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.7f);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    finish();
                    return false;
                }
                return false;
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout relativeContainer = new RelativeLayout(mContext);
        relativeContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeContainer.setBackgroundResource(android.R.color.transparent);

        relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setBackgroundResource(android.R.color.transparent);
        reLayoutParams = new RelativeLayout.LayoutParams(adWidth, adHeight);
        reLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.setLayoutParams(reLayoutParams);
        relativeContainer.addView(relativeLayout);
        show(interteristalPosID, type);

        return relativeContainer;
    }

    private void show(String id, int type) {
        if (type == 1) {
            load(id, AdSize.InterstitialOther);
        } else if (type == 2) {
            load(id, AdSize.InterstitialForVideoBeforePlay);
        } else if (type == 3) {
            load(id, AdSize.InterstitialForVideoPausePlay);
        }

    }

    private void load(String id, AdSize adSize) {
        String adPlaceId = id; // 重要：请填上您的广告位ID
        interAd = new InterstitialAd(mContext, adSize, adPlaceId);
        interAd.setListener(new InterstitialAdListener() {

            @Override
            public void onAdClick(InterstitialAd arg0) {
                Log.i("ZanTingYe_AD", "onAdClick");
                sendUpdate("onClick", true);
            }

            @Override
            public void onAdDismissed() {
                Log.i("ZanTingYe_AD", "onAdDismissed");
                sendUpdate("onClose", false);
                dismissAllowingStateLoss();
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("ZanTingYe_AD", "onAdFailed");
                sendUpdate("onError", false);
            }

            @Override
            public void onAdReady() {
                Log.i("ZanTingYe_AD", "onAdReady");
                interAd.showAdInParentForVideoApp((Activity) mContext, relativeLayout);
                sendUpdate("onSuccess", true);
            }

            @Override
            public void onAdPresent() {

            }
        });

        reLayoutParams.width = adWidth;
        reLayoutParams.height = adHeight;
        interAd.loadAdForVideoApp(adWidth, adHeight);
    }

    public void finish() {
        if (interAd != null) {
            interAd.destroy();
            sendUpdate("onClose", false);
            dismissAllowingStateLoss();
        }
    }

    public CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void sendUpdate(String content, boolean keepCallback) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", content);
            sendUpdate(obj, keepCallback);
        } catch (Exception e) {
        }
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback) {
        sendUpdate(obj, keepCallback, PluginResult.Status.OK);
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback, PluginResult.Status status) {
        PluginResult result = new PluginResult(status, obj);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

}

