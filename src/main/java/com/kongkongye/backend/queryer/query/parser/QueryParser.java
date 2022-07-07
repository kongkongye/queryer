package com.kongkongye.backend.queryer.query.parser;

import com.kongkongye.backend.queryer.SqlHelper;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * query字段解析器
 */
public interface QueryParser {
    void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
               Object obj, String alias, Field field, String fieldName, String sqlFieldName);
}
