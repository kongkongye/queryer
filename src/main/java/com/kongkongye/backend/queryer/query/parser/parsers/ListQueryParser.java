package com.kongkongye.backend.queryer.query.parser.parsers;

import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.ReflectionUtil;
import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * in (xxx)
 */
public class ListQueryParser implements QueryParser {
    @Override
    public void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                      Object obj, String alias, Field field, String fieldName, String sqlFieldName) {
        List value = (List) ReflectionUtil.getField(field, obj);
        if (value != null) {
            if (value.isEmpty()) {
                whereSql.append(" and false ");
            } else {
                whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" in (:").append(fieldName).append(") ");
                params.put(fieldName, value);
            }
        }
    }
}
