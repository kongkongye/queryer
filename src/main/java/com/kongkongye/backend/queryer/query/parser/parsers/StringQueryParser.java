package com.kongkongye.backend.queryer.query.parser.parsers;

import com.google.common.base.Strings;
import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.ReflectionUtil;
import com.kongkongye.backend.queryer.query.annotation.QueryLike;
import com.kongkongye.backend.queryer.query.parser.QueryParser;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 不为null或空判断
 */
public class StringQueryParser implements QueryParser {
    @Override
    public void parse(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                      Object obj, String alias, Field field, String fieldName, String sqlFieldName) {
        String value = (String) ReflectionUtil.getField(field, obj);
        if (!Strings.isNullOrEmpty(value)) {
            QueryLike queryLike = field.getDeclaredAnnotation(QueryLike.class);
            if (queryLike != null) {//like
                String left = queryLike.left() ? "%" : "";
                String right = queryLike.right() ? "%" : "";
                whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" " + queryLike.like() + " :").append(fieldName).append(" ");
                params.put(fieldName, left + value + right);
            } else {//相等
                //空内容，不加限制
                value = value.trim();
                if (value.startsWith("%") && value.endsWith("%")) {
                    if (value.length() < 2) {
                        return;
                    }
                    String v = value.substring(1, value.length() - 1);
                    if (v.trim().isEmpty()) {
                        return;
                    }
                }
                //加sql
                whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" like :").append(fieldName).append(" ");
//                    whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(" = :").append(fieldName).append(" ");
                params.put(fieldName, value);
            }
        }
    }
}
