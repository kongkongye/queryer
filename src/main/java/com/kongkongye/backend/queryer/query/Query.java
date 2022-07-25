package com.kongkongye.backend.queryer.query;

/**
 * 请求
 * <p>
 * 如果同时传入白名单与黑名单，黑名单优先，取白名单-黑名单的交集
 */
public interface Query {
    /**
     * @return 排序
     */
    String getQOrder();

    /**
     * @return 查询类型
     */
    QueryTypeEn getQType();

    /**
     * 查询字段白名单
     * (非null或空字符串时有效，空数组表示白名单为空)
     *
     * @return json array string
     */
    String getQWhite();

    /**
     * 查询字段黑名单
     * (非null或空字符串时有效，空数组表示黑名单为空)
     *
     * @return json string
     */
    String getQBlack();
}
