package com.zee.http.request;

import android.os.Environment;
import android.support.annotation.NonNull;


import com.zee.http.bean.RecordDownFileInfo;
import com.zee.http.bean.DownFileManager;
import com.zee.http.utils.MyOkHandlerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载文件
 */
public class DownloadFileRequest extends BaseRequest<DownloadFileRequest> {

    public DownloadFileRequest(final String url) {
        super(url);
    }

    public void execute(DownloadFileCallBackListener listener) {
        final String saveDir = listener.getSaveFilePath();
        listener.onStart();
        final RecordDownFileInfo recordDownFileInfo = DownFileManager.getDownFileBean(url);//检测是不是下载过
        Request.Builder requestBuilder = new Request.Builder();

        if (recordDownFileInfo.start > 0) {//如果有记录
            mHeaders.putAll("Range", "bytes=" + recordDownFileInfo.start + "-" + new Long(recordDownFileInfo.end - 1).toString());
            requestBuilder = mHeaders.getHeaders();
        }
        Request request = requestBuilder.url(url).tag(mTag).build();
        okhttp3.Call rawCall = generateCall(request);


        rawCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                RandomAccessFile randomAccessFile = null;
                String savePath = isExistDir(saveDir);
                try {
                    int curValue = 0;
                    is = response.body().byteStream();

                    //获取文件总长度
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    if (!file.exists()) {
                        recordDownFileInfo.start = 0;
                    }
                    randomAccessFile = new RandomAccessFile(file, "rwd");

                    randomAccessFile.seek(recordDownFileInfo.start);

                    while ((len = is.read(buf)) != -1) {
                        randomAccessFile.write(buf, 0, len);
                        recordDownFileInfo.start += len;
                        if (recordDownFileInfo.end == 0) {
                            recordDownFileInfo.end = total;
                            DownFileManager.add(url, recordDownFileInfo);
                        }

                        int progress = (int) (recordDownFileInfo.start * 1.0f / recordDownFileInfo.end * 100);
                        if (progress == 100) {
                            System.out.println("$progress");
                        }
                        if (curValue != progress) {
                            MyOkHandlerUtils.runOnMainThread(() -> listener.onDownloadProgress(progress, recordDownFileInfo.start, total, 1));
                            curValue = progress;
                        }
                    }
                    MyOkHandlerUtils.runOnMainThread(() -> listener.onDownloadSuccess(file));
                } catch (Exception e) {
                    MyOkHandlerUtils.runOnMainThread(() -> listener.onError(e));
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                        }
                    } catch (IOException e) {
                    }
                }
                RecordDownFileInfo temPRecordDownFileInfo = DownFileManager.getDownFileBean(url);//检测是不是下载过
                if (temPRecordDownFileInfo.reDownFinish()) {
                    DownFileManager.remove(url);//删除下载记录
                }
                MyOkHandlerUtils.runOnMainThread(() -> listener.onEnd());
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
