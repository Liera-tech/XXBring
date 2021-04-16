package com.liera.lib_xxbring.request.impl;

import com.liera.lib_xxbring.enums.HttpMethod;

import java.util.Map;

public abstract class XXBringGetRequest extends XXBringBaseRequest {

    public XXBringGetRequest(Object tag) {
        super(tag);
    }

    /**
     * 子类重写此方法可添加请求头
     * @param mHeaders
     */
    @Override
    protected void addHeaders(Map<String, Object> mHeaders) {

    }

    /**
     * @{hide}
     * 请求方式
     *
     * 此处为GET,支持以下几种类型:
     * {@link HttpMethod.POST post请求}
     * {@link HttpMethod.GET get请求}
     * 不建议子类重写
     *
     * @return
     */
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    /**
     * 响应成功数据是否在子线程
     *
     * 默认为主线程
     * true 响应成功内容在子线程
     * false 响应成功内容在主线程
     * 子类重写此方法可更换响应成功线程
     *
     * @return
     */
    @Override
    public boolean isResponseSuccessThread() {
        return false;
    }

    /**
     * 响应失败数据是否在子线程
     *
     * 默认为主线程
     * true 响应失败在子线程
     * false 响应失败在主线程
     * 子类重写此方法可更换响应失败线程
     *
     * @return
     */
    @Override
    public boolean isResponseFailThread() {
        return false;
    }
}
