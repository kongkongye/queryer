package com.kongkongye.backend.queryer.common;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    private Map<String, Object> origin;

    public Maps() {
        this.origin = new HashMap<>();
    }

    public Maps(Map<String, Object> origin) {
        this.origin = origin;
    }

    /**
     * 转换为java对象
     */
    public <T> T toJava(Class<T> cls) {
        return new JSONObject(origin).toJavaObject(cls);
    }

    public Map<String, Object> getOrigin() {
        return origin;
    }

    public Object get(String key) {
        return origin.get(key);
    }

    public Object put(String key, Object value) {
        return origin.put(key, value);
    }

    public Number getNumber(String key) {
        return (Number) origin.get(key);
    }

    public Long getLong(String key) {
        Number n = getNumber(key);
        return n != null ? n.longValue() : null;
    }

    public Integer getInteger(String key) {
        Number n = getNumber(key);
        return n != null ? n.intValue() : null;
    }

    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }
}
