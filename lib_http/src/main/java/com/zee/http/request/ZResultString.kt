package com.zee.http.request

import android.text.TextUtils

/**
 *created by zee on 2021/3/30.
 *网络数据返回序列化
 */
abstract class ZResultString : AbsStringResult() {

    inline fun <reified T : Any> getObjectForKey(name: String): T? {
        val json = jsonObject.optString(name)
        return if (TextUtils.isEmpty(json)) {
            null
        } else
            GsonTools.getObject(jsonObject.optString(name), T::class.java) as T
    }

    inline fun <reified T : Any> getObjectForValue(json: String?): T? {
        return if (TextUtils.isEmpty(json)) {
            null
        } else GsonTools.getObject(json, T::class.java) as T
    }

    inline fun <reified T : Any> getArrayListForKey(name: String): ArrayList<T> {
        return GsonTools.getObject(jsonObject.optString(name), T::class.java, true)
    }

    inline fun <reified T : Any> getArrayListForValue(json: String): ArrayList<T> {
        return GsonTools.getObject(json, T::class.java, true)
    }

}