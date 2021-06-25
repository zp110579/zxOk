package com.zee.http.socket;

import android.os.Looper;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * created by zee on 2021/6/10.
 */
 class OKWebSocketListener extends WebSocketListener {
    private MyWebSocket mMyWebSocket;

    OKWebSocketListener(MyWebSocket socket) {
        this.mMyWebSocket = socket;
    }

    @Override
    public void onOpen(WebSocket webSocket, final Response response) {
        mMyWebSocket.mWebSocket = webSocket;
        mMyWebSocket.setStatus(mMyWebSocket.CONNECTED);
        mMyWebSocket.connected();
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketOpen(response);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketOpen(response);
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, final ByteString bytes) {
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketMessage(bytes);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketMessage(bytes);
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, final String text) {
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketMessage(text);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketMessage(text);
            }
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, final int code, final String reason) {
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketClosing(code, reason);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketClosing(code, reason);
            }
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, final int code, final String reason) {
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketClosed(code, reason);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketClosed(code, reason);
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
        mMyWebSocket.tryReconnect();
        if (mMyWebSocket.mMyWebSocketListener != null) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                mMyWebSocket.wsMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMyWebSocket.mMyWebSocketListener.onSocketFailure(t, response);
                    }
                });
            } else {
                mMyWebSocket.mMyWebSocketListener.onSocketFailure(t, response);
            }
        }
    }

}
