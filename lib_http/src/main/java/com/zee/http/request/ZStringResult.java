package com.zee.http.request;


import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;


public abstract class ZStringResult extends AbsStringResult {


    public JSONObject optJSONObject(String name) {
        return mJsonObject.optJSONObject(name);
    }

    public JSONArray optJSONArray(String name) {
        return mJsonObject.optJSONArray(name);
    }


    public int optInt(String name) {
        return mJsonObject.optInt(name);
    }

    public boolean optBoolean(String name) {
        return mJsonObject.optBoolean(name);
    }

    public int optInt(String name, int fallback) {
        return mJsonObject.optInt(name, fallback);
    }

    public String optString(String name) {
        return mJsonObject.optString(name);
    }

    protected <T extends Object> T optObject(String name, Class<?> cla) {
        return GsonTools.getObject(optString(name), cla);
    }

    protected <T> List<T> optList(String name, Class<?> cla) {
        return GsonTools.getDataList(optString(name), cla);
    }

    protected <T extends Object> T optObject(String name, Class<?> cla, boolean isListType) {
        return GsonTools.getObject(optString(name), cla, isListType);
    }

    protected <T extends Object> T optValueToObject(String value, Class<?> cla) {
        return GsonTools.getObject(value, cla);
    }

    protected <T> List<T> optValueToList(String value, Class<?> cla) {
        return GsonTools.getDataList(value, cla);
    }


}
