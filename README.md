## 介绍

此库用来快速实现查询逻辑，本质上是组装sql语句进行查询，目前sql语句是按mysql来写的，其他数据库可能不适用（如sqlserver）。

### 项目定位

1. 简单增删改：用`spring data`的`CrudRepository`接口。
2. 简单查询：用`spring data`的`Repository`接口功能，如findAll()等。
3. 复杂查询：此项目定位。
4. 高性能查询：如果存在大量需要高性能的复杂sql查询，推荐使用mybatis等写原生sql的框架。

## 思想

`定义式`指定`查询条件`与`返回字段`，`注解`辅助解析，避免字符串拼接。

本质还是写原生sql，只是用`字段+注解`来辅助自动生成sql，以减少大部分样板代码。
比如我查询的返回类里写了name字段，那自然是要查出name字段的；
比如我查询的条件类里写了name字段，那自然是想通过name来当作条件筛选数据的。

## 快速使用

### xxxQuery

```java

@Data
@AutoQuery
public class UserQuery extends BaseQuery {
    private String name;
    private Boolean disabled;
}
```

### xxxDTO

```java

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

```java

@Component
public class UserDao {
    @Autowired
    protected NamedParameterJdbcTemplate template;

    public SqlHelperBuilder<UserDTO> help(UserQuery query) {
        return new JdbcSqlHelperBuilder<>(template, (selSql, fromSql, whereSql, groupSql, params) -> {
            //额外的sql写这里
        }, query, UserDTO.class);
    }
}
```

### 使用

```java

public class SomeService {
    public void someQuery() {
        SqlHelper<UserDTO> helper = userDao.help(new UserQuery()).build();
        //获得数量
        int count = helper.getCount();
        //获得全部列表
        List<UserDTO> users = helper.getListParsed();
        //获取分页列表
        Pagination<UserDTO> pagination = helper.getPageParsed(new Paging(1, 10));
        //获取一个
        UserDTO user = helper.getOneParsed();
    }
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

### 字段白名单与黑名单

客户端通过传入`qWhite`与`qBlack`可选择性改变查询的字段。
比如只查需要的id,code,name字段；或不查可能很长的note,description字段等。

## 常见问题

### 会不会有sql注入的风险？

用的是`prepared statement`，没有注入风险。

## 举例

### 基础使用

```java
public class UserDao {
    /**
     * 返回SqlHelperBuilder的好处是可以继续扩展
     */
    public SqlHelperBuilder<UserDTO> help1(UserQuery query) {
        return help((selSql, fromSql, whereSql, groupSql, params) -> {
            //逻辑
        }, query, UserDTO.class);
    }

    public SqlHelper<UserDTO> help2(UserQuery query) {
        return help((selSql, fromSql, whereSql, groupSql, params) -> {
            //逻辑
        }, query, UserDTO.class).build();
    }
}

```

### 多个视图复用查询逻辑

多个方法返回不同的DTO，但是复用公共逻辑（尤其是公共逻辑代码量比较大的时候）：

```java
/**
 * UserDetailDTO 跟 UserBriefDTO 没有继承关系要求。
 */
public class UserDao {
    private <T> SqlHelperBuilder<T> help(UserQuery query, Class<T> dtoCls) {
        return help((selSql, fromSql, whereSql, groupSql, params) -> {
            //公共逻辑
        }, query, dtoCls);
    }

    /**
     * 查用户详细信息
     * UserDetailQuery 继承了 UserQuery
     */
    public SqlHelperBuilder<UserDetailDTO> helpDetail(UserDetailQuery query) {
        return help(query, UserDetailDTO.class).then((selSql, fromSql, whereSql, groupSql, params) -> {
            //detail额外逻辑
        });
    }

    /**
     * 查用户简易信息
     * UserBriefQuery 继承了 UserQuery
     */
    public SqlHelperBuilder<UserBriefDTO> helpBrief(UserBriefQuery query) {
        return help(query, UserBriefDTO.class).then((selSql, fromSql, whereSql, groupSql, params) -> {
            //brief额外逻辑
        });
    }
}

```

## 不足

1. 目前如果搜索字符串中包含%等特殊字符，搜索结果就会不对（不过就算不用这个框架，也是会有这个问题的）