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
public @interface QueryOp {
    /**
     * 操作符，如>=表示 o.time >= :time (即sql字段永远在左边，变量在右边)
     */
    String value();
}
