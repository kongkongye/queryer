package com.kongkongye.backend.queryer.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query用的，表示使用Like而不是相等
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryLike {
    /**
     * 是否左边模糊搜索
     */
    boolean left() default true;

    /**
     * 是否右边模糊搜索
     */
    boolean right() default true;
}
