package com.zee.http.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.zee.http.bean.HttpHeaders;
import com.zee.http.utils.OkLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

class CacheDao extends DataBaseDao {

    public CacheDao() {
        super(new CacheHelper());
    }

    /**
     * 根据key获取缓存
     */
    public CacheBean get(String key) {
        String selection = CacheHelper.KEY + "=?";
        String[] selectionArgs = new String[]{key};
        List<CacheBean> cacheEntities = get(selection, selectionArgs);
        return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
    }

    /**
     * 移除一个缓存
     */
    public boolean remove(String key) {
        String whereClause = CacheHelper.KEY + "=?";
        String[] whereArgs = new String[]{key};
        int delete = delete(whereClause, whereArgs);
        return delete > 0;
    }

    public CacheBean parseCursorToBean(Cursor cursor) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setId(cursor.getInt(cursor.getColumnIndex(CacheHelper.ID)));
        cacheBean.setKey(cursor.getString(cursor.getColumnIndex(CacheHelper.KEY)));
        cacheBean.setCurTime(cursor.getLong(cursor.getColumnIndex(CacheHelper.LOCAL_EXPIRE)));

        byte[] headerData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.HEAD));
        ByteArrayInputStream headerBAIS = null;
        ObjectInputStream headerOIS = null;
        try {
            if (headerData != null) {
                headerBAIS = new ByteArrayInputStream(headerData);
                headerOIS = new ObjectInputStream(headerBAIS);
                Object header = headerOIS.readObject();
                cacheBean.setResponseHeaders((HttpHeaders) header);
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (headerOIS != null) headerOIS.close();
                if (headerBAIS != null) headerBAIS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        byte[] dataData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.DATA));
        ByteArrayInputStream dataBAIS = null;
        ObjectInputStream dataOIS = null;
        try {
            if (dataData != null) {
                dataBAIS = new ByteArrayInputStream(dataData);
                dataOIS = new ObjectInputStream(dataBAIS);
                String data = dataOIS.readUTF();
                cacheBean.setData(data);
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (dataOIS != null) dataOIS.close();
                if (dataBAIS != null) dataBAIS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }
        return cacheBean;
    }

    @Override
    public ContentValues getContentValues(CacheBean cacheBean) {
        ContentValues values = new ContentValues();
        values.put(CacheHelper.KEY, cacheBean.getKey());
        values.put(CacheHelper.LOCAL_EXPIRE, cacheBean.getCurTime());

        HttpHeaders headers = cacheBean.getResponseHeaders();
        ByteArrayOutputStream headerBAOS = null;
        ObjectOutputStream headerOOS = null;
        try {
            if (headers != null) {
                headerBAOS = new ByteArrayOutputStream();
                headerOOS = new ObjectOutputStream(headerBAOS);
                headerOOS.writeObject(headers);
                headerOOS.flush();
                byte[] headerData = headerBAOS.toByteArray();
                values.put(CacheHelper.HEAD, headerData);
            }
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (headerOOS != null) headerOOS.close();
                if (headerBAOS != null) headerBAOS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        String data = cacheBean.getData();
        ByteArrayOutputStream dataBAOS = null;
        ObjectOutputStream dataOOS = null;
        try {
            if (data != null) {
                dataBAOS = new ByteArrayOutputStream();
                dataOOS = new ObjectOutputStream(dataBAOS);
                dataOOS.writeUTF(data);
                dataOOS.flush();
                byte[] dataData = dataBAOS.toByteArray();
                values.put(CacheHelper.DATA, dataData);
            }
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (dataOOS != null) dataOOS.close();
                if (dataBAOS != null) dataBAOS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }
        return values;
    }

    @Override
    protected String getTableName() {
        return CacheHelper.TABLE_NAME;
    }
}