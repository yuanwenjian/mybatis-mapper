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
            <!--通用sql解析-->
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
```
PhoneImeiMapper mapper = sqlSession.getMapper(PhoneImeiMapper.class); // 通过session获取mapper

MapperCondition mapperCondition = new MapperCondition();
Criteria criteria = mapperCondition.createCriteria(PhoneImei.class);
List<Long> ids = new ArrayList<>();
ids.add(12l);
ids.add(13l);
ids.add(14l);
criteria.andIn("imeiId",ids);
List<PhoneImei> phoneImeiList = mapper.findByCondition(mapperCondition);
```


- 关联查询
xml 配置
```xml
    <select id="joinMapperCondition" parameterType="com.github.condition.JoinMapperCondition"
            resultType="demo.model.PhoneImei">
        SELECT 
        <if test="distinct">
                    distinct
        </if>
        phoneImei.* FROM wms_phone_imei phoneImei
        JOIN wms_user user ON phoneImei.imei_id=user.user_id
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

```java
public class MybatisMapperTest {


    private PhoneImeiMapper mapper;

    @Before
    public void testPre() {
        String resource = "config/mybatis-config.xml";
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder(); //创建sqlSessionFactoryBuilder
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(reader);  //配置
            SqlSession sqlSession = sqlSessionFactory.openSession(); //获取session
            mapper = sqlSession.getMapper(PhoneImeiMapper.class); // 通过session获取mapper
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    /**
     * 单表多条件查询 and
     * ==>  Preparing: SELECT * FROM wms_phone_imei WHERE ( imei_id in ( ? , ? , ? ) AND tenant_id= ? ) 
     *   ==> Parameters: 12(Long), 13(Long), 14(Long), 111(Integer)
     */
    public void testAnd() {
        MapperCondition mapperCondition = new MapperCondition();
        Criteria criteria = mapperCondition.createCriteria(PhoneImei.class);
        List<Long> ids = new ArrayList<>();
        ids.add(12l);
        ids.add(13l);
        ids.add(14l);
        criteria.andIn("imeiId",ids);
        criteria.andEqual("tenantId",111);

        List<PhoneImei> phoneImeiList = mapper.findByCondition(mapperCondition);

    }

    @Test
    /**
     * 单表or查询
     * ==>  Preparing: SELECT * FROM wms_phone_imei WHERE ( imei_id= ? ) or( tenant_id= ? ) 
     * ==> Parameters: 14(Integer), 50001(Integer)
     */
    public void testOr() {
        MapperCondition mapperCondition = new MapperCondition();
        Criteria criteria = mapperCondition.createCriteria(PhoneImei.class);
        criteria.andEqual("imeiId", 14);
        Criteria orCriteria = mapperCondition.or(PhoneImei.class);
        orCriteria.andEqual("tenantId", 50001);
        List<PhoneImei> phoneImeiList = mapper.findByCondition(mapperCondition);

    }

    @Test
    /**
     * 关联查询 and查询
     * ==>  Preparing: SELECT phoneImei.* FROM wms_phone_imei phoneImei JOIN wms_user user ON phoneImei.imei_id=user.user_id WHERE ( user.user_id= ? AND phoneImei.imei_id= ? ) 
     * ==> Parameters: 15(Integer), 14(Integer)
     */
    public void testJoinAnd() {
        JoinMapperCondition mapperCondition = new JoinMapperCondition();
        Criteria phoneCriteria = mapperCondition.createJoinCriteria(PhoneImei.class);
        Criteria userCriteria = mapperCondition.createJoinCriteria(User.class);
        phoneCriteria.andEqual("imeiId", 14);
        userCriteria.andEqual("userId", 15);
        mapper.joinMapperCondition(mapperCondition);
    }

    @Test
    /**
     * 关联查询 or
     * ==>  Preparing: SELECT distinct phoneImei.* FROM wms_phone_imei phoneImei JOIN wms_user user ON phoneImei.imei_id=user.user_id WHERE ( phoneImei.imei_id= ? ) or( user.user_id= ? AND user.user_name= ? ) LIMIT 1,20 
     * ==> Parameters: 14(Integer), 15(Integer), aa(String)
     */
    public void testJoinOr() {

        JoinMapperCondition mapperCondition = new JoinMapperCondition();
        Criteria phoneCriteria = mapperCondition.createJoinCriteria(PhoneImei.class);
        Criteria userCriteria = mapperCondition.or(User.class);
        userCriteria.andEqual("userId", 15);
        userCriteria.andEqual("userName","aa");
        phoneCriteria.andEqual("imeiId", 14);
        mapperCondition.setPage(1,20);
        mapperCondition.setDistinct(true);
        mapper.joinMapperCondition(mapperCondition);
    }

    @Test
    /**
     * 排序查询
     * ==>  Preparing: SELECT phoneImei.* FROM wms_phone_imei phoneImei JOIN wms_user user ON phoneImei.imei_id=user.user_id WHERE ( phoneImei.imei_id= ? ) or( user.user_id= ? AND user.user_name= ? ) order by phoneImei.imei_id asc 
     * ==> Parameters: 14(Integer), 15(Integer), aa(String)
     */
    public void testOrder() {
        JoinMapperCondition mapperCondition = new JoinMapperCondition();
        Criteria phoneCriteria = mapperCondition.createJoinCriteria(PhoneImei.class);
        Criteria userCriteria = mapperCondition.or(User.class);
        userCriteria.andEqual("userId", 15);
        userCriteria.andEqual("userName","aa");
        phoneCriteria.andEqual("imeiId", 14);
        mapperCondition.appendOrder(PhoneImei.class,"imeiId",true);
        mapper.joinMapperCondition(mapperCondition);
    }
}
```