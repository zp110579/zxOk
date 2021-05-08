package com.zee.http.netWorkManager;

import com.zee.http.request.ZxRequest;
import com.zee.http.cache.CacheMode;

public final class NetWorFactor {

    public NetWorFactor(ZxRequest request) {
        CacheMode mCacheMode = request.getCacheMode();
        switch (mCacheMode) {
            case NO_CACHE_USE_NET:
                new NetWorkNoCaseUseNet(request);
                break;
            case USE_CACHE_USE_NET:
                new NetWorkUseCacheUseNet(request);
                break;
            case NO_USE_CACHE:
                new NetWorkNoUseCache(request);
                break;
            case NET_FAIL_USE_CACHE:
                new NetWorkNetFailUseCache(request);
                break;
            default://默认
                new NetWorkDefault(request);
                break;
        }
    }
}
