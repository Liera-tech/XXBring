package com.liera.lib_xxbring.request.impl;

import com.liera.lib_xxbring.request.IXXBringRequest;

import java.util.HashMap;
import java.util.Map;

abstract class XXBringBaseRequest implements IXXBringRequest {

    private Map<String, Object> mParameters = new HashMap<>();
    private Map<String, Object> mHeaders = new HashMap<>();
    protected Object tag;

    XXBringBaseRequest(Object tag) {
        this.tag = tag;
    }

    @Override
    public Object getRequestTag() {
        return this.tag;
    }

    @Override
    public Map<String, Object> getHeaders() {
        addHeaders(mHeaders);
        return mHeaders;
    }

    @Override
    public Map<String, Object> getParameters() {
        addParameters(mParameters);
        return mParameters;
    }

    protected abstract void addHeaders(Map<String, Object> mHeaders);

    protected abstract void addParameters(Map<String, Object> mParameters);

    /**
     * 响应数据为json格式时有效
     * 是否查看json打印数据(效率稍低)
     * @return
     */
    public boolean isShowJsonData() {
        return false;
    }
}
