package com.kongkongye.backend.queryer.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询DTO用的，表示解析
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelParse {
    /**
     * 是否启用自动解析
     */
    boolean enable() default true;

    /**
     * 别名
     */
    String alias() default "";

    /**
     * 数据库字段名，默认为fieldName转下划线格式
     */
    String sqlFieldName() default "";
}
