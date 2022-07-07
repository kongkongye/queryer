package com.kongkongye.backend.queryer.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query用的，表示字段值是否是null
 * 字段需要是Boolean类型，true值代表需要是null，false值代表需要不是null
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryNull {
}
