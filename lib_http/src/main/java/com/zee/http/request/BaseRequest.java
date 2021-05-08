package com.zee.http.request;

import com.zee.http.MyOk;
import com.zee.http.bean.HttpHeaders;
import com.zee.http.utils.HttpLoggingInterceptor;
import com.zee.http.utils.MyOkLogInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@SuppressWarnings("unchecked")
abstract class BaseRequest<T extends BaseRequest> {
    String url;
    private long readTimeOut;
    private long writeTimeOut;
    private long connectTimeout;
    int retryCount;

    Object mTag;
    HttpHeaders mHeaders = new HttpHeaders();
    private List<Interceptor> interceptors = new ArrayList<>();
    private boolean isShowLog = false;
    private String logTag = "http";

    BaseRequest(String url) {
        this.url = url;
    }

    public T tag(Object tag) {
        this.mTag = tag;
        return (T) this;
    }


    public T setReadTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (T) this;
    }


    public T setWriteTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (T) this;
    }

    /**
     * 连接时间
     * @param connectTimeout
     * @return
     */
    public T setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return (T) this;
    }

    /**
     * 超时重试次数
     * @param retryCount
     * @return
     */
    public T setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return (T) this;
    }

    public T showLog(boolean isShowLog) {
        this.isShowLog = isShowLog;
        return (T) this;
    }

    public T showLog(boolean isShowLog, String tag) {
        this.isShowLog = isShowLog;
        logTag = tag;
        return (T) this;
    }

    public T addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        return (T) this;
    }

    public T addHeaders(HashMap<String, Object> hashMap) {
        if (hashMap != null) {
            mHeaders.putAll(hashMap);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addHeaders(String key, Object value) {
        mHeaders.putAll(key, String.valueOf(value));
        return (T)this;
    }

    public String getUrl() {
        return url;
    }

    public okhttp3.Call generateCall(Request request) {
        okhttp3.Call call ;
        if (isShowLog) {
            addInterceptor(new MyOkLogInterceptor(logTag));
        }
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && interceptors.size() == 0) {
            call = MyOk.getInstance().getOkHttpClient().newCall(request);
        } else {
            OkHttpClient.Builder newClientBuilder = MyOk.getInstance().getOkHttpClient().newBuilder();
            if (readTimeOut > 0) {
                newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            }
            if (writeTimeOut > 0) {
                newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            }
            if (connectTimeout > 0) {
                newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            }
            if (interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {
                    newClientBuilder.addInterceptor(interceptor);
                }
            }
            call = newClientBuilder.build().newCall(request);
        }

        return call;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
