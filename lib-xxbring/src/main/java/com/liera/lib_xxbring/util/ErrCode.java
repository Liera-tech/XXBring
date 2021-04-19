package com.liera.lib_xxbring.util;

public interface ErrCode {

    //请求转义异常
    int REQUEST_EXCEPTION_ESCAPE = 1001;
    //请求地址格式有误
    int REQUEST_EXCEPTION_REQUEST_ADDRESS = 1002;
    //请求设备无网络
    int REQUEST_EXCEPTION_NOT_INTERNET = 1003;
    //请求方式未实现
    int REQUEST_EXCEPTION_NOT_IMPLEMENTED = 1004;
    //请求类型未实现
    int REQUEST_EXCEPTION_NOT_REQUEST = 1005;
    //媒体文件不存在
    int REQUEST_EXCEPTION_FILE_NOT_FOUND = 1006;
    //请求连接失败
    int REQUEST_EXCEPTION_INTERNET_FAIL = 1007;
    //请求数据写入失败
    int REQUEST_EXCEPTION_WRITE_DATA_FAIL = 1008;

    //响应失败
    int RESPONSE_EXCEPTION_FAIL = 2000;
    //响应成功
    int RESPONSE_EXCEPTION_SUCCESS = 2001;
    //响应字节数组异常
    int RESPONSE_EXCEPTION_BYTE_ARRAY = 2002;
    //响应inputStream流异常
    int RESPONSE_EXCEPTION_INPUT_STREAM = 2003;
    //响应数据异常
    int RESPONSE_EXCEPTION_STRING = 2004;
}
