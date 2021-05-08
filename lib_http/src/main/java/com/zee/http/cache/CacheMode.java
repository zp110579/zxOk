package com.zee.http.cache;

public enum CacheMode {
    DEFAULT("默认"),

    NO_USE_CACHE("不使用缓存"),

    NET_FAIL_USE_CACHE("网络失败使用缓存"),

    NO_CACHE_USE_NET("没有缓存再请求网络"),

    USE_CACHE_USE_NET("先使用缓存在请求网络");

    String explain;

    CacheMode(String explain) {
        this.explain = explain;
    }

    public String getExplain() {
        return explain;
    }
}

