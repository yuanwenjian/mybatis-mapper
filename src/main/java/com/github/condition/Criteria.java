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

    public Criteria andIsNull(String property) {
        addCondition(getColumn(property) + " is null");
        return (Criteria) this;
    }

    public Criteria andIsNotNull(String property) {
        addCondition(getColumn(property) +" is not null");
        return (Criteria) this;
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



    public <T> Criteria andIn(String property,List<T> values) {
        if(values!=null && values.size()>1000){

            addCondition(buildBatchInCondition(getColumn(property),values,false));
        }else{
            addCondition(getColumn(property) + " in", values, property);
        }
        return (Criteria) this;
    }

    public <T> Criteria andInAsChar(String property,List<T> values) {
        if(values!=null && values.size()>1000){

            addCondition(buildBatchInCondition(getColumn(property),values,true));
        }else{
            addCondition(getColumn(property) + " in", values, property);
        }
        return (Criteria) this;
    }

    private String buildBatchInCondition(String column,List<?> inValues,boolean isChar) {
        StringBuilder sb=new StringBuilder();
        int batchSize=1000;
        int count=(inValues.size()%batchSize==0)?inValues.size()/batchSize:inValues.size()/batchSize+1;
        sb.append("(");
        for(int i=0;i<count;i++){
            int endIndex=(i+1)*batchSize>inValues.size()?inValues.size():(i+1)*batchSize;
            List<?> batchInValues=inValues.subList(i*batchSize,endIndex);
            StringBuilder batchSb=new StringBuilder();
            batchSb.append(column+" in (");
            for(Object each:batchInValues){
                if(isChar){
                    batchSb.append("'"+each+"',");
                }else{
                    batchSb.append(each+",");
                }
            }
            if(i!=0){
                sb.append(" or ");
            }
            sb.append(batchSb.substring(0,batchSb.length()-1)+")");
        }
        sb.append(")");
        return sb.toString();
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
