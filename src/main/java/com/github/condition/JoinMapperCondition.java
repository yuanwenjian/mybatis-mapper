package com.github.condition;

import com.github.annotation.TableAlias;
import com.github.exception.MapperException;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class JoinMapperCondition {
    private static Logger LOG = LoggerFactory.getLogger(JoinMapperCondition.class);
    private List<JoinCriteria> criterias;

    public <T> Criteria<T> createJoinCriteria(Class<T> tClass) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            if (criterias == null) {
                criterias = new ArrayList<>();
            }
            String tableAliasName = tClass.getSimpleName();
            TableAlias tableAlias = tClass.getAnnotation(TableAlias.class);
            if (tableAlias != null && StringUtils.isNotEmpty(tableAlias.alias())) {
                tableAliasName = tableAlias.alias();
            }else {
                LOG.warn("别名为空,使用{}为默认别名",tClass.getSimpleName());
            }
            Criteria<T> criteria = new Criteria<>(tClass,tableAliasName);
            if (CollectionUtils.isEmpty(criterias)) {
                criterias = new ArrayList<>();
                criterias.add(new JoinCriteria(criteria));
            } else {
                for (JoinCriteria joinCriteria : criterias) {
                    joinCriteria.addCriteria(criteria);
                }
            }
            return criteria;
        }
    }

    public <T> Criteria<T> createJoinCriteria(Class<T> tClass,String alias) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            if (criterias == null) {
                criterias = new ArrayList<>();
            }

            Criteria<T> criteria = new Criteria<>(tClass,alias);
            if (CollectionUtils.isEmpty(criterias)) {
                criterias = new ArrayList<>();
                criterias.add(new JoinCriteria(criteria));
            } else {
                for (JoinCriteria joinCriteria : criterias) {
                    joinCriteria.addCriteria(criteria);
                }
            }
            return criteria;
        }
    }

    public <T> Criteria<T> createInnerCriteria(Class<T> tClass) {
        Criteria<T> criteria = new Criteria<>(tClass);
        return criteria;
    }

    public <T> Criteria<T> createInnerCriteria(Class<T> tClass,String alias) {
        Criteria<T> criteria = new Criteria<>(tClass,alias);
        return criteria;
    }

    public Criteria or(Class tclass, String alias) {
        Criteria criteria = createInnerCriteria(tclass, alias);
        if (CollectionUtils.isEmpty(criterias)) {
            criterias.add(new JoinCriteria(criteria));
        }

        return criteria;
    }

    public Criteria or(Class tclass) {
        Criteria criteria = createInnerCriteria(tclass);
        if (CollectionUtils.isEmpty(criterias)) {
            criterias = new ArrayList<>();
        }
        criterias.add(new JoinCriteria(criteria));
        return criteria;
    }

}
