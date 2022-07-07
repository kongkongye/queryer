package com.kongkongye.backend.queryer.query;

/**
 * 请求
 */
public interface Query {
    /**
     * @return 排序
     */
    String getOrderBy();

    /**
     * @return 查询类型
     */
    QueryTypeEn getQueryType();
}
