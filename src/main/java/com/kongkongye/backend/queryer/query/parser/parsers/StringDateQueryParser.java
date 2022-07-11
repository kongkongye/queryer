package com.kongkongye.backend.queryer.query.parser.parsers;

import com.google.common.base.Strings;
import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.ReflectionUtil;
import com.kongkongye.backend.queryer.common.Util;
import com.kongkongye.backend.queryer.query.annotation.QueryEnd;
import com.kongkongye.backend.queryer.query.annotation.QueryOp;
import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 字符串形式时间比较
 *
 * @see Util#toMilli(String, boolean)
 */
public class StringDateQueryParser implements QueryParser {
    @Override
    public void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                      Object obj, String alias, Field field, String fieldName, String sqlFieldName) {
        String value = (String) ReflectionUtil.getField(field, obj);
        if (!Strings.isNullOrEmpty(value)) {
            QueryOp queryOp = field.getDeclaredAnnotation(QueryOp.class);
            QueryEnd queryEnd = field.getDeclaredAnnotation(QueryEnd.class);
            whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" ").append(queryOp.value()).append(" :").append(fieldName).append(" ");
            params.put(fieldName, Util.toMilli(value, queryEnd != null && queryEnd.value()));
        }
    }
}
