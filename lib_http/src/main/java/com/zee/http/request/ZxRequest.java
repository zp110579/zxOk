package com.zee.http.request;


import com.google.gson.Gson;
import com.zee.http.MyOk;
import com.zee.http.bean.HttpHeaders;
import com.zee.http.cache.CacheBean;
import com.zee.http.cache.CacheMode;
import com.zee.http.netWorkManager.NetWorFactor;
import com.zee.http.utils.OkLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

@SuppressWarnings(value = {"unchecked", "deprecation"})
public class ZxRequest extends BaseRequest<ZxRequest> {
    /**
     * 默认Post类型
     */
    private String mMethod = "POST";
    private OKHttpParams mParams = new OKHttpParams();
    /**
     * 添加header
     */
    private ICallBackResult mICallBackResult;
    /**
     * 缓存时间:-1 表示永久有效,默认值即为 -1
     */
    private long cacheTime = CacheBean.CACHE_NEVER_EXPIRE;
    /**
     * 缓存key
     */
    private String cacheKey;

    /**
     * 默认是没有缓存
     */
    private CacheMode mCacheMode = CacheMode.NO_USE_CACHE;
    private boolean isJsonParam = false;
    private String postJsonParam = null;

    public ZxRequest(String url) {
        super(url);
    }

    public ZxRequest get() {
        mMethod = "GET";
        return this;
    }

    public ZxRequest post() {
        //默认
        mMethod = "POST";
        return this;
    }

    public ZxRequest patch() {
        this.mMethod = "PATCH";
        return this;
    }

    public ZxRequest get(Map<String, Object> params) {
        mMethod = "GET";
        if (params != null) {
            this.mParams.putParams(params);
        }
        return this;
    }

    public ZxRequest post(Map<String, Object> params) {
        //默认
        mMethod = "POST";
        if (params != null) {
            this.mParams.putParams(params);
        }
        return this;
    }

    public ZxRequest postJson() {
        //默认
        mMethod = "POST";
        isJsonParam = true;
        return this;
    }

    public ZxRequest postJson(Map<String, Object> params) {
        //默认
        mMethod = "POST";
        isJsonParam = true;
        if (params != null) {
            this.mParams.putParams(params);
        }
        return this;
    }

    public ZxRequest postJson(String customParams) {
        //默认
        mMethod = "POST";
        isJsonParam = true;
        postJsonParam = customParams;
        return this;
    }


    public boolean isJsonParam() {
        return isJsonParam;
    }

    public OKHttpParams getParams() {
        return mParams;
    }

    @SuppressWarnings("unchecked")
    public ZxRequest putParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public ZxRequest putParams(HashMap<String, Object> hashMap) {
        if (hashMap != null) {
            mParams.putParams(hashMap);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public ZxRequest cacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheBean.CACHE_NEVER_EXPIRE;
        this.cacheTime = cacheTime;
        return this;
    }

    public ZxRequest setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public String getCacheKey() {
        return cacheKey;
    }


    public ZxRequest setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    @SuppressWarnings("unchecked")
    public void execute(final ICallBackResult listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not NULL");
        }
        mICallBackResult = listener;

        //获得头部公共参数
        HashMap<String, Object> headerHashMap = MyOk.getInstance().getCommonHeader();
        if (headerHashMap.size() > 0) {
            mHeaders.putAll(headerHashMap);
        }

        //获得公共参数
        HashMap<String, Object> paramsHashMap = MyOk.getInstance().getCommonParams();
        if (paramsHashMap.size() > 0) {
            mParams.putParams(paramsHashMap);
        }

        if (cacheKey == null) {
            cacheKey = mParams.createUrlFromParams(url);
        }

        mICallBackResult.onStart();
        new NetWorFactor(this);
    }

    public ICallBackResult getICallBackResult() {
        return mICallBackResult;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public Request generateRequest() {
        RequestBody requestBody = mParams.getRequestBody();
        switch (mMethod) {
            case "GET":
                Request.Builder requestBuilder = mHeaders.getHeaders();
                url = mParams.createUrlFromParams(url);
                return requestBuilder.get().url(url).tag(mTag).build();
            default:
                if (isJsonParam) {
                    if (postJsonParam == null) {
                        requestBody = mParams.getRequestBodyJson();
                    } else {
                        //自己定义的参数
                        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postJsonParam);
                    }
                }
                break;
        }
        try {
            mHeaders.putAll(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            OkLogger.e(e);
        }
        Request.Builder requestBuilder = mHeaders.getHeaders();
        if (mMethod.equals("PATCH")) {
            return requestBuilder.patch(requestBody).url(url).tag(mTag).build();
        }
        //POST
        return requestBuilder.post(requestBody).url(url).tag(mTag).build();
    }

    /**
     * 根据当前的请求参数，生成对应的 Call 任务
     */
    public okhttp3.Call generateCall() {
        final Request request = generateRequest();
        return generateCall(request);
    }
}
