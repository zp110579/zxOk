package com.zee.http.request;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.bean.ProgressRequestBody;
import com.zee.http.bean.UploadFileParams;
import com.zee.http.utils.MyOkHandlerUtils;
import com.zee.http.utils.OkLogger;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传数据或者文件
 */
public class UploadFileRequest extends BaseRequest<UploadFileRequest> {

    private UploadingFileCallBackListener mZRequestListener;
    private UploadFileParams mUploadFileParams;
    private int currentRetryCount = 0;

    public UploadFileRequest(String url) {
        super(url);
    }

    public UploadFileRequest setUploadFileParams(UploadFileParams uploadFileParams) {
        mUploadFileParams = uploadFileParams;
        return this;
    }

    public Request generateRequest() {
        RequestBody requestBody = wrapRequestBody(mUploadFileParams.getRequestBody());

        try {
            mHeaders.putAll(HttpHeaders.HEAD_KEY_CONTENT_LENGTH, String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            OkLogger.e(e);
        }
        Request.Builder requestBuilder = mHeaders.getHeaders();
        return requestBuilder.post(requestBody).url(url).tag(mTag).build();
    }

    public okhttp3.Call generateCall() {
        final Request request = generateRequest();
        return generateCall(request);
    }

    public void execute(UploadingFileCallBackListener listener) {
        //构建请求
        mZRequestListener = listener;
        okhttp3.Call rawCall = generateCall();
        listener.onStart();
        rawCall.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (currentRetryCount < retryCount) {
                    okhttp3.Call newCall = generateCall(call.request());
                    newCall.enqueue(this);
                    currentRetryCount++;
                } else {
                    if (mZRequestListener != null) {
                        MyOkHandlerUtils.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                mZRequestListener.onError(e);
                                mZRequestListener.onEnd();
                            }
                        });
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MyOkHandlerUtils.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String data = response.body().string();
                            mZRequestListener.setJsonObject(data);
                            mZRequestListener.onUploadSuccess(data);
                        } catch (Exception e) {
                            mZRequestListener.onError(e);
                            e.printStackTrace();
                        } finally {
                            mZRequestListener.onEnd();
                        }
                    }
                });
            }
        });
    }

    private RequestBody wrapRequestBody(RequestBody requestBody) {
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody);
        if (mZRequestListener != null) {
            progressRequestBody.setListener(new ProgressRequestBody.Listener() {
                @Override
                public void onRequestProgress(final long bytesWritten, final long contentLength, final long networkSpeed) {
                    MyOkHandlerUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mZRequestListener.onUploadProgress(bytesWritten * 1.0f / contentLength, bytesWritten, contentLength, networkSpeed);
                        }
                    });
                }
            });
        }
        return progressRequestBody;
    }
}
