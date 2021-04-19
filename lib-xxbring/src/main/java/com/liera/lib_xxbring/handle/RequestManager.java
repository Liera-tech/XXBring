package com.liera.lib_xxbring.handle;

import com.google.gson.Gson;
import com.liera.lib_xxbring.bean.XXBringData;
import com.liera.lib_xxbring.enums.HttpMethod;
import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.util.ErrCode;
import com.liera.lib_xxbring.util.XXBringLog;
import java.io.InputStream;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.Map;

public abstract class RequestManager {

    private static final String TAG = "RequestManager";

    protected final static Gson mGson = new Gson();
    protected XXBringData mXXBringBean;

    public void execute(XXBringData bringBean) {
        this.mXXBringBean = bringBean;

        if (runIsThread()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    request();
                }
            }).start();
            return;
        }
        request();
    }

    private void request() {
        HttpMethod method = mXXBringBean.getReq().getMethod();

        if (method == HttpMethod.GET) {
            getHandle();
            return;
        }
        if (method == HttpMethod.POST) {
            postHandle();
            return;
        }
        responseFail(ErrCode.REQUEST_EXCEPTION_NOT_IMPLEMENTED, new Exception("请求方式未实现"));
    }

    protected String getUrl(String url, String httpMethod) throws Exception {
        final IXXBringRequest request = mXXBringBean.getReq();
        Map<String, Object> params = request.getParameters();
        StringBuilder stringBuilder = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            if (httpMethod.toUpperCase().equals("GET"))
                stringBuilder.append("?");
            for (String key : params.keySet()) {
                Object value = params.get(key);
                XXBringLog.i(TAG, "%s请求->请求参数:%s:%s", httpMethod, key, value);
                stringBuilder
                        .append(key)
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(value), "UTF-8"))
                        .append("&");
            }
        }
        String toString = stringBuilder.substring(0, stringBuilder.length() - 1);
        XXBringLog.i(TAG, "%s请求--完整请求地址:%s", httpMethod, toString);
        return toString;
    }

    protected void responseFail(final int errCode, final Exception e) {
        mXXBringBean.responseFail(errCode, e);
    }

    protected void responseJsonReader(final Reader reader) {
        mXXBringBean.responseJsonReader(reader, mGson);
    }

    protected void responseData(final String string) {
        mXXBringBean.responseData(string, mGson);
    }

    protected void responseByteArray(final byte[] bytes) {
        mXXBringBean.responseByteArray(bytes);
    }

    protected void responseInputStream(final InputStream inputStream) {
        mXXBringBean.responseInputStream(inputStream);
    }

    protected void responseOther() {
        mXXBringBean.responseOther();
    }

    //是否需要运行在子线程
    protected abstract boolean runIsThread();

    protected abstract void getHandle();

    protected abstract void postHandle();
}
