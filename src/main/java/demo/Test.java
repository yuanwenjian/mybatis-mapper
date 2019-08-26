package demo;

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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String resource = "config/mybatis-config.xml";
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder(); //创建sqlSessionFactoryBuilder
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(reader);  //配置
            SqlSession sqlSession = sqlSessionFactory.openSession(); //获取session
            PhoneImeiMapper mapper = sqlSession.getMapper(PhoneImeiMapper.class); // 通过session获取mapper

//            JoinMapperCondition mapperCondition = new JoinMapperCondition();
//            Criteria<PhoneImei> criteria = mapperCondition.createJoinCriteria(PhoneImei.class);
//            Criteria<User> userCriteria = mapperCondition.createJoinCriteria(User.class,"user");
//            mapperCondition.appendOrder("phoneImei","imeiId",true);
//            mapperCondition.appendOrder(User.class,"userId",true);
//            Criteria phoneCriteria = mapperCondition.or(PhoneImei.class);
//            Criteria phoneCriteria = mapperCondition.or(PhoneImei.class);
//            phoneCriteria.andEqual("imeiNum", "868498020093495");
//            phoneCriteria.andEqual("tenantId",50001);
//
            MapperCondition mapperCondition = new MapperCondition();
            Criteria criteria = mapperCondition.createCriteria(PhoneImei.class);
            mapperCondition.appendOrderByFiled("imeiId");

            List<Long> ids = new ArrayList<>();
            ids.add(12l);
            ids.add(13l);
            ids.add(14l);
            criteria.andIn("imeiId",ids);
//            userCriteria.andEqual("userId",23);
//            List<PhoneImei> phoneImeiList = mapper.joinCondition(mapperCondition); //查询
//            List<PhoneImei> phoneImeiList = mapper.joinMapperCondition(mapperCondition);
            List<PhoneImei> phoneImeiList = mapper.findByCondition(mapperCondition);
            System.out.println(phoneImeiList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
