package com.zee.http;

import android.app.Application;

import com.zee.http.request.DownloadFileRequest;
import com.zee.http.request.UploadFileRequest;
import com.zee.http.request.ZxRequest;
import com.zee.http.utils.HttpLoggingInterceptor;
import com.zee.http.utils.MyOkHandlerUtils;
import com.zee.http.utils.OkLogger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public final class MyOk {

    /**
     * 目标：获取数据,文件下载，上传，数据缓存,apk安装
     * 默认的超时时间
     */
    private static final int DEFAULT_MILLISECONDS = 30000;

    /**
     * ok请求的客户端
     */
    private OkHttpClient.Builder okHttpClientBuilder;
    private OkHttpClient okHttpClient;
    /**
     * 公共的头部
     */
    private HashMap<String, Object> mCommonHeader = new HashMap<>();

    /**
     * 公共的参数
     */
    private HashMap<String, Object> mCommonParams = new HashMap<>();

    MyOk() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(UnSafeHostnameVerifier);
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    public static MyOk getInstance() {
        return ZxOkHolder.holder;
    }

    private static class ZxOkHolder {
        private static MyOk holder = new MyOk();
    }

    public static void init(Application paApplication) {
        MyOkHandlerUtils.setApplication(paApplication);
    }

    /**
     * 添加公共头
     *
     * @param header
     */
    public void addCommonHeader(HashMap<String, Object> header) {
        if (header != null) {
            mCommonHeader.putAll(header);
        }
    }

    /**
     * 添加公共的参数
     */
    public void putCommonParams(HashMap<String, Object> params) {
        if (params != null) {
            mCommonParams.putAll(params);
        }
    }

    public HashMap<String, Object> getCommonParams() {
        return mCommonParams;
    }

    public HashMap<String, Object> getCommonHeader() {
        return mCommonHeader;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = okHttpClientBuilder.build();
        }
        return okHttpClient;
    }

    /**
     * 调试模式,第三个参数表示所有catch住的log是否需要打印
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    public MyOk debug(String tag, Level level, boolean isPrintException) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(tag);
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(level);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        OkLogger.debug(isPrintException);
        return this;
    }

    public static ZxRequest load(String url) {
        return new ZxRequest(url);
    }

    /**
     * 下载文件
     */
    public static DownloadFileRequest downloadFile(String url) {
        return new DownloadFileRequest(url);
    }

    /**
     * 上传文件
     */
    public static UploadFileRequest uploadFile(String url) {
        return new UploadFileRequest(url);
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    private static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

}
