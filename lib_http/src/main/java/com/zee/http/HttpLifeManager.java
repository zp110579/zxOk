package com.zee.http;

public class HttpLifeManager {
    private HttpLifeListener mHttpLifeListener;

    public void setHttpLifeListener(HttpLifeListener httpLifeListener) {
        mHttpLifeListener = httpLifeListener;
    }

    public HttpLifeListener getHttpLifeListener() {
        return mHttpLifeListener;
    }

    public void onDestroy() {
        if (mHttpLifeListener != null) {
            mHttpLifeListener.onDestroy();
        }
    }
}
