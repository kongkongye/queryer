package com.kongkongye.backend.queryer.query.annotation;

import com.kongkongye.backend.queryer.en.DialectEn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query用的，表示自动解析
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoQuery {
    /**
     * 别名
     */
    String alias() default "a";

    DialectEn dialect() default DialectEn.mysql;
}
