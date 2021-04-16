package com.liera.lib_xxbring.request;

import com.liera.lib_xxbring.enums.HttpMethod;

import java.util.Map;

public interface IXXBringRequest {

    /**
     * 请求地址
     * @return
     */
    String getUrl();

    /**
     * 为请求设置一个tag
     * @return
     */
    Object getRequestTag();
    /**
     * 请求方式
     * @return
     */
    HttpMethod getMethod();

    /**
     * 请求头
     * @return
     */
    Map<String, Object> getHeaders();

    /**
     * 请求参数
     * @return
     */
    Map<String, Object> getParameters();


    /**
     * 响应数据成功是否在子线程
     *
     * true 响应成功内容在子线程
     * false 响应成功内容在主线程
     *
     * @return
     */
    boolean isResponseSuccessThread();

    /**
     * 响应数据失败是否在子线程
     *
     * true 响应失败在子线程
     * false 响应失败在主线程
     *
     * @return
     */
    boolean isResponseFailThread();

    /**
     * 响应数据为json格式时有效
     * 是否查看json字符串数据(效率稍低)
     * @return
     */
    boolean isShowJsonData();
}
