package com.kongkongye.backend.queryer.query.annotation;

import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query用的，表示解析
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParse {
    /**
     * 是否启用自动解析
     */
    boolean enable() default true;

    /**
     * 解析器，默认根据字段类型获取注册的默认解析器
     */
    Class<? extends QueryParser> parser() default QueryParser.class;

    /**
     * 别名
     */
    String alias() default "";

    /**
     * 字段名，默认为field的name
     * (这个一般不用改)
     */
    String fieldName() default "";

    /**
     * 数据库字段名，默认为fieldName转下划线格式
     */
    String sqlFieldName() default "";
}
