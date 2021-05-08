package com.zee.http.netWorkManager;

import com.zee.http.cache.CacheBean;
import com.zee.http.cache.CacheManager;
import com.zee.http.cache.SaveCacheData;
import com.zee.http.request.AbsStringResult;
import com.zee.http.request.ICallBackResult;
import com.zee.http.request.ZCacheStringResult;
import com.zee.http.request.ZStringResult;
import com.zee.http.request.ZxRequest;
import com.zee.http.utils.MyOkHandlerUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

abstract class AbstractNetWork {
    CacheBean mCacheBean;
    ZxRequest mBaseRequest;
    ICallBackResult mICallBackResult;
    private int currentRetryCount = 0;

    public AbstractNetWork(ZxRequest baseRequest) {
        this.mBaseRequest = baseRequest;
        mICallBackResult = baseRequest.getICallBackResult();
    }

    /**
     * 读取缓存
     */
    protected void onReadCacheData() {
        try {
            mCacheBean = CacheManager.INSTANCE.get(mBaseRequest.getCacheKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCacheBean != null) {
            //判断缓存是否已经过期
            mCacheBean.checkExpire(mBaseRequest);
        }
    }

    /**
     * 网络获取数据失败
     *
     * @param ex
     */
    protected void onHttpFail(Exception ex) {
        onError(false, ex, true);
    }

    /**
     * 网络获取数据成功
     */
    protected boolean onHttpSuccess(String data, int responseCode) {
        return true;
    }

    /**
     * @param data        获得数据
     * @param isFromCache 是否来自缓存
     * @param isNextEnd   获得数据成功后，下一步进入end方法
     */
    void sendSuccessResultCallback(final String data, final boolean isFromCache, boolean isNextEnd) {
        boolean isIntercept = false;
        if (mICallBackResult != null) {
            onSuccess(isFromCache, data, isNextEnd);
        }
    }

    /**
     * 开始网络请求
     */
    public void onStartHttp() {
        currentRetryCount = 0;
        //构建请求
        okhttp3.Call rawCall = mBaseRequest.generateCall();
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < mBaseRequest.getRetryCount()) {
                    currentRetryCount++;
                    okhttp3.Call newCall = mBaseRequest.generateCall(call.request());
                    newCall.enqueue(this);
                } else {
                    //请求失败，一般为url地址错误，网络错误等,并且过滤用户主动取消的网络请求
                    if (!call.isCanceled()) {
                        onHttpFail(e);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                int responseCode = response.code();
                //响应失败，一般为服务器内部错误，或者找不到页面等
                if (responseCode == 404 || responseCode >= 500) {
                    onHttpFail(new IllegalStateException("服务器数据异常!"));
                    return;
                }
                try {
                    String data = response.body().string();
                    if (onHttpSuccess(data, responseCode)) {
                        //网络请求成功，保存缓存数据
                        new SaveCacheData(mBaseRequest).saveCache(response.headers(), data);
                        //网络请求成功回调
                        sendSuccessResultCallback(data, false, true);
                    }
                } catch (Exception e) {
                    //一般为服务器响应成功，但是数据解析错误
                    onHttpFail(e);
                }
            }
        });
    }

    public void onError(boolean isCache, Exception e, boolean isNextEnd) {
        StringBuilder builder=new StringBuilder(mBaseRequest.getUrl());
        builder.append("\n");
        builder.append(e.getMessage());
        Exception exception=new Exception(builder.toString());
        MyOkHandlerUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mICallBackResult instanceof AbsStringResult) {

                    mBaseRequest.getUrl();
                    ((AbsStringResult) mICallBackResult).onError(exception);
                } else if (mICallBackResult instanceof ZCacheStringResult) {
                    ((ZCacheStringResult) mICallBackResult).onError(isCache, exception);
                }
                if (isNextEnd) {
                    mICallBackResult.onEnd();
                }
            }
        });
    }

    public void onSuccess(boolean isCache, String data, boolean isNextEnd) {
        if (mICallBackResult instanceof AbsStringResult) {
            try {
                mICallBackResult.setJsonObject(data);
            } catch (JSONException e) {
                ((AbsStringResult) mICallBackResult).onError(e);
            }
            ((AbsStringResult) mICallBackResult).onSuccessAsyncThread(data);
        } else if (mICallBackResult instanceof ZCacheStringResult) {
            try {
                mICallBackResult.setJsonObject(data);
            } catch (JSONException e) {
                ((ZCacheStringResult) mICallBackResult).onError(isCache, e);
            }
            ((ZCacheStringResult) mICallBackResult).onSuccessAsyncThread(data, isCache);
        }
        if (isNextEnd) {
            MyOkHandlerUtils.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    mICallBackResult.onEnd();
                }
            });
        }
    }
}
