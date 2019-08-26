package com.github.condition;

import lombok.Data;

import java.util.List;

@Data
public class Condition {

    private Object firstValue;

    private Object secondValue;

    private String symbol;

    private Boolean single;

    private Boolean collection;

    private Boolean between;

    private Boolean sql;

    public Condition(String symbol) {
        this.symbol = symbol;
        this.sql = true;

    }

    public Condition(String symbol, Object firstValue) {
        this.firstValue = firstValue;
        this.symbol = symbol;
        if (firstValue instanceof List<?>) {
            this.collection = true;
        } else {
            this.single = true;
        }
    }

    public Condition(String symbol, Object firstValue, Object secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.symbol = symbol;
        this.between = true;
    }


}
