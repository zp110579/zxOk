package com.zee.http.socket;

import okhttp3.Response;
import okio.ByteString;

public abstract class MyWebSocketListener {

    public void onSocketOpen(Response response) {
    }

    public void onSocketMessage(String text) {
    }

    public void onSocketMessage(ByteString bytes) {
    }

    public void onSocketReconnect() {
    }

    public void onSocketClosing(int code, String reason) {
    }

    public void onSocketClosed(int code, String reason) {
    }

    public void onSocketFailure(Throwable t, Response response) {
    }
}
