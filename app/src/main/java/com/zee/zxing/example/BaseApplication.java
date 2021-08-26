package com.zee.zxing.example;

import android.app.Application;

import com.zee.http.MyOk;
import com.zee.utils.ZLibrary;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MyOk.init(this);
        ZLibrary.init(this,true);
    }
}
