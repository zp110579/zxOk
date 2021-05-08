package com.zee.http.netWorkManager;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.cache.CacheBean;
import com.zee.http.cache.CacheMode;
import com.zee.http.request.ZxRequest;

/**
 * 开始连接网络,默认连接方式
 */

final class NetWorkDefault extends AbstractNetWork {
    public NetWorkDefault(ZxRequest baseRequest) {
        super(baseRequest);
        onStartHttp();
    }

    @Override
    protected boolean onHttpSuccess(String data, int responseCode) {
        if (responseCode == 304) {
            //304缓存数据
            onReadCacheData();
            addCacheHeaders(mBaseRequest, mCacheBean);
            if (mCacheBean == null) {
                onError(true, new IllegalArgumentException("服务器响应码304，但是客户端没有缓存！"), true);
            } else {
                HttpHeaders headers = mCacheBean.getResponseHeaders();
                if (data == null || headers == null) {
                    //由于没有序列化等原因,可能导致数据为空
                    onError(true, new IllegalArgumentException("没有获取到缓存,或者缓存已经过期!"), true);
                } else {
                    sendSuccessResultCallback(data, true, true);
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 对每个请求添加默认的请求头，如果有缓存，并返回缓存实体对象
     * Cache-Control: max-age=0                            以秒为单位
     * If-Modified-Since: Mon, 19 Nov 2012 08:38:01 GMT    缓存文件的最后修改时间。
     * If-None-Match: "0693f67a67cc1:0"                    缓存文件的ETag值
     * Cache-Control: no-cache                             不使用缓存
     * Pragma: no-cache                                    不使用缓存
     * Accept-Language: zh-CN,zh;q=0.8                     支持的语言
     * User-Agent:                                         用户代理，它的信息包括硬件平台、系统软件、应用软件和用户个人偏好
     * Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36
     *
     * @param request   请求类
     * @param cacheBean 缓存实体类
     */
    public static void addCacheHeaders(ZxRequest request, CacheBean cacheBean) {
        CacheMode cacheMode = request.getCacheMode();
        //1. 按照标准的 http 协议，添加304相关请求头
        if (cacheBean != null && cacheMode == CacheMode.DEFAULT) {
            HttpHeaders responseHeaders = cacheBean.getResponseHeaders();
            if (responseHeaders != null) {
                String eTag = responseHeaders.get(HttpHeaders.HEAD_KEY_E_TAG);
                if (eTag != null) {
                    request.addHeaders(HttpHeaders.HEAD_KEY_IF_NONE_MATCH, eTag);
                }
                long lastModified = HttpHeaders.getLastModified(responseHeaders.get(HttpHeaders.HEAD_KEY_LAST_MODIFIED));
                if (lastModified > 0) {
                    request.addHeaders(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE, HttpHeaders.formatMillisToGMT(lastModified));
                }
            }
        }
    }
}
