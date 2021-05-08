package com.zee.http.request;

import android.support.annotation.WorkerThread;

/**
 * 文件上传监听器
 */
public abstract class UploadingFileCallBackListener extends ICallBackResult {


    /**
     * 上传文件成功
     */
    @WorkerThread
    public abstract void onUploadSuccess(String data);

    /**
     * 上传文件失败
     */
    @WorkerThread
    public void onError(Exception e) {
    }

    /**
     * @param progress     当前上传的进度
     * @param currentSize  当前上传的字节数
     * @param totalSize    总共需要上传的字节数
     * @param networkSpeed 当前上传的速度 字节/秒
     */
    public void onUploadProgress(float progress, long currentSize, long totalSize, long networkSpeed) {
    }

}
