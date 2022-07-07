package com.kongkongye.backend.queryer.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询DTO用的，表示自动添加from语句
 * 递归父类直到找到第一个有@Table注解的类；
 * 如果没找到则无效
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFrom {
    /**
     * 别名
     */
    String alias() default "a";
}
