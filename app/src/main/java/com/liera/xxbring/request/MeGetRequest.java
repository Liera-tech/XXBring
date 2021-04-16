package com.liera.xxbring.request;

import com.liera.lib_xxbring.request.impl.XXBringGetRequest;
import com.liera.xxbring.MainActivity;

import java.util.Map;

public class MeGetRequest extends XXBringGetRequest {

    private final String url;

    public MeGetRequest(Object tag, String url) {
        super(tag);
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
//        return "https://www.baidu.com";
    }

    //添加请求头
    @Override
    protected void addHeaders(Map<String, Object> mHeaders) {
        mHeaders.put("token", "H%&FGfdII*GHKgkGO&GI");
    }

    //添加请求参数
    @Override
    protected void addParameters(Map<String, Object> mParameters) {
        mParameters.put("name", "张三");
    }

    //响应成功是否在子线程
    @Override
    public boolean isResponseSuccessThread() {
        //如果响应数据是inputStream流,就在子线程
        if(tag.equals(MainActivity.INPUT_TAG)) return true;

        //普通的就可以不在子线程中
        return super.isResponseSuccessThread();
    }

    //响应失败是否在子线程
    @Override
    public boolean isResponseFailThread() {
        return super.isResponseFailThread();
    }

    //是否打印本次json响应数据日志
    @Override
    public boolean isShowJsonData() {
        return super.isShowJsonData();
    }
}
