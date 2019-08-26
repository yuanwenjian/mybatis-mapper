package com.github;

import com.github.condition.Criteria;
import com.github.condition.JoinMapperCondition;
import com.github.condition.MapperCondition;
import demo.mapper.PhoneImeiMapper;
import demo.model.PhoneImei;
import demo.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
