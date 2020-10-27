//  WeexPluginInappModule.m

#import "WeexPluginInappModule.h"
#import "RMStore.h"
#import "RMStoreUserDefaultsPersistence.h"
//#import "RMStoreKeychainPersistence.h"
//#import "VerifyStoreReceipt.h"

#import <WeexPluginLoader/WeexPluginLoader.h>
#import <Security/Security.h>
#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>
#import <objc/runtime.h>

#define NILABLE(obj) ((obj) != nil ? (NSObject *)(obj) : (NSObject *)[NSNull null])


@implementation WeexPluginInappModule

WX_PlUGIN_EXPORT_MODULE(weexPluginInapp, WeexPluginInappModule)
WX_EXPORT_METHOD(@selector(show:))
/**
 create actionsheet

 @param json json
 */
-(void) show: (NSString *)json
{
    NSError *jsonError;
    NSData *objectData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *args = [NSJSONSerialization JSONObjectWithData:objectData  options:NSJSONReadingMutableContainers error:&jsonError];
    UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat: @"%@", args[@"title"]] message: args[@"message"] delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:@"dismiss", nil];
    [alertview show];
    // UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:@"title" message:@"module weexPluginInapp is created sucessfully" delegate:self cancelButtonTitle:@"cancel" otherButtonTitles:@"ok", nil];
    // [alertview show];

}
//@end

//
//  NatDeviceInfo.m
//
//  Created by huangyake on 17/1/7.
//  Copyright © 2017 Instapp. All rights reserved.
//

- (NSDictionary*)deviceProperties {
    return @{
             @"vendor": @"Apple",
             @"platform": @"iOS",
             @"os": @"iOS"
             };
}
- (NSDictionary*)JSONConvert: (NSString *)json {
    NSError *jsonError;
    NSData *objectData = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *args = [NSJSONSerialization JSONObjectWithData:objectData  options:NSJSONReadingMutableContainers error:&jsonError];
    return args;
}

WX_EXPORT_METHOD(@selector(info::))
/**
 info
 @param callback IAPCallback
 */
- (void)info:(NSString *)json :(IAPCallback)callback{
    NSDictionary* args = [self JSONConvert :json];
//    NSDictionary* deviceProperties = [self deviceProperties];
    NSLog( @"%@", args );

    callback(args);
}

WX_EXPORT_METHOD(@selector(getProductInfo::))
- (void)getProductInfo:(NSString *)json :(IAPCallback)callback {
    NSLog( @"%@", [self JSONConvert :json] );
    NSDictionary* args = [self JSONConvert :json];
    id productIds = args[@"list"];

    if (![productIds isKindOfClass:[NSArray class]]) {
        NSLog(@"ProductIds must be an array");
        return;
    }

    NSSet *products = [NSSet setWithArray:productIds];
//    DumpObjcMethods([RMStore defaultStore]);
    // DumpObjcMethods(object_getClass([RMStore defaultStore]) /* Metaclass */);

//    NSLog([RMStore canMakePayments] ? @"Yes" : @"No" );
    [[RMStore defaultStore] requestProducts:products success:^(NSArray *products, NSArray *invalidProductIdentifiers) {
        NSLog( @"%@", products );
        NSLog( @"%@", invalidProductIdentifiers );

        NSMutableDictionary *result = [NSMutableDictionary dictionary];
        NSMutableArray *validProducts = [NSMutableArray array];
        for (SKProduct *product in products) {
            NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc] init];
            [numberFormatter setFormatterBehavior:NSNumberFormatterBehavior10_4];
            [numberFormatter setNumberStyle:NSNumberFormatterCurrencyStyle];
            [numberFormatter setLocale:product.priceLocale];
            NSString *currencyCode = [numberFormatter currencyCode];

            [validProducts addObject:@{
                                       @"productId": NILABLE(product.productIdentifier),
                                       @"title": NILABLE(product.localizedTitle),
                                       @"description": NILABLE(product.localizedDescription),
                                       @"priceAsDecimal": NILABLE(product.price),
                                       @"price": NILABLE([RMStore localizedPriceOfProduct:product]),
                                       @"currency": NILABLE(currencyCode)
                                       }];
        }
        [result setObject:validProducts forKey:@"products"];
        [result setObject:invalidProductIdentifiers forKey:@"invalidProductsIds"];
        callback(@{@"result": result});

    } failure:^(NSError *error) {

        NSLog(@"%@", error);
        callback(@{@"error": error});

    }];
//    callback([productIds objectAtIndex:0]);
}

WX_EXPORT_METHOD(@selector(buy::))
- (void)buy:(NSString *)productId :(IAPCallback)callback {
//    NSLog( @"%@", [self JSONConvert :json] );
    NSLog( @"%@", productId );
    if (![productId isKindOfClass:[NSString class]]) {
        NSLog(@"ProductId must be a string");
        return;
    }
    [[RMStore defaultStore] addPayment:productId success:^(SKPaymentTransaction *transaction) {
        NSURL *receiptURL = [[NSBundle mainBundle] appStoreReceiptURL];
        NSData *receiptData = [NSData dataWithContentsOfURL:receiptURL];
        NSString *encReceipt = [receiptData base64EncodedStringWithOptions:0];
        callback(@{@"result": @{
                               @"transactionId": NILABLE(transaction.transactionIdentifier),
                               @"receipt": NILABLE(encReceipt)
                               }});
    } failure:^(SKPaymentTransaction *transaction, NSError *error) {
        callback(@{@"result": @{
                               @"errorCode": NILABLE([NSNumber numberWithInteger:error.code]),
                               @"errorMessage": NILABLE(error.localizedDescription)
                               }});
    }];
}

