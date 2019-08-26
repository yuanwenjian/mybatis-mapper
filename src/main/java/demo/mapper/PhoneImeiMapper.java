package demo.mapper;

import com.github.condition.JoinMapperCondition;
import com.github.condition.MapperCondition;
import demo.model.PhoneImei;

import java.util.List;

/**
 * Created with Intellij IDEA
 * Author: xuziling
 * Date:  2019/1/22
 * Description:
 */

public interface PhoneImeiMapper {
    PhoneImei findById(Long imeiId);

    List<PhoneImei> findAll();

    List<PhoneImei> findByCondition(MapperCondition condition);

    List<PhoneImei> joinCondition(MapperCondition condition);

    List<PhoneImei> joinMapperCondition(JoinMapperCondition condition);

}
