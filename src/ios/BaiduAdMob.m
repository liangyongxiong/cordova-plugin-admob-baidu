//
//  BaiduAdMob.m
//

#import "BaiduAdMob.h"
#import "BaiduMobAdSDK/BaiduMobCpuInfoManager.h"


#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height
#define IS_IPHONEX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)

@implementation BaiduAdMob
{
    NSString *appkey;
    BaiduMobAdView* sharedAdView;//横幅
    BaiduMobAdInterstitial *interstitialAdView;//插屏
    NSString *interstitialAdType;
    UIView *interstitialAdCustomAdView;
    BaiduMobAdSplash *splash;//开屏
    UIView *splashCustomView;
    UIView *skipView;
    UIButton *skipBtn;
    NSTimer *skipTimer;
    NSInteger skipTime;
}

#pragma mark - Banner
- (void)showBannerAd:(CDVInvokedUrlCommand*)command
{
    self.bannerCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];
         //使用嵌入广告的方法实例。
        sharedAdView = [[BaiduMobAdView alloc] init];
        sharedAdView.AdUnitTag = posId;
        sharedAdView.AdType = BaiduMobAdViewTypeBanner;
        if ([dic objectForKey:@"align"] && [@"top" isEqualToString:[dic objectForKey:@"align"]]) {
             sharedAdView.frame = CGRectMake(0, 20, ScreenWidth, 50);
        } else {
             sharedAdView.frame = CGRectMake(0, ScreenHeight-50, ScreenWidth, 50);
        }
        [[[UIApplication sharedApplication] keyWindow] addSubview:sharedAdView];
        sharedAdView.delegate = self;
        [sharedAdView start];
    }
}
#pragma mark BaiduMobAdViewDelegate
//appid
- (NSString *)publisherId
{
    return appkey;//appkey
}
- (void)willDisplayAd:(BaiduMobAdView *)adview
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)failedDisplayAd:(BaiduMobFailReason)reason
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)didAdClicked
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
//点击关闭的时候移除广告
- (void)didAdClose
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}

- (void)hideBannerAd:(CDVInvokedUrlCommand*)command
{
    [sharedAdView removeFromSuperview];
    sharedAdView.delegate = nil;
    sharedAdView = nil;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}