WX_EXPORT_METHOD(@selector(subscribe::))
- (void)subscribe:(NSString *)productId :(IAPCallback)callback {
	/* alias to */
    //buy(productId, callback);
}

WX_EXPORT_METHOD(@selector(manageSubscriptions))
-(void) manageSubscriptions {
    NSURL *URL = [NSURL URLWithString:@"https://apps.apple.com/account/subscriptions"];

	#if TARGET_OS_IPHONE
    if (@available(iOS 10.0, *)) {
        [[UIApplication sharedApplication] openURL:URL options:@{} completionHandler:nil];
    } else {
        // Fallback on earlier versions
       [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
    }
	#else
	    [[NSWorkspace sharedWorkspace] openURL:URL];
	#endif

}

WX_EXPORT_METHOD(@selector(restorePurchases::))
- (void)restorePurchases:(NSString *)json :(IAPCallback)callback {
    NSLog( @"%@", [self JSONConvert :json] );
    NSDictionary* args = [self JSONConvert :json];

    NSURL *receiptURL = [[NSBundle mainBundle] appStoreReceiptURL];
    NSData *receiptData = [NSData dataWithContentsOfURL:receiptURL];

    NSLog( @"receiptURL %@", receiptURL );
    NSLog( @"receiptData %@", receiptData );

    [[RMStore defaultStore] restoreTransactionsOnSuccess:^(NSArray *transactions){
         NSLog( @"restore %@", transactions );
        NSMutableArray *validTransactions = [NSMutableArray array];
        NSMutableDictionary *result = [NSMutableDictionary dictionary];
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        formatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        formatter.timeZone = [NSTimeZone timeZoneForSecondsFromGMT:0];
        formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ss'Z'";
        for (SKPaymentTransaction *transaction in transactions) {
            NSString *transactionDateString = [formatter stringFromDate:transaction.transactionDate];
            [validTransactions addObject:@{
                                           @"productId": NILABLE(transaction.payment.productIdentifier),
                                           @"date": NILABLE(transactionDateString),
                                           @"transactionId": NILABLE(transaction.transactionIdentifier),
                                           @"transactionState": NILABLE([NSNumber numberWithInteger:transaction.transactionState])
                                           }];
        }
        [result setObject:validTransactions forKey:@"transactions"];
        callback(@{@"result": result});

    } failure:^(NSError *error) {
        callback(@{@"result": @{
                              @"errorCode": NILABLE([NSNumber numberWithInteger:error.code]),
                              @"errorMessage": NILABLE(error.localizedDescription)
                              }});
    }];
}

WX_EXPORT_METHOD(@selector(getReceipt::))
- (void)getReceipt:(NSString *)json :(IAPCallback)callback {
    NSLog( @"%@", [self JSONConvert :json] );
    NSDictionary* args = [self JSONConvert :json];

    [[RMStore defaultStore] refreshReceiptOnSuccess:^{
        NSURL *receiptURL = [[NSBundle mainBundle] appStoreReceiptURL];
        NSData *receiptData = [NSData dataWithContentsOfURL:receiptURL];
        NSString *encReceipt = [receiptData base64EncodedStringWithOptions:0];
        callback(@{@"result": @{@"receipt": NILABLE(encReceipt) }});
    } failure:^(NSError *error) {
         callback(@{@"result": @{@"errorCode": NILABLE([NSNumber numberWithInteger:error.code]),
                                 @"errorMessage": NILABLE(error.localizedDescription)
                                 }});
    }];
}

WX_EXPORT_METHOD(@selector(acceptStoredPayments::))
- (BOOL)acceptStoredPayments:(NSString *)json :(IAPCallback)callback {
    NSLog( @"%@", [self JSONConvert :json] );
    // NSDictionary* args = [self JSONConvert :json];

    [[RMStore defaultStore] acceptStoredStorePayments^{
        callback(@{@"result": @{@"payments": YES }});
    } failure:^(NSError *error) {
         callback(@{@"result": @{@"errorCode": NILABLE([NSNumber numberWithInteger:error.code]),
                                 @"errorMessage": NILABLE(error.localizedDescription)
                                 }});
    }];
}

void DumpObjcMethods(Class clz) {

    unsigned int methodCount = 0;
    Method *methods = class_copyMethodList(clz, &methodCount);

    printf("Found %d methods on '%s'\n", methodCount, class_getName(clz));

    for (unsigned int i = 0; i < methodCount; i++) {
        Method method = methods[i];

        printf("\t'%s' has method named '%s' of encoding '%s'\n",
               class_getName(clz),
               sel_getName(method_getName(method)),
               method_getTypeEncoding(method));

        /**
         *  Or do whatever you need here...
         */
    }

    free(methods);
}

@end

