package com.liera.xxbring;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.liera.lib_xxbring.XXBring;
import com.liera.lib_xxbring.callback.XXBringInputStreamCallback;
import com.liera.lib_xxbring.callback.XXBringJsonArrayCallback;
import com.liera.lib_xxbring.callback.XXBringJsonObjectCallback;
import com.liera.lib_xxbring.callback.XXBringTextCallback;
import com.liera.lib_xxbring.request.IXXBringRequest;
import com.liera.lib_xxbring.response.IXXBringResponse;
import com.liera.xxbring.bean.MeRequestBodyBean;
import com.liera.xxbring.request.MeGetRequest;
import com.liera.xxbring.request.MePostBodyRequest;
import com.liera.xxbring.request.MePostParmeterRequest;
import com.liera.xxbring.request.MePostUploadRequest;
import com.liera.xxbring.response.MePostJsonArrayResponse;
import com.liera.xxbring.response.MePostJsonObjectResponse;
import com.liera.xxbring.response.MePostUploadResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    //为每一个请求设置一个单独的tag
    public static final int INPUT_TAG = 0x01;
    public static final int TEXT_TAG = 0x02;
    public static final int UPLOAD_TAG = 0x03;
    public static final int JSON_ARRAY_TAG = 0x04;
    public static final int JSON_OBJECT_TAG = 0x05;

    private XXBring mXXBring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.get_to_text).setOnClickListener(this);
        findViewById(R.id.get_to_inputStream).setOnClickListener(this);
        findViewById(R.id.post_to_jsonObject).setOnClickListener(this);
        findViewById(R.id.post_to_jsonArray).setOnClickListener(this);
        findViewById(R.id.post_to_upload).setOnClickListener(this);

        //配置请求,这个可以放到一个统一的地方
        mXXBring = new XXBring.Builder()
                //是否打印日志
                .setDebug(true)
