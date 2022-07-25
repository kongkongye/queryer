package com.kongkongye.backend.queryer.jdbc;

import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.SqlHelperBuilder;
import com.kongkongye.backend.queryer.query.Query;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcSqlHelperBuilder<R> extends SqlHelperBuilder<R> {
    @Getter
    @Setter
    private NamedParameterJdbcTemplate template;

    public JdbcSqlHelperBuilder(NamedParameterJdbcTemplate template, SqlHelper.HelpBuilder helpBuilder, Query query) {
        super(helpBuilder, query);
        this.template = template;
    }

    public JdbcSqlHelperBuilder(NamedParameterJdbcTemplate template, SqlHelper.HelpBuilder helpBuilder, Query query, Class<R> cls) {
        super(helpBuilder, query, cls);
        this.template = template;
    }

    @Override
    public SqlHelper<R> doBuild() {
        return new JdbcSqlHelper<>(template, selSql.toString(), fromSql, whereSql.toString(), groupSql.toString(), params, query, cls, converters, peekers);
    }
}
