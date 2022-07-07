package com.kongkongye.backend.queryer;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.primitives.Primitives;
import com.kongkongye.backend.common.Paging;
import com.kongkongye.backend.common.util.ReflectionUtil;
import com.kongkongye.backend.queryer.dto.annotation.AutoFrom;
import com.kongkongye.backend.queryer.dto.annotation.AutoSel;
import com.kongkongye.backend.queryer.dto.annotation.QueryTable;
import com.kongkongye.backend.queryer.dto.annotation.SelParse;
import com.kongkongye.backend.queryer.query.Query;
import com.kongkongye.backend.queryer.query.QueryTypeEn;
import com.kongkongye.backend.queryer.query.annotation.AutoQuery;
import com.kongkongye.backend.queryer.query.annotation.QueryNull;
import com.kongkongye.backend.queryer.query.annotation.QueryParse;
import com.kongkongye.backend.queryer.query.parser.QueryParser;
import com.kongkongye.backend.queryer.query.parser.parsers.BooleanQueryParser;
import com.kongkongye.backend.queryer.query.parser.parsers.DirectQueryParser;
import com.kongkongye.backend.queryer.query.parser.parsers.ListQueryParser;
import com.kongkongye.backend.queryer.query.parser.parsers.StringQueryParser;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class QueryUtil {
    private static Map<Class, QueryParser> defaultParsers = new HashMap<>();

    static {
        defaultParsers.put(List.class, new ListQueryParser());
        defaultParsers.put(String.class, new StringQueryParser());
        defaultParsers.put(Byte.class, new DirectQueryParser());
        defaultParsers.put(Integer.class, new DirectQueryParser());
        defaultParsers.put(Long.class, new DirectQueryParser());
        defaultParsers.put(Float.class, new DirectQueryParser());
        defaultParsers.put(Double.class, new DirectQueryParser());
        defaultParsers.put(Boolean.class, new BooleanQueryParser());
    }

    public static void parseQuery(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                                  @Nullable Query query) {
        //query为null
        if (query == null) {
            return;
        }

        Class<? extends Query> queryCls = query.getClass();
        AutoQuery autoQuery = queryCls.getDeclaredAnnotation(AutoQuery.class);
        //没有加自动解析注解
        if (autoQuery == null) {
            return;
        }

        for (Field field : queryCls.getDeclaredFields()) {
            boolean parseEnable = true;

            QueryParse queryParse = field.getDeclaredAnnotation(QueryParse.class);
            if (queryParse != null) {
                parseEnable = queryParse.enable();
            }

            if (parseEnable) {
                String alias = (queryParse == null || queryParse.alias().isEmpty()) ? autoQuery.alias() : queryParse.alias();
                String fieldName = (queryParse == null || Strings.isNullOrEmpty(queryParse.fieldName())) ? field.getName() : queryParse.fieldName();
                String sqlFieldName = (queryParse == null || Strings.isNullOrEmpty(queryParse.sqlFieldName())) ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName) : queryParse.sqlFieldName();
                QueryUtil.parseQueryField(selSql, fromSql, whereSql, groupSql, params, query, alias, field, queryParse != null ? queryParse.parser() : null, fieldName, sqlFieldName);
            }
        }
    }

    @SneakyThrows
    private static void parseQueryField(StringBuilder selSql, SqlHelper.FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params,
                                        Object obj, String alias, Field field, Class<? extends QueryParser> parserCls, String fieldName, String sqlFieldName) {
        //QueryNull
        QueryNull queryNull = field.getDeclaredAnnotation(QueryNull.class);
        if (queryNull != null) {
            Boolean value = (Boolean) ReflectionUtil.getField(field, obj);
            if (value != null) {
                whereSql.append(" and ").append(alias).append(".").append(sqlFieldName).append(value ? " is null " : " is not null ");
                return;
            }
        }
        //实例化解析器
        QueryParser QueryParser = null;
        if (parserCls != null && parserCls != QueryParser.class) {
            QueryParser = parserCls.newInstance();
        }
        //使用默认解析器
        if (QueryParser == null) {
            QueryParser = defaultParsers.get(Primitives.wrap(field.getType()));
        }
        //处理
        if (QueryParser != null) {
            QueryParser.parse(selSql, fromSql, whereSql, groupSql, params, obj, alias, field, fieldName, sqlFieldName);
        }
    }

    public static void parseSel(StringBuilder selSql, @Nullable Class<?> dtoCls) {
        //dtoCls为null
        if (dtoCls == null) {
            return;
        }

        //自动解析判断
        AutoSel autoSel = dtoCls.getDeclaredAnnotation(AutoSel.class);
        //没有加自动解析注解
        if (autoSel == null) {
            return;
        }

        //自动添加
        selSql.append(getSelSql(dtoCls, null, autoSel.alias()));
    }

    public static void parseFrom(SqlHelper.FromBuilder fromSql, @Nullable Class<?> dtoCls) {
        //dtoCls为null
        if (dtoCls == null) {
            return;
        }

        //自动解析判断
        AutoFrom autoFrom = dtoCls.getDeclaredAnnotation(AutoFrom.class);
        //没有加注解
        if (autoFrom == null) {
            return;
        }

        //检测添加from语句
        doParseFrom(fromSql, dtoCls, autoFrom.alias());
    }

    public static void doParseFrom(SqlHelper.FromBuilder fromSql, Class<?> cls, String alias) {
        QueryTable table = cls.getDeclaredAnnotation(QueryTable.class);
        if (table != null) {
            fromSql.getList().add(0, new SqlHelper.FromBuilder.Item(null, " from " + table.value() + " " + alias));
//            fromSql.append(" from ").append(table.name()).append(" ").append(alias);
        } else {
            Class<?> parentCls = cls.getSuperclass();
            if (parentCls != null) {
                doParseFrom(fromSql, parentCls, alias);
            }
        }
    }

    /**
     * ignores为null
     *
     * @see #getSelSql(Class, Set)
     */
    public static String getSelSql(Class dtoCls) {
        return getSelSql(dtoCls, null);
    }

    /**
     * alias为a
     *
     * @see #getSelSql(Class, Set, String)
     */
    public static String getSelSql(Class dtoCls, Set<String> ignores) {
        return getSelSql(dtoCls, ignores, "a");
    }

    /**
     * @param ignores 忽略的字段名(原始的)
     * @see #getSelSqlArgs(String, Class, Set)
     */
    public static String getSelSql(Class dtoCls, Set<String> ignores, String alias) {
        return "select " + getSelSqlArgs(alias, dtoCls, ignores) + " ";
    }

    /**
     * @param ignores 忽略的字段名(原始的)
     * @see #convertSelSqlArgs(List, Set, String, Class)
     */
    public static String getSelSqlArgs(String alias, Class dtoCls, Set<String> ignores) {
        List<String> args = new ArrayList<>();
        convertSelSqlArgs(args, ignores, alias, dtoCls);
        return String.join(",", args);
    }

    public static void convertSelSqlArgs(List<String> args, @Nullable Set<String> ignores, String alias, Class dtoCls) {
        //本身
        for (Field field : dtoCls.getDeclaredFields()) {
            //检测忽略
            if (ignores == null || !ignores.contains(field.getName())) {
                boolean parseEnable = true;

                SelParse parse = field.getDeclaredAnnotation(SelParse.class);
                if (parse != null) {
                    parseEnable = parse.enable();
                }

                if (parseEnable) {//解析
                    String fieldAlias = (parse == null || parse.alias().isEmpty()) ? alias : parse.alias();
                    String fieldName = field.getName();
                    String sqlFieldName = (parse == null || Strings.isNullOrEmpty(parse.sqlFieldName())) ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName) : parse.sqlFieldName();
                    String arg = fieldAlias + "." + sqlFieldName + " " + fieldName;
                    args.add(arg);
                }
            }
        }

        //父
        Class superCls = dtoCls.getSuperclass();
        if (superCls != null && superCls != Object.class) {
            convertSelSqlArgs(args, ignores, alias, superCls);
        }
    }

    public static Object query(@Nullable QueryTypeEn queryType, Paging paging, SqlHelper sqlHelper) {
        if (queryType == null) {
            queryType = QueryTypeEn.page;
        }
        switch (queryType) {
            case page:
                return sqlHelper.getPageParsed(paging);
            case list:
                return sqlHelper.getListParsed();
            case get:
                return sqlHelper.getOneParsed();
            case count:
                return sqlHelper.getCount();
            default:
                throw new RuntimeException("未识别查询类型！");
        }
    }
}
