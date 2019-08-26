package com.github.condition;

import com.github.annotation.Column;
import com.github.annotation.TableAlias;
import com.github.exception.MapperException;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JoinMapperCondition {
    private static Logger LOG = LoggerFactory.getLogger(JoinMapperCondition.class);
    private List<JoinCriteria> criterias;

    private String orderByClause;

    private Boolean distinct;

    private Integer offset;

    private Integer size;


    private Map<String, Class> aliasMap = new HashMap<>();

    public <T> Criteria<T> createJoinCriteria(Class<T> tClass) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            if (criterias == null) {
                criterias = new ArrayList<>();
            }
            String tableAliasName = getTableAlias(tClass);
            Criteria<T> criteria = createJoinCriteria(tClass, tableAliasName);
            return criteria;
        }
    }

    public <T> Criteria<T> createJoinCriteria(Class<T> tClass, String alias) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            if (criterias == null) {
                criterias = new ArrayList<>();
            }

            Criteria<T> criteria = new Criteria<>(tClass, alias);
            if (CollectionUtils.isEmpty(criterias)) {
                criterias = new ArrayList<>();
                criterias.add(new JoinCriteria(criteria));
            } else {
                for (JoinCriteria joinCriteria : criterias) {
                    joinCriteria.addCriteria(criteria);
                }
            }
            this.aliasMap.put(alias, tClass);
            return criteria;
        }
    }

    public <T> Criteria<T> createInnerCriteria(Class<T> tClass) {
        if (tClass == null) {
            throw new MapperException("class is null");
        }
        if (criterias == null) {
            criterias = new ArrayList<>();
        }
        String tableAliasName = getTableAlias(tClass);
        Criteria<T> criteria = createInnerCriteria(tClass, tableAliasName);
        return criteria;
    }

    public <T> Criteria<T> createInnerCriteria(Class<T> tClass, String alias) {
        Criteria<T> criteria = new Criteria<>(tClass, alias);
        this.aliasMap.put(alias, tClass);
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

    public void setPage(Integer offset, Integer size) {
        this.offset = offset;
        this.size = size;
    }

    private String getTableAlias(Class tClass) {
        String tableAliasName = tClass.getSimpleName();
        TableAlias tableAlias = (TableAlias) tClass.getAnnotation(TableAlias.class);
        if (tableAlias != null && StringUtils.isNotEmpty(tableAlias.alias())) {
            tableAliasName = tableAlias.alias();
        } else {
            LOG.warn("别名为空,使用{}为默认别名", tClass.getSimpleName());
        }
        return tableAliasName;
    }

    private String getColumn(Class tClass, String field) {
        try {
            String column = field;
            Field declaredField = tClass.getDeclaredField(field);
            Column columnAnnotation = declaredField.getDeclaredAnnotation(Column.class);
            if (columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.value())) {
                column = columnAnnotation.value();
            }
            for (String tableAlias : aliasMap.keySet()) {
                if (aliasMap.get(tableAlias).equals(tClass)) {
                    return tableAlias + "." + column;
                }
            }
            return column;
        } catch (NoSuchFieldException e) {
            LOG.warn("filed:{} 不存在", field);
            e.printStackTrace();
            throw new MapperException("filed 不存在");
        }
    }


    public void appendOrder(Class tClass, String field, Boolean asc) {
        String tableAlias = getTableAlias(tClass);
        if (aliasMap.containsKey(tableAlias)) {
            String column = getColumn(tClass, field);
            if (StringUtils.isEmpty(this.orderByClause)) {
                this.orderByClause = column + " " + orderType(asc);
            } else {
                this.orderByClause=this.orderByClause+","+column + " " + orderType(asc);
            }
        } else {
            throw new MapperException("JoinMapperCondition 不包含" + tClass.getSimpleName());
        }
    }

    public void appendOrder(String tableAlias, String field, Boolean asc) {
        if (aliasMap.containsKey(tableAlias)) {
            Class tClass = aliasMap.get(tableAlias);
            String column = getColumn(tClass, field);
            if (StringUtils.isEmpty(this.orderByClause)) {
                this.orderByClause = column + " " + orderType(asc);
            } else {
                this.orderByClause=this.orderByClause+","+column + " " + orderType(asc);
            }
        } else {
            throw new MapperException("JoinMapperCondition 不包含别名" + tableAlias);
        }
    }

    private String defaultOrderType() {
        return "asc";
    }

    private String orderType(Boolean asc) {
        return asc ? "asc" : "desc";
    }


}
