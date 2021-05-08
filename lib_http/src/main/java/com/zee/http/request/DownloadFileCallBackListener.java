package com.zee.http.request;


import android.support.annotation.UiThread;

import java.io.File;

/**
 * 下载文件监听器
 */
public abstract class DownloadFileCallBackListener implements NetWorkListener {
    /**
     * 保存文件的路径
     */
    private String saveFilePath = "Download";

    public DownloadFileCallBackListener() {
    }

    public DownloadFileCallBackListener(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    /**
     * 开始请求
     */
    @Override
    @UiThread
    public void onStart() {

    }

    /**
     * 请求结束
     */
    @Override
    @UiThread
    public void onEnd() {

    }

    /**
     * 下载文件成功
     *
     * @param File 下载成功后的文件
     */
    public abstract void onDownloadSuccess(File File);


    /**
     * 下载进度的数据
     *
     * @param progress     当前下载的进度:10%
     * @param currentSize  当前下载的字节数:10
     * @param totalSize    总共需要下载的字节数:100
     * @param networkSpeed 当前下载的速度 字节/秒
     */
    public abstract void onDownloadProgress(float progress, long currentSize, long totalSize, long networkSpeed);

}
