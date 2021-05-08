package com.zee.http.netWorkManager;

import com.zee.http.request.ZxRequest;

/**
 * 没有缓存请求网络
 */
final class NetWorkNoUseCache extends AbstractNetWork {

    public NetWorkNoUseCache(ZxRequest request) {
        super(request);
        onStartHttp();
    }

    @Override
    protected boolean onHttpSuccess(String data, int responseCode) {
        sendSuccessResultCallback(data, false, true);
        return false;
    }
}
