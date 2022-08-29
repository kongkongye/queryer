package com.kongkongye.backend.queryer.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see com.kongkongye.backend.queryer.query.parser.parsers.ListQueryParser
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryIn {
    /**
     * true: in
     * false: not in
     */
    boolean value() default true;
}
