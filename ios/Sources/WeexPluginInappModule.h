//  WeexPluginInappModule.h

#import <Foundation/Foundation.h>
#import <WeexSDK/WeexSDK.h>
#import <StoreKit/StoreKit.h>

@interface WeexPluginInappModule : NSObject<WXModuleProtocol>
typedef void (^IAPCallback)(id result);
+ (id)singletonManger;

- (void)show:(NSString *)json;
- (void)info:(NSString *)json :(IAPCallback)callback;
- (void)getProductInfo:(NSString *)json :(IAPCallback)callback;
- (void)getReceipt:(NSString *)json :(IAPCallback)callback;
- (void)acceptStoredPayments:(NSString *)json :(IAPCallback)callback;
- (void)restorePurchases:(NSString *)json :(IAPCallback)callback;
- (void)buy:(NSString *)productId :(IAPCallback)callback;
- (void)subscribe:(NSString *)productId :(IAPCallback)callback;
- (void)manageSubscriptions;

@end
