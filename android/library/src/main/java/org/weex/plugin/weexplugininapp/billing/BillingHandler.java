package org.weex.plugin.weexplugininapp.billing;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.taobao.weex.bridge.JSCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingHandler implements BillingProvider {
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

    @Override
    public BillingManager getBillingManager() {
        return this.mBillingManager;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    private class UpdateListener implements BillingManager.BillingUpdatesListener {

        @Override
        public void onBillingClientSetupFinished() {
            Log.d("UpdateListener", "onBillingClientSetupFinished");
            this.query();

        }

        @Override
        public void onConsumeFinished(String token, int result) {
            Log.i(TAG + "!", token);
            Log.i(TAG + "!", String.valueOf(result));
            Log.i(TAG + "!", "consumeasync finished");

        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            Log.d("onPurchasesUpdated", String.valueOf(purchases == null));
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases, int responseCode) {
            boolean success = true;

            response.put("billingResponseCode", responseCode);

            if (responseCode == BillingClient.BillingResponse.OK) {
                response.put("success", true);
                Log.i(TAG + "!", String.valueOf(responseCode));

                for (Purchase purchase : purchases) {
                    Log.i(TAG + "!", purchase.toString());
                    String purchaseToken = purchase.getPurchaseToken();

                    if (productType == BillingClient.SkuType.INAPP) {
                        mBillingManager.consumeAsync(purchaseToken);
                    }

                    response.put("purchase", purchase);
                    response.put("purchaseToken", purchaseToken);
                    response.put("productId", purchase.getSku());
                }
            } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
                response.put("productId", productId);
                success = true;
            }
            else {
                success = false;
                response.put("error", true);
                response.put("productId", productId);
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
                public void onSkuDetailsResponse(int responseCode,
                                                 List<SkuDetails> skuDetailsList) {
                    if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                        Log.i("skuDetailsList", String.valueOf(skuDetailsList.size()));

                        for (SkuDetails details : skuDetailsList) {
                            Log.i(TAG, "Found sku: " + details);

                            inList.add(new SkuRowData(details.getSku(), details.getTitle(),
                                    details.getPrice(), details.getDescription(),
                                    details.getType()));

                            Log.i(TAG, "INITING purchase flow " + productId);

                            mBillingManager.startPurchaseFlow(productId, details.getType());
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
