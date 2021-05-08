package com.zee.http.bean;


import com.zee.http.cache.CacheMode;

import java.util.HashMap;

/**
 * @author Administrator
 * @date 2017/10/9 0009
 */
public class DrHttpParams {
    private Class<?> mResultClass;
    private boolean isListType = false;
    private long connectTimeout = 30L;
    private boolean isPrintCallBackResult;
    private CacheMode mCacheMode = null;
    private HashMap<String, String> params = new HashMap<>();

    public void put(String key, String value) {
        params.put(key, value);
    }

    public void put(String key, int value) {
        params.put(key, value + "");
    }

    public void put(String key, double value) {
        params.put(key, value + "");
    }

    public void setResultClass(Class<?> zlcass) {
        setResultClass(zlcass, false);
    }

    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /**
     * 设置缓存方式及连接时长
     *
     * @param cacheMode
     * @param secondTime
     */
    public void setCacheType(CacheMode cacheMode, int secondTime) {
        mCacheMode = cacheMode;
        connectTimeout = secondTime * 1000;
    }

    public void setResultClass(Class<?> zlcass, boolean isListType) {
        this.mResultClass = zlcass;
        this.isListType = isListType;
    }

    public Class<?> getResultClass() {
        return mResultClass;
    }

    public boolean isListType() {
        return isListType;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public boolean isPrintCallBackResult() {
        return isPrintCallBackResult;
    }

    public void setPrintCallBackResult() {
        isPrintCallBackResult = true;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public String toString() {
        return "DrHttpParams{" +
                "\n, ResultClass=" + mResultClass +
                "\n, connectTimeout=" + connectTimeout +
                "\n, isPrintCallBackResult=" + isPrintCallBackResult +
                "\n, params=" + params +
                '}';
    }
}
