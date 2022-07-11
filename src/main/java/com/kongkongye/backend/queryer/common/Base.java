package com.kongkongye.backend.queryer.common;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 通用基类
 */
public abstract class Base {
    protected Double getDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    protected boolean isTrue(Boolean value) {
        return value != null && value;
    }

    protected boolean isFalse(Boolean value) {
        return value == null || !value;
    }

    protected static Map<String, Object> toMap(Maps data) {
        return data.getOrigin();
    }

    protected static List<Map<String, Object>> toMap(List<Maps> data) {
        return data.stream().map(Maps::getOrigin).collect(Collectors.toList());
    }

    protected static Pagination<Map<String, Object>> toMap(Pagination<Maps> data) {
        Pagination<Map<String, Object>> newData = new Pagination<>(data.getPageSize(), data.getCurrentPage(), data.getTotal());
        newData.setDataList(data.getDataList().stream().map(Maps::getOrigin).collect(Collectors.toList()));
        return newData;
    }

    protected Maps toMaps(@Nullable Map<String, Object> map) {
        return new Maps(map != null ? map : new HashMap<>());
    }

    protected List<Maps> toMaps(@Nullable List<Map<String, Object>> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.stream().map(Maps::new).collect(Collectors.toList());
    }

    protected Pagination<Maps> toMaps(@NonNull Pagination<Map<String, Object>> pagination) {
        Pagination<Maps> result = new Pagination<>(pagination.getPageSize(), pagination.getCurrentPage(), pagination.getTotal());
        result.setDataList(pagination.getDataList().stream().map(Maps::new).collect(Collectors.toList()));
        return result;
    }

    /**
     * 包装
     */
    public <T> T wrap(T o, Consumer<T> consumer) {
        consumer.accept(o);
        return o;
    }

    public interface Converter<F, T> {
        T convert(F from);
    }

    /**
     * 转换
     *
     * @param <F> 输入
     * @param <T> 输出
     */
    public <F, T> T convert(F from, Converter<F, T> converter) {
        return converter.convert(from);
    }

    public interface Producer<T> {
        T produce();
    }

    /**
     * 生产
     *
     * @param <T> 输出
     */
    public <T> T of(Producer<T> producer) {
        return producer.produce();
    }
}
