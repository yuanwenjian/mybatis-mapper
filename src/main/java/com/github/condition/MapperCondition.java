package com.github.condition;

import com.github.annotation.Column;
import com.github.exception.MapperException;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class MapperCondition {

    private static final Logger LOG = LoggerFactory.getLogger(MapperCondition.class);

    private List<Criteria> criterias;

    private String orderByClause;

    private Boolean distinct;

    private Integer offset;

    private Integer size;

    private Class tclass;

    public MapperCondition() {
        criterias = new ArrayList<>();
    }

    public <T> Criteria<T> createCriteria(Class<T> tClass) {
        synchronized (this) {
            if (tClass == null) {
                throw new MapperException("class is null");
            }
            this.tclass = tClass;
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

    public void setPage(Integer offset, Integer size) {
        this.offset=offset;
        this.size = size;
    }

    private String getColumn(String field) {

        try {
            String column = field;
            Field declaredField = this.tclass.getDeclaredField(field);
            Column columnAnnotation = declaredField.getDeclaredAnnotation(Column.class);
            if (columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.value())) {
                column = columnAnnotation.value();
            }
            return column;
        } catch (NoSuchFieldException e) {
            LOG.warn("filed:{} 不存在",field);
            e.printStackTrace();
            throw new MapperException("filed 不存在");
        }

    }

    private String defaultOrderType() {
        return "asc";
    }

    private String orderType(Boolean asc) {
        return asc ? "asc" : "desc";
    }

    public String appendOrderByFiled(String... fields) {

        StringBuffer sbFields = new StringBuffer();
        for (String field : fields) {
            sbFields.append(getColumn(field)).append(" ").append(defaultOrderType()).append(",");
        }
        if (StringUtils.isEmpty(this.orderByClause)) {
            this.orderByClause = StringUtils.removeEnd(sbFields.toString(),",");
        }else {
            this.orderByClause = this.orderByClause+","+StringUtils.removeEnd(sbFields.toString(),",");

        }
        return this.orderByClause;
    }

    public String addOrderField(String field, Boolean asc) {

        String appedOrder = getColumn(field) + " " + orderType(asc);
        if (StringUtils.isEmpty(this.orderByClause)) {
            this.orderByClause =  appedOrder;
        }else {
            this.orderByClause = this.orderByClause + "," + appedOrder;
        }
        return this.orderByClause;
    }
}
