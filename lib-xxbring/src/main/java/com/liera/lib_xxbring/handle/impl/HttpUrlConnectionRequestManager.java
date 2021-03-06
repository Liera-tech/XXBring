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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

public class HttpUrlConnectionRequestManager extends RequestManager {

    private static final String TAG = "HttpUrlConnectionRequestManager";

    @Override
    protected boolean runIsThread() {
        return true;
    }

    @Override
    protected void getHandle() {
        String url;
        try {
            url = getUrl(mXXBringBean.getReq().getUrl(), "get");
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_ESCAPE, e);
            return;
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpURLConnection("get", url);
        } catch (IOException e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_INTERNET_FAIL, e);
            return;
        }

        try {
            // ??????
            httpURLConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_INTERNET_FAIL, e);
            return;
        }

        responseBody(mXXBringBean.getReq(), httpURLConnection);
    }

    @Override
    protected void postHandle() {
        IXXBringRequest request = mXXBringBean.getReq();
        String url = request.getUrl();
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = getHttpURLConnection("post", url);
        } catch (IOException e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_INTERNET_FAIL, e);
            return;
        }

        String contentType;
        String data;

        if (request instanceof XXBringPostParmeterRequest) {
            contentType = "application/x-www-form-urlencoded";

            try {
                data = getUrl("", "post");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                responseFail(ErrCode.REQUEST_EXCEPTION_WRITE_DATA_FAIL, e);
                return;
            }

            if (requestBody(httpURLConnection, contentType, data, null)) return;

            responseBody(request, httpURLConnection);
        } else if (request instanceof XXBringPostBodyRequest) {
            contentType = "application/json; charset=utf-8";

            XXBringRequestBody xxBringRequestBody = ((XXBringPostBodyRequest) request).getRequestBody();
            String jsonDataBody = mGson.toJson(xxBringRequestBody);

            try {
                data = URLEncoder.encode(jsonDataBody, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                try {
                    httpURLConnection.disconnect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                responseFail(ErrCode.REQUEST_EXCEPTION_WRITE_DATA_FAIL, e);
                return;
            }

            if (requestBody(httpURLConnection, contentType, data, null)) return;

            responseBody(request, httpURLConnection);
        } else if (request instanceof XXBringFileUploadRequest) {
            contentType = "multipart/form-data;boundary=" + BOUNDARY;

            StringBuilder strParams = getStrParams(request.getParameters());

            if (requestBody(httpURLConnection, contentType, strParams.toString(), null)) return;

            responseBody(request, httpURLConnection);
        } else {
            Exception exception = new Exception("?????????????????????");
            try {
                throw exception;
            } catch (Exception e) {
                e.printStackTrace();
            }
            responseFail(ErrCode.REQUEST_EXCEPTION_NOT_REQUEST, exception);
            return;
        }
    }

    /**
     * ???post????????????????????????
     */
    private static StringBuilder getStrParams(Map<String, Object> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : strParams.entrySet()) {
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// ?????????????????????????????????????????????????????????????????????
                    .append(entry.getValue().toString())
                    .append(LINE_END);
        }
        return strSb;
    }

    private void responseBody(IXXBringRequest request, HttpURLConnection httpURLConnection) {
        try {
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(responseCode + "??????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_WRITE_DATA_FAIL, e);
            try {
                httpURLConnection.disconnect();
            } catch (Exception e1) {
                e.printStackTrace();
            }
            return;
        }

        InputStream inputStream;
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_WRITE_DATA_FAIL, e);
            try {
                httpURLConnection.disconnect();
            } catch (Exception e1) {
                e.printStackTrace();
            }
            return;
        }

        //??????
        XXBringBaseCallback callback = mXXBringBean.getCallback();

        if (callback instanceof XXBringInputStreamCallback) {
            XXBringLog.i(TAG, "inputStream??????tag:" + request.getRequestTag());
            responseInputStream(inputStream);

            close(httpURLConnection, inputStream);
            return;
        }

        if (callback instanceof XXBringByteArrayCallback) {
            XXBringLog.i(TAG, "byteArray??????tag:" + request.getRequestTag());

            try {
                responseByteArray(toByteArray(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_BYTE_ARRAY, e);
            }
            close(httpURLConnection, inputStream);
            return;
        }

        if (callback instanceof XXBringJsonObjectCallback) {
            XXBringLog.i(TAG, "jsonObject??????tag:" + request.getRequestTag());
            if (!request.isShowJsonData() || !mXXBringBean.isShowJsonData()) {
                // ????????????
                Reader reader = new BufferedReader(new InputStreamReader(inputStream));
                responseJsonReader(reader);
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                close(httpURLConnection, inputStream);
                return;
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String lines;
                StringBuffer sb = new StringBuffer();
                while ((lines = reader.readLine()) != null) {
                    lines = URLDecoder.decode(lines, "utf-8");
                    sb.append(lines);
                }
                reader.close();

                String string = sb.toString();
                XXBringLog.i(TAG, "tag:" + request.getRequestTag() + "????????????:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            close(httpURLConnection, inputStream);
            return;
        }
        if (callback instanceof XXBringJsonArrayCallback) {
            XXBringLog.i(TAG, "jsonArray??????tag:" + request.getRequestTag());

            if (!request.isShowJsonData() || !mXXBringBean.isShowJsonData()) {
                // ????????????
                Reader reader = new BufferedReader(new InputStreamReader(inputStream));
                responseJsonReader(reader);
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                close(httpURLConnection, inputStream);
                return;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String lines;
                StringBuffer sb = new StringBuffer();
                while ((lines = reader.readLine()) != null) {
                    lines = URLDecoder.decode(lines, "utf-8");
                    sb.append(lines);
                }
                reader.close();

                String string = sb.toString();
                XXBringLog.i(TAG, "tag:" + request.getRequestTag() + "????????????:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            close(httpURLConnection, inputStream);
            return;
        }
        if (callback instanceof XXBringTextCallback) {
            XXBringLog.i(TAG, "text??????tag:" + request.getRequestTag());
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String lines;
                StringBuffer sb = new StringBuffer();
                while ((lines = reader.readLine()) != null) {
                    lines = URLDecoder.decode(lines, "utf-8");
                    sb.append(lines);
                }
                reader.close();

                String string = sb.toString();
                XXBringLog.i(TAG, "tag:" + request.getRequestTag() + "????????????:%s", string);
                responseData(string);
            } catch (IOException e) {
                e.printStackTrace();
                responseFail(ErrCode.RESPONSE_EXCEPTION_STRING, e);
            }
            close(httpURLConnection, inputStream);
            return;
        }
        responseOther();
        close(httpURLConnection, inputStream);
    }

    private void close(HttpURLConnection httpURLConnection, InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ????????????
        httpURLConnection.disconnect();
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        byte[] bytes = output.toByteArray();
        output.close();
        return bytes;
    }

    private static final String CHARSET = "utf-8";                         //????????????
    private static final String PREFIX = "--";                            //??????
    private static final String BOUNDARY = UUID.randomUUID().toString();  //???????????? ????????????
    private static final String LINE_END = "\r\n";

    private boolean requestBody(HttpURLConnection httpURLConnection, String contentType, String data, File file) {
        // http??????????????????????????????true, ??????????????????false;
        httpURLConnection.setDoOutput(true);
        // ???????????????httpUrlConnection???????????????????????????true;
        httpURLConnection.setDoInput(true);
        // Post ????????????????????????
        httpURLConnection.setUseCaches(false);
        //???????????????????????????
        httpURLConnection.setInstanceFollowRedirects(true);
        //?????????????????????json
        httpURLConnection.setRequestProperty("Content-Type", contentType);
        try {
            // ??????
            httpURLConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
            responseFail(ErrCode.REQUEST_EXCEPTION_INTERNET_FAIL, e);
            return true;
        }

        try {
            // POST??????
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());

            out.writeBytes(data);
            out.flush();

            if (file != null) {
                XXBringFileUploadRequest req = (XXBringFileUploadRequest) mXXBringBean.getReq();
                //????????????
                StringBuilder fileSb = new StringBuilder();
                fileSb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(LINE_END)
                        /**
                         * ????????????????????? name?????????????????????????????????key ????????????key ??????????????????????????????
                         * filename??????????????????????????????????????? ??????:abc.png
                         */
                        .append("Content-Disposition: form-data; name=\"" + req.getFileFormDataName() + "\"; filename=\""
                                + file.getName() + "\"" + LINE_END)
                        .append("Content-Type: " + req.getFileMediaType() + LINE_END) //?????????ContentType????????? ????????? ???Content-Type
                        .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                        .append(LINE_END);// ?????????????????????????????????????????????????????????????????????
                out.writeBytes(fileSb.toString());
                out.flush();
                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                is.close();
                out.writeBytes(LINE_END);

                //??????????????????
                out.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                httpURLConnection.disconnect();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            responseFail(ErrCode.REQUEST_EXCEPTION_WRITE_DATA_FAIL, e);
            return true;
        }

        return false;
    }

    private HttpURLConnection getHttpURLConnection(String httpMethod, String url) throws IOException {
        URL URL = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) URL.openConnection();

        //??????????????????
        httpURLConnection.setRequestMethod(httpMethod.toUpperCase());

        Map<String, Object> header = mXXBringBean.getReq().getHeaders();
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                Object value = header.get(key);
                XXBringLog.i(TAG, "%s??????->???????????????:%s:%s", httpMethod, key, value);

                if (value != null && !TextUtils.isEmpty(value.toString())) {
                    try {
                        String en = URLEncoder.encode(value.toString().trim(), "UTF-8");
                        XXBringLog.i(TAG, "%s??????->??????????????????:%s:%s", httpMethod, key, en);
                        httpURLConnection.setRequestProperty(key, value.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return httpURLConnection;
    }
}
