package com.github.condition;

import com.github.exception.MapperException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinCriteria {

    private Map<Class, Criteria> criteriaMap;


    public JoinCriteria() {
    }

    public JoinCriteria(Criteria criteria) {
        addCriteria(criteria);
    }


    public void addCriteria(Criteria criteria) {
        if (criteriaMap == null) {
            criteriaMap = new HashMap<>();
        }
        Class tclass = criteria.getTClass();
        if (criteriaMap.containsKey(tclass)) {
            throw new MapperException("class[" + tclass.getSimpleName() + "]重复");
        }
        criteriaMap.put(tclass,criteria);
    }

    public List<Condition> getConditions() {
        List<Condition> conditions = new ArrayList<>();
        for (Criteria criteria : criteriaMap.values()) {
            conditions.addAll(criteria.getConditions());
        }
        return conditions;
    }

    public Boolean getValid() {
        if (criteriaMap.isEmpty()) {
            return false;
        }else {
            for (Criteria criteria : criteriaMap.values()) {
                if (criteria.getValid()) {
                    return true;
                }
            }
            return false;
        }
    }


}
