package com.zee.http.request;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OKHttpParams {
    public LinkedHashMap<String, String> mParamsHashMap = new LinkedHashMap<>();

    public void put(String key, Object value) {
        String tempValue = "";

        if (value instanceof Integer) {
            tempValue = String.valueOf(value);
        } else if (value instanceof String) {
            tempValue = value.toString();
        } else if (value instanceof Float) {
            tempValue = String.valueOf(value);
        } else if (value instanceof Double) {
            tempValue = String.valueOf(value);
        } else if (value instanceof Boolean) {
            tempValue = String.valueOf(value);
        } else if (value instanceof Long) {
            tempValue = String.valueOf(value);
        } else if (value instanceof Character) {
            tempValue = String.valueOf(value);
        } else {
            throw new IllegalStateException(value.getClass() + " is no support, only supprot int,String,float,double,long,boolean,Chat类型，其他类型暂时不支持");
        }
        putParams(key, tempValue);
    }

    private void putParams(String key, String value) {
        if (key != null && value != null) {
            mParamsHashMap.put(key, value);
        }
    }

    public void putParams(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                putParams(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }

    public RequestBody getRequestBody() {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key : mParamsHashMap.keySet()) {
            String urlValues = mParamsHashMap.get(key);
            bodyBuilder.add(key, urlValues);

        }
        return bodyBuilder.build();
    }


    public RequestBody getRequestBodyJson() {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key : mParamsHashMap.keySet()) {
            String urlValues = mParamsHashMap.get(key);
            bodyBuilder.add(key, urlValues);
        }
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(mParamsHashMap));
    }

    /**
     * get和数据库里缓存key用
     *
     * @param url 请求地址
     * @return
     */
    public String createUrlFromParams(String url) {
        try {
            Map<String, String> params = mParamsHashMap;
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            for (Map.Entry<String, String> urlParams : params.entrySet()) {
                String urlValues = urlParams.getValue();
                //对参数进行 utf-8 编码,防止头信息传中文
                String urlValue = URLEncoder.encode(urlValues, "UTF-8");
                sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");

            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            Log.e("MyOK", e.toString());
        }
        return url;
    }
}
