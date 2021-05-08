package com.zee.http.cache;

import android.text.TextUtils;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.request.ZxRequest;
import com.zee.http.utils.OkLogger;

import java.util.Locale;
import java.util.StringTokenizer;

import okhttp3.Headers;

/**
 * 获得网络数据后缓存到数据库中
 */

public class SaveCacheData {
    private ZxRequest baseRequest;

    public SaveCacheData(ZxRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     *
     * @param headers 响应头
     */
    public void saveCache(Headers headers, String data) {
        CacheBean cache = createCacheEntity(headers, data, baseRequest.getCacheMode(), baseRequest.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            CacheManager.INSTANCE.remove(baseRequest.getCacheKey());
        } else {
            //缓存命中，更新缓存
            CacheManager.INSTANCE.replace(baseRequest.getCacheKey(), cache);
        }
    }


    /**
     * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
     * Cache-Control: public                             响应被缓存，并且在多用户间共享
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
     * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间
     * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间）
     * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间
     * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值
     * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
     *
     * @param responseHeaders 返回数据中的响应头
     * @param data            解析出来的数据
     * @param cacheMode       缓存的模式
     * @param cacheKey        缓存的key
     * @return 缓存的实体类
     */
    public  CacheBean createCacheEntity(Headers responseHeaders, String data, CacheMode cacheMode, String cacheKey) {

        long localExpire = 0;   // 缓存相对于本地的到期时间

        if (cacheMode == CacheMode.DEFAULT) {
            long date = HttpHeaders.getDate(responseHeaders.get(HttpHeaders.HEAD_KEY_DATE));
            long expires = HttpHeaders.getExpiration(responseHeaders.get(HttpHeaders.HEAD_KEY_EXPIRES));
            String cacheControl = HttpHeaders.getCacheControl(responseHeaders.get(HttpHeaders.HEAD_KEY_CACHE_CONTROL), responseHeaders.get(HttpHeaders.HEAD_KEY_PRAGMA));

            //没有缓存头控制，不需要缓存
            if (TextUtils.isEmpty(cacheControl) && expires <= 0) return null;

            long maxAge = 0;
            if (!TextUtils.isEmpty(cacheControl)) {
                StringTokenizer tokens = new StringTokenizer(cacheControl, ",");
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
                    if (token.equals("no-cache") || token.equals("no-store")) {
                        //服务器指定不缓存
                        return null;
                    } else if (token.startsWith("max-age=")) {
                        try {
                            //获取最大缓存时间
                            maxAge = Long.parseLong(token.substring(8));
                            //服务器缓存设置立马过期，不缓存
                            if (maxAge <= 0) return null;
                        } catch (Exception e) {
                            OkLogger.e(e);
                        }
                    }
                }
            }

            //获取基准缓存时间，优先使用response中的date头，如果没有就使用本地时间
            long now = System.currentTimeMillis();
            if (date > 0) now = date;

                if (maxAge > 0) {
                // Http1.1 优先验证 Cache-Control 头
                localExpire = now + maxAge * 1000;
            } else if (expires >= 0) {
                // Http1.0 验证 Expires 头
                localExpire = expires;
            }
        } else {
            localExpire = System.currentTimeMillis();
        }

        //将response中所有的头存入 HttpHeaders，原因是写入数据库的对象需要实现序列化，而ok默认的Header没有序列化
        HttpHeaders headers = new HttpHeaders();
        for (String headerName : responseHeaders.names()) {
            headers.putAll(headerName, responseHeaders.get(headerName));
        }

        //构建缓存实体对象
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(cacheKey);
        cacheBean.setData(data);
        cacheBean.setCurTime(localExpire);
        cacheBean.setResponseHeaders(headers);
        return cacheBean;
    }
}
