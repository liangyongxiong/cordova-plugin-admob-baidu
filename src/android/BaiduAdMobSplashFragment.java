package cn.liangyongxiong.cordova.plugin.admob.baidu;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

public class BaiduAdMobSplashFragment extends DialogFragment {
    public static final String APPID = "APPID";//应用id
    public static final String SplashPosID = "SplashPosID";
    public static final String IMAGE = "image";
    public static final String HEIGHT = "height";
    private String appId = "";//应用id
    private String splashPosID = "";
    private String image;
    private int height;

    private ViewGroup container;
    private TextView skipView;
    private ImageView bgImageView;
    private SplashAd splashAd;
    private Context mContext;

    private SplashAdListener listener = new SplashAdListener() {
        @Override
        public void onAdDismissed() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("type", "onClose");
                sendUpdate(obj, false);
            } catch (Exception e) {
            }

            jumpWhenCanClick(); // 跳转至您的应用主界面
        }

        @Override
        public void onAdFailed(String reason) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("type", "onError");
                obj.put("msg", reason);
                sendUpdate(obj, false);
            } catch (Exception e) {
            }

            jump();
        }

        @Override
        public void onAdPresent() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("type", "onSuccess");
                sendUpdate(obj, true);
            } catch (Exception e) {
            }
        }

        @Override
        public void onAdClick() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("type", "onClick");
                sendUpdate(obj, true);
            } catch (Exception e) {
            }
        }
    };

    public static BaiduAdMobSplashFragment newInstance(String appid, String bannerPosID, String image, int height) {
        BaiduAdMobSplashFragment fragment = new BaiduAdMobSplashFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(SplashPosID, bannerPosID);
        bundle.putString(IMAGE, image);
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
        image = getArguments().getString(IMAGE);
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

        // 背景图片
        bgImageView = new ImageView(mContext);
        bgImageView.setAdjustViewBounds(true);
        bgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgImageView.setBackgroundColor(Color.WHITE);
        bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(bgImageView);

        // 广告容器+底部填充图
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(linearLayout);

        this.container = new FrameLayout(mContext);
        this.container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        linearLayout.addView(this.container);

        ImageView bottomImageView = new ImageView(mContext);
        bottomImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bottomImageView.setAdjustViewBounds(true);
        bottomImageView.setImageBitmap(getImageFromAssets(image));
        bottomImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, height)));
        linearLayout.addView(bottomImageView);

        // 如果开屏需要支持vr,needRequestVRAd(true)
        splashAd = new SplashAd(mContext, container, listener, splashPosID, true);
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

    private Bitmap getImageFromAssets(String imageName) {
        AssetManager am = mContext.getAssets();
        InputStream is = null;
        try {
            is = am.open("www/" + imageName);//得到图片流
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }

    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        if (container != null) container.clearAnimation();
        System.gc();
        super.onDestroy();
    }

    public CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
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
