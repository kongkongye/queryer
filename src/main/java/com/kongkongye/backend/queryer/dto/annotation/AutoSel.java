package com.kongkongye.backend.queryer.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询DTO用的，表示自动select字段
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoSel {
    /**
     * 别名
     */
    String alias() default "a";
}
