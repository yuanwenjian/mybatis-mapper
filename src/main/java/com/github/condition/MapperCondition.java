package com.github.condition;

import com.github.exception.MapperException;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class MapperCondition {

    private static final Logger LOG = LoggerFactory.getLogger(MapperCondition.class);

    private List<Criteria> criterias;

    private String orderByField;

    private Boolean distinct;

    private Integer offset;

    private Integer size;


    public <T> Criteria<T> createCriteria(Class<T> tClass) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            if (criterias == null) {
                criterias = new ArrayList<>();
            }
            Criteria<T> criteria = new Criteria<>(tClass);
            criterias.add(criteria);
            return criteria;
        }
    }

    public Criteria or(Class tclass) {
        Criteria criteria = new Criteria(tclass);
        if (CollectionUtils.isEmpty(criterias)) {
            criterias = new ArrayList<>();
        }
        criterias.add(criteria);
        return criteria;
    }
}
