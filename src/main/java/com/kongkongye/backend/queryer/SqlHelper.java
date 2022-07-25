package com.kongkongye.backend.queryer;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kongkongye.backend.queryer.common.Base;
import com.kongkongye.backend.queryer.common.Maps;
import com.kongkongye.backend.queryer.common.Pagination;
import com.kongkongye.backend.queryer.common.Paging;
import com.kongkongye.backend.queryer.query.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SqlHelper<R> extends Base {
    public static class FromBuilder {
        @Data
        @AllArgsConstructor
        public static class Item {
            /**
             * 意义在于获取countsql时，如果join的没有用到，就会忽略这条join语句
             * from而非join语句不需要加alias
             */
            @Nullable
            private String alias;
            private String joinSql;
        }

        @Getter
        private List<Item> list = new ArrayList<>();

        public void append(String joinSql) {
            //获取别名
            String alias = null;
            List<String> ss = Arrays.stream(joinSql.split(" ")).filter(e -> !Strings.isNullOrEmpty(e)).collect(Collectors.toList());
            if (ss.size() >= 3) {
                int joinIdx = "join".equalsIgnoreCase(ss.get(0)) ? 0 : ("join".equalsIgnoreCase(ss.get(1)) ? 1 : -1);
                if (joinIdx != -1 && ss.size() >= joinIdx + 3) {
                    alias = ss.get(joinIdx + 2);
                }
            }

            list.add(new Item(alias, joinSql));
        }
    }

    public interface HelpBuilder {
        /**
         * 实现请填充内容,参数都给了
         */
        void build(StringBuilder selSql, FromBuilder fromSql, StringBuilder whereSql, StringBuilder groupSql, Map<String, Object> params);
    }

    public interface Converter {
        Maps convert(Maps maps);
    }

    public interface Peeker<E> {
        void peek(E obj);
    }

    protected String selSql;
    protected FromBuilder fromSql;
    protected String whereSql;
    protected String groupSql;
    @Getter
    protected Map<String, Object> params;

    protected Query query;

    protected List<Converter> converters;
    protected List<Peeker<R>> peekers;

    /**
     * 结果类
     */
    @Nullable
    protected Class<R> cls;

//    public SqlHelper(String selSql, FromBuilder fromSql, @Nullable String whereSql, @Nullable String groupSql, Map<String, Object> params, @Nullable Query query) {
//        this(selSql, fromSql, whereSql, groupSql, params, query, null);
//    }

    public SqlHelper(String selSql, FromBuilder fromSql, @Nullable String whereSql, @Nullable String groupSql, Map<String, Object> params, @Nullable Query query, @Nullable Class<R> cls,
                     List<Converter> converters, List<Peeker<R>> peekers) {
        this.selSql = selSql;
        this.fromSql = fromSql;
        this.whereSql = whereSql;
        this.groupSql = groupSql;
        this.params = params;
        this.query = query;
        this.cls = cls;
        this.converters = converters;
        this.peekers = peekers;
    }

    /**
     * 获取数量
     *
     * @return >=0
     */
    public abstract int getCount();

    /**
     * 取一个(如果有多个,会取第一个)
     */
    public Maps getOne() {
        Pagination<Maps> result = getPage(new Paging(1, 1));
        return result.getDataList().isEmpty() ? null : result.getDataList().get(0);
    }

    public List<Maps> getList() {
        //获取
        List<Maps> mapsList = doGetList();
        //convert
        List<Maps> convertedList = convert(mapsList);
        //返回
        return convertedList;
    }

    public Pagination<Maps> getPage(Paging paging) {
        //获取
        List<Maps> dataList = doGetPage(paging);
        //convert
        List<Maps> convertedList = convert(dataList);
        //返回
        Pagination<Maps> pagination = new Pagination<>(paging.getPageSize(), paging.getPage(), getCount());
        pagination.setDataList(convertedList);
        return pagination;
    }

    protected abstract List<Maps> doGetList();

    protected abstract List<Maps> doGetPage(Paging paging);

    /**
     * 有传入cls时此方法才有效
     * <p>
     * 取一个(如果有多个,会取第一个)
     */
    public R getOneParsed() {
        Maps result = getOne();
        if (result != null) {
            R obj = result.toJava(cls);
            peek(obj);
            return obj;
        }
        return null;
    }

    /**
     * 有传入cls时此方法才有效
     */
    public List<R> getListParsed() {
        return getList().stream().map(e -> e.toJava(cls)).peek(this::peek).collect(Collectors.toList());
    }

    /**
     * 有传入cls时此方法才有效
     */
    public Pagination<R> getPageParsed(Paging paging) {
        Pagination<Maps> pagination = getPage(paging);
        Pagination<R> result = new Pagination<>(pagination.getPageSize(), pagination.getCurrentPage(), pagination.getTotal());
        result.setDataList(pagination.getDataList().stream().map(e -> e.toJava(cls)).peek(this::peek).collect(Collectors.toList()));
        return result;
    }

    /**
     * 获取from+where+group的sql
     */
    public String getSql(boolean isCount) {
        //whereSql
        String whereSql = this.whereSql != null ? this.whereSql : " ";
        String groupSql = this.groupSql != null ? this.groupSql : " ";
        String checkSql = (whereSql + groupSql).toLowerCase();

        //优化fromSql里多余的join
        List<FromBuilder.Item> selItems = new ArrayList<>(this.fromSql.getList());
        while (true) {
            boolean hasRemove = selItems.removeIf(item -> {
                //检测忽略
                if (isCount && !Strings.isNullOrEmpty(item.getAlias()) && !checkSql.contains(item.getAlias().toLowerCase() + ".")) {
                    //检测其他from里也没引用这个alias
                    if (!checkOtherFromContain(selItems, item)) {
                        return true;
                    }
                }
                return false;
            });
            if (!hasRemove) {
                break;
            }
        }
        StringBuilder fromSql = new StringBuilder();
        for (FromBuilder.Item item : selItems) {
            fromSql.append(" " + item.getJoinSql() + " ");
        }

        return String.join(" ", Lists.newArrayList(fromSql.toString(), whereSql + groupSql));
    }

    /**
     * @return false表示其他from里也没包含这项
     */
    protected boolean checkOtherFromContain(List<FromBuilder.Item> list, FromBuilder.Item item) {
        for (FromBuilder.Item checkItem : list) {
            //不是item本身
            if (checkItem != item) {
                //检测包含
                if (checkItem.getJoinSql().toLowerCase().contains(item.getAlias().toLowerCase() + ".")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getCountSql() {
        return String.join(" ", Lists.newArrayList("select count(*) from ( select 1 ", getSql(true), ") v"));
    }

    public String getOrderBySql(String orderBy) {
        if (orderBy != null) {
            String[] ss = orderBy.split(",");
            List<String> orders = Arrays.stream(ss).map((Function<String, String>) String::trim).filter(e -> !e.isEmpty()).collect(Collectors.toList());
            if (!orders.isEmpty()) {
                StringBuilder orderQl = new StringBuilder(" order by ");
                boolean first = true;
                for (String order : orders) {
                    String flag = order.substring(0, 1);
                    Preconditions.checkArgument("+".equals(flag) || "-".equals(flag));
                    boolean asc = "+".equals(flag);
                    String field = order.substring(1);
                    if (first) {
                        first = false;
                    } else {
                        orderQl.append(",");

                    }
                    orderQl.append(" " + field + " " + (asc ? "asc" : "desc"));
                }
                return orderQl.toString();
            }
        }
        return "";
    }

    public String getListSql() {
        String orderSql = query != null ? getOrderBySql(query.getQOrder()) : "";
        return String.join(" ", Lists.newArrayList(selSql, getSql(false), orderSql));
    }

    public String getLimitSql(Paging paging) {
        return " limit " + (paging.getPage() - 1) * paging.getPageSize() + "," + paging.getPageSize() + " ";
    }

    protected List<Maps> convert(List<Maps> list) {
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    protected Maps convert(Maps maps) {
        for (Converter converter : this.converters) {
            maps = converter.convert(maps);
        }
        return maps;
    }

    protected void peek(List<R> list) {
        for (R r : list) {
            peek(r);
        }
    }

    protected void peek(R obj) {
        for (Peeker<R> peeker : this.peekers) {
            peeker.peek(obj);
        }
    }
}