#pragma mark - Interstitial 插屏广告
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command
{
     self.interstitialCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];
        interstitialAdType = [NSString stringWithFormat:@"%@",[dic objectForKey:@"type"]];
        interstitialAdView = [[BaiduMobAdInterstitial alloc]init];
        interstitialAdView.AdUnitTag = posId;
        interstitialAdView.delegate = self;

        if ([@"1" isEqualToString:interstitialAdType]) {
            interstitialAdView.interstitialType = BaiduMobAdViewTypeInterstitialOther;
            [interstitialAdView load];
        } else if ([@"2" isEqualToString:interstitialAdType]){
            interstitialAdView.interstitialType = BaiduMobAdViewTypeInterstitialBeforeVideo;
            [self setUpInterstitialAdView];
        } else if ([@"3" isEqualToString:interstitialAdType]){
            interstitialAdView.interstitialType = BaiduMobAdViewTypeInterstitialPauseVideo;
            [self setUpInterstitialAdView];
        }
    }
}
- (void)hideInterstitialAd:(CDVInvokedUrlCommand*)command
{
    [interstitialAdCustomAdView removeFromSuperview];
    interstitialAdView.delegate = nil;
    interstitialAdView = nil;
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)setUpInterstitialAdView{
    CGFloat viewWidth = ScreenWidth*0.8;
    CGFloat viewHeight = ScreenHeight*0.8;
    [interstitialAdView loadUsingSize:CGRectMake((ScreenWidth-viewWidth)*0.5, (ScreenHeight-viewHeight)*0.5, viewWidth, viewHeight)];
}
#pragma mark BaiduMobAdInterstitialDelegate
//广告预加载成功
- (void)interstitialSuccessToLoadAd:(BaiduMobAdInterstitial *)interstitial
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];

    if (interstitialAdView.isReady){
        if ([@"1" isEqualToString:interstitialAdType]) {
            [interstitialAdView presentFromRootViewController:[[UIApplication sharedApplication] keyWindow].rootViewController];
        } else {
            CGFloat viewWidth = ScreenWidth*0.8;
            CGFloat viewHeight = ScreenHeight*0.8;
            UIView *customAdView = [[UIView alloc]initWithFrame:CGRectMake((ScreenWidth-viewWidth)*0.5, (ScreenHeight-viewHeight)*0.5, viewWidth, viewHeight)] ;
            customAdView.backgroundColor = [UIColor clearColor];
            [[[UIApplication sharedApplication] keyWindow] addSubview:customAdView];
            [interstitialAdView presentFromView:customAdView];
            interstitialAdCustomAdView = customAdView;
        }
    }
}
- (void)interstitialFailToLoadAd:(BaiduMobAdInterstitial *)interstitial
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialDidAdClicked:(BaiduMobAdInterstitial *)interstitial
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialDidDismissScreen:(BaiduMobAdInterstitial *)interstitial
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
#pragma mark - Splash
- (void)showSplashAd:(CDVInvokedUrlCommand*)command
{
    self.splashCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];

        [BaiduMobAdSetting setMaxVideoCacheCapacityMb:30];
        splash = [[BaiduMobAdSplash alloc] init];
        splash.delegate = self;
        splash.AdUnitTag = posId;
        splash.canSplashClick = YES;

        splashCustomView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, ScreenWidth, ScreenHeight)];
        splashCustomView.backgroundColor = [UIColor whiteColor];
        [[[UIApplication sharedApplication] keyWindow] addSubview:splashCustomView];

        //在baiduSplashContainer用做上展现百度广告的容器，注意尺寸必须大于200*200，并且baiduSplashContainer需要全部在window内，同时开机画面不建议旋转
        CGFloat height = [[[dic objectForKey:@"bottom"] objectForKey:@"height"] floatValue];
        UIView * baiduSplashContainer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, ScreenWidth,ScreenHeight-height)];
        [splashCustomView addSubview:baiduSplashContainer];

        skipView = [[UIView alloc]initWithFrame:CGRectMake(ScreenWidth-90, 35, 75, 35)];
        skipView.backgroundColor = [UIColor colorWithRed:114/255.0f green:114/255.0f blue:114/255.0f alpha:0.7];
        skipView.layer.cornerRadius = 35*0.5;
        skipView.layer.masksToBounds = YES;
        [splashCustomView addSubview:skipView];

        skipBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        skipBtn.backgroundColor =  [UIColor clearColor];
        [skipBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        skipBtn.frame = skipView.frame;
        skipBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [skipBtn addTarget:self action:@selector(removeSplash) forControlEvents:UIControlEventTouchUpInside];
        skipTime = 5;
       // [skipBtn setTitle:[NSString stringWithFormat:@"跳过 %@",@(skipTime)] forState:UIControlStateNormal];
        [skipBtn setTitle:[NSString stringWithFormat:@"跳过"] forState:UIControlStateNormal];
        [splashCustomView addSubview:skipBtn];

        skipView.hidden = YES;
        skipBtn.hidden = YES;

        //skipTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(onTimer) userInfo:nil repeats:YES];

        UIImage *img = [self createImgWithPath:[[dic objectForKey:@"bottom"] objectForKey:@"image"]];
        UIImageView *logo = [[UIImageView alloc] initWithImage:img];
        logo.contentMode = UIViewContentModeScaleAspectFill;
        logo.frame = CGRectMake(0, ScreenHeight-height, ScreenWidth,height);
        [splashCustomView addSubview:logo];

        //在的baiduSplashContainer里展现百度广告
        [splash loadAndDisplayUsingContainerView:baiduSplashContainer];
    }
}
- (void)onTimer
{
    if (skipTime == 0) {
        [skipTimer invalidate];
        skipTimer = nil;
    } else{
        [skipBtn setTitle:[NSString stringWithFormat:@"跳过 %@ 秒",@(skipTime--)] forState:UIControlStateNormal];
    }
}

#pragma mark  BaiduMobAdSplashDelegate
- (void)splashSuccessPresentScreen:(BaiduMobAdSplash *)splash
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];

    skipView.hidden = NO;
    skipBtn.hidden = NO;
}
- (void)splashlFailPresentScreen:(BaiduMobAdSplash *)splash withError:(BaiduMobFailReason) reason
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];

     [self removeSplash];
}
- (void)splashDidClicked:(BaiduMobAdSplash *)splash
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];
}
- (void)splashDidDismissScreen:(BaiduMobAdSplash *)splash
{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];

    [self removeSplash];
}
- (void)removeSplash
{
    [skipTimer invalidate];
    skipTimer = nil;
    if (splash) {
        [splashCustomView removeFromSuperview];
        splash.delegate = nil;
        splash = nil;
    }
}

- (void)getCpuInfoUrl:(CDVInvokedUrlCommand*)command
{
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        NSString *app = [dic objectForKey:@"app"];
        NSString *channel = [dic objectForKey:@"channel"];
        NSString *urlStr = [[BaiduMobCpuInfoManager shared] getCpuInfoUrlWithChannelId:channel appId:app];

        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"type":@"onSuccess",@"url":urlStr}];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

#pragma mark - json解析
- (NSDictionary *)returnDicWithJsonStr:(CDVInvokedUrlCommand*)command
{
    NSArray* options = command.arguments;
    NSString *jsonStr = [options firstObject];
    if (jsonStr.length>0) {
        NSData *jsonData = [jsonStr dataUsingEncoding:NSUTF8StringEncoding];
        NSError *err;
        NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&err];
        if(!err) {
            return dic;
        } else {
            NSLog(@"======解析失败====%@",err);
            return nil;
        }
    } else {
        NSLog(@"======字符串为空====");
        return nil;
    }
}
//生成img
- (UIImage*)createImgWithPath:(NSString*)altPath
{
    UIImage* result = nil;
    NSString* path = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:[NSString pathWithComponents:@[@"www", altPath]]];
    NSData* data = [NSData dataWithContentsOfFile:path];
    result = [UIImage imageWithData:data];
    return result;
}
@end


