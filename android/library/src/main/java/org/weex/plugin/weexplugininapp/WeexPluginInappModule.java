package org.weex.plugin.weexplugininapp;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import org.weex.plugin.weexplugininapp.billing.BillingHandler;

import java.util.HashMap;
import java.util.Map;

@WeexModule(name = "weexPluginInapp")
public class WeexPluginInappModule extends WXModule {

    private static final String TAG = "WeexPluginInappModule";

    // Define the callback
    JSCallback jsCallback;
    Activity thisActivity;
    String productId;

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

    @JSMethod(uiThread = true)
    public void show() {
        Log.d(TAG, "Showing!!!");
        Toast.makeText(mWXSDKInstance.getContext(), "Module weexPluginInapp is created sucessfully ", Toast.LENGTH_SHORT).show();
    }

    @JSMethod(uiThread = true)
    public void testSuccess(String productId, JSCallback jsCallback) {
        Log.d(TAG, "Testing success");

        Toast.makeText(mWXSDKInstance.getContext(),
                "Testing case: product purchase success ", Toast.LENGTH_SHORT
        ).show();

        this.jsCallback = jsCallback;
        this.response.put("productId", productId);
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());
        this.productId = productId;

        Log.d("productId", this.response.toString());

        this.doPurchase();

//        this.jsCallback.invoke(this.response);
    }

    @JSMethod(uiThread = true)
    public void testFailure(String productId, JSCallback jsCallback) {
        Log.d(TAG, "Testing failure");
        Toast.makeText(mWXSDKInstance.getContext(),
                "Testing case: product purchase failure ", Toast.LENGTH_SHORT
        ).show();

        this.jsCallback = jsCallback;
        this.response.put("productId", productId);
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());
        this.productId = productId;

        Log.d("productId", this.response.toString());

        this.doPurchase();

//        this.jsCallback.invoke(this.response);
    }

    @JSMethod(uiThread = true)
    public void testUnavailable(String productId, JSCallback jsCallback) {
        Log.d(TAG, "Testing unavailable!!!");
        Toast.makeText(mWXSDKInstance.getContext(),
                "Testing case: product purchase unavailable ", Toast.LENGTH_SHORT
        ).show();

        this.jsCallback = jsCallback;
        this.response.put("productId", productId);
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());
        this.productId = productId;

        Log.d("productId", this.response.toString());

        this.doPurchase();
    }

    @JSMethod(uiThread = true)
    public void purchase(String productId, JSCallback jsCallback) {
        Log.d(TAG, "purchasing " + productId);

        this.jsCallback = jsCallback;
        this.response.put("productId", productId);
        this.thisActivity = ((Activity) mWXSDKInstance.getContext());
        this.productId = productId;

        this.doPurchase();
    }

    private void doPurchase() {
        BillingHandler bh = new BillingHandler(thisActivity, productId, this.jsCallback);
        bh.init();
    }
}