package com.zee.http.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class MyOkLogInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName("UTF-8");
    private String TAG = "StoreInterceptor";
    private Logger logger;

    public MyOkLogInterceptor() {
        logger = Logger.getLogger("Http");
    }

    public MyOkLogInterceptor(String tag) {
        logger = Logger.getLogger(tag);
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        long startNs = System.nanoTime();
        Request request = chain.request();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        response = decrypt(response);
        String requestBody = printRequestBody(request.body());
        String responseBody = printResponseBody(response.body());
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = request.method() + ' ' + request.url() + ' ' + protocol;

        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        printMessage(request, tookMs, requestBody, responseBody, requestStartMessage, clone);

        return response;
    }

    private synchronized void printMessage(Request request, long tookMs, String requestBody, String responseBody, String requestStartMessage, Response clone) {
        final int fixLength = 3 * 1024;
        StringBuilder logSBuilder = new StringBuilder("-Http(Message)-");
        logSBuilder.append("\r\n");
        logSBuilder.append("------------ Http Begin --------------\r\n");
        logSBuilder.append("0. " + requestStartMessage + "\r\n");
        logSBuilder.append("1. headers = " + request.headers().toString());
        logSBuilder.append("2. requestBody = " + requestBody + "\r\n");

        for (int i = 0; i < responseBody.length(); i += fixLength) {
            if (i + fixLength < responseBody.length()) {
                logSBuilder.append("3. responseBody = " + responseBody.substring(i, i + fixLength));
            } else {
                logSBuilder.append("3. responseBody = " + responseBody.substring(i));
            }
        }
        logSBuilder.append("\r\n");
        logSBuilder.append("4. " + clone.code() + ' ' + clone.message() + " (" + tookMs + "ms）");
        logSBuilder.append("\r\n");
        logSBuilder.append("------------ Http End --------------\r\n");
        logSBuilder.append("\r\n");
        log(logSBuilder.toString());
    }

    private Response decrypt(Response response) throws IOException {
        if (response.isSuccessful()) {
            //the response data
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String string = buffer.clone().readString(charset);
            ResponseBody responseBody = ResponseBody.create(contentType, string);
            response = response.newBuilder().body(responseBody).build();
        }
        return response;
    }

    private String printRequestBody(RequestBody requestBody) throws IOException {
        String body = null;
        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            body = buffer.readString(charset);
        }
        return body;
    }

    private String printResponseBody(ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return "";
        }
        BufferedSource source = responseBody.source();
        // Buffer the entire body.
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        return buffer.clone().readString(charset);
    }

    public void log(String message) {
        logger.log(java.util.logging.Level.INFO, message);
    }
}
