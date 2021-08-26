package com.zee.http.socket;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * created by zee on 2021/6/10.
 */
public class MyWebSocket {
    final static int CONNECTED = 1;
    final static int CONNECTING = 0;
    final static int RECONNECT = 2;
    final static int DISCONNECTED = -1;

    final static int NORMAL_CLOSE = 1000;
    final static int ABNORMAL_CLOSE = 1001;

    final static int RECONNECT_INTERVAL = 10 * 1000;    //重连自增步长
    final static long RECONNECT_MAX_TIME = 120 * 1000;   //最大重连间隔
    private String wsUrl;
    WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private int mStatus = DISCONNECTED;     //websocket连接状态
    private boolean isNeedReconnect;          //是否需要断线自动重连
    private boolean isManualClose = false;         //是否为手动关闭websocket连接
    MyWebSocketListener mMyWebSocketListener;
    private Lock mLock;
    Handler wsMainHandler = new Handler(Looper.getMainLooper());
    private int reconnectCount = 0;   //重连次数
    private String heartText = "";//心跳包发送内容
    private int heartInterval = 0;//心跳包发送间隔时间
    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMyWebSocketListener != null) {
                mMyWebSocketListener.onSocketReconnect();
            }
            buildConnect();
        }
    };

    Runnable heartRun = new Runnable() {
        public void run() {
            sendMessage(heartText);
            wsMainHandler.postDelayed(this, heartInterval);
        }
    };

    private WebSocketListener mWebSocketListener = new OKWebSocketListener(this);

    private MyWebSocket(Builder builder) {
        wsUrl = builder.wsUrl;
        heartInterval = builder.heartTime;
        heartText = builder.heartText;
        isNeedReconnect = builder.needReconnect;
        mOkHttpClient = new OkHttpClient().newBuilder().pingInterval(builder.interval, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();
        this.mLock = new ReentrantLock();
    }

    private void initWebSocket() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
        }
        if (mRequest == null) {
            mRequest = new Request.Builder().url(wsUrl).build();
        }
        mOkHttpClient.dispatcher().cancelAll();
        try {
            mLock.lockInterruptibly();
            try {
                mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
            } finally {
                mLock.unlock();
            }
        } catch (InterruptedException e) {
        }
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public void setMyWebSocketListener(MyWebSocketListener myWebSocketListener) {
        this.mMyWebSocketListener = myWebSocketListener;
    }

    public synchronized boolean isConnected() {
        return mStatus == CONNECTED;
    }

    public synchronized int getStatus() {
        return mStatus;
    }

    public synchronized void setStatus(int status) {
        this.mStatus = status;
    }

    public void startConnect() {
        isManualClose = false;
        buildConnect();
        if (heartInterval > 0) {
            wsMainHandler.removeCallbacks(heartRun);
            wsMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {//延迟0.5秒发送心跳
                    wsMainHandler.post(heartRun);
                }
            }, 500);
        }
    }

    public void stopConnect() {
        isManualClose = true;
        if (heartInterval > 0) {
            wsMainHandler.removeCallbacks(heartRun);
        }
        disconnect();
    }

    void tryReconnect() {
        if (!isNeedReconnect | isManualClose) {
            return;
        }

        setStatus(RECONNECT);

        long delay = reconnectCount * RECONNECT_INTERVAL;
        wsMainHandler.postDelayed(reconnectRunnable, delay > RECONNECT_MAX_TIME ? RECONNECT_MAX_TIME : delay);
        reconnectCount++;
    }

    private void cancelReconnect() {
        wsMainHandler.removeCallbacks(reconnectRunnable);
        reconnectCount = 0;
    }

    void connected() {
        cancelReconnect();
    }

    private void disconnect() {
        if (mStatus == DISCONNECTED) {
            return;
        }
        cancelReconnect();
        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().cancelAll();
        }
        if (mWebSocket != null) {
            boolean isClosed = mWebSocket.close(NORMAL_CLOSE, "normal close");
            //非正常关闭连接
            if (!isClosed) {
                if (mMyWebSocketListener != null) {
                    mMyWebSocketListener.onSocketClosed(ABNORMAL_CLOSE, "abnormal close");
                }
            }
        }
        setStatus(DISCONNECTED);
    }

    private synchronized void buildConnect() {
        switch (getStatus()) {
            case CONNECTED:
            case CONNECTING:
                break;
            default:
                setStatus(CONNECTING);
                initWebSocket();
        }
    }

    //发送消息
    public boolean sendMessage(String msg) {
        return send(msg);
    }

    private boolean sendMessage(ByteString byteString) {
        return send(byteString);
    }

    private boolean send(Object msg) {
        boolean isSend = false;
        if (mWebSocket != null && mStatus == CONNECTED) {
            if (msg instanceof String) {
                isSend = mWebSocket.send((String) msg);
            } else if (msg instanceof ByteString) {
                isSend = mWebSocket.send((ByteString) msg);
            }
            //发送消息失败，尝试重连
            if (!isSend) {
                tryReconnect();
            }
        }
        return isSend;
    }

    public static final class Builder {

        private String wsUrl;
        private boolean needReconnect = true;
        private int interval = 15;
        private int heartTime = 0;
        private String heartText = "";

        public Builder(String val) {
            wsUrl = val;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public Builder setHeartPackage(int heartTime, String heartText) {
            this.heartTime = heartTime;
            this.heartText = heartText;
            return this;
        }

        public Builder needReconnect(boolean val) {
            needReconnect = val;
            return this;
        }

        public MyWebSocket build() {
            return new MyWebSocket(this);
        }
    }
}

