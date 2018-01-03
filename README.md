## Purpose
通过 Javascript 调用百度广告联盟（MSSP）SDK 接口，渲染广告内容

## NPM
https://www.npmjs.com/package/cordova-plugin-admob-baidu

## Installation

    cordova plugin add cordova-plugin-admob-baidu

## Usage

#### 横幅广告（Banner）

`align` : 显示位置（top-顶部，bottom-底部）

    var banner = cordova.BaiduAdMob.BannerAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        align: 'bottom',
    }).addEventListener('onSuccess',function(event) {
        console.log('Baidu AdMob banner onSuccess');
        setTimeout(function() {
            banner.hide();
        }, 3000);
    }).addEventListener('onError',function(event) {
        console.log('Baidu AdMob banner onError');
    }).addEventListener('onClose',function(event) {
        console.log('Baidu AdMob banner onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Baidu AdMob banner onClick');
    });

#### 插屏广告（Interstitial）

`type` : 插屏类型（1-其它，2-视频播放前倒计时，3-视频播放中暂停）

    var interstitial = cordova.BaiduAdMob.InterstitialAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        type: 3,
    }).addEventListener('onSuccess',function(event) {
        console.log('Baidu AdMob interstitial onSuccess');
        setTimeout(function() {
            interstitial.hide();
        }, 5000);
    }).addEventListener('onError',function(event) {
        console.log('Baidu AdMob interstitial onError');
    }).addEventListener('onClose',function(event) {
        console.log('Baidu AdMob interstitial onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Baidu AdMob interstitial onClick');
    });

#### 开屏广告（Splash）

`bottom` : 底部填充图片，包括图片路径和填充区域高度（单位：dp）

    var splash = cordova.BaiduAdMob.SplashAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        bottom: {
            image: 'images/bottom.jpg',
            height: 120,
        },
    }).addEventListener('onSuccess',function(event) {
        console.log('Baidu AdMob splash onSuccess');
    }).addEventListener('onError',function(event) {
        console.log('Baidu AdMob splash onError');
    }).addEventListener('onClose',function(event) {
        console.log('Baidu AdMob splash onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Baidu AdMob splash onClick');
    });

## Credits
Empty

## Supported Platforms
+ iOS 5.0+
+ Android 2.0+

## License
This project is licensed under Aapache License 2.0. See LICENSE file.
