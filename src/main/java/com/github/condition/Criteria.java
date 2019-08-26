package com.github.condition;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.github.annotation.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class Criteria<T> {

    private Class<T> tClass;

    private String alias;

    private List<Condition> conditions=new ArrayList<>();

    public Criteria(Class<T> tClass) {
        this.tClass = tClass;
    }

    public Criteria(Class<T> tClass, String alias) {
        this.tClass = tClass;
        this.alias = alias;
    }

    private String getColumn(String field) {
        String column = field;
        try {
            Field classField = this.tClass.getDeclaredField(field);
            Column columnAnnotation = classField.getDeclaredAnnotation(Column.class);
            column = classField.getName();
            if (columnAnnotation != null && StringUtils.isNotEmpty(columnAnnotation.value())) {
                column = columnAnnotation.value();
            }

            System.out.println(column);
        } catch (NoSuchFieldException e) {
            System.out.println("field "+field+"没有对应字段");
            e.printStackTrace();
        }
        if (StringUtils.isNotEmpty(this.alias)) {
            return this.alias + "." + column;
        }
        return column;
    }

    public void andEqual(String field, Object value) {
        addCondition(getColumn(field) + "=", value);
    }

    public void andNotEqual(String field, Object value) {
        addCondition(getColumn(field) + "<>", value);
    }

    public void andBetween(String field, Object firstValue, Object secondValue) {
        addCondition(getColumn(field) + " between", firstValue, secondValue);
    }

    public void andNotBetween(String filed, Object firstValue, Object secondeValue) {
        addCondition(getColumn(filed) + " not between", firstValue, secondeValue);
    }

    public void andGreat(String field, Object firstValue) {
        addCondition(getColumn(field) + ">", firstValue);
    }

    public void andGreatEqual(String field, Object firstValue) {
        addCondition(getColumn(field) + " >=", firstValue);
    }

    public void andLess(String field, Object firstValue) {
        addCondition(getColumn(field) + " <", firstValue);
    }

    public void andLessEqual(String field, Object firstValue) {
        addCondition(getColumn(field) + " <=", firstValue);
    }



    public void andIn(String field, List<?> values) {
        addCondition(getColumn(field) + " in", values);
    }

    public void andNotIn(String field, List<?> firstValues) {
        addCondition(getColumn(field) + " not in ", firstValues);
    }

    public void andLike(String field, Object firstValue) {
        addCondition(getColumn(field) + " like", "%" + firstValue + "%");
    }

    public void andLikeRight(String field, Object firstValue) {
        addCondition(getColumn(field) + " like", firstValue + "%");
    }

    public void andLiktLeft(String field, Object firstValue) {
        addCondition(getColumn(field) + " like", "%" + firstValue);
    }
    public void andSql(String sql) {
        addCondition(sql);
    }

    public void or(String sql) {
        Criteria criteria = new Criteria(this.tClass);

    }

    public Condition addCondition(String sql) {
        Condition condition = new Condition(sql);
        this.conditions.add(condition);
        return condition;
    }

    public Condition addCondition(String symbol,Object value) {
        Condition condition = new Condition(symbol, value);
        this.conditions.add(condition);
        return condition;
    }

    public Condition addCondition(String symbol, Object firstValue, Object secondValue) {
        Condition condition = new Condition(symbol, firstValue, secondValue);
        this.conditions.add(condition);
        return condition;
    }

    public Boolean getValid() {
        return CollectionUtils.isNotEmpty(conditions);
    }
}
