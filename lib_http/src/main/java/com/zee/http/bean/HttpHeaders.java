package com.zee.http.bean;

import android.text.TextUtils;

import com.zee.http.utils.OkLogger;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Headers;
import okhttp3.Request;

public class HttpHeaders implements Serializable {
    public static final String HEAD_KEY_E_TAG = "ETag";
    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";
    public static final String FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'";
    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";
    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";
    public static final String HEAD_KEY_DATE = "Date";
    public static final String HEAD_KEY_EXPIRES = "Expires";
    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";
    public static final String HEAD_KEY_PRAGMA = "Pragma";

    public LinkedHashMap<String, String> headersMap = new LinkedHashMap<>();


    public String get(String key) {
        return headersMap.get(key);
    }

    public void putAll(String key, String value) {
        if (key != null && value != null) {
            headersMap.put(key, value);
        }
    }

    public void putAll(HashMap<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            headersMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    public static long getLastModified(String lastModified) {
        try {
            return parseGMTToMillis(lastModified);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static long getDate(String gmtTime) {
        try {
            return parseGMTToMillis(gmtTime);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static long getExpiration(String expiresTime) {
        try {
            return parseGMTToMillis(expiresTime);
        } catch (ParseException e) {
            return -1;
        }
    }

    public static String getCacheControl(String cacheControl, String pragma) {
        // first http1.1, second http1.0
        if (cacheControl != null) {
            return cacheControl;
        } else if (pragma != null) {
            return pragma;
        } else {
            return null;
        }
    }

    public static long parseGMTToMillis(String gmtTime) throws ParseException {
        if (TextUtils.isEmpty(gmtTime)) {
            return 0;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        formatter.setTimeZone(GMT_TIME_ZONE);
        Date date = formatter.parse(gmtTime);
        return date.getTime();
    }

    public static String formatMillisToGMT(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
        return simpleDateFormat.format(date);
    }

    /**
     * 通用的拼接请求头
     */
    public Request.Builder getHeaders() {
        Request.Builder requestBuilder = new Request.Builder();
        if (headersMap.isEmpty()) return requestBuilder;
        Headers.Builder headerBuilder = new Headers.Builder();
        try {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            OkLogger.e(e);
        }
        requestBuilder.headers(headerBuilder.build());
        return requestBuilder;
    }
}
