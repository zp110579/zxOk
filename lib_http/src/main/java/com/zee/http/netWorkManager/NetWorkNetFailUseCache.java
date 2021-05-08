package com.zee.http.netWorkManager;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.request.ZxRequest;
import com.zee.http.utils.MyOkHandlerUtils;

/**
 * 没有网络使用缓存
 */
final class NetWorkNetFailUseCache extends AbstractNetWork {

    public NetWorkNetFailUseCache(ZxRequest request) {
        super(request);
        onStartHttp();
    }

    @Override
    protected void onHttpFail(Exception ex) {
        onReadCacheData();

        if (mCacheBean != null && !mCacheBean.isExpired()) {
            String data = mCacheBean.getData();
            HttpHeaders headers = mCacheBean.getResponseHeaders();
            if (data == null || headers == null) {
                onError(true, new IllegalArgumentException("没有获取到缓存,或者缓存已经过期!"), true);
            } else {
                onSuccess(true, data, true);
            }
        } else {
            onError(false, new IllegalArgumentException("没有获取到缓存,或者缓存已经过期!"), true);
        }
    }
}
