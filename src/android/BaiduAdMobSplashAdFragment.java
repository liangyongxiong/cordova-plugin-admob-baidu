package cn.liangyongxiong.cordova.plugin.admob.baidu;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shion on 2017/12/15.
 */

public class BaiduAdMobSplashAdFragment extends DialogFragment {
    public static final String APPID = "APPID";//应用id
    public static final String SplashPosID = "SplashPosID";
    public static final String DELAY = "delay";
    public static final String BOTTOM = "bottom";
    public static final String HEIGHT = "height";
    private String appId = "";//应用id
    private String splashPosID = "";
    private int delay;
    private String bottom;
    private int height;

    private ViewGroup container;
    private TextView skipView;
    private ImageView bgImageView;
    private static final String SKIP_TEXT = " 跳过 %d 秒";
    private Context mContext;

    private SplashAdListener listener = new SplashAdListener() {
        @Override
        public void onAdDismissed() {
            Log.i("RSplashActivity", "onAdDismissed");
            sendUpdate("onClose", false);
            jumpWhenCanClick(); // 跳转至您的应用主界面
        }

        @Override
        public void onAdFailed(String arg0) {
            Log.i("RSplashActivity", "onAdFailed");
            sendUpdate("onError", false);
            jump();
        }

        @Override
        public void onAdPresent() {
            Log.i("RSplashActivity", "onAdPresent");
            sendUpdate("onSuccess", true);
        }

        @Override
        public void onAdClick() {
            Log.i("RSplashActivity", "onAdClick");
            sendUpdate("onClick", true);
        }
    };


    public static BaiduAdMobSplashAdFragment newInstance(String appid, String bannerPosID, int delay,
                                                         String bottom, int height
    ) {
        BaiduAdMobSplashAdFragment fragment = new BaiduAdMobSplashAdFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(SplashPosID, bannerPosID);
        bundle.putInt(DELAY, delay);
        bundle.putString(BOTTOM, bottom);
        bundle.putInt(HEIGHT, height);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appId = getArguments().getString(APPID);
        splashPosID = getArguments().getString(SplashPosID);
        delay = getArguments().getInt(DELAY);
        bottom = getArguments().getString(BOTTOM);
        height = getArguments().getInt(HEIGHT);
        AdView.setAppSid(mContext, appId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                }
                return false;
            }
        });


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //1.背景图片
        bgImageView = new ImageView(mContext);
        bgImageView.setAdjustViewBounds(true);
        bgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgImageView.setBackgroundColor(Color.WHITE);
        bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(bgImageView);
        //2.广告容器+底部的Logo
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(linearLayout);

        this.container = new FrameLayout(mContext);
        this.container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        linearLayout.addView(this.container);

        ImageView logoImageView = new ImageView(mContext);
        logoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        logoImageView.setAdjustViewBounds(true);
        logoImageView.setImageBitmap(getImageFromAssets(bottom));
        logoImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, height)));
        linearLayout.addView(logoImageView);

        String adPlaceId = splashPosID; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        // 如果开屏需要支持vr,needRequestVRAd(true)
//        SplashAd.needRequestVRAd(true);
        new SplashAd(mContext, container, listener, adPlaceId, true);


        //第三部分 计时器
//        skipView = new TextView(mContext);
//        RelativeLayout.LayoutParams skipLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        skipLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        skipLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        skipLayoutParams.topMargin = dp2px(mContext, 16);
//        skipLayoutParams.rightMargin = dp2px(mContext, 16);
//        skipView.setLayoutParams(skipLayoutParams);
//        skipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//        skipView.setTextColor(Color.WHITE);
//        skipView.setGravity(Gravity.CENTER);
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setColor(Color.parseColor("#80000000"));
//        drawable.setCornerRadius(dp2px(mContext, 45));
//        drawable.setStroke(dp2px(mContext, 1), Color.WHITE);
//        skipView.setBackground(drawable);
//        skipView.setPadding(dp2px(mContext, 9), dp2px(mContext, 5), dp2px(mContext, 9), dp2px(mContext, 5));
//        skipView.setVisibility(View.INVISIBLE);
//        relativeLayout.addView(skipView);
        return relativeLayout;
    }


    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            this.dismissAllowingStateLoss();
        } else {
            canJumpImmediately = true;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        this.dismissAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }


    public Bitmap getImageFromAssets(String imageName) {
        AssetManager am = mContext.getAssets();
        InputStream is = null;
        try {
            is = am.open("www/" + imageName);//得到图片流
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }

    public int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        if (container != null) container.clearAnimation();
        System.gc();
        super.onDestroy();
    }

    public CallbackContext scallbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.scallbackContext = callbackContext;
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
        scallbackContext.sendPluginResult(result);
    }

}
