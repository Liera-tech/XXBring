package com.liera.xxbring.request;

import com.liera.lib_xxbring.request.impl.XXBringPostBodyRequest;
import com.liera.xxbring.bean.MeRequestBodyBean;

public class MePostBodyRequest extends XXBringPostBodyRequest {

    private final String url;

    public MePostBodyRequest(Object tag, String url, MeRequestBodyBean body) {
        super(tag, body);
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
