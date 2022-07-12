## 介绍

此库用来快速实现查询逻辑，本质上是组装sql语句进行查询，目前sql语句是按mysql来写的，其他数据库可能不适用（如sqlserver）。

## 思想

`定义式`指定`查询条件`与`返回字段`，`注解`辅助解析，避免字符串拼接。

## 实现

默认只提供`jdbc`的实现，在包`com.kongkongye.backend.queryer.jdbc`内，若要hibernate或mybatis实现，需要自行扩展(一般没有必要)。

## 快速使用

### xxxQuery

``` java

@Data
@AutoQuery
public class UserQuery extends BaseQuery {
    private String name;
    private Boolean disabled;
}
```

### xxxDTO

``` java

@AutoSel
@AutoFrom
@Data
@QueryTable("user")
public class UserDTO {
    private Long id;
    private String name;
    private Boolean disabled;
}
```

### xxxDao

``` java

@Component
public class UserDao {
    @Autowired
    protected NamedParameterJdbcTemplate template;

    public SqlHelper<UserDTO> help(UserQuery query) {
        return JdbcUtil.help(template, (selSql, fromSql, whereSql, groupSql, params) -> {
            //额外的sql写这里
        }, query, UserDTO.class);
    }
}
```

### 使用

``` java

public void someQuery(){
     SqlHelper<UserDTO> helper=userDao.help(new UserQuery());
     //获得数量
     int count=helper.getCount();
     //获得全部列表
     List<UserDTO> users=helper.getListParsed();
     //获取分页列表
     Pagination<UserDTO> pagination=helper.getPageParsed(new Paging(1,10));
     //获取一个
     UserDTO user=helper.getOneParsed();
}

```

## 功能

### select

可以手动添加select语句，也可以在DTO上添加`@AutoSel`来自动添加select语句。
select的字段是DTO里的字段，字段上可以添加`@SelParse`注解来辅助解析。

### from

可以手动添加from语句，也可以在DTO上添加`@AutoFrom`来自动添加from语句。

### where

可以手动添加where语句，也可以在Query上添加`@AutoQuery`来自动解析where条件。

### group

help方法的groupSql里可以添加自定义group相关sql

### order by

Query的基类里有orderBy字段，设置后可以自动解析排序语句。

### limit

SqlHelper里获取分页数据会自动进行limit

## 常见问题

### 会不会有sql注入的风险？

用的是`prepared statement`，没有注入风险。

## 不足

1. 目前如果搜索字符串中包含%等特殊字符，搜索结果就会不对（不过就算不用这个框架，也是会有这个问题的）