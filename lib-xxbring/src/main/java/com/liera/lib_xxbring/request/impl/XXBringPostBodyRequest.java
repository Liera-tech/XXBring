package com.liera.lib_xxbring.request.impl;

import com.liera.lib_xxbring.bean.XXBringRequestBody;

import java.util.Map;

public abstract class XXBringPostBodyRequest extends XXBringPostRequest {

    private final XXBringRequestBody body;

    public XXBringPostBodyRequest(Object tag, XXBringRequestBody body) {
        super(tag);
        this.body = body;
    }

    /**
     * 当 {@link XXBringPostRequest#getRequestType() 为PARAMETER参数类型时有效}
     * 子类重写无效, 不建议子类重写
     *
     * @param mParameters
     */
    @Override
    protected void addParameters(Map<String, Object> mParameters) {

    }

    /**
     * 获取json请求体
     * @return
     */
    public XXBringRequestBody getRequestBody() {
        return body != null ? body :new XXBringRequestBody() {
        };
    }
}
