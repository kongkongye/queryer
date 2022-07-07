package com.kongkongye.backend.queryer.query.annotation;

import com.kongkongye.backend.queryer.query.parser.parsers.StringDateQueryParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see StringDateQueryParser
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryEnd {
    /**
     * 默认true
     */
    boolean value() default true;
}
