Mybatis-mapper用于统一构建条件查询

使用方法如下

1.引入pom文件
```xml
<dependency>
    <groupId>com.github</groupId>
        <artifactId>mybatis-mapper</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

2.java调用

- 单表查询
xml 配置
```xml
    <select id="findByCondition"
            parameterType="com.github.condition.MapperCondition"
            resultType="demo.model.PhoneImei">
        SELECT
        <if test="distinct">
            distinct
        </if>
        *
        FROM wms_phone_imei
        <if test="_parameter!=null">
            <include refid="com.github.common.mapperCondition"></include>
        </if>
        <if test="orderByClause !=null">
            order by ${orderByClause}
        </if>
        <if test="offset !=null and size !=null">
            LIMIT ${offset},${size}
        </if>

    </select>
```

java调用