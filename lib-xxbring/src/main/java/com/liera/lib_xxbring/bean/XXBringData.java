package com.liera.lib_xxbring.bean;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.liera.lib_xxbring.XXBring;
import com.liera.lib_xxbring.callback.XXBringBaseCallback;
import com.liera.lib_xxbring.callback.XXBringByteArrayCallback;
import com.liera.lib_xxbring.callback.XXBringInputStreamCallback;
import com.liera.lib_xxbring.callback.XXBringJsonArrayCallback;
import com.liera.lib_xxbring.callback.XXBringJsonObjectCallback;
import com.liera.lib_xxbring.callback.XXBringTextCallback;
import com.liera.lib_xxbring.handle.RequestManager;
import com.liera.lib_xxbring.handle.impl.OkHttpRequestManager;
import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.response.IXXBringResponse;
import com.liera.lib_xxbring.util.ErrCode;
import com.liera.lib_xxbring.util.XXBringLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import okhttp3.ResponseBody;

public class XXBringData {

    private static final String TAG = "XXBringData";
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private final boolean isShowJsonData;
    private final IXXBringRequest req;
    private final Class<? extends IXXBringResponse> respClass;
    private final XXBringBaseCallback callback;
    private RequestManager requestManager;

    public XXBringData(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringBaseCallback callback, RequestManager requestManager, boolean isShowJsonData) {
        this.req = req;
        this.respClass = respClass;
        this.callback = callback;
        this.requestManager = requestManager;
        this.isShowJsonData = isShowJsonData;
    }

    public IXXBringRequest getReq() {
        return req;
    }

    public XXBringBaseCallback getCallback() {
        return callback;
    }

    public boolean isShowJsonData() {
        return isShowJsonData;
    }

    public void execute() {
        if (req.getRequestTag() == null) {
            XXBringLog.i(TAG, "请求没有设置tag");
            callback.responseFail(req, ErrCode.REQUEST_EXCEPTION_NOT_INTERNET, new Exception("请求未设置tag"), mHandler);
            return;
        }
        if (!XXBring.checkNet()) {
            XXBringLog.i(TAG, "tag:" + req.getRequestTag() + "设备无网络");
            callback.responseFail(req, ErrCode.REQUEST_EXCEPTION_NOT_INTERNET, new Exception("设备无网络"), mHandler);
            return;
        }
        XXBringLog.i(TAG, "开始任务,tag:" + req.getRequestTag());
        requestManager.execute(this);
    }

    public void responseFail(final int errCode, final Exception e) {
        if (!req.isResponseFailThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    XXBringLog.i(TAG, "响应失败,回调主线程tag:" + req.getRequestTag());
                    callback.responseFail(req, errCode, e, mHandler);
                }
            });
            return;
        }
        XXBringLog.i(TAG, "响应失败,回调子线程tag:" + req.getRequestTag());
        callback.responseFail(req, errCode, e, mHandler);
    }

    public void responseJsonReader(Reader reader, Gson mGson) {
        if (callback instanceof XXBringJsonObjectCallback) {
            final XXBringJsonObjectCallback back = (XXBringJsonObjectCallback) callback;
            try {
                final IXXBringResponse ixxBringResponse = mGson.fromJson(reader, respClass);
                if (!req.isResponseSuccessThread()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            back.jsonResponseObjectSuccess(req, ixxBringResponse, mHandler);
                        }
                    });
                    return;
                }
                back.jsonResponseObjectSuccess(req, ixxBringResponse, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            return;
        }
        if (callback instanceof XXBringJsonArrayCallback) {
            final XXBringJsonArrayCallback back = (XXBringJsonArrayCallback) callback;
            try {
                final ArrayList<? extends IXXBringResponse> m = jsonToList(mGson, reader, respClass);
                if (!req.isResponseSuccessThread()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            back.jsonResponseArraySuccess(req, m, mHandler);
                        }
                    });
                    return;
                }
                back.jsonResponseArraySuccess(req, m, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
        }
    }

    public void responseData(final String string, Gson mGson) {
        if (callback instanceof XXBringJsonObjectCallback) {
            final XXBringJsonObjectCallback back = (XXBringJsonObjectCallback) callback;
            final IXXBringResponse ixxBringResponse = mGson.fromJson(string, respClass);
            if (!req.isResponseSuccessThread()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        back.jsonResponseObjectSuccess(req, ixxBringResponse, mHandler);
                    }
                });
                return;
            }
            back.jsonResponseObjectSuccess(req, ixxBringResponse, mHandler);
            return;
        }
        if (callback instanceof XXBringJsonArrayCallback) {
            final XXBringJsonArrayCallback back = (XXBringJsonArrayCallback) callback;
            try {
                final ArrayList<? extends IXXBringResponse> m = jsonToList(mGson, string, respClass);
                if (!req.isResponseSuccessThread()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            back.jsonResponseArraySuccess(req, m, mHandler);
                        }
                    });
                    return;
                }
                back.jsonResponseArraySuccess(req, m, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            return;
        }
        if (callback instanceof XXBringTextCallback) {
            XXBringLog.i(TAG, "text回调tag:" + req.getRequestTag());
            final XXBringTextCallback back = (XXBringTextCallback) callback;
            if (!req.isResponseSuccessThread()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        back.textResponseSuccess(req, string, mHandler);
                    }
                });
                return;
            }
            back.textResponseSuccess(req, string, mHandler);
            return;
        }
    }

    public void responseByteArray(final byte[] bytes) {
        final XXBringByteArrayCallback back = (XXBringByteArrayCallback) callback;
        if (!req.isResponseSuccessThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    back.byteArrayResponseSuccess(req, bytes, mHandler);
                }
            });
            return;
        }
        back.byteArrayResponseSuccess(req, bytes, mHandler);
    }

    public void responseInputStream(final InputStream inputStream) {
        final XXBringInputStreamCallback back = (XXBringInputStreamCallback) callback;
        try {
            if (!req.isResponseSuccessThread()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        back.inputStreamResponseSuccess(req, inputStream, mHandler);
                    }
                });
                return;
            }
            back.inputStreamResponseSuccess(req, inputStream, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.RESPONSE_EXCEPTION_INPUT_STREAM, e);
            return;
        }
    }

    public void responseOther() {
        callback.responseFail(req, ErrCode.RESPONSE_EXCEPTION_SUCCESS, new Exception("响应回调类型未定义"), mHandler);
    }

    private static <T> ArrayList<T> jsonToList(Gson mGson, String json, Class<T> cls) {
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(mGson.fromJson(elem, cls));
        }
        return list;
    }

    private static <T> ArrayList<T> jsonToList(Gson mGson, Reader reader, Class<T> cls) {
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(reader).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(mGson.fromJson(elem, cls));
        }
        return list;
    }

    @Override
    public String toString() {
        return "XXBringData{" +
                ", req=" + req +
                ", respClass=" + respClass +
                ", callback=" + callback +
                ", requestManager=" + requestManager +
                '}';
    }
}
