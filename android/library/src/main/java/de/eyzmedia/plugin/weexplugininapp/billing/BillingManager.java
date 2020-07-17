package de.eyzmedia.plugin.weexplugininapp.billing;

import android.app.Activity;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
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

        void onConsumeFinished(String token, @BillingClient.BillingResponseCode int result);

        void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases);

        void responsdError(BillingResult billingResult);

    }

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener) {
        mActivity = activity;
        mBillingUpdatesListener = updatesListener;

        mBillingClient = BillingClient.newBuilder(mActivity)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        startServiceConnectionIfNeeded(null);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        mBillingUpdatesListener.onPurchasesUpdated(billingResult, purchases);
    }

    /**
     * Trying to restart service connection if it's needed or just execute a request.
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
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                        // Generating Consume Response listener
                        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(BillingResult response, String purchaseToken) {
                                int responseCode = response.getResponseCode();
                                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
                            }
                        };

                        Purchase.PurchasesResult pr = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                        List<Purchase> pList = pr.getPurchasesList();

                        for (Purchase iitem : pList) {
                            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                    .setPurchaseToken(iitem.getPurchaseToken())
                                    .build();


                            mBillingClient.consumeAsync(consumeParams, onConsumeListener);
                        }

                        mBillingUpdatesListener.onBillingClientSetupFinished();

                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    } else {
                        Log.w(TAG, "onBillingSetupFinished() error code: " + billingResult);
                        mBillingUpdatesListener.responsdError(billingResult);
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Log.w(TAG, "onBillingServiceDisconnected()");
                }
            });
        }
    }

    /**
     * One of the most important methods. Queries the play store for the sku abd calls the passed listener.
     *
     * @param itemType inapp or subs
     * @param skuList  list of skus
     * @param listener Listener to callback.
     */
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
                            public void onSkuDetailsResponse(BillingResult response,
                                                             List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(response, skuDetailsList);
                            }
                        });
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }


    /**
     * Starts the purchase flow
     *
     * @param skuDetails skudetails
     */
    public void startPurchaseFlow(final SkuDetails skuDetails) {
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();

                BillingResult billingResult = mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
                int responseCode = billingResult.getResponseCode();

                Log.i("responseCode", String.valueOf(responseCode));
            }
        };

        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public void destroy() {
        mBillingClient.endConnection();
    }

    /**
     * Consume transactional products for re-purchase.
     *
     * @param purchaseToken
     */
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
            public void onConsumeResponse(BillingResult response, String purchaseToken) {
                int responseCode = response.getResponseCode();
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchaseToken)
                                .build();

                mBillingClient.consumeAsync(consumeParams, onConsumeListener);
            }
        };

        startServiceConnectionIfNeeded(consumeRequest);
    }

    public void acknowledgePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.i(TAG, "Purchased acknowledged");
                        }
                    }
                });
            }
        }
    }
}
