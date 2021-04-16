package com.liera.xxbring.request;

import com.liera.lib_xxbring.request.impl.XXBringFileUploadRequest;

import java.io.File;
import java.util.Map;

public class MePostUploadRequest extends XXBringFileUploadRequest {

    private final File file;

    public MePostUploadRequest(Object tag, File file) {
        super(tag);
        this.file = file;
    }

    //上传文件的绝对路径
    @Override
    public File getAbsoluteFile() {
        return file;
    }

    //这个方法就相当于设置Content-Type: image/jpeg
    @Override
    public String getFileMediaType() {
        return "image/jpeg";
    }

    //Content-Disposition: form-data; name="file"; filename="test.jpg"
    //这个方法就相当于设置name="file"
    @Override
    public String getFileFormDataName() {
        return "file";
    }

    //添加请求参数
    @Override
    protected void addParameters(Map<String, Object> mParameters) {
        mParameters.put("userName", "张三");
    }

    //上传的url
    @Override
    public String getUrl() {
        return "https://fanyi.baidu.com";
    }
}
