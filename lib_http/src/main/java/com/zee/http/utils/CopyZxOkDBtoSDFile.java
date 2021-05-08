package com.zee.http.utils;

import android.os.Environment;
import android.util.Log;

import com.zee.http.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CopyZxOkDBtoSDFile {

    static String DATABASE_NAME = "zxok_cache.db";

    static String oldPath = "data/data/" + BuildConfig.APPLICATION_ID + "/databases/" + DATABASE_NAME;
    static String newPath = Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME;

    public CopyZxOkDBtoSDFile() {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            Log.i("CopyFile", "复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}
