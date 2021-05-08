package com.zee.http.bean;

/**
 * created by zee on 2021/1/15.
 * 记录OkHttp下载文件的进度
 */
public class RecordDownFileInfo {
    public long start; //记录已经下载的长度
    public long end;  //文件的总长度

    public RecordDownFileInfo(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public boolean reDownFinish(){//是否已经全部
        return  start==end;
    }
}
