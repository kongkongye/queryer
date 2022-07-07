package com.kongkongye.backend.queryer.jdbc;

import com.kongkongye.backend.queryer.QueryUtil;
import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.query.Query;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class JdbcUtil {
    /**
     * 1. 会自动添加sel参数
     * 2. 如果whereSql为空则会自动添加" where 1=1 "
     * 3. 会自动添加query参数
     */
    public static <R> SqlHelper<R> help(NamedParameterJdbcTemplate template, SqlHelper.HelpBuilder helpBuilder, @Nullable Query query, @Nullable Class<R> cls) {
        //构造
        StringBuilder selSql = new StringBuilder();
        SqlHelper.FromBuilder fromSql = new SqlHelper.FromBuilder();
        StringBuilder whereSql = new StringBuilder();
        StringBuilder groupSql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        //自动添加sel参数
        QueryUtil.parseSel(selSql, cls);
        //自动添加from语句
        QueryUtil.parseFrom(fromSql, cls);
        //build
        helpBuilder.build(selSql, fromSql, whereSql, groupSql, params);
        //whereSql
        if (whereSql.length() <= 0) {
            whereSql.append(" where 1=1 ");
        } else {
            String where = whereSql.toString().trim().toLowerCase();
            if (!where.startsWith("where")) {
                whereSql.insert(0, " where 1=1 ");
            }
        }
        //自动添加query参数
        QueryUtil.parseQuery(selSql, fromSql, whereSql, groupSql, params, query);
        //返回
        return new JdbcSqlHelper<>(template, selSql.toString(), fromSql, whereSql.toString(), groupSql.toString(), params, query, cls);
    }
}
