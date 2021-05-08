package com.zee.http.cache;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.request.ZxRequest;

import java.io.Serializable;

public class CacheBean implements Serializable {

    private static final long serialVersionUID = -4337711009801627866L;
    public static final long CACHE_NEVER_EXPIRE = -1;        //缓存永不过期

    private long id;
    private String key;
    private String data;
    private long curTime;

    /**
     * 该缓存是否过期
     */
    private boolean isExpired;
    private HttpHeaders mHttpHeaders;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpHeaders getResponseHeaders() {
        return mHttpHeaders;
    }

    public void setResponseHeaders(HttpHeaders headers) {
        mHttpHeaders = headers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public boolean isExpired() {
        return isExpired;
    }

    /**
     * @return 检测是否过期
     */
    public void checkExpire(ZxRequest request) {
        CacheMode cacheMode = request.getCacheMode();
        long cacheTime = request.getCacheTime();
        //304的默认缓存模式,设置缓存时间无效,需要依靠服务端的响应头控制
        boolean isTrue;
        long baseTime = System.currentTimeMillis();//基准时间,小于当前时间视为过期
        if (cacheTime == CACHE_NEVER_EXPIRE) {
            isTrue = false;
        } else if (cacheMode == CacheMode.DEFAULT) {
            isTrue = getCurTime() < baseTime;
        } else {
            isTrue = curTime + cacheTime < baseTime;
        }
        isExpired = isTrue;
    }

    @Override
    public String toString() {
        return "CacheBean{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", responseHeaders=" + mHttpHeaders +
                ", data=" + data +
                ", curTime=" + curTime +
                '}';
    }
}