package com.liera.lib_xxbring;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.liera.lib_xxbring.bean.XXBringData;
import com.liera.lib_xxbring.callback.XXBringByteArrayCallback;
import com.liera.lib_xxbring.callback.XXBringBaseCallback;
import com.liera.lib_xxbring.callback.XXBringInputStreamCallback;
import com.liera.lib_xxbring.callback.XXBringJsonArrayCallback;
import com.liera.lib_xxbring.callback.XXBringJsonObjectCallback;
import com.liera.lib_xxbring.callback.XXBringTextCallback;
import com.liera.lib_xxbring.handle.RequestManager;
import com.liera.lib_xxbring.handle.impl.OkHttpRequestManager;
import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.response.IXXBringResponse;
import com.liera.lib_xxbring.util.XXBringLog;

public class XXBring {

    private static final String TAG = "XXBring";
    private RequestManager requestManager;
    private boolean isShowJsonData;

    private XXBring(boolean isDebug, RequestManager requestManager, boolean isShowJsonData) {
        XXBringLog.isDebug(isDebug);
        this.requestManager = requestManager;
        this.isShowJsonData = isShowJsonData;
    }

    public static class Builder {

        private boolean isDebug;
        private RequestManager requestManager;
        private boolean isShowJsonData = BuildConfig.DEBUG;

        public Builder setDebug(boolean debug) {
            this.isDebug = debug;
            return this;
        }

        //设置自己的网络请求框架
        public Builder setRequestManager(RequestManager requestManager) {
            this.requestManager = requestManager;
            return this;
        }

        //所有的json请求是否显示响应数据日志
        public Builder isShowJsonData(boolean isShowJsonData) {
            this.isShowJsonData = isShowJsonData;
            return this;
        }

        public XXBring build() {
            return new XXBring(isDebug, requestManager, isShowJsonData);
        }
    }

    /**
     * 回调为inputStream类型的请求
     *
     * @param req      请求体
     * @param callback inputStream回调
     */
    public void request(IXXBringRequest req, XXBringInputStreamCallback callback) {
        requestCallback(req, null, callback);
    }

    /**
     * 回调为byteArray类型的请求
     *
     * @param req      请求体
     * @param callback byteArray回调
     */
    public void request(IXXBringRequest req, XXBringByteArrayCallback callback) {
        requestCallback(req, null, callback);
    }

    /**
     * 回调为text类型的请求
     *
     * @param req      请求体
     * @param callback text回调
     */
    public void request(IXXBringRequest req, XXBringTextCallback callback) {
        requestCallback(req, null, callback);
    }

    /**
     * 回调为jsonObject类型的请求
     *
     * @param req       请求体
     * @param respClass object字节码
     * @param callback  jsonObject回调
     */
    public void request(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringJsonObjectCallback callback) {
        requestCallback(req, respClass, callback);
    }

    /**
     * 回调为jsonArray类型的请求
     *
     * @param req       请求体
     * @param respClass object字节码
     * @param callback  jsonArray回调
     */
    public void request(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringJsonArrayCallback callback) {
        requestCallback(req, respClass, callback);
    }

    private void requestCallback(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringBaseCallback callback) {
        new XXBringData(req, respClass, callback, requestManager != null ? requestManager : OkHttpRequestManager.create(), isShowJsonData).execute();
    }

    public static void cancelRequest(Object...tag) {
        OkHttpRequestManager.cancel(tag);
    }

    public static void cancelAll() {
        cancelRequest(null);
    }

    private static ConnectivityManager mConnectivityManager;

    public static void init(Context context) {
        if (mConnectivityManager == null)
            mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean checkNet() {
        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
