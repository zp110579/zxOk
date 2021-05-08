package com.zee.http.netWorkManager;

import com.zee.http.request.ZxRequest;
import com.zee.http.bean.HttpHeaders;

/**
 * 如果没有缓存就读取网络
 */
final class NetWorkNoCaseUseNet extends AbstractNetWork {

    public NetWorkNoCaseUseNet(ZxRequest request) {
        super(request);
        onReadCacheData();
        if (mCacheBean != null && !mCacheBean.isExpired()) {
            String data = mCacheBean.getData();
            HttpHeaders headers = mCacheBean.getResponseHeaders();
            if (data == null || headers == null) {
                //缓存失效过期之类，那就读取网络
                onStartHttp();
            } else {
                sendSuccessResultCallback(data, true, true);
            }
        } else {
            onStartHttp();
        }
    }
}
