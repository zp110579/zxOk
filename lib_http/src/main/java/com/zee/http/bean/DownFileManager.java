package com.zee.http.bean;

import java.util.HashMap;

/**
 * created by zee on 2021/1/15.
 * 记录所有下载文件的进度的管理
 */
public class DownFileManager {
    /**
     * <下载路径,已经下载的进度>
     */
    private static HashMap<String, RecordDownFileInfo> recordDownFileSize = new HashMap<String, RecordDownFileInfo>();


    public static RecordDownFileInfo getDownFileBean(String url) {
        RecordDownFileInfo recordDownFileInfo = recordDownFileSize.get(url);
        if (recordDownFileInfo == null) {
            recordDownFileInfo = new RecordDownFileInfo(0, 0);
        }
        return recordDownFileInfo;
    }

    public static void add(String httpUrl, RecordDownFileInfo recordDownFileInfo) {
        recordDownFileSize.put(httpUrl, recordDownFileInfo);
    }

    /**
     * 下载文成，移除下载记录
     *
     * @param httpUrl
     */
    public static void remove(String httpUrl) {
        recordDownFileSize.remove(httpUrl);
    }
}
