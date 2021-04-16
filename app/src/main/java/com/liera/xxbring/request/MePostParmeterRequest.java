package com.liera.xxbring.request;

import com.liera.lib_xxbring.request.impl.XXBringPostParmeterRequest;

import java.util.Map;

public class MePostParmeterRequest extends XXBringPostParmeterRequest {

    private final String url;

    public MePostParmeterRequest(Object tag, String url) {
        super(tag);
        this.url = url;
    }

    @Override
    protected void addParameters(Map<String, Object> mParameters) {
        mParameters.put("userName", "李四");
    }

    @Override
    public String getUrl() {
        return url;
    }
}
