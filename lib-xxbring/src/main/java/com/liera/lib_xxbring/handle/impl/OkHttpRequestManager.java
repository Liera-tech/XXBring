package com.liera.lib_xxbring.handle.impl;

import android.text.TextUtils;

import com.liera.lib_xxbring.bean.XXBringRequestBody;
import com.liera.lib_xxbring.callback.XXBringBaseCallback;
import com.liera.lib_xxbring.callback.XXBringByteArrayCallback;
import com.liera.lib_xxbring.callback.XXBringInputStreamCallback;
import com.liera.lib_xxbring.callback.XXBringJsonArrayCallback;
import com.liera.lib_xxbring.callback.XXBringJsonObjectCallback;
import com.liera.lib_xxbring.callback.XXBringTextCallback;
import com.liera.lib_xxbring.handle.RequestManager;
import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.request.impl.XXBringFileUploadRequest;
import com.liera.lib_xxbring.request.impl.XXBringPostBodyRequest;
import com.liera.lib_xxbring.request.impl.XXBringPostParmeterRequest;
import com.liera.lib_xxbring.util.ErrCode;
import com.liera.lib_xxbring.util.XXBringLog;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpRequestManager extends RequestManager implements Callback {

    private static final String TAG = "OkHttpRequestManager";

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected boolean runIsThread() {
        return false;
    }

    @Override
    protected void getHandle() {
        Request.Builder builder = setHeader("get");

        String url;
        try {
            url = getUrl(mXXBringBean.getReq().getUrl(), "get");
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_ESCAPE, e);
            return;
        }

        try {
            builder.url(url).get();
            Request okRequest = builder.build();
            okHttpClient.newCall(okRequest).enqueue(this);
        } catch (final Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_REQUEST_ADDRESS, e);
        }
    }

    @Override
    protected void postHandle() {
        Request.Builder builder = setHeader("post");

        final IXXBringRequest request = mXXBringBean.getReq();
        String url = request.getUrl();
        XXBringLog.i(TAG, "post请求->完整请求地址:%s", url);

        if (request instanceof XXBringPostParmeterRequest) {
            FormBody.Builder formBodyBuilder = setParmeters(request);
            builder.url(url).post(formBodyBuilder.build());
        } else if (request instanceof XXBringPostBodyRequest) {
            RequestBody requestBody = setRequestBody((XXBringPostBodyRequest) request);
            if (requestBody == null) return;
            builder.url(url).post(requestBody);
        } else if (request instanceof XXBringFileUploadRequest) {
            MultipartBody.Builder requestBody = getFileFormat(url, (XXBringFileUploadRequest) request);
            if (requestBody == null) return;
            setMultipartBodyParmeters(request, requestBody);
            builder.url(url).post(requestBody.build());
        } else {
            try {
                throw new Exception("请求类型未实现");
            } catch (Exception e) {
                e.printStackTrace();
                responseFail(ErrCode.REQUEST_EXCEPTION_NOT_REQUEST, e);
            }
            return;
        }

        try {
            Request okRequest = builder.build();
            okHttpClient.newCall(okRequest).enqueue(this);
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_REQUEST_ADDRESS, e);
        }
    }

    private MultipartBody.Builder getFileFormat(String url, XXBringFileUploadRequest req) {
        MediaType MEDIA_TYPE_IMAGE = MediaType.parse(req.getFileMediaType());
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = req.getAbsoluteFile();
        XXBringLog.i(TAG, "post请求->上传文件请求地址:%s,文件路径:%s", url, file);
        if (file != null && file.exists()) {
            String fileFormatDataName = req.getFileFormDataName();
            String name = file.getName();
            XXBringLog.i(TAG, "post请求->上传文件文件参数:name:%s,filename:%s", fileFormatDataName, name);
            requestBody.addFormDataPart(fileFormatDataName, name, RequestBody.create(MEDIA_TYPE_IMAGE, file));
            return requestBody;
        }

        Exception exception = new Exception("媒体文件不存在");

        try {
            throw exception;
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseFail(ErrCode.REQUEST_EXCEPTION_FILE_NOT_FOUND, exception);
        return null;
    }

    private void setMultipartBodyParmeters(IXXBringRequest req, MultipartBody.Builder requestBody) {
        Map<String, Object> params = req.getParameters();
        if (params != null) {
            // map 里面是请求中所需要的 key 和 value
            for (String key : params.keySet()) {
                Object value = params.get(key);
                XXBringLog.i(TAG, "post请求->上传文件请求参数:%s:%s", key, value);
                requestBody.addFormDataPart(key, String.valueOf(value));
            }
        }
    }

    private RequestBody setRequestBody(XXBringPostBodyRequest request) {
        XXBringRequestBody xxBringRequestBody = request.getRequestBody();
        String jsonDataBody = mGson.toJson(xxBringRequestBody);
        RequestBody requestBody = RequestBody.create(mediaType, jsonDataBody);
        XXBringLog.i(TAG, "post请求--请求json对象参数:%s", jsonDataBody);
        return requestBody;
    }

    private FormBody.Builder setParmeters(IXXBringRequest request) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        Map<String, Object> params = request.getParameters();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                Object value = params.get(key);
                XXBringLog.i(TAG, "post请求->请求参数:%s:%s", key, value);
                try {
                    formBodyBuilder.addEncoded(key, URLEncoder.encode(String.valueOf(value), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return formBodyBuilder;
    }

    private Request.Builder setHeader(String httpMethod) {
        Request.Builder builder = new Request.Builder();
        IXXBringRequest req = mXXBringBean.getReq();
        Object requestTag = req.getRequestTag();
        if (requestTag != null) {
            builder.tag(requestTag);
        }

        Map<String, Object> header = req.getHeaders();
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                Object value = header.get(key);
                XXBringLog.i(TAG, "%s请求->请求头参数:%s:%s", httpMethod, key, value);

                if (value != null && !TextUtils.isEmpty(value.toString())) {
                    try {
                        String en = URLEncoder.encode(value.toString().trim(), "UTF-8");
                        XXBringLog.i(TAG, "%s请求->头参数转义后:%s:%s", httpMethod, key, en);
                        builder.header(key, en);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return builder;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (call.isCanceled()) {
            XXBringLog.i(TAG, "tag:" + mXXBringBean.getReq().getRequestTag() + "请求失败,请求被关闭");
            return;
        }
        XXBringLog.i(TAG, "数据响应异常:%s", e);
        call.cancel();

        try {
            throw e;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        responseFail(ErrCode.RESPONSE_EXCEPTION_FAIL, e);
    }

    @Override
    public void onResponse(Call call, final Response response) {
        if (call.isCanceled()) {
            XXBringLog.i(TAG, "tag:" + mXXBringBean.getReq().getRequestTag() + "请求响应成功,请求被关闭");
            return;
        }

        boolean successful = response.isSuccessful();
        if (!successful) {
            int code = response.code();
            Exception exception = new Exception(code + "异常");
            try {
                throw exception;
            } catch (Exception e) {
                e.printStackTrace();
            }
            responseFail(code, exception);
            return;
        }

        ResponseBody body = response.body();
        XXBringBaseCallback callback = mXXBringBean.getCallback();
        IXXBringRequest req = mXXBringBean.getReq();

        if (callback instanceof XXBringJsonObjectCallback) {
            XXBringLog.i(TAG, "jsonObject回调tag:" + req.getRequestTag());
            if (!req.isShowJsonData() || !mXXBringBean.isShowJsonData()) {
                Reader reader = body.charStream();
                responseJsonReader(reader);

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.close();
                return;
            }
            try {
                String string = body.string();
                XXBringLog.i(TAG, "tag:" + req.getRequestTag() + "响应数据:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            response.close();
            return;
        }
        if (callback instanceof XXBringJsonArrayCallback) {
            XXBringLog.i(TAG, "jsonArray回调tag:" + req.getRequestTag());

            if (!req.isShowJsonData() || !mXXBringBean.isShowJsonData()) {
                Reader reader = body.charStream();
                responseJsonReader(reader);

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.close();
                return;
            }

            try {
                String string = body.string();
                XXBringLog.i(TAG, "tag:" + req.getRequestTag() + "响应数据:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            response.close();
            return;
        }
        if (callback instanceof XXBringTextCallback) {
            XXBringLog.i(TAG, "text回调tag:" + req.getRequestTag());
            try {
                final String string = body.string();
                XXBringLog.i(TAG, "tag:" + req.getRequestTag() + "响应数据:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            response.close();
            return;
        }

        if (callback instanceof XXBringByteArrayCallback) {
            XXBringLog.i(TAG, "byteArray回调tag:" + req.getRequestTag());

            try {
                responseByteArray(body.bytes());
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_BYTE_ARRAY, e);
            }
            response.close();
            return;
        }
        if (callback instanceof XXBringInputStreamCallback) {
            XXBringLog.i(TAG, "inputStream回调tag:" + req.getRequestTag());
            try {
                responseInputStream(body.byteStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.close();
            return;
        }
        responseOther();
        response.close();
    }

    public static void cancel(Object... requestTag) {
        //正在排队的call
        List<Call> calls = okHttpClient.dispatcher().queuedCalls();
        if (!calls.isEmpty())
            for (Call call : calls) {
                if (requestTag != null) {
                    for (Object o : requestTag) {
                        if (o.equals(call.request().tag())) {
                            XXBringLog.i(TAG, "停止正在排队的call：" + o);
                            call.cancel();
                        }
                    }
                } else {
                    XXBringLog.i(TAG, "停止正在排队的call：" + call.request().tag());
                    call.cancel();
                }
            }
        //正在执行的call
        List<Call> calls1 = okHttpClient.dispatcher().runningCalls();
        if (!calls1.isEmpty())
            for (Call call : calls1) {
                if (requestTag != null) {
                    for (Object o : requestTag) {
                        if (o.equals(call.request().tag())) {
                            XXBringLog.i(TAG, "停止正在执行的call：" + o);
                            call.cancel();
                        }
                    }
                } else {
                    XXBringLog.i(TAG, "停止正在执行的call：" + call.request().tag());
                    call.cancel();
                }
            }
    }
}
