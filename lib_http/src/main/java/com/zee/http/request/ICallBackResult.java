package com.zee.http.request;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.zee.http.utils.MyOkHandlerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class ICallBackResult {
    JSONObject mJsonObject;

    /**
     * 开始请求
     */
    @UiThread
    public void onStart() {

    }

    /**
     * 请求结束
     */
    @UiThread
    public void onEnd() {
    }

    public void setJsonObject(String data) throws JSONException {
        mJsonObject = new JSONObject(data);
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    protected final void runOnUiThread(Runnable action) {
        MyOkHandlerUtils.runOnMainThread(action);
    }


}
