package com.zee.http.cache;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum CacheManager {

    INSTANCE;

    private Lock mLock;
    private CacheDao cacheDao;

    CacheManager() {
        mLock = new ReentrantLock();
        cacheDao = new CacheDao();
    }

    /**
     * 获取缓存
     *
     * @param key 缓存的Key
     * @return 缓存的对象实体
     */
    public CacheBean get(String key) {
        mLock.lock();
        try {
            return cacheDao.get(key);
        } finally {
            mLock.unlock();
        }
    }


    public List<CacheBean> getAll() {
        mLock.lock();
        try {
            return cacheDao.getAll();
        } finally {
            mLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public CacheBean replace(String key, CacheBean entity) {
        mLock.lock();
        try {
            entity.setKey(key);
            cacheDao.replace(entity);
            return entity;
        } finally {
            mLock.unlock();
        }
    }

    public boolean remove(String key) {
        if (key == null) return true;
        mLock.lock();
        try {
            return cacheDao.remove(key);
        } finally {
            mLock.unlock();
        }
    }

    public boolean clear() {
        mLock.lock();
        try {
            return cacheDao.deleteAll() > 0;
        } finally {
            mLock.unlock();
        }
    }
}