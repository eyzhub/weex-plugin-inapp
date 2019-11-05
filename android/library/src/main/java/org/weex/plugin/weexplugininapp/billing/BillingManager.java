package org.weex.plugin.weexplugininapp.billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";

    private final BillingClient mBillingClient;
    private final Activity mActivity;
    private final BillingUpdatesListener mBillingUpdatesListener;
    private Set<String> mTokensToBeConsumed;

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientSetupFinished();

        void onConsumeFinished(String token, @BillingClient.BillingResponse int result);

        void onPurchasesUpdated(List<Purchase> purchases);

        void onPurchasesUpdated(List<Purchase> purchases, int responseCode);
    }

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener) {
        mActivity = activity;
        mBillingUpdatesListener = updatesListener;

        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        startServiceConnectionIfNeeded(null);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        boolean isPurchasesNull = purchases == null;

        if (responseCode == BillingClient.BillingResponse.OK) {
            Log.i(TAG, "Purchase done");
        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            Log.i(TAG, "Purchase already owned");
        }

        Log.i(TAG, String.valueOf(isPurchasesNull));
        Log.i(TAG, "onPurchasesUpdated() response: " + responseCode);


        mBillingUpdatesListener.onPurchasesUpdated(purchases, responseCode);
    }

    /**
     * Trying to restart service connection if it's needed or just execute a request.
     * <p>Note: It's just a primitive example - it's up to you to implement a real retry-policy.</p>
     *
     * @param executeOnSuccess This runnable will be executed once the connection to the Billing
     *                         service is restored.
     */
    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        Log.i(TAG, String.valueOf(mBillingClient.isReady()));

        if (mBillingClient.isReady()) {
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                    if (billingResponse == BillingClient.BillingResponse.OK) {
                        Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                        Log.d("executeOnSuccess", String.valueOf(executeOnSuccess == null));

                        mBillingUpdatesListener.onBillingClientSetupFinished();

                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    } else {
                        Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                    }
                    Log.i(TAG, String.valueOf(mBillingClient.isReady()));
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(TAG, "onBillingServiceDisconnected()");
                }
            });
        }
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(itemType)
                        .build();

                mBillingClient.querySkuDetailsAsync(skuDetailsParams,
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                                             List<SkuDetails> skuDetailsList) {
//                                Log.i(TAG + "!", skuList.toString());
//                                Log.i(TAG + "!", itemType);
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }


    public void startPurchaseFlow(final String skuId, final String billingType) {
        Log.i("billingtype", billingType + " " + skuId);
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setType(billingType)
                        .setSku(skuId)
                        .build();
                int responseCode = mBillingClient.launchBillingFlow(mActivity, billingFlowParams);

                Log.i("responseCode", String.valueOf(responseCode));
            }
        };


//        int responseCode = mBillingClient.launchBillingFlow(this.mActivity, builder.build());
//        Log.i("responseCode", String.valueOf(responseCode));
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public void destroy() {
        mBillingClient.endConnection();
    }

    public void consumeAsync(final String purchaseToken) {
        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        mTokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String purchaseToken) {
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                mBillingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };

        startServiceConnectionIfNeeded(consumeRequest);
    }
}
