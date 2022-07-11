package com.kongkongye.backend.queryer.query.parser.parsers;

import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.ReflectionUtil;
import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * null与0代表false
 * 1代表true
 */
public class BooleanQueryParser implements QueryParser {
    @Override
    public void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                      Object obj, String alias, Field field, String fieldName, String sqlFieldName) {
        Boolean value = (Boolean) ReflectionUtil.getField(field, obj);
        if (value != null) {
            if (value) {
                whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" = 1 ");
            } else {
                whereSql.append(" and (").append(alias).append(".").append(sqlFieldName).append(" = 0 or ").append(alias).append(".").append(sqlFieldName).append(" is null) ");
            }
        }
    }
}
