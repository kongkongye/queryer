package com.kongkongye.backend.queryer.query.parser.parsers;

import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.ReflectionUtil;
import com.kongkongye.backend.queryer.query.annotation.QueryOp;
import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 不为null判断
 */
public class DirectQueryParser implements QueryParser {
    @Override
    public void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                      Object obj, String alias, Field field, String fieldName, String sqlFieldName) {
        Object value = ReflectionUtil.getField(field, obj);
        if (value != null) {
            QueryOp queryOp = field.getDeclaredAnnotation(QueryOp.class);
            whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" " + (queryOp != null ? queryOp.value() : "=") + " :").append(fieldName).append(" ");
            params.put(fieldName, value);
        }
    }
}
