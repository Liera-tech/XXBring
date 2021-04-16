package com.liera.lib_xxbring.request.impl;

import com.liera.lib_xxbring.enums.HttpMethod;

import java.util.Map;

public abstract class XXBringPostRequest extends XXBringBaseRequest {

    XXBringPostRequest(Object tag) {
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
     * 此处为POST,支持以下几种类型:
     * {@link HttpMethod.POST post请求}
     * {@link HttpMethod.GET get请求}
     * 不建议子类重写
     *
     * @return
     */
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    /**
     * 响应数据成功是否在子线程
     *
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
     * 响应数据失败是否在子线程
     *
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
