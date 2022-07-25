package com.kongkongye.backend.queryer;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.kongkongye.backend.queryer.query.Query;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.*;

public abstract class SqlHelperBuilder<R> {
    protected List<SqlHelper.HelpBuilder> helpBuilders = new ArrayList<>();
    protected StringBuilder selSql = new StringBuilder();
    protected SqlHelper.FromBuilder fromSql = new SqlHelper.FromBuilder();
    protected StringBuilder whereSql = new StringBuilder();
    protected StringBuilder groupSql = new StringBuilder();
    @Getter
    protected Map<String, Object> params = new HashMap<>();

    protected Query query;

    protected List<SqlHelper.Converter> converters = new ArrayList<>();
    protected List<SqlHelper.Peeker<R>> peekers = new ArrayList<>();

    /**
     * 结果类
     */
    @Nullable
    protected Class<R> cls;

    public SqlHelperBuilder(SqlHelper.HelpBuilder helpBuilder, @Nullable Query query) {
        this(helpBuilder, query, null);
    }

    public SqlHelperBuilder(SqlHelper.HelpBuilder helpBuilder, @Nullable Query query, Class<R> cls) {
        this.helpBuilders.add(helpBuilder);
        this.query = query;
        this.cls = cls;
    }

    /**
     * 扩展逻辑
     */
    public SqlHelperBuilder<R> then(SqlHelper.HelpBuilder helpBuilder) {
        helpBuilders.add(helpBuilder);
        return this;
    }

    /**
     * 添加Maps转换器(按顺序)
     *
     * @return 本身
     */
    public SqlHelperBuilder<R> addConverter(SqlHelper.Converter converter) {
        this.converters.add(converter);
        return this;
    }

    /**
     * 添加对象执行器(按顺序)
     *
     * @return 本身
     */
    public SqlHelperBuilder<R> addPeeker(SqlHelper.Peeker<R> peeker) {
        this.peekers.add(peeker);
        return this;
    }

    /**
     * 1. 会自动添加sel参数
     * 2. 如果whereSql为空则会自动添加" where 1=1 "
     * 3. 会自动添加query参数
     */
    public SqlHelper<R> build() {
        //自动添加sel参数
        Set<String> white = !Strings.isNullOrEmpty(query.getQWhite()) ? new HashSet<>(JSON.parseArray(query.getQWhite(), String.class)) : null;
        Set<String> black = !Strings.isNullOrEmpty(query.getQBlack()) ? new HashSet<>(JSON.parseArray(query.getQBlack(), String.class)) : null;
        QueryUtil.parseSel(selSql, cls, white, black);
        //自动添加from语句
        QueryUtil.parseFrom(fromSql, cls);
        //build
        for (SqlHelper.HelpBuilder helpBuilder : helpBuilders) {
            helpBuilder.build(selSql, fromSql, whereSql, groupSql, params);
        }
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
        //返回执行构建
        return doBuild();
    }

    /**
     * 执行构建生成SqlHelper
     */
    protected abstract SqlHelper<R> doBuild();
}
