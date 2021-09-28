package com.zee.http.request;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.zee.http.utils.MyOkHandlerUtils;

import java.io.File;

/**
 * 下载APK并安装
 */
public abstract class DownAPKAndInstallCallBackListener extends DownloadFileCallBackListener {


    /**
     * 下载文件成功
     */
    @Override
    public void onDownloadSuccess(File file) {
        installApk(file);
    }

    /**
     * apk安装成功
     */
    public void onInstallSuccess() {
        Log.i("DownAPK", "安装成功");
    }


    @Override
    public void onError(Exception e) {
        Log.e("MyOK", e.toString());
    }

    @Override
    public void onDownloadProgress(float progress, long currentSize, long totalSize, long networkSpeed) {
        Log.i("MyOK", "下载进度" + progress + " " + currentSize + " " + totalSize);
    }


    public static void installApk(final File pakFile) {
        if (pakFile.length() == 0L) {
            Log.e("MyOK", "apk大小：0K,无法安装");
            return;
        }

        MyOkHandlerUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Context context = MyOkHandlerUtils.getApplication();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(
                            context, context.getPackageName() + ".fileprovider", pakFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(pakFile), "application/vnd.android.package-archive");
                }
                context.startActivity(intent);
            }
        });

    }
}
