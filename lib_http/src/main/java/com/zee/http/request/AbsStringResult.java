package com.zee.http.request;

import android.support.annotation.WorkerThread;
import android.util.Log;

/**
 * created by zee on 2021/3/30.
 */
public abstract class AbsStringResult extends ICallBackResult {

    /**
     * @param data
     * @return
     */
    public abstract void onSuccessAsyncThread(String data);


    /**
     * 请求数据或者是响应失败
     */
    @WorkerThread
    public void onError(Exception e) {
        StringBuilder builder = new StringBuilder(e.getMessage());
        if (mJsonObject != null) {
            builder.append("\n");
            builder.append(mJsonObject.toString());
        }
        Log.e("ZRequestString", "进入onError()->" + builder.toString());
    }


}
