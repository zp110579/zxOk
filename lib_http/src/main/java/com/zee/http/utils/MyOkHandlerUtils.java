package com.zee.http.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;


public class MyOkHandlerUtils {
    private static Handler mMainUIHandler = new Handler(Looper.getMainLooper());
    private static Application mApplication;

    public static void runOnMainThread(Runnable paRunnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            paRunnable.run();
        } else {
            mMainUIHandler.post(paRunnable);
        }
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static void setApplication(Application paMApplication) {
        mApplication = paMApplication;
    }
}
