package org.weex.plugin.weexplugininapp.billing;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingHandler {
    private static final String TAG = "BillingHandler";
    private BillingManager mBillingManager;
    private UpdateListener mUpdateListener;
    private String productId;
    private String productType;
    private JSCallback jsCallback;
    private Activity mActivity;

    // Our repsonse map
    private Map<String, Object> response = new HashMap();

    public BillingHandler(Activity mActivity, String productId, String productType, JSCallback jsCallback) {
        mUpdateListener = new UpdateListener();
        this.productId = productId;
        this.productType = productType;
        this.jsCallback = jsCallback;
        this.mActivity = mActivity;
    }

    public void startPurchaseFlow() {
        mBillingManager = new BillingManager(this.mActivity, this.getUpdateListener());
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    private class UpdateListener implements BillingManager.BillingUpdatesListener {

        @Override
        public void onBillingClientSetupFinished() {
            Log.d("UpdateListener", "Billing ClientSetup Finished");
            this.query();

        }

        @Override
        public void onConsumeFinished(String token, int result) {
            Log.i(TAG + "!", token);
            Log.i(TAG + "!", String.valueOf(result));
            Log.i(TAG + "!", "consumeasync finished");

        }

        public void responsdError(BillingResult billingResult) {
            response.put("billingResponseCode", billingResult.getResponseCode());
            response.put("success", false);
            response.put("message", billingResult.getDebugMessage());
            jsCallback.invoke(response);
        }

        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            boolean success = false;
            response.put("billingResponseCode", billingResult.getResponseCode());

            if (billingResult == null) {
                success = false;
                response.put("error", true);
                response.put("productId", productId);
            } else {
                int responseCode = billingResult.getResponseCode();

                if (responseCode == BillingClient.BillingResponseCode.OK) {
                    response.put("success", true);

                    for (Purchase purchase : purchases) {
                        Log.i(TAG + "!", purchase.toString());
                        String purchaseToken = purchase.getPurchaseToken();

                        if (productType == BillingClient.SkuType.INAPP) {
                            mBillingManager.consumeAsync(purchaseToken);
                        }

                        mBillingManager.acknowledgePurchase(purchase);

                        response.put("purchase", purchase);
                        response.put("purchaseToken", purchaseToken);
                        response.put("productId", purchase.getSku());
                    }
                    Log.i(TAG + "!", String.valueOf(responseCode));
                } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    response.put("productId", productId);
                    success = true;
                } else {
                    success = false;
                    response.put("error", true);
                    response.put("productId", productId);
                }
            }

            response.put("success", success);

            jsCallback.invoke(response);
        }

        /*
            Call back when product is not found.
         */
        private void respondNoProductsFound() {
            response.put("success", false);
            response.put("error", true);
            response.put("productId", null);

            jsCallback.invoke(response);
        }

        private void query() {
            final List<SkuRowData> inList = new ArrayList<>();
            SkuDetailsResponseListener responseListener = new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                    int responseCode = billingResult.getResponseCode();

                    if (responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        Log.i("skuDetailsList", String.valueOf(skuDetailsList.size()));

                        if (skuDetailsList.size() == 0) {
                            Log.i(TAG, "Product not found" + productId);
                            mUpdateListener.respondNoProductsFound();
                        }

                        for (SkuDetails details : skuDetailsList) {
                            Log.i(TAG, "Found sku: " + details);

                            inList.add(new SkuRowData(details.getSku(), details.getTitle(),
                                    details.getPrice(), details.getDescription(),
                                    details.getType()));

                            Log.i(TAG, "INITING purchase flow " + productId);

                            mBillingManager.startPurchaseFlow(details);
                        }
                    } else {
                        respondNoProductsFound();
                    }
                }
            };

            List<String> inSkus = Arrays.asList(productId);
            mBillingManager.querySkuDetailsAsync(productType, inSkus, responseListener);
        }
    }
}
