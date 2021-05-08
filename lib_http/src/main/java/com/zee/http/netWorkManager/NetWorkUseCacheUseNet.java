package com.zee.http.netWorkManager;

import com.zee.http.request.ZxRequest;
import com.zee.http.bean.HttpHeaders;

/**
 * 先读取缓存，然后继续网络访问
 */

final class NetWorkUseCacheUseNet extends AbstractNetWork {

    public NetWorkUseCacheUseNet(ZxRequest request) {
        super(request);
        onReadCacheData();
        //先使用缓存，不管是否存在，仍然请求网络
        if (mCacheBean != null && !mCacheBean.isExpired()) {
            String data = mCacheBean.getData();
            HttpHeaders headers = mCacheBean.getResponseHeaders();
            if (data == null || headers == null) {
            } else {
                sendSuccessResultCallback(data, true, false);
            }
        }
        onStartHttp();
    }

}
