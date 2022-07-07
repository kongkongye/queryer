package com.kongkongye.backend.queryer.query;

import com.kongkongye.backend.queryer.query.annotation.AutoQuery;
import lombok.Data;

/**
 * 提供的基础请求实现
 * (如果自己的请求实现不想继承此类，也可以自行实现Query接口)
 */
@Data
@AutoQuery
public class BaseQuery implements Query {
    protected String orderBy;
    protected QueryTypeEn queryType;
}
