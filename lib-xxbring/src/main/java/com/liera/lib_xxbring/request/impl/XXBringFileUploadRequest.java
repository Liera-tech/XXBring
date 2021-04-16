package com.liera.lib_xxbring.request.impl;

import java.io.File;

public abstract class XXBringFileUploadRequest extends XXBringPostRequest {

    public XXBringFileUploadRequest(Object tag) {
        super(tag);
    }

    /**
     * 获取请求文件
     * @return
     */
    public abstract File getAbsoluteFile();

    /**
     * 媒体文件类型
     * @return
     */
    public String getFileMediaType(){
        return "image/*";
    }

    /**
     * 媒体文件上传name
     * 子类重写可更改上传name
     *
     * @return
     */
    public String getFileFormDataName(){
        return "upload";
    }

}
