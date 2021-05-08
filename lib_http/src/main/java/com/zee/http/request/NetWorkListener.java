package com.zee.http.request;

interface NetWorkListener {

    /**
     * 连接开始
     */
    void onStart();


    /**
     * 连接错误
     *
     * @param e
     */
    void onError(Exception e);

    /**
     * 连接结束
     */
    void onEnd();
}
