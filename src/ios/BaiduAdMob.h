//
//  BaiduAdMob.h
//
//

#import <Cordova/CDV.h>
#import <Cordova/CDVViewController.h>

#import "BaiduMobAdSDK/BaiduMobAdDelegateProtocol.h"
#import "BaiduMobAdSDK/BaiduMobAdInterstitial.h"
#import "BaiduMobAdSDK/BaiduMobAdSplash.h"
#import "BaiduMobAdSDK/BaiduMobAdView.h"
#import "BaiduMobAdSDK/BaiduMobAdSetting.h"
@interface BaiduAdMob : CDVPlugin<BaiduMobAdViewDelegate,BaiduMobAdInterstitialDelegate,BaiduMobAdSplashDelegate>

@property(nonatomic, strong)CDVInvokedUrlCommand *bannerCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *interstitialCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *splashCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *nativeCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *prerollCommand;

- (void)showBannerAd:(CDVInvokedUrlCommand*)command;
- (void)hideBannerAd:(CDVInvokedUrlCommand*)command;
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)hideInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)showSplashAd:(CDVInvokedUrlCommand*)command;
- (void)getCpuInfoUrl:(CDVInvokedUrlCommand*)command;
@end




