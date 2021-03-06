package com.kongkongye.backend.queryer.jdbc;

import com.google.common.base.Preconditions;
import com.kongkongye.backend.queryer.SqlHelper;
import com.kongkongye.backend.queryer.common.Maps;
import com.kongkongye.backend.queryer.common.Paging;
import com.kongkongye.backend.queryer.query.Query;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;

public class JdbcSqlHelper<R> extends SqlHelper<R> {
    @Getter
    @Setter
    private NamedParameterJdbcTemplate template;

//    public JdbcSqlHelper(NamedParameterJdbcTemplate template, String selSql, FromBuilder fromSql, String whereSql, String groupSql, Map<String, Object> params, Query query) {
//        super(selSql, fromSql, whereSql, groupSql, params, query);
//        this.template = template;
//    }

    public JdbcSqlHelper(NamedParameterJdbcTemplate template, String selSql, FromBuilder fromSql, String whereSql, String groupSql, Map<String, Object> params, Query query, Class<R> cls,
                         List<Converter> converters, List<Peeker<R>> peekers) {
        super(selSql, fromSql, whereSql, groupSql, params, query, cls, converters, peekers);
        this.template = template;
    }

    @Override
    public int getCount() {
        SqlRowSet sqlRowSet = template.queryForRowSet(getCountSql(), params);
        Preconditions.checkArgument(sqlRowSet.next());
        return sqlRowSet.getInt(1);
    }

    @Override
    protected List<Maps> doGetList() {
        String listSql = getListSql();
        List<Map<String, Object>> list = template.queryForList(listSql, params);
        return toMaps(list);
    }

    @Override
    protected List<Maps> doGetPage(Paging paging) {
        List<Map<String, Object>> list = template.queryForList(getListSql() + getLimitSql(paging), params);
        return toMaps(list);
    }
}
