package com.zee.http.request;


import android.support.annotation.WorkerThread;
import android.util.Log;


public abstract class ZCacheStringResult extends ICallBackResult {


    /**
     *
     * @param data
     * @param isFromCache
     * @return
     */
    public abstract void onSuccessAsyncThread(String data, boolean isFromCache);

    /**
     * 请求数据或者是响应失败
     *
     * @param isCache 是否是缓存数据
     */
    @WorkerThread
    public void onError(boolean isCache, Exception e) {
        Log.e("ZRequestString", isCache + "进入onError()->" + Thread.currentThread().getName());
    }
}
