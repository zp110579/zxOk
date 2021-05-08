package com.zee.http.request;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zee.http.bean.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class GsonTools {
    protected final static Gson gson = GsonBuilder.getInstance().getGson();

    public static <T extends Object> T getObject(String str, Class<?> cla, boolean isListType) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Object object;
        if (isListType) {//ArrayList类型
            object = getDataList(str, cla);
        } else {
            object = getObject(str, cla);
        }
        return (T) object;
    }

    public static <T extends Object> T getObject(String str, Class<?> cla) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return (T) gson.fromJson(str, cla);
    }

    public static <T> List<T> getDataList(String dataStr, Class<?> cla) {
        List<T> tempList = new ArrayList<T>();
        if (TextUtils.isEmpty(dataStr)) {
            return tempList;
        }

        Type type = new ListParameterizedType(cla);
        tempList = gson.fromJson(dataStr, type);
        return tempList;
    }

    private static class ListParameterizedType implements ParameterizedType {
        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