//                //设置自己的请求网络框架,不设置默认是okhttp请求
//                .setRequestManager()
//                //是否打印全局请求json数据,默认是debug版本打印,正式版本不打印
//                .isShowJsonData(false)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发起get请求,响应数据是text数据
            case R.id.get_to_text:
                getToText();
                break;
            //发起get请求响应数据是inputStream流
            case R.id.get_to_inputStream:
                getToInputStream();
                break;
            //发起post请求,传递json数据,响应结果是jsonObject数据
            case R.id.post_to_jsonObject:
                postToJsonObject();
                break;
            //发起post请求,传递参数,响应结果是jsonArray数据
            case R.id.post_to_jsonArray:
                postToJsonArray();
                break;
            //发起post请求上传文件,响应结果是json数据
            case R.id.post_to_upload:
                postToUploadFile();
                break;
        }
    }

    //里面的请求地址和文件地址自行设置
    private void postToUploadFile() {
        mXXBring.request(new MePostUploadRequest(UPLOAD_TAG, new File(Environment.getExternalStorageDirectory(), "1.txt")), MePostUploadResponse.class, mXXBringJsonObjectCallback);
    }

    //里面的请求地址和文件地址自行设置
    private void postToJsonArray() {
        mXXBring.request(new MePostParmeterRequest(JSON_ARRAY_TAG, "https://fanyi.baidu.com"), MePostJsonArrayResponse.class, mXXBringJsonArrayCallback);
    }

    //里面的请求地址和文件地址自行设置
    private void postToJsonObject() {
        MeRequestBodyBean meRequestBody = new MeRequestBodyBean();
        meRequestBody.setName("张三");
        meRequestBody.setPwd("123456");

        mXXBring.request(new MePostBodyRequest(JSON_OBJECT_TAG, "https://fanyi.baidu.com", meRequestBody), MePostJsonObjectResponse.class, mXXBringJsonObjectCallback);
    }

    //里面的请求地址和文件地址自行设置
    private void getToInputStream() {
        mXXBring.request(new MeGetRequest(INPUT_TAG, "https://fanyi.baidu.com"), mXXBringInputCallback);
    }

    //里面的请求地址和文件地址自行设置
    public void getToText() {
        mXXBring.request(new MeGetRequest(TEXT_TAG, "https://www.baidu.com"), mXXBringTextCallback);
    }

    //获取inputStream流回调
    private XXBringInputStreamCallback mXXBringInputCallback = new XXBringInputStreamCallback() {
        @Override
        public void inputStreamResponseSuccess(IXXBringRequest req, InputStream input, Handler mHandler) {
            if (req instanceof MeGetRequest) {
                String name = Thread.currentThread().getName();
                Log.i(TAG, "打印所在线程:" + name);

                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //如果是子线程
                boolean isThread = req.isResponseSuccessThread();
                if (isThread) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String name = Thread.currentThread().getName();
                            Log.i(TAG, "到主线程,打印线程:" + name);
                            Toast.makeText(MainActivity.this, "inputStream子线程响应成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                Toast.makeText(MainActivity.this, "inputStream主线程响应成功", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        public void responseFail(IXXBringRequest req, int respCode, Exception e, Handler mHandler) {
            Log.i(TAG, "InputCallback,tag:" + req.getRequestTag() + "请求失败, respCode:" + respCode + ", 异常:" + e.getMessage());
            boolean responseFailThread = req.isResponseFailThread();
            if (responseFailThread) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "inputStream子线程响应失败", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            Toast.makeText(MainActivity.this, "inputStream响应失败,respCode:" + respCode + ", 异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //获取text流回调
    private XXBringTextCallback mXXBringTextCallback = new XXBringTextCallback() {
        @Override
        public void textResponseSuccess(IXXBringRequest req, final String text, Handler mHandler) {
            if (req instanceof MeGetRequest) {
                Log.i(TAG, "获取到text内容:" + text);

                //如果是子线程
                boolean isThread = req.isResponseSuccessThread();
                if (isThread) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String name = Thread.currentThread().getName();
                            Log.i(TAG, "到主线程,打印线程:" + name);
                            Toast.makeText(MainActivity.this, "text子线程响应成功:" + text, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                Toast.makeText(MainActivity.this, "text主线程响应成功:" + text, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        public void responseFail(IXXBringRequest req, int respCode, Exception e, Handler mHandler) {
            Log.i(TAG, "TextCallback,tag:" + req.getRequestTag() + "请求失败, respCode:" + respCode + ", 异常:" + e.getMessage());
            boolean responseFailThread = req.isResponseFailThread();
            if (responseFailThread) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "text子线程响应失败", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            Toast.makeText(MainActivity.this, "text响应失败,respCode:" + respCode + ", 异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //获取jsonObject回调
    private XXBringJsonObjectCallback mXXBringJsonObjectCallback = new XXBringJsonObjectCallback() {
        @Override
        public void jsonResponseObjectSuccess(IXXBringRequest req, IXXBringResponse resp, Handler mHandler) {
            if (req instanceof MePostUploadRequest) {
                final MePostUploadResponse data = (MePostUploadResponse) resp;
                Log.i(TAG, "JsonObjectCallback,tag:" + req.getRequestTag() + "数据:" + data);

                //如果是子线程
                boolean isThread = req.isResponseSuccessThread();
                if (isThread) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String name = Thread.currentThread().getName();
                            Log.i(TAG, "到主线程,打印线程:" + name);
                            Toast.makeText(MainActivity.this, "JsonObject子线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this, "JsonObject主线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (req instanceof MePostBodyRequest) {
                final MePostUploadResponse data = (MePostUploadResponse) resp;
                Log.i(TAG, "JsonObjectCallback,tag:" + req.getRequestTag() + "数据:" + data);

                //如果是子线程
                boolean isThread = req.isResponseSuccessThread();
                if (isThread) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String name = Thread.currentThread().getName();
                            Log.i(TAG, "到主线程,打印线程:" + name);
                            Toast.makeText(MainActivity.this, "JsonObject子线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this, "JsonObject主线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        @Override
        public void responseFail(IXXBringRequest req, int respCode, Exception e, Handler mHandler) {
            Log.i(TAG, "JsonObject,tag:" + req.getRequestTag() + "请求失败, respCode:" + respCode + ", 异常:" + e.getMessage());
//            if (req instanceof MePostUploadRequest) {
//
//            }
            boolean responseFailThread = req.isResponseFailThread();
            if (responseFailThread) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "JsonObject子线程响应失败", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            Toast.makeText(MainActivity.this, "JsonObject响应失败,respCode:" + respCode + ", 异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //获取jsonObject回调
    private XXBringJsonArrayCallback mXXBringJsonArrayCallback = new XXBringJsonArrayCallback() {

        @Override
        public void jsonResponseArraySuccess(IXXBringRequest req, ArrayList<? extends IXXBringResponse> resp, Handler mHandler) {
            if (req instanceof MePostParmeterRequest) {
                final ArrayList<MePostJsonArrayResponse> data = (ArrayList<MePostJsonArrayResponse>) resp;
                Log.i(TAG, "JsonArrayCallback,tag:" + req.getRequestTag() + "数据:" + data);

                //如果是子线程
                boolean isThread = req.isResponseSuccessThread();
                if (isThread) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String name = Thread.currentThread().getName();
                            Log.i(TAG, "到主线程,打印线程:" + name);
                            Toast.makeText(MainActivity.this, "JsonArray子线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this, "JsonArray主线程响应成功:" + data.toString(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        @Override
        public void responseFail(IXXBringRequest req, int respCode, Exception e, Handler mHandler) {
            Log.i(TAG, "JsonArray,tag:" + req.getRequestTag() + "请求失败, respCode:" + respCode + ", 异常:" + e.getMessage());
            boolean responseFailThread = req.isResponseFailThread();
            if (responseFailThread) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "JsonArray子线程响应失败", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            Toast.makeText(MainActivity.this, "JsonArray响应失败,respCode:" + respCode + ", 异常:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //页面销毁时,通过tag关闭请求(tag要设置唯一,避免因其他请求有相同的tag导致被关闭)
        XXBring.cancelRequest(INPUT_TAG, TEXT_TAG);
        //或者直接cancelAll将所有的请求全部停止(包括非本页面的请求)
        //XXBring.cancelAll();
    }
}
