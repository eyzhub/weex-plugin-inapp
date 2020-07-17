package de.eyzmedia.plugin.weexinapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.android.billingclient.api.BillingClient;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.eyzmedia.plugin.weexinapp.billing.BillingHandler;

@WeexModule(name = "weexPluginInapp")
public class WeexPluginInappModule extends WXModule {

    private static final String TAG = "WeexPluginInappModule";

    // Define the callback
    private JSCallback jsCallback;
    private Activity thisActivity;

    // Inapp repsonse map
    private Map<String, Object> response = new HashMap();

    //sync ret example
    //TODO: Auto-generated method example
    @JSMethod(uiThread = true)
    public String syncRet(String param) {
        return param;
    }

    //async ret example
    //TODO: Auto-generated method example
    @JSMethod(uiThread = true)
    public void asyncRet(String param, JSCallback callback) {
        callback.invoke(param);
    }


    private String getDataByKey(String params, String key) {
        String data;

        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(params);
            data = (String) json.get(key);
        } catch (Throwable t) {
            data = null;
            Log.e("-> show", "Could not parse malformed JSON: \"" + json + "\"");
        }

        return data;
    }

    private String getDataFromJSONString(String message) {
        StringBuilder data = new StringBuilder();
        JSONObject json = new JSONObject();

        try {
            json = new JSONObject(message);

            Iterator<String> it = json.keys();

            while (it.hasNext()) {
                String key = it.next();
                data.append(key).append(": ").append(json.get(key));
            }
        } catch (Throwable t) {
            data = new StringBuilder();
            Log.e("-> show", "Could not parse malformed JSON: \"" + json + "\"");
        }

        return data.toString();
    }

    @JSMethod()
    public void show(String message) {
        Log.d(TAG, "-> Showing!!!");
        String data = this.getDataFromJSONString(message);
        Toast.makeText(mWXSDKInstance.getContext(), data, Toast.LENGTH_SHORT).show();
    }

    @JSMethod()
    public void buy(String productId, JSCallback jsCallback) {
        Log.d(TAG, "-> buy " + productId);

        this.jsCallback = jsCallback;
        this.response.put("productId", productId);
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());

        Log.d("productId", this.response.toString());

        BillingHandler bh = new BillingHandler(thisActivity, productId, BillingClient.SkuType.INAPP, this.jsCallback);
        bh.startPurchaseFlow();
    }

    @JSMethod()
    public void subscribe(String productId, JSCallback jsCallback) {
        Log.d(TAG, "-> Subscribe " + productId);

        this.jsCallback = jsCallback;
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());

        BillingHandler bh = new BillingHandler(thisActivity, productId, BillingClient.SkuType.SUBS, this.jsCallback);
        bh.startPurchaseFlow();
    }

    @JSMethod(uiThread = true)
    public void manageSubscriptions() {
        Log.d(TAG, "-> manageSubscriptions");

        this.thisActivity = ((Activity) mWXSDKInstance.getContext());
        Uri webpage = Uri.parse("http://play.google.com/store/account/subscriptions");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        this.thisActivity.startActivity(intent);
    }

    @JSMethod(uiThread = true)
    public void getProductInfo(String params, String key, JSCallback jsCallback) {
        Log.d(TAG, "-> getProductInfo");
        String data = this.getDataByKey(params, key);

        if (data == null) {
            jsCallback.invoke(null);
        } else {
            // params.list is an array with all product id, iterate getting information about each product
            // and return all at once in the callback
            // JSONArray list = json.getJSONArray("list");

            Map<String, Object> response = new HashMap();
            response.put("getProductInfo", null);
            jsCallback.invoke(response);
        }
    }
}