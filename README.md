# Baidu Admob for MSSP / Cordova Plugin

[![release](https://img.shields.io/badge/release-1.0.8-blue.svg)](https://github.com/liangyongxiong/cordova-plugin-admob-baidu/releases)
[![platforms](https://img.shields.io/badge/platforms-iOS%20%7C%20Android-lightgrey.svg)](https://github.com/liangyongxiong/cordova-plugin-admob-baidu)
[![qq](https://img.shields.io/badge/qq-331338391-blue.svg)](http://wpa.qq.com/msgrd?v=3&uin=331338391&menu=yes)

通过 Javascript 调用百度广告联盟（MSSP）SDK 接口，渲染广告内容

官方 SDK 下载：

+ [Android 通用版](https://baidu-ssp.gz.bcebos.com/mssp/sdk/BaiduMobAds_MSSP_bd_SDK_android_v5.6.zip)

+ [iOS 通用版](https://baidu-ssp.gz.bcebos.com/mssp/sdk/BaiduMobAds_MSSP_bd_SDK_iOS_v4.5.zip)

## NPM
https://www.npmjs.com/package/cordova-plugin-admob-baidu

## Installation

通过 Cordova Plugins 安装

```shell
$ cordova plugin add cordova-plugin-admob-baidu
```

通过 URL 安装

```shell
$ cordova plugin add https://github.com/liangyongxiong/cordova-plugin-admob-baidu.git
```

## Usage

`YOUR_APP_ID` : 应用媒体ID

`YOUR_POSITION_ID` : 广告位ID

#### 横幅广告（Banner）

`align` : 显示位置（top-顶部，bottom-底部）

```javascript
var banner = cordova.BaiduAdMob.BannerAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    align: 'bottom',
}).addEventListener('onSuccess', function(event) {
    console.log('Baidu AdMob banner onSuccess');
    setTimeout(function() {
        banner.hide();
    }, 3000);
}).addEventListener('onError', function(event) {
    console.log('Baidu AdMob banner onError');
}).addEventListener('onClose', function(event) {
    console.log('Baidu AdMob banner onClose');
}).addEventListener('onClick', function(event) {
    console.log('Baidu AdMob banner onClick');
});
```

#### 插屏广告（Interstitial）

`type` : 插屏类型（1-其它，2-视频播放前倒计时，3-视频播放中暂停）

```javascript
var interstitial = cordova.BaiduAdMob.InterstitialAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    type: 3,
}).addEventListener('onSuccess', function(event) {
    console.log('Baidu AdMob interstitial onSuccess');
    setTimeout(function() {
        interstitial.hide();
    }, 5000);
}).addEventListener('onError', function(event) {
    console.log('Baidu AdMob interstitial onError');
}).addEventListener('onClose', function(event) {
    console.log('Baidu AdMob interstitial onClose');
}).addEventListener('onClick', function(event) {
    console.log('Baidu AdMob interstitial onClick');
});
```

#### 开屏广告（Splash）

`bottom` : 底部填充区域，包括图片路径和填充区域高度（单位：dp）

```javascript
var splash = cordova.BaiduAdMob.SplashAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    bottom: {
        image: 'images/bottom.jpg',
        height: 120,
    },
}).addEventListener('onSuccess', function(event) {
    console.log('Baidu AdMob splash onSuccess');
}).addEventListener('onError', function(event) {
    console.log('Baidu AdMob splash onError');
}).addEventListener('onClose', function(event) {
    console.log('Baidu AdMob splash onClose');
}).addEventListener('onClick', function(event) {
    console.log('Baidu AdMob splash onClick');
});
```

## FAQ
Empty

## Support
+ [ＱＱ](http://wpa.qq.com/msgrd?v=3&uin=331338391&menu=yes)
+ [邮箱](mailto:331338391@qq.com)

## Contribute
Please contribute! Look at the [issues](https://github.com/liangyongxiong/cordova-plugin-admob-baidu/issues).

## License
This project is licensed under [MIT](https://github.com/liangyongxiong/cordova-plugin-admob-baidu/blob/master/LICENSE).

