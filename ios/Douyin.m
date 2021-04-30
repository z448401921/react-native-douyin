#import "Douyin.h"
#import <DouyinOpenSDK/DouyinOpenSDKAuth.h>
#import "DouyinOpenSDKShare.h"
#import<DouyinOpenSDK/DouyinOpenSDKApplicationDelegate.h>


@implementation Douyin

RCT_EXPORT_MODULE(DouYinModule)





RCT_EXPORT_METHOD(auth:(NSString *)scope
                  state:(NSString *)state
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
      
      DouyinOpenSDKAuthRequest *req = [[DouyinOpenSDKAuthRequest alloc] init];
      
      req.permissions = [NSOrderedSet orderedSetWithObject:scope];
     // req.state=state;
      
      UIViewController *vc =  [UIApplication sharedApplication].keyWindow.rootViewController;

     [req sendAuthRequestViewController:vc completeBlock:^(DouyinOpenSDKAuthResponse * _Nonnull resp) {
     if (resp.errCode == 0) {
               resolve(@{
                   @"authCode": resp.code
                       });
            } else{
                [NSString stringWithFormat:@"Author failed code : %@, msg : %@",@(resp.errCode), resp.errString];
                reject([NSString stringWithFormat:@"%@",@(resp.errCode)],resp.errString,nil);
            }
        }];
   
  });
}


RCT_EXPORT_METHOD(init:(NSString *)appid
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
      
     [[DouyinOpenSDKApplicationDelegate sharedInstance] registerAppId:appid];
  });
}

@end
