# XXBring
一款基于okhttp实现的网络请求响应框架,支持get/post请求/json解析/流数据处理/文件下载等.

如何集成到项目？

1.将JitPack存储库添加到项目根目录的build.gradle中

allprojects {
        repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
}


2.添加依赖到moudle
dependencies {
        implementation 'com.github.Liera-tech:XXBring:0.1.0'
}


API使用：
//配置请求,这个可以放到一个统一的地方

XXBring mXXBring = new XXBring.Builder()

        //是否打印日志,默认debug版本打印日志,非debug版本不打印
        
        .setDebug(true)
        
        //设置自己的请求网络框架,不设置默认是okhttp请求
        
        //.setRequestManager()
        
        //是否打印全局请求json数据,默认是debug版本打印,非debug版本不打印
        
        //.isShowJsonData(false)
        
        .build();


 * 回调为inputStream类型的请求
 *
 * @param req      请求体
 * @param callback inputStream回调
 
mXXBring.request(IXXBringRequest req, XXBringInputStreamCallback callback);



 * 回调为byteArray类型的请求
 *
 * @param req      请求体
 * @param callback byteArray回调
 
mXXBring.request(IXXBringRequest req, XXBringByteArrayCallback callback);



 * 回调为text类型的请求
 *
 * @param req      请求体
 * @param callback text回调
 
mXXBring.request(IXXBringRequest req, XXBringTextCallback callback);



 * 回调为jsonObject类型的请求
 *
 * @param req       请求体
 * @param respClass object字节码
 * @param callback  jsonObject回调
 
mXXBring.request(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringJsonObjectCallback callback);



 * 回调为jsonArray类型的请求
 *
 * @param req       请求体
 * @param respClass object字节码
 * @param callback  jsonArray回调
 
mXXBring.request(IXXBringRequest req, Class<? extends IXXBringResponse> respClass, XXBringJsonArrayCallback callback);



请求支持以下情况:

XXBringFileUploadRequest(文件上传请求)
XXBringGetRequest(Get请求)
XXBringPostBodyRequest(post请求发送Json数据)
XXBringPostParmeterRequest(post请求携带参数)


响应回调支持如下类型:

XXBringByteArrayCallback(响应结果为byte数组)
XXBringInputStreamCallback(响应结果为InputStream数据流)
XXBringJsonArrayCallback(响应结果为json数组)
XXBringJsonObjectCallback(响应结果为json对象)
XXBringTextCallback(响应结果为纯文本)


请求中可通过配置请求设置以下:

 * 请求地址
 * @return
 
String getUrl();



 * 为请求设置一个tag
 * @return
 
Object getRequestTag();



 * 请求方式
 * @return
 
HttpMethod getMethod();



 * 请求头
 * @return
 
Map<String, Object> getHeaders();



 * 请求参数
 * @return
 
Map<String, Object> getParameters();



 * 响应数据成功是否在子线程
 *
 * true 响应成功内容在子线程
 * false 响应成功内容在主线程
 *
 * @return
 
boolean isResponseSuccessThread();



 * 响应数据失败是否在子线程
 *
 * true 响应失败在子线程
 * false 响应失败在主线程
 *
 * @return
 
boolean isResponseFailThread();



 * 响应数据为json格式时有效
 * 是否查看json字符串数据(效率稍低)
 * @return
 
boolean isShowJsonData();
